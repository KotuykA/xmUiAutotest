import static helpers.WebDriverHelper.BASE_URL;
import static helpers.WebDriverHelper.findById;
import static helpers.WebDriverHelper.jsClick;
import static helpers.WebDriverHelper.scrollToElement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static helpers.WebDriverHelper.findByXpath;
import static helpers.WebDriverHelper.getDriver;
import static helpers.WebDriverHelper.getWait;
import static helpers.WebDriverHelper.waitElementHasText;
import static helpers.WebDriverHelper.waitElementVisibility;
import static pages.EconomicCalendarPage.CALENDAR_IFRAME_CONTENT_ID;
import static pages.EconomicCalendarPage.LINK_DISCLAIMER_XPATH;
import static pages.EconomicCalendarPage.MOB_TIME_FILTER_BTN_XPATH;
import static pages.EconomicCalendarPage.TIMELINE_SLIDER_XPATH;
import static pages.EconomicCalendarPage.TIMELINE_TEXT_XPATH;
import static pages.NavigationBarPage.MOB_MENU_BTN_XPATH;
import static pages.NavigationBarPage.MOB_RESEARCH_BTN_XPATH;
import static pages.NavigationBarPage.RESEARCH_BTN_XPATH;
import static pages.ResearchAndEducationPage.MOB_CALENDAR_LINK_XPATH;
import static pages.ResearchAndEducationPage.clickCalendarBtn;
import static pages.ResearchAndEducationPage.switchToCalendarIframe;
import static pages.RiskNotificationPage.LINK_RISK_WARNING_XPATH;

public class XmUiTest extends BaseTest {

    private static Stream<Dimension> resolutionProvider() {
        // browser resolution should be maximum, by default
        Dimension originalResolution = getDriver().manage().window().getSize();
        List<Dimension> resolutions = Arrays.asList(
                new Dimension(800, 600),
                new Dimension(1024, 768),
                // this test should, at the same time, leave the browser resolution in the default state
                // (instead of additional post-condition implementation)
                originalResolution
        );
        return resolutions.stream();
    }

    @ParameterizedTest
    @MethodSource("resolutionProvider")
    public void riskDisclosureAppearanceWithBrowserResolutionTest(Dimension browserResolution) {
        LOG.info("Testing browser resolution: {}", browserResolution.toString());
        getDriver().get(BASE_URL);
        getDriver().manage().window().setSize(browserResolution);

        // block with logic that is differed for the small screen resolution
        if (browserResolution.getWidth() < 1024) {
            LOG.debug("Screen resolution is small flow.");
            findByXpath(MOB_MENU_BTN_XPATH).click();
            findByXpath(MOB_RESEARCH_BTN_XPATH).click();
            findByXpath(MOB_CALENDAR_LINK_XPATH).click();

            switchToCalendarIframe();
            findByXpath(MOB_TIME_FILTER_BTN_XPATH).click();
        } else {
            LOG.debug("Screen resolution is normal flow.");
            findByXpath(RESEARCH_BTN_XPATH).click();
            clickCalendarBtn();
            switchToCalendarIframe();
        }

        LOG.info("Verifying timeline slider work.");
        scrollToElement(findById(CALENDAR_IFRAME_CONTENT_ID));
        int today = -2;
        moveDateRunner(today);
        waitElementHasText(TIMELINE_TEXT_XPATH, "Today");
        int tomorrow = -1;
        moveDateRunner(tomorrow);
        waitElementHasText(TIMELINE_TEXT_XPATH, "Tomorrow");
        int nextWeek = 1;
        moveDateRunner(nextWeek);
        waitElementHasText(TIMELINE_TEXT_XPATH, "Next Week");
        int nextMonth = 3;
        moveDateRunner(nextMonth);
        waitElementHasText(TIMELINE_TEXT_XPATH, "Next Month");

        getDriver().switchTo().defaultContent();

        jsClick(findByXpath(LINK_DISCLAIMER_XPATH));

        LOG.info("Verifying that only one browser tab exist.");
        Set<String> windowHandles = getDriver().getWindowHandles();
        Assertions.assertEquals(1, windowHandles.size(), "Unexpected amount of tabs exist.");

        jsClick(findByXpath(LINK_RISK_WARNING_XPATH));

        LOG.info("Verifying that the second browser tab opened.");
        getWait().until(ExpectedConditions.numberOfWindowsToBe(2));
        windowHandles = getDriver().getWindowHandles();
        for (String handle : windowHandles) {
            getDriver().switchTo().window(handle);
        }

        LOG.info("Sanity second browser tab verification.");
        String pageUrl = getDriver().getCurrentUrl();
        Assertions.assertTrue(pageUrl.contains("Risk-Disclosures"), "Wrong document opened. The URL without the RD inside: " + pageUrl);
        Assertions.assertTrue(pageUrl.contains(".pdf?"), "The tab, probably not a document, the url is: " + pageUrl);

        LOG.info("Performing post-conditions.");
        getDriver().close();
        getDriver().switchTo().window(getDriver().getWindowHandles().iterator().next());
    }

    private static void moveDateRunner(int point) {
        WebElement element = waitElementVisibility(TIMELINE_SLIDER_XPATH);

        // Calculate the coordinates based on the position
        int xCoordinate = calculateXCoordinate(element, point);

        // Emulate the click by moving to the calculated X-coordinate and clicking
        Actions actions = new Actions(getDriver());
        actions.dragAndDropBy(element, xCoordinate, 0)
                .build()
                .perform();
    }

    private static int calculateXCoordinate(WebElement element, int position) {
        // Determine the maximum value
        int maxValue = Integer.parseInt(element.getAttribute("aria-valuemax"));
        // Calculate the percentage position based on the position
        double percentage = (double) position / maxValue;

        // Calculate the X-coordinate based on the percentage
        int sliderWidth = element.getSize().getWidth();
        int xCoordinate = (int) Math.round(percentage * sliderWidth);

        return xCoordinate;
    }

}
