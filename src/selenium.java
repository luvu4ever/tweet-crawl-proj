import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class selenium {
    private static final int DAY_GAP = 3;
    private static final int MIN_FAVES = 5;
    private static final int MIN_RETWEET = 0;
    private static final int MIN_REPLY = 0;
    private static final String TWEET_XPATH = "//article[@data-testid='tweet']";
    private static final String USER_TAG_XPATH = ".//div[@data-testid='User-Name']/div";
    private static final String RETWEET_XPATH = ".//div[@data-testid='retweet']";
    private static final String LIKE_XPATH = ".//div[@data-testid='like']";
    private static final String TIME_XPATH = ".//time";
    private static final String SCROLL_SCRIPT = "window.scrollTo(0, document.body.scrollHeight);";
    private static final int SCROLL_DELAY_MS = 3000;
    private static final int MAX_SCROLL_ATTEMPTS = 3;
    private static final int MAX_TWEETS = 1000;

    private static Double lastPosition = -1.0;

    public static List<String> UserTags = new ArrayList<>();
    public static List<String> TimeStamps = new ArrayList<>();
    public static List<String> Tweets = new ArrayList<>();
    public static List<Integer> reTweets = new ArrayList<>();
    public static List<Integer> Likes = new ArrayList<>();

    public static String pUsername = "crawl_nigh12359";
    public static String pEmail = "usetocrawl1@gmail.com";
    public static String pPassword = "onlyforcrawl1";

    public static void main(String[] args) {
        //read keyword frome file
        String keyword = "boredapeyc";
        String query = queryMaker(keyword, "2021-08-01", 2, 0, 0);

        System.setProperty("webdriver.chrome.driver", "src/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");

        WebDriver driver = new ChromeDriver(options);

        driver.get("https://twitter.com/login");
        driver.manage().window().maximize();
//        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        threadSleep(1000);
        login(driver, pUsername, pPassword);
        threadSleep(1000);
        search(driver, query);
//        //select profile
//        driver.findElement(By.xpath("//span[contains(text(),'People')]")).click();
//        WebElement profile = driver.findElement(By.xpath("//*[@id='react-root']/div/div/div[2]/main/div/div/div/div/div/div[3]/section/div/div/div[1]/div/div/div/div/div[2]/div/div[1]/div/div[1]/a/div/div[1]/span/span"));
//        profile.click();
        threadSleep(1000);
        driver.findElement(By.xpath("//span[contains(text(),'Latest')]")).click();

        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        Object lastPositionObject = jsExecutor.executeScript("return window.pageYOffset;");
        lastPosition = Double.parseDouble(lastPositionObject.toString());
        System.out.println(lastPosition);
//        Double lastPosition = (Double) jsExecutor.executeScript("return window.pageYOffset;");
        try {
//            while (lastPosition != -1L) {
            while (!scrollAble(jsExecutor).equals(-1.0)) {
                List<WebElement> articles = driver.findElements(By.xpath(TWEET_XPATH));
                for (WebElement article : articles) {
//                    if (isAd(article, keyword))
//                        continue;
//                    get data from tweet article
                    getTweetData(article);
                }
                System.out.println(lastPosition);
                if (UserTags.size() > MAX_TWEETS)
                    break;
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        printtoCSV(keyword);
//        System.out.println(UserTags.size() + " " + TimeStamps.size() + " " + Tweets.size() + " " + Replys.size() + " " + reTweets.size() + " " + Likes.size());
//        for(String userTag : UserTags){
//            System.out.println(userTag);
//        }

    }

    private static void threadSleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void login(WebDriver driver, String username, String password) {
        WebElement emailField = driver.findElement(By.xpath("//input[@name='text']"));
        emailField.sendKeys(username);
        driver.findElement(By.xpath("//span[contains(text(),'Next')]")).click();

//        WebElement usernameField = driver.findElement(By.xpath("//input[@name='text']"));
//        usernameField.sendKeys(username);
//        driver.findElement(By.xpath("//span[contains(text(),'Next')]")).click();

        WebElement passwordField = driver.findElement(By.xpath("//input[@name='password']"));
        passwordField.sendKeys(password);
        driver.findElement(By.xpath("//span[contains(text(),'Log in')]")).click();
    }

    private static void search(WebDriver driver, String keyword) {
        WebElement searchBox = driver.findElement(By.xpath("//input[@aria-label=\"Search query\"]"));
        searchBox.sendKeys(keyword);
        searchBox.sendKeys(Keys.ENTER);
    }

    private static boolean isAd(WebElement article, String keyword) {
        //tweet text have no keyword -> ad
        //tweet replies have no keyword -> ad
        //whole article contains href -> not ad

        WebElement tweet = article.findElement(By.xpath(".//div[@data-testid='tweetText']"));
        String text = tweet.getText();
        if (text.toLowerCase().contains(keyword.toLowerCase()))
            return false;
        return true;
    }

    private static String queryMaker(String keyword, String since, int min_faves, int min_retweets, int min_replies) {
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
        String query = keyword + " min_faves:" + min_faves + " min_retweets:" + min_retweets + " min_replies:" + min_replies + " -filter:replies";
        if (since != null)
            query += " since:" + since;
        if (until != null)
            query += " until:" + until;
        return query;
    }

    public static void printtoCSV(String keyword) {
        File csvOutputFile = new File(keyword + ".csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            for (int i = 0; i < UserTags.size(); i++) {
                pw.printf("%s,%s,%s,%s,%s\n", UserTags.get(i), TimeStamps.get(i), Tweets.get(i), reTweets.get(i), Likes.get(i));
            }
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < UserTags.size(); i++) {
//            System.out.println("%s,%s,%s,%s,%s,%s\n", UserTags.get(i), TimeStamps.get(i), Tweets.get(i), Replys.get(i), reTweets.get(i), Likes.get(i));
            System.out.println(UserTags.get(i) + "," + TimeStamps.get(i) + "," + Tweets.get(i) + "," + reTweets.get(i) + "," + Likes.get(i));
        }
//        System.out.println(UserTags.size() + " " + TimeStamps.size() + " " + Tweets.size() + " " + Replys.size() + " " + reTweets.size() + " " + Likes.size());

    }

    private static void getTweetData(WebElement article) {
//        get tweet
        //remove /n from tweet
        WebElement tweet = article.findElement(By.xpath(".//div[@data-testid='tweetText']"));
        Tweets.add(tweet.getText().replaceAll("\n", " "));
//                System.out.println(tweet.getText());
//        get user tag
        WebElement userTag = article.findElement(By.xpath(USER_TAG_XPATH));
        UserTags.add(userTag.getText().replaceAll("\n", " "));

//        get time
        WebElement time = article.findElement(By.xpath(TIME_XPATH));
        TimeStamps.add(time.getAttribute("datetime").replaceAll("\n", " "));
//        System.out.println(userTag.getText());
//        get retweet
        WebElement reTweet = article.findElement(By.xpath(RETWEET_XPATH));
        //get int out of text
//        System.out.println(reTweet.getText().replaceAll("\n", ""));
        if (reTweet.getText().replaceAll("\n", "").equals("")) {
            reTweets.add(0);
        } else {
            reTweets.add(Integer.parseInt(reTweet.getText().replaceAll("\n", "")));
        }
//        get like
        WebElement like = article.findElement(By.xpath(LIKE_XPATH));
        if (like.getText().replaceAll("\n", "").equals("")) {
            Likes.add(0);
        } else {
            Likes.add(Integer.parseInt(like.getText().replaceAll("\n", "")));
        }
    }

    public static Double scrollAble(JavascriptExecutor jsExecutor) {
//        jsExecutor.executeScript(SCROLL_SCRIPT);
//        try {
//            Thread.sleep(SCROLL_DELAY_MS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        return 0L;
        int scrollAttempt = 0;
        while (true) {
            jsExecutor.executeScript(SCROLL_SCRIPT);
            try {
                Thread.sleep(SCROLL_DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            Double currPosition = (Double) jsExecutor.executeScript("return window.pageYOffset;");
            Object currPositionObject = jsExecutor.executeScript("return window.pageYOffset;");
            if (currPositionObject == null)
                return -1.0;
            Double currPosition = Double.parseDouble(currPositionObject.toString());

            if (!lastPosition.equals(currPosition)) {
                lastPosition = currPosition;
                return currPosition;
            }
            scrollAttempt++;
            if (scrollAttempt > MAX_SCROLL_ATTEMPTS) {
                return -1.0;
            }
        }
    }

    public static String addDayToString(String startDay, int dayGap){
        String[] sinceSplit = startDay.split("-");
        int year = Integer.parseInt(sinceSplit[0]);
        int month = Integer.parseInt(sinceSplit[1]);
        int day = Integer.parseInt(sinceSplit[2]);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        c.add(Calendar.DATE, dayGap);
        return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE);
    }

    public static void run(String keyword, String startDay, String endDay) {
        String tmp = startDay;
        while (true) {
            //read keyword frome file
            String query = queryMaker(keyword, startDay, MIN_FAVES, MIN_RETWEET, MIN_REPLY);
            System.setProperty("webdriver.chrome.driver", "src/chromedriver-win64/chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless");

            WebDriver driver = new ChromeDriver(options);

            driver.get("https://twitter.com/login");
            driver.manage().window().maximize();
//        driver.manage().deleteAllCookies();
            driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

            threadSleep(1000);
            login(driver, pUsername, pPassword);
            threadSleep(1000);
            search(driver, query);
//        //select profile
//        driver.findElement(By.xpath("//span[contains(text(),'People')]")).click();
//        WebElement profile = driver.findElement(By.xpath("//*[@id='react-root']/div/div/div[2]/main/div/div/div/div/div/div[3]/section/div/div/div[1]/div/div/div/div/div[2]/div/div[1]/div/div[1]/a/div/div[1]/span/span"));
//        profile.click();
            threadSleep(1000);
            driver.findElement(By.xpath("//span[contains(text(),'Latest')]")).click();

            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            Object lastPositionObject = jsExecutor.executeScript("return window.pageYOffset;");
            lastPosition = Double.parseDouble(lastPositionObject.toString());
            System.out.println(lastPosition);
//        Double lastPosition = (Double) jsExecutor.executeScript("return window.pageYOffset;");
            try {
//            while (lastPosition != -1L) {
                while (!scrollAble(jsExecutor).equals(-1.0)) {
                    List<WebElement> articles = driver.findElements(By.xpath(TWEET_XPATH));
                    for (WebElement article : articles) {
//                    if (isAd(article, keyword))
//                        continue;
//                    get data from tweet article
                        getTweetData(article);
                    }
                    System.out.println(lastPosition);
                    if (UserTags.size() > MAX_TWEETS)
                        break;
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } finally {
                driver.quit();
            }
            printtoCSV(keyword);
            startDay = addDayToString(startDay, DAY_GAP + 1);
            if(startDay.compareTo(endDay) >= 0){
                break;
            }
        }
    }
}






