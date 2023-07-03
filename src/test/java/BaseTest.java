import helpers.WebDriverHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebElement;

import java.sql.DriverManager;

import static helpers.WebDriverHelper.findByCss;
import static helpers.WebDriverHelper.findById;
import static pages.CookiesPopupPage.ACCEPT_ALL_BTN_CSS;
import static pages.CookiesPopupPage.COOKIES_POPUP_ID;

public class BaseTest {
    public static final Logger LOG = LogManager.getLogger(DriverManager.class);

    private static final String EXECUTION_BROWSER = "Chrome";

    @BeforeAll
    public static void setUp() {
        LOG.trace("Setting up Selenium for work.");
        WebDriverHelper.setDriver(EXECUTION_BROWSER);
        WebDriverHelper.setWait();
        closeCookiesPopup();
    }

    @AfterAll
    public static void tearDown() {
        WebDriverHelper.quitDriver();
    }

    private static void closeCookiesPopup() {
        // Locate the cookies pop-up element
        WebElement cookiesPopup = findById(COOKIES_POPUP_ID);

        // Check if the cookies pop-up is displayed
        if (cookiesPopup.isDisplayed()) {
            LOG.trace("Popup was located. Accepting it.");
            findByCss(ACCEPT_ALL_BTN_CSS)
                    .click();
        }
    }

}
