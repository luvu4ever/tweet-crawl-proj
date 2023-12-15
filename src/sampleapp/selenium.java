package sampleapp;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class selenium {
    private static final int DAY_GAP = 3;
    private static final int MIN_FAVES = 5;
    private static final int MIN_RETWEET = 0;
    private static final int MIN_REPLY = 0;
    private static final int FILTER_REPLIES = 1;
    private static final double MEET_RELOAD_CONDITION = 1000;
    private static final String TWEET_XPATH = "//article[@data-testid='tweet']";
    private static final String USER_NAME_XPATH = ".//div[@data-testid='User-Name']/div";
    private static final String RETWEET_XPATH = ".//div[@data-testid='retweet']";
    private static final String LIKE_XPATH = ".//div[@data-testid='like']";
    private static final String REPLY_XPATH = ".//div[@data-testid='reply']";
    private static final String TIME_XPATH = ".//time";
    private static final String ACCOUNT_LINK = ".//span[contains(text(),'@')]";

    private static final String GROUP_XPATH = ".//div[@role='group']";

    private static final String SCROLL_SCRIPT = "window.scrollTo(0, document.body.scrollHeight);";
    private static final int SCROLL_DELAY_MS = 3000;
    private static final int MAX_SCROLL_ATTEMPTS = 3;
    private static final int MAX_TWEETS = 1000;
    private static Double lastPosition = -1.0;
    public static List<Tweet> Tweets = new ArrayList<>();

    public static String pUsername = "crawl_nigh12359";
    //    public static String pEmail = "usetocrawl1@gmail.com";
    public static String pPassword = "onlyforcrawl1";

    public static void main(String[] args) {
        //read keyword from file
        String keyword = "boredapeyc";
        run(keyword, "2021-08-04", "2021-08-07");
    }

    private static void threadSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void changeAccount() {
        //change account, read username and password in different line
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("src/sampleapp/account.txt"));
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                reader = new BufferedReader(new FileReader("src/sampleapp/account.txt"));
                line = reader.readLine();
                pUsername = line;
                line = reader.readLine();
                pPassword = line;
            } else {
                while (line != null) {
                    pUsername = line;
                    line = reader.readLine();
                    pPassword = line;
                    line = reader.readLine();
                }
            }
            reader.close();
        } catch (IOException e) {
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
    }

    private static void logout(WebDriver driver) {
        driver.get("https://twitter.com/logout");
        driver.findElement(By.xpath("//span[contains(text(),'Log out')]")).click();
    }

    private static void search(WebDriver driver, String keyword, String since, int min_faves, int min_retweets, int min_replies, int filter_replies) {
        String search_url = "https://twitter.com/search?q=" + queryMaker(keyword, since, min_faves, min_retweets, min_replies, filter_replies) + "&src=typed_query&f=live";
        driver.get(search_url);
    }

    private static boolean isAd(WebElement article) {
        if (article.isDisplayed())
            return false;
        return true;
    }


    private static String queryMaker(String keyword, String since, int min_faves, int min_retweets, int min_replies, int filter_replies) {
        //until = since + 3 days
        String until = null;
        if (since != null) {
            String[] sinceSplit = since.split("-");
            int year = Integer.parseInt(sinceSplit[0]);
            int month = Integer.parseInt(sinceSplit[1]);
            int day = Integer.parseInt(sinceSplit[2]);
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            c.add(Calendar.DATE, DAY_GAP);
            until = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE);
        }
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
//    public static void printToJSON(String keyword){
//        File jsonOutputFile = new File(keyword + ".json");
//        try{
//            PrintWriter pw = new PrintWriter(jsonOutputFile);
//            pw.println("{");
//            pw.println("\"data\": [");
//            for(int i = 0; i < UserTags.size(); i++){
//                pw.println("{");
//                pw.println("\"time\": \"" + TimeStamps.get(i) + "\",");
//                pw.println("\"account\": \"" + Accounts.get(i) + "\",");
//                pw.println("\"tweet\": \"" + Tweets.get(i) + "\",");
//                pw.println("\"reply\": \"" + Replys.get(i) + "\",");
//                pw.println("\"retweet\": \"" + reTweets.get(i) + "\",");
//                pw.println("\"like\": \"" + Likes.get(i) + "\"");
//                if(i == UserTags.size() - 1)
//                    pw.println("}");
//                else
//                    pw.println("},");
//            }
//            pw.println("]");
//            pw.println("}");
//            pw.close();
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }

    //print to screen
    public static void printToScreen() {
        for (int i = 0; i < Tweets.size(); i++) {
            System.out.println(Tweets.get(i).getAccount() + " " + Tweets.get(i).getTimeStamp() + " " + Tweets.get(i).getReply() + " " + Tweets.get(i).getRetweet() + " " + Tweets.get(i).getLike());
        }
    }

    private static void getTweetData(WebElement article) {
//        get account href
        WebElement accountWebElement = article.findElement(By.xpath(ACCOUNT_LINK));
        String account = (accountWebElement.getText().replaceAll("\n", " "));
//        get tweet
        WebElement tweetTextWeb = article.findElement(By.xpath(".//div[@data-testid='tweetText']"));
        String tweetText = (tweetTextWeb.getText().replaceAll("\n", " "));
//        get user tag
//        WebElement userNameWebElement = article.findElement(By.xpath(USER_NAME_XPATH));
//        String userName = (userNameWebElement.getText().replaceAll("\n", " "));
//        get time
        WebElement timeWebElement = article.findElement(By.xpath(TIME_XPATH));
        String time = (timeWebElement.getAttribute("datetime").replaceAll("\n", " "));
        //        get reply

        WebElement replyWebElement = article.findElement(By.xpath(REPLY_XPATH));
//        int reply;
//        if (replyWebElement.getText().replaceAll("\n", "").equals("")) {
//            reply = 0;
//        } else {
//            reply = (Integer.parseInt(replyWebElement.getText().replaceAll("\n", "")));
//        }
////        get retweet
//        WebElement retweetWebElement = article.findElement(By.xpath(RETWEET_XPATH));
//        int retweet;
//        if (retweetWebElement.getText().replaceAll("\n", "").equals("")) {
//            retweet = 0;
//        } else {
//            retweet = (Integer.parseInt(retweetWebElement.getText().replaceAll("\n", "")));
//        }
////        get like
//        int like;
//        WebElement likeWebElement = article.findElement(By.xpath(LIKE_XPATH));
//        if (likeWebElement.getText().replaceAll("\n", "").equals("")) {
//            like = 0;
//        } else {
//            like = (Integer.parseInt(likeWebElement.getText().replaceAll("\n", "")));
//        }
//        System.out.println(account + " " + time + " " + reply + " " + retweet + " " + like);
        WebElement groupWebElement = article.findElement(By.xpath(GROUP_XPATH));
        String group = groupWebElement.getAttribute("aria-label");
        String[] parts = group.split(",");
        int reply = 0, retweet = 0, like = 0, bookmark = 0;
        for (String part : parts) {
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

    public static Double scrollAble(WebDriver driver) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        int scrollAttempt = 0;
        while (true) {
            jsExecutor.executeScript(SCROLL_SCRIPT);
            try {
                Thread.sleep(SCROLL_DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Object currPositionObject = jsExecutor.executeScript("return window.pageYOffset;");
            if (currPositionObject == null)
                return -1.0;
            Double currPosition = Double.parseDouble(currPositionObject.toString());
//            System.out.println(currPosition);
            if (!lastPosition.equals(currPosition)) {
                lastPosition = currPosition;
                return currPosition;
            }
            scrollAttempt++;
            if (scrollAttempt >= MAX_SCROLL_ATTEMPTS) {
                return -1.0;
            }
        }
    }

    public static Double scrollDown(WebDriver driver) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        int scrollAttempt = 0;
        while (true) {
            jsExecutor.executeScript(SCROLL_SCRIPT);
            try {
                Thread.sleep(SCROLL_DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Object currPositionObject = jsExecutor.executeScript("return window.pageYOffset;");
            if (currPositionObject == null)
                return -1.0;
            Double currPosition = Double.parseDouble(currPositionObject.toString());
            System.out.println(currPosition);
            if (!lastPosition.equals(currPosition)) {
                if(currPosition == 59808.0)
                    System.out.println("error over here");
                lastPosition = currPosition;
                return currPosition;
            }
            System.out.println("error over here");
            scrollAttempt++;
            if (scrollAttempt > MAX_SCROLL_ATTEMPTS) {
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
        c.set(year, month, day);
        c.add(Calendar.DATE, dayGap);
        return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE);
    }

    public static WebDriver initBrowser() {
        System.setProperty("webdriver.chrome.driver", "src/chromedriver-win64/chromedriver.exe");
        String extensionPath = "E:\\Work\\Project\\Crawl_Tweet\\src\\Adguard.crx";
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(new File(extensionPath));
        options.addArguments("--disable-notifications");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(1));
        driver.close();
        driver.switchTo().window(tabs.get(0));

        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        threadSleep(1000);
        return driver;
    }

    public static void run(String keyword, String startDay, String endDay) {
        String tmp = startDay;

        WebDriver driver = initBrowser();
        while (true) {

            threadSleep(1000);
            login(driver, pUsername, pPassword);
            threadSleep(5000);
//            search(driver, query);
            search(driver, keyword, startDay, MIN_FAVES, MIN_RETWEET, MIN_REPLY, FILTER_REPLIES);
            threadSleep(1000);

//            driver.findElement(By.xpath("//span[contains(text(),'Latest')]")).click();

//            Object lastPositionObject = jsExecutor.executeScript("return window.pageYOffset;");
//            lastPosition = Double.parseDouble(lastPositionObject.toString());
//        Double lastPosition = (Double) jsExecutor.executeScript("return window.pageYOffset;");
            Double lastPosition = -2.0;
            try {
////            while (lastPosition != -1L) {
//                while (scrollAble(driver) != -1.0) {
//                    List<WebElement> articles = driver.findElements(By.xpath(TWEET_XPATH));
//                    for (WebElement article : articles) {
//                        if (isAd(article))
//                            continue;
////                    get data from tweet article
//                        getTweetData(article);
//                    }
//                }
                while (!lastPosition.equals(-1.0)) {
                    lastPosition = scrollAble(driver);
                    List<WebElement> articles = driver.findElements(By.xpath(TWEET_XPATH));
                    for (WebElement article : articles) {
                        if (isAd(article))
                            continue;
                        getTweetData(article);
                    }
                    System.out.println(lastPosition);
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } finally {
//                driver.quit();
            }
            printToScreen();
            if (lastPosition.compareTo(MEET_RELOAD_CONDITION * 1.0) < 0) {
                logout(driver);
                threadSleep(1000);
                changeAccount();
                threadSleep(1000);
                login(driver, pUsername, pPassword);
                continue;
            }
            startDay = addDayToString(startDay, DAY_GAP + 1);
//            printToCSV(keyword);
            if (startDay.compareTo(endDay) >= 0) {
//                printToCSV(keyword);
                break;
            }
        }
    }
}






