package de.edittrich.spike.jwt;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

public class Application {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {

		try {
			log.debug("Create");
			
			//
			
			KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
			gen.initialize(2048);
			KeyPair keyPair = gen.generateKeyPair();

			//

			JWK jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
					.privateKey((RSAPrivateKey) keyPair.getPrivate()).algorithm(JWSAlgorithm.RS512).keyID("rsa1")
					.build();

			JWKSet jwkSet = new JWKSet(jwk);

			PrintWriter writer = new PrintWriter("jwks.json", "UTF-8");
			writer.println(jwkSet.toPublicJWKSet().toString());
			writer.close();

			log.debug("JWKSet: {}", jwkSet.toString());
			log.debug("JWKSet Public: {}", jwkSet.toPublicJWKSet().toString());

			//

			JWSSigner signer = new RSASSASigner((RSAPrivateKey) keyPair.getPrivate());

			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
					.audience("JWT Spike")
					.subject("spike")
					.issuer("https://edittrich.de")
					.expirationTime(new Date(new Date().getTime() + 15 * 60 * 1000))
					.issueTime(new Date(new Date().getTime()))
					.jwtID(UUID.randomUUID().toString())
					.build();

			SignedJWT signedJWT = new SignedJWT(
					new JWSHeader(JWSAlgorithm.RS512, null, null, null, null, null, null, null, null, null, "rsa1", null, null),
					claimsSet);

			signedJWT.sign(signer);
			String signedJWTserialized = signedJWT.serialize();

			log.debug("accessToken: {}", signedJWTserialized);

			//
			
			log.debug("Validate");

			ConfigurableJWTProcessor<SecurityContext> jwtProcessorI = new DefaultJWTProcessor<SecurityContext>();

			JWKSet jwkSetI = JWKSet.load(new File("jwks.json"));
			JWKSource<SecurityContext> keySourceI = new ImmutableJWKSet<SecurityContext>(jwkSetI);

			JWSAlgorithm expectedJWSAlgI = JWSAlgorithm.RS512;
			JWSKeySelector<SecurityContext> keySelectorI = new JWSVerificationKeySelector<SecurityContext>(
					expectedJWSAlgI, keySourceI);

			jwtProcessorI.setJWSKeySelector(keySelectorI);

			SecurityContext ctx = null;
			JWTClaimsSet claimsSetI = jwtProcessorI.process(signedJWT, ctx);
			
			log.debug("accessToken: {}", signedJWT.serialize());
			log.debug("accessToken Header: {}", signedJWT.getHeader());
			log.debug("accessToken Payload: {}", claimsSetI.toJSONObject());
			
			//
			
			log.debug("Validate dbAPI");
			
			String accessTokenR = "eyJraWQiOiJyc2ExIiwiYWxnIjoiUlM1MTIifQ.eyJhdWQiOiIzYTZiN2JiNS0yZDliLTRiNTYtYWM0NS1mYWU3NGMxMTQxMzEiLCJpc3MiOiJodHRwczpcL1wvc2ltdWxhdG9yLWFwaS5kYi5jb21cL2d3XC9vaWRjXC8iLCJleHAiOjE1MDEyNDYzNjAsImlhdCI6MTUwMTI0NTc2MCwianRpIjoiZGUxNWQxMjItNzczYi00ZDlhLWJmMWMtNjBlNTU0ZGJkNjZlIn0.MZbCKFgWSTjFHehcArKG7Emjpj5oAr0YxbtbPUWPDmRAnzphsjgFtXAaV_HYv7nU4SUg_bKM4zkdi3ojkvDDhq2b2y-rbaHFCK65rM0LurZ_Fjco7JAdODAKJVxYwO9j9d1xPZPjMJDaTixrg2uHkX_7Y7zkEPcqf6VG3vQalj4ra6gjg0DUfYD7f4ayJltJyaUhmyH8W7k8g-LhyqxFfE0RkpBB-0x_mP5RbmPNGfsY6ZSYhkBqPGw45x4Kf_S_9cowelbndpP7KuEjPejsyf5YK28pIt0axxXBGwIY1-Y1WNf8pjL2QraBMbotrdmFXgsTkGs7AYKlRLflgt_6Iw";
			
			ConfigurableJWTProcessor<SecurityContext> jwtProcessorR = new DefaultJWTProcessor<SecurityContext>();
			JWKSource<SecurityContext> keySourceR = new RemoteJWKSet<SecurityContext>(new URL("https://simulator-api.db.com/gw/oidc/jwk"));
		
			JWSAlgorithm expectedJWSAlgR = JWSAlgorithm.RS512;
			JWSKeySelector<SecurityContext> keySelectorR = new JWSVerificationKeySelector<SecurityContext>(expectedJWSAlgR, keySourceR);
			
			jwtProcessorR.setJWSKeySelector(keySelectorR);
		
			ctx = null;
			JWTClaimsSet claimsSetR = jwtProcessorR.process(accessTokenR, ctx);			

			log.debug("accessToken: {}", accessTokenR);
			log.debug("accessToken Header: {}", SignedJWT.parse(accessTokenR).getHeader());
			log.debug("accessToken Payload: {}", claimsSetR.toJSONObject());

		} catch (Exception e) {
			System.err.println("Caught Exception: " + e.getMessage());
		}

	}

}