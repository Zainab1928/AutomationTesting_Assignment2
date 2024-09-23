import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class FileUploadExample {
    WebDriver driver;
    WebDriverWait wait;
    String filePath = "C:\\Users\\zaina\\Downloads\\Abc.txt"; // Update this path to your file
    
    // ExtentReports variables
    ExtentReports extent = new ExtentReports();
    ExtentSparkReporter spark = new ExtentSparkReporter("target/FileUpload.html");
    ExtentTest test;

    @BeforeClass
    public void setup() {
        // Configure ExtentReports
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("File Upload Test Report");
        extent.attachReporter(spark);

        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "D:\\Java-Selenium\\Drivers\\chromedriver.exe");

        // Initialize ChromeDriver with options
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);

        // Maximize the browser window
        driver.manage().window().maximize();

        // Initialize WebDriverWait
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Open the target URL
        driver.get("https://demo.automationtesting.in/FileUpload.html");

        // Wait until the entire webpage is fully loaded
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    @Test
    public void testFileUpload() {
        test = extent.createTest("File Upload Test").assignAuthor("Zainab").assignCategory("Functional Test").assignDevice("Windows");

        try {
            // Locate the "Browse" (file input) button and make it visible if needed
            WebElement browseButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-4")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", browseButton);

            // Send the file path to the file input element
            browseButton.sendKeys(filePath);
            test.info("File selected successfully.");
            System.out.println("File selected successfully.");

            // Check for overlays and wait for them to disappear (if necessary)
            waitForElementToDisappear(By.tagName("iframe"));

            // Locate the "Upload" button
            WebElement uploadButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button[type='submit']")));
            
            // Use JavaScript to click the upload button if the standard click fails
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", uploadButton);
            test.info("Upload button clicked.");
            System.out.println("Upload button clicked.");

            // Optional: Add some wait time to ensure the file gets uploaded
            Thread.sleep(3000);
            test.pass("File uploaded successfully.");
            System.out.println("File uploaded successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            String screenshotPath = captureScreenshot("FileUploadFailure");
            test.fail("Test failed due to exception: " + e.getMessage())
                .addScreenCaptureFromPath(screenshotPath);
            Assert.fail("File upload test failed: " + e.getMessage());
        }
    }

    private void waitForElementToDisappear(By locator) {
        try {
            WebDriverWait overlayWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            overlayWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            System.out.println("Overlay or iframe still present: " + e.getMessage());
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
