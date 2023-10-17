package sampleapp;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;


public class selenium {

    public static long scrollDownPage(WebDriver driver, long lastPosition, int scrollAttempt, int maxAttempts, int numSecondsToLoad) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean endOfScrollRegion = false;

        while (!endOfScrollRegion) {
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try {
                Thread.sleep(numSecondsToLoad * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long currPosition = (long) js.executeScript("return window.pageYOffset;");
            if (currPosition == lastPosition) {
                if (scrollAttempt < maxAttempts) {
                    endOfScrollRegion = true;
                } else {
                    scrollDownPage(driver, lastPosition, scrollAttempt + 1, maxAttempts, numSecondsToLoad);
                }
            }
            lastPosition = currPosition;

        }
        return lastPosition;
    }
    public static void main(String[] args) {
        String query = "Bored Ape Yacht Club";
        String pUsername = "crawl_nigh12359";
        String pEmail = "usetocrawl1@gmail.com";
        String pPassword = "onlyforcrawl1";

        System.setProperty("webdriver.chrome.driver","D:\\Downloads in D\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        driver.get("https://twitter.com/login");
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        WebElement  emailField = driver.findElement(By.xpath("//input[@name='text']"));
        emailField.sendKeys(pEmail);
        driver.findElement(By.xpath("//span[contains(text(),'Next')]")).click();

        WebElement usernameField = driver.findElement(By.xpath("//input[@name='text']"));
        usernameField.sendKeys(pUsername);
        driver.findElement(By.xpath("//span[contains(text(),'Next')]")).click();

        WebElement passwordField = driver.findElement(By.xpath("//input[@name='password']"));
        passwordField.sendKeys(pPassword);
        driver.findElement(By.xpath("//span[contains(text(),'Log in')]")).click();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement searchBox = driver.findElement(By.xpath("//input[@aria-label=\"Search query\"]"));
        searchBox.sendKeys(query);
        searchBox.sendKeys(Keys.ENTER);

        driver.findElement(By.xpath("//span[contains(text(),'People')]")).click();
        WebElement profile = driver.findElement(By.xpath("//*[@id='react-root']/div/div/div[2]/main/div/div/div/div/div/div[3]/section/div/div/div[1]/div/div/div/div/div[2]/div/div[1]/div/div[1]/a/div/div[1]/span/span"));
        profile.click();

        List<String> UserTags = new ArrayList<>();
        List<String> TimeStamps = new ArrayList<>();
        List<String> Tweets = new ArrayList<>();
        List<String> Replys = new ArrayList<>();
        List<String> reTweets = new ArrayList<>();
        List<String> Likes = new ArrayList<>();

        List<WebElement> articles;

        while(true){
            articles = driver.findElements(By.xpath("//article[@data-testid='tweet']"));
            for(WebElement article : articles){
                WebElement userTag = article.findElement(By.xpath(".//div[@data-testid='User-Name']/div"));
                UserTags.add(userTag.getText());

                WebElement timeStamp = article.findElement(By.xpath(".//time"));
                TimeStamps.add(timeStamp.getAttribute("datetime"));

                WebElement tweet = article.findElement(By.xpath(".//div[@data-testid='tweetText']"));
                Tweets.add(tweet.getText());

                WebElement reply = article.findElement(By.xpath(".//div[@data-testid='reply']"));
                Replys.add(reply.getText());

                WebElement reTweet = article.findElement(By.xpath(".//div[@data-testid='retweet']"));
                reTweets.add(reTweet.getText());

                WebElement like = article.findElement(By.xpath(".//div[@data-testid='like']"));
                Likes.add(like.getText());
            }
//            lastPosition = scrollDownPage(driver, lastPosition, numSecondsToLoad, scrollAttempt, maxAttempts);
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Set<String> uniqueTweets = new HashSet<>(Tweets);
            if (uniqueTweets.size() > 100) {
                break;
            }
            System.out.println(UserTags.size());
        }
//        System.out.println(UserTags.size() + " " + TimeStamps.size() + " " + Tweets.size() + " " + Replys.size() + " " + reTweets.size() + " " + Likes.size());
        for(String userTag : UserTags){
            System.out.println(userTag);
        }
        driver.quit();

    }
}







