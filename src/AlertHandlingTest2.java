import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlertHandlingTest2 {
    
    ExtentReports extent = new ExtentReports();
    ExtentSparkReporter spark = new ExtentSparkReporter("target/Alert.html");
    ExtentTest test;

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("MyReport");
        extent.attachReporter(spark);

        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "D:\\Java-Selenium\\Drivers\\chromedriver.exe");

        // Initialize ChromeDriver
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);

        // Initialize WebDriverWait
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Open the URL
        driver.get("https://letcode.in/waits");
    }

    @Test
    public void testAlertHandling() {
        test = extent.createTest("Alert Handling Test").assignAuthor("Zainab").assignCategory("functional testcase").assignDevice("Windows");
        try {
            // Wait for the alert button to be clickable
            WebElement alertButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("accept")));
            
            // Assert that the alert button is displayed and enabled
            Assert.assertTrue(alertButton.isDisplayed(), "Alert button is not displayed.");
            Assert.assertTrue(alertButton.isEnabled(), "Alert button is not enabled.");

            // Print message before clicking the alert button
            System.out.println("Clicking the alert button...");
            test.info("Clicking the alert button...");

            // Click the alert button
            alertButton.click();

            // Wait for the alert to be present
            wait.until(ExpectedConditions.alertIsPresent());

            // Switch to the alert
            Alert alert = driver.switchTo().alert();

            // Accept the alert
            alert.accept();

            // Print message after handling the alert
            System.out.println("Alert accepted.");
            test.info("Alert accepted.");

            // Capture screenshot after alert is handled successfully
            String screenshotPath = captureScreenshot("AlertHandled");
            test.pass("Alert handling test passed successfully.")
                .addScreenCaptureFromPath(screenshotPath);

        } catch (Exception e) {
            // If any exception occurs, fail the test and print the stack trace
            e.printStackTrace();
            String screenshotPath = captureScreenshot("AlertHandlingFailure");
            test.fail("Test failed due to an exception: " + e.getMessage())
                .addScreenCaptureFromPath(screenshotPath);
            Assert.fail("Test failed due to an exception: " + e.getMessage());
        }
    }

    @AfterClass
    public void teardown() {
        // Flush the report
        extent.flush();

        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }

    // Method to capture a screenshot
    public String captureScreenshot(String screenshotName) {
        String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        // Define the path for saving the screenshot under src/images
        String dest = System.getProperty("user.dir") + "/target/" + screenshotName + "_" + dateName + ".png";
        
        // Capture screenshot and store it at the destination
        TakesScreenshot ts = (TakesScreenshot) driver;
        File source = ts.getScreenshotAs(OutputType.FILE);
        try {
            FileHandler.copy(source, new File(dest));
            System.out.println("Screenshot taken: " + dest);
        } catch (IOException e) {
            System.out.println("Exception while taking screenshot: " + e.getMessage());
        }
        return dest; // Return the path for attaching to ExtentReports
    }
}
