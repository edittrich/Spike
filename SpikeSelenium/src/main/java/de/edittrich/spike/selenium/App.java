package de.edittrich.spike.selenium;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		String exePath = "C:\\Apps\\ChromeDriver\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", exePath);
		WebDriver driver = new ChromeDriver();

		try {
			driver.navigate().to(new URL("https://www.google.com"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		WebDriverWait wait = new WebDriverWait(driver, 5000);
		wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.tagName("input"), 0));

		WebElement input = driver.findElement(By.cssSelector("#lst-ib"));

		input.sendKeys("Spark Java file upload example - First Few Lines");
		input.sendKeys(Keys.ENTER);

		driver.close();
	}
}