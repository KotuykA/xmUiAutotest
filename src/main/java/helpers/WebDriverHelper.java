package helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.opera.OperaDriver;
import io.github.bonigarcia.wdm.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.DriverManager;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class WebDriverHelper {
    private static final Logger LOG = LogManager.getLogger(DriverManager.class);

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static JavascriptExecutor executor;
    protected static final Duration DEFAULT_WAIT_SECONDS = Duration.ofSeconds(4);
    protected static final Duration DEFAULT_POLLING_MILLIS = Duration.ofMillis(200);
    public static final String BASE_URL = "https://www.xm.com";

    public static void setDriver(String browser) {
        LOG.warn("Initialization WebDriver started.");
        if (driver == null) {
            // Didn't get from the start that the task was about three different resolutions, not BROWSERS.
            // Decided to leave it like this. Just to show how I would probably handle it for this task. At this stage.
            switch (browser.toLowerCase()) {
                case "chrome" -> {
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver();
                }
                case "firefox" -> {
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                }
                case "opera" -> {
                    WebDriverManager.operadriver().setup();
                    driver = new OperaDriver();
                }
                default -> throw new IllegalArgumentException(
                        "Unexpected browser was specified for WebDriver initialization: " + browser);
            }
        }
        driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);
        driver.manage().window().maximize();
        driver.get(BASE_URL);
        executor = (JavascriptExecutor) driver;
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static void quitDriver() {
        LOG.trace("Closing webdriver instance.");
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public static void setWait() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(2).getSeconds());
    }

    public static FluentWait<WebDriver> getWait() {
        return getWait(DEFAULT_WAIT_SECONDS, DEFAULT_POLLING_MILLIS);
    }

    public static FluentWait<WebDriver> getWait(int secondsToWait) {
        return getWait(Duration.ofSeconds(secondsToWait), DEFAULT_POLLING_MILLIS);
    }

    public static FluentWait<WebDriver> getWait(Duration timeout, Duration polling) {
        return wait.withTimeout(timeout)
                .pollingEvery(polling)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class);
    }

    public static WebElement waitElementVisibility(String elementXpath) {
        LOG.debug("Will waite for an element visibility: {}", elementXpath);
        return getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXpath)));
    }

    public static Boolean waitElementHasText(String elementXpath, String expectedText) {
        LOG.debug("Will waite for the element '{}' has text: {}", elementXpath, expectedText);
        return getWait().until(ExpectedConditions.textToBePresentInElement(findByXpath(elementXpath), expectedText));

    }

    public static WebElement findById(String elementId) {
        LOG.debug("Looking for an element with ID: {}", elementId);
        return driver.findElement(By.id(elementId));
    }

    public static WebElement findByCss(String elementCss) {
        LOG.debug("Looking for an element with cssSelector: {}", elementCss);
        return driver.findElement(By.cssSelector(elementCss));
    }

    public static WebElement findByXpath(String elementXpath) {
        LOG.debug("Looking for an element with xpath: {}", elementXpath);
        return driver.findElement(By.xpath(elementXpath));
    }

    public static void scrollToElement(WebElement elementToScroll) {
        LOG.trace("Scrolling to the element {}", elementToScroll);
        executor.executeScript("arguments[0].scrollIntoView(true);", elementToScroll);
    }

    public static void jsClick(WebElement elementToClick) {
        // I am not fan of this approach. But I couldn't get why it was tricky to get this links to click at.
        LOG.debug("Clicking with JS at the element: {}", elementToClick.getLocation());
        executor.executeScript("arguments[0].click();", elementToClick);
    }

}
