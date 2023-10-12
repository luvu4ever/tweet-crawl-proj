package sampleapp;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import java.util.concurrent.TimeUnit;


public class selenium {
    public static void main(String[] args) {
        String query = "#nft";

        System.setProperty("webdriver.chrome.driver","D:\\Downloads in D\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        driver.get("https://twitter.com/login");
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//input[@name='text']")).sendKeys("usetocrawl1@gmail.com");
        driver.findElement(By.xpath("//span[contains(text(),'Next')]")).click();
        driver.findElement(By.xpath("//input[@name='text']")).sendKeys("crawl_nigh12359");
        driver.findElement(By.xpath("//span[contains(text(),'Next')]")).click();
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys("onlyforcrawl1");
        driver.findElement(By.xpath("//span[contains(text(),'Log in')]")).click();

        driver.findElement(By.xpath("//input[@aria-label=\"Search query\"]")).sendKeys(query);
        driver.findElement(By.xpath("//input[@aria-label=\"Search query\"]")).sendKeys(Keys.ENTER);




    }
}
