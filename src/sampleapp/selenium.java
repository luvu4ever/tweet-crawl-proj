package sampleapp;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;


public class selenium {
    private static final int DAY_GAP = 3;
    private static final int MIN_FAVES = 5;
    private static final int MIN_RETWEET = 0;
    private static final int MIN_REPLY = 0;
    private static final int FILTER_REPLIES = 0;
    private static final Double MEET_RELOAD_CONDITION = 100000.0;
    private static final Double FIRST_RELOAD_CONDITION = 1000.0;
    private static final String TWEET_XPATH = "//article[@data-testid='tweet']";
    private static final String USER_NAME_XPATH = ".//div[@data-testid='User-Name']/div";
    private static final String RETWEET_XPATH = ".//div[@data-testid='retweet']";
    private static final String LIKE_XPATH = ".//div[@data-testid='like']";
    private static final String REPLY_XPATH = ".//div[@data-testid='reply']";
    private static final String TIME_XPATH = ".//time";
    private static final String ACCOUNT_TAG = ".//span[contains(text(),'@')]";
    private static final String LINK_XPATH = "./div/div/div[2]/div[2]/div[1]/div/div[1]/div/div/div[2]/div/div[3]/a";

    private static final String RELOAD_XPATH = "//div[@data-testid='primaryColumn']//span[contains(text(),'Retry')]";

    private static final String GROUP_XPATH = ".//div[@role='group']";

//    private static final String SCROLL_SCRIPT = "window.scrollTo(0, document.body.scrollHeight);";
    private static final double AMOUNT_PER_SCROLL = 5000.0;
    private static final String SCROLL_SCRIPT = "window.scrollBy(0, " + (int) AMOUNT_PER_SCROLL + ");";
    private static final int LONG_DELAY_MS = 5000;
    private static final int SHORT_DELAY_MS = 1000;

    private static final int MAX_SCROLL_ATTEMPTS = 3;
    private static final int MAX_TWEETS = 1000;
    private static Double lastPosition = -1.0;
    public static List<Tweet> Tweets = new ArrayList<>();
    public static Map<String, Integer> TweetIdMap = new HashMap<>();

    public static String pUsername;
    //    public static String pEmail = "usetocrawl1@gmail.com";
    public static String pPassword;

    public static void main(String[] args) {
        //read keyword from file
        String keyword = "boredapeyc";
        run(keyword, "2023-12-15", "2023-12-18");
    }

    private static void threadSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void login(WebDriver driver, String username, String password) {
        driver.get("https://twitter.com/login");
        WebElement emailField = driver.findElement(By.xpath("//input[@name='text']"));
        emailField.sendKeys(username);
        driver.findElement(By.xpath("//span[contains(text(),'Next')]")).click();

        WebElement passwordField = driver.findElement(By.xpath("//input[@name='password']"));
        passwordField.sendKeys(password);
        driver.findElement(By.xpath("//span[contains(text(),'Log in')]")).click();
        threadSleep(LONG_DELAY_MS);
    }

    private static void logout(WebDriver driver) {
        driver.get("https://twitter.com/logout");
        threadSleep(LONG_DELAY_MS);
        driver.findElement(By.xpath("//span[text()='Log out']")).click();
        threadSleep(LONG_DELAY_MS);
    }

    private static void search(WebDriver driver, String keyword, String since, int min_faves, int min_retweets, int min_replies, int filter_replies) {
        String search_url = "https://twitter.com/search?q=" + queryMaker(keyword, since, min_faves, min_retweets, min_replies, filter_replies) + "&src=typed_query&f=live";
        driver.get(search_url);
        threadSleep(LONG_DELAY_MS);
    }

    private static boolean isAd(WebElement article) {
        if (article.isDisplayed())
            return false;
        return true;
    }

