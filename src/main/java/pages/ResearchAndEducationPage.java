package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static helpers.WebDriverHelper.findByXpath;
import static helpers.WebDriverHelper.getDriver;
import static helpers.WebDriverHelper.getWait;
import static helpers.WebDriverHelper.scrollToElement;

public class ResearchAndEducationPage {

    public static final String CALENDAR_LINK_XPATH = "//li/a[contains(text(),'Economic Calendar')]";
    public static final String MOB_CALENDAR_LINK_XPATH = "//li//span[contains(text(),'Economic Calendar')]";
    public static final String CALENDAR_IFRAME_ID = "iFrameResizer0";

    public static void clickCalendarBtn(){
        scrollToElement(findByXpath(CALENDAR_LINK_XPATH));
        findByXpath(CALENDAR_LINK_XPATH).click();
    }

    public static void switchToCalendarIframe(){
        getWait(20).until(ExpectedConditions.visibilityOfElementLocated(By.id(CALENDAR_IFRAME_ID)));
        getDriver().switchTo().frame(getDriver().findElement(By.id(CALENDAR_IFRAME_ID)));
    }


}
