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

public class FileDownloadExample {
    
    WebDriver driver;
    String downloadDir = "C:\\Users\\zaina\\Downloads"; // Download directory
    WebDriverWait wait;

    // ExtentReports variables
    ExtentReports extent = new ExtentReports();
    ExtentSparkReporter spark = new ExtentSparkReporter("target/FileDownload.html");
    ExtentTest test;

    @BeforeClass
    public void setup() {
        // Configure ExtentReports
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("File Download Test Report");
        extent.attachReporter(spark);

        // Set the path to the ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "D:\\Java-Selenium\\Drivers\\chromedriver.exe");

        // Set Chrome options for automatic file download
        ChromeOptions options = new ChromeOptions();
        options.addArguments("download.default_directory=" + downloadDir);
        options.addArguments("download.prompt_for_download=false");
        options.addArguments("download.directory_upgrade=true");
        options.addArguments("safebrowsing.enabled=true");

        // Initialize ChromeDriver with options
        driver = new ChromeDriver(options);

        // Maximize the browser window
        driver.manage().window().maximize();

        // Open the target URL
        driver.get("https://demo.automationtesting.in/FileDownload.html");

        // Initialize WebDriverWait
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Wait until the entire webpage is fully loaded
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    @Test
    public void testFileDownload() {
        test = extent.createTest("File Download Test").assignAuthor("Zainab").assignCategory("Functional Test").assignDevice("Windows");

        try {
            // Step 1: Enter text into the text area
            WebElement textArea = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("textbox")));
            Assert.assertTrue(textArea.isDisplayed(), "Text area is not displayed");
            String textData = "This is a sample text for testing file download.";
            textArea.sendKeys(textData);
            test.info("Text data entered into text area.");

            // Step 2: Click the "Generate File" button
            WebElement generateFileButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("createTxt")));
            Assert.assertTrue(generateFileButton.isDisplayed(), "Generate File button is not displayed");
            generateFileButton.click();
            test.info("Generate File button clicked.");

            // Step 3: Click the "Download" button to download the file
            WebElement downloadButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("link-to-download")));
            Assert.assertTrue(downloadButton.isDisplayed(), "Download button is not displayed");
            downloadButton.click();
            test.info("Download button clicked.");

            // Wait for the file to download
            String fileName = "info.txt"; // The file name provided in the download button
            File file = new File(downloadDir + "\\" + fileName);
            int attempts = 0;
            while (!file.exists() && attempts < 30) {
                Thread.sleep(1000); // Wait for 1 second before checking again
                attempts++;
            }

            Assert.assertTrue(file.exists(), "File download failed: " + fileName);
            test.pass("File downloaded successfully: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
            String screenshotPath = captureScreenshot("FileDownloadFailure");
            test.fail("Test failed due to exception: " + e.getMessage())
                .addScreenCaptureFromPath(screenshotPath);
            Assert.fail("Test failed due to exception: " + e.getMessage());
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