    private static String queryMaker(String keyword, String since, int min_faves, int min_retweets, int min_replies, int filter_replies) {
        //until = since + 3 days
        String until = addDayToString(since, DAY_GAP);
        String query = keyword + "%20";
        if (min_faves > 0)
            query += "min_faves%3A" + min_faves + "%20";
        if (min_retweets > 0)
            query += "min_retweets%3A" + min_retweets + "%20";
        if (min_replies > 0)
            query += "min_replies%3A" + min_replies + "%20";
        if (filter_replies == 1)
            query += "-filter%3Areplies%20";
        if (since != null)
            query += "since%3A" + since + "%20";
        if (until != null)
            query += " until%3A" + until + "%20";
        return query;
    }

//    //print data to a json file

    //print to screen
    public static void printToScreen() {
        for (int i = 0; i < Tweets.size(); i++) {
            System.out.println(Tweets.get(i).getAccount() + " " + Tweets.get(i).getPost_time() + " " + Tweets.get(i).getReply() + " " + Tweets.get(i).getRetweet() + " " + Tweets.get(i).getLike());
        }
    }

    private static void getTweetData(WebElement article) {
        String post_link, post_id;
        try {
            WebElement linkWebElement = article.findElement(By.xpath(LINK_XPATH));
            post_link = linkWebElement.getAttribute("href");
            String linkParts[] = post_link.split("/");
            post_id = linkParts[linkParts.length - 1];
            if (TweetIdMap.get(post_id) != null && TweetIdMap.get(post_id).equals(1))
                return;
            TweetIdMap.put(post_id, 1);
        } catch (Exception e){
            return;
        }
//        get account href
        WebElement accountWebElement = article.findElement(By.xpath(ACCOUNT_TAG));
        String account = (accountWebElement.getText().replaceAll("\n", " "));
//        get tweet
        String tweetText;
        try {
            WebElement tweetTextWeb = article.findElement(By.xpath(".//div[@data-testid='tweetText']"));
            tweetText = (tweetTextWeb.getText().replaceAll("\n", " "));
        } catch (Exception e){
            tweetText = "";
        }
//        get user tag
//        WebElement userNameWebElement = article.findElement(By.xpath(USER_NAME_XPATH));
//        String userName = (userNameWebElement.getText().replaceAll("\n", " "));
//        get time
        String time;
        try {
            WebElement timeWebElement = article.findElement(By.xpath(TIME_XPATH));
            time = (timeWebElement.getAttribute("datetime").replaceAll("\n", " "));
        } catch (Exception e){
            return;
        }
//        System.out.println(account + " " + time + " " + reply + " " + retweet + " " + like);
//        WebElement linkWebElement = article.findElement(By.xpath(".//a[@role='link']"));

        String group;
        try {
            WebElement groupWebElement = article.findElement(By.xpath(GROUP_XPATH));
            group = groupWebElement.getAttribute("aria-label");
        } catch (Exception e){
            return;
        }
        String[] Gparts = group.split(",");
        int reply = 0, retweet = 0, like = 0, bookmark = 0;
        for (String part : Gparts) {
            if(part.contains("repl")){
                reply = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                continue;
            }
            if(part.contains("repost")){
                retweet = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                continue;
            }
            if(part.contains("like")){
                like = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                continue;
            }
            bookmark = Integer.parseInt(part.replaceAll("[^0-9]", ""));
        }
        System.out.println(account + " " + time + " " + reply + " " + retweet + " " + like + " " + bookmark);
//        System.out.println(group);
//        String tweetText = "";
//        int like = 0;
        Tweets.add(new Tweet(account, time, tweetText, reply, retweet, like));
    }



