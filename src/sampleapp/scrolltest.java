import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class scrolltest{
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "E:\\Work\\Project\\Crawl_Tweet\\src\\chromedriver-win64\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.get("https://en.wikipedia.org/wiki/Main_Page");

        //scroll to footer
        WebElement footer = driver.findElement(By.id("footer"));
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", footer);

        //get scroll position
        JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
        Double scrollPosition = (Double) jsExecutor.executeScript("return window.pageYOffset;");
        System.out.println("Scroll Position : " + scrollPosition);

        driver.quit();
    }
}