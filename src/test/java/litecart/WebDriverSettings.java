package litecart;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by Lilia Andreeva on 29.11.2019
 */
public class WebDriverSettings {
    public static final String chromeDriverPath = "C:/_Personal/SelStart/chromedriver.exe";
    public ChromeDriver driver;
    public WebDriverWait wait;
    public Actions actions;

    @Before
    public void setup () {
        System.setProperty("webdriver.chrome.driver",chromeDriverPath );
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        actions = new Actions(driver);
        wait = new WebDriverWait(driver, 20);
    }

    /**
     * Found on https://stackoverflow.com
     * If all you need to do is wait for the html on the page to become stable before trying to interact with elements,
     * you can poll the DOM periodically and compare the results, if the DOMs are the same within the given poll time,
     * you're golden. Something like this where you pass in the maximum wait time and the time between page polls before comparing.
     * Simple and effective.
     *
     * @param maxWaitMillis
     * @param pollDelimiter
     * @throws InterruptedException
     *
     */
    public void waitForJavascript(int maxWaitMillis, int pollDelimiter) throws InterruptedException {
        double startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + maxWaitMillis) {
            String prevState = driver.getPageSource();
            Thread.sleep(pollDelimiter); // <-- would need to wrap in a try catch
            if (prevState.equals(driver.getPageSource())) {
                return;
            }
        }
    }
    @After
    public void close() {
       driver.quit();
    }
}