    public static Double scrollDown(WebDriver driver) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        int scrollAttempt = 0;
        while (true) {
            jsExecutor.executeScript(SCROLL_SCRIPT);
            threadSleep(LONG_DELAY_MS);
            Object currPositionObject = jsExecutor.executeScript("return window.pageYOffset;");
            if (currPositionObject == null)
                return -1.0;
            Double currPosition = Double.parseDouble(currPositionObject.toString());
            if(currPosition.compareTo(FIRST_RELOAD_CONDITION * 1.0) < 0){
                return currPosition;
            }
            if (!lastPosition.equals(currPosition)) {
                lastPosition = currPosition;
                return currPosition;
            }
            scrollAttempt++;
            System.out.println(scrollAttempt + " " + currPosition);
            if (scrollAttempt >= MAX_SCROLL_ATTEMPTS) {
                return -1.0;
            }
        }
    }
    public static String addDayToString(String startDay, int dayGap) {
        String[] sinceSplit = startDay.split("-");
        int year = Integer.parseInt(sinceSplit[0]);
        int month = Integer.parseInt(sinceSplit[1]);
        int day = Integer.parseInt(sinceSplit[2]);
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day); // month is 0-based
        c.add(Calendar.DATE, dayGap);
        return String.format("%d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE));
    }

    public static WebDriver initBrowser() {
        System.setProperty("webdriver.chrome.driver", "src/chromedriver-win64/chromedriver.exe");
        String extensionPath = "E:\\Work\\Project\\Crawl_Tweet\\src\\Adguard.crx";
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(new File(extensionPath));
        options.addArguments("--disable-notifications");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        threadSleep(LONG_DELAY_MS);
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        driver.close();
        driver.switchTo().window(tabs.get(0));

        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        threadSleep(LONG_DELAY_MS);
        return driver;
    }

    private static boolean reloadButtonDetected(WebDriver driver){
        try{
            WebElement reloadButton = driver.findElement(By.xpath(RELOAD_XPATH));
//            reloadButton.click();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public static void run(String keyword, String startDay, String endDay) {
        String tmp = startDay;
        int finished = 0;
        AccountManager accountManager = new AccountManager("src/sampleapp/account.txt");
        String accountDetails[] = accountManager.changeAccount();
        pUsername = accountDetails[0];
        pPassword = accountDetails[1];
        WebDriver driver = initBrowser();
        login(driver, pUsername, pPassword);
        while (startDay.compareTo(endDay) <= 0) {
//            search(driver, query);
            search(driver, keyword, startDay, MIN_FAVES, MIN_RETWEET, MIN_REPLY, FILTER_REPLIES);

            Double lastPosition = -2.0;
            Double currPosition = -2.0;
            try {
                while (true) {
                    lastPosition = currPosition;
                    currPosition = scrollDown(driver);
                    if(currPosition.compareTo(FIRST_RELOAD_CONDITION * 1.0) < 0){
                        break;
                    }
                    List<WebElement> articles = driver.findElements(By.xpath(TWEET_XPATH));
                    for (WebElement article : articles) {
                        if (isAd(article))
                            continue;
                        getTweetData(article);
                    }
//                    System.out.println(lastPosition);
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } finally {
//                driver.quit();
            }
//            printToScreen();
            System.out.println("last position: " + lastPosition + " " + currPosition);
            if(reloadButtonDetected(driver)){
                logout(driver);
                accountDetails = accountManager.changeAccount();
                pUsername = accountDetails[0];
                pPassword = accountDetails[1];
                System.out.println("change account to " + pUsername + " " + pPassword);
                driver.manage().deleteAllCookies();
                login(driver, pUsername, pPassword);
                continue;
            }
//            if (lastPosition.compareTo(FIRST_RELOAD_CONDITION * 1.0) < 0) {
//                logout(driver);
//                changeAccount();
//                login(driver, pUsername, pPassword);
//                continue;
//            }
//            try{
//                WebElement reloadButton = driver.findElement(By.xpath("//div[@data-testid='primaryColumn']//div[@role='button']"));
//                finished = 0;
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//            if(finished == 0){
//                logout(driver);
//                changeAccount();
//                login(driver, pUsername, pPassword);
//                continue;
//            }
            startDay = addDayToString(startDay, DAY_GAP + 1);
            System.out.println(startDay + " " + endDay);
//            printToCSV(keyword);
        }
        driver.quit();
    }
}






