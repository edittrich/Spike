/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.edittrich.spike.springoauth2;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CompositeFilter;

@SpringBootApplication
@RestController
@EnableOAuth2Client
public class Application extends WebSecurityConfigurerAdapter {

	@Autowired
	OAuth2ClientContext oauth2ClientContext;

	/*
	 * @Autowired private ClientRegistrationRepository clientRegistrationRepository;
	 */

	@RequestMapping("/user")
	public Principal user(Principal principal) {
		return principal;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**", "/webjars/**", "/error**").permitAll()
				.anyRequest().authenticated().and().exceptionHandling()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
				/*
				 * .and().oauth2Login().authorizationEndpoint()
				 * .authorizationRequestResolver(new
				 * CustomAuthorizationRequestResolver(this.clientRegistrationRepository,
				 * "/oauth2/authorization"))
				 */
				.and().logout().logoutSuccessUrl("/").permitAll().and().csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
				.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)

		;
		// @formatter:on
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(
			OAuth2ClientContextFilter filter) {
		FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<OAuth2ClientContextFilter>();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	private Filter ssoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();

		OAuth2ClientAuthenticationProcessingFilter dbapiFilter = new OAuth2ClientAuthenticationProcessingFilter(
				"/login/dbapi");
		OAuth2RestTemplate dbapiTemplate = new OAuth2RestTemplate(dbapi(), oauth2ClientContext);
		dbapiFilter.setRestTemplate(dbapiTemplate);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(dbapiResource().getUserInfoUri(),
				dbapi().getClientId());
		tokenServices.setRestTemplate(dbapiTemplate);
		dbapiFilter.setTokenServices(tokenServices);
		filters.add(dbapiFilter);

		OAuth2ClientAuthenticationProcessingFilter netidFilter = new OAuth2ClientAuthenticationProcessingFilter(
				"/login/netid");
		OAuth2RestTemplate netidTemplate = new OAuth2RestTemplate(netid(), oauth2ClientContext);
		netidFilter.setRestTemplate(netidTemplate);
		tokenServices = new UserInfoTokenServices(netidResource().getUserInfoUri(), netid().getClientId());
		tokenServices.setRestTemplate(netidTemplate);
		netidFilter.setTokenServices(tokenServices);
		filters.add(netidFilter);

		filter.setFilters(filters);
		return filter;
	}

	@Bean
	@ConfigurationProperties("dbapi.client")
	public AuthorizationCodeResourceDetails dbapi() {
		return new AuthorizationCodeResourceDetails();
	}

	@Bean
	@ConfigurationProperties("dbapi.resource")
	public ResourceServerProperties dbapiResource() {
		return new ResourceServerProperties();
	}

	@Bean
	@ConfigurationProperties("netid.client")
	public AuthorizationCodeResourceDetails netid() {
		return new AuthorizationCodeResourceDetails();
	}

	@Bean
	@ConfigurationProperties("netid.resource")
	public ResourceServerProperties netidResource() {
		return new ResourceServerProperties();
	}

}
