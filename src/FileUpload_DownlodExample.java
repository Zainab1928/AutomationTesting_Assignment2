import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class FileUpload_DownlodExample {

    private WebDriver driver;
    private ExtentReports extent;
    private ExtentTest test;
    private String downloadDir = "C:\\Users\\zaina\\Downloads"; // Adjust the path as needed
    private String filePathUpload = "C:\\Users\\zaina\\Downloads\\Abc.txt"; // Adjust the path as needed
    private String pdfFileName = "sample.pdf";
    private String txtFileName = "sample.txt";
    private String xlsFileName = "sample.xlsx";

    @BeforeMethod
    public void setUp() {
        // Set up Extent Reports with ExtentSparkReporter
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/fileUploadDownload.html");
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("File Upload and Download Test");
        sparkReporter.config().setReportName("Test Automation Report");

        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Set up ChromeDriver with options
        System.setProperty("webdriver.chrome.driver", "D:\\Java-Selenium\\Drivers\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("download.default_directory=" + downloadDir);
        options.addArguments("download.prompt_for_download=false");
        options.addArguments("download.directory_upgrade=true");
        options.addArguments("safebrowsing.enabled=true");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test(priority = 1)
    public void testFileDownload() {
        test = extent.createTest("File Download Test", "Testing file download functionality.").assignAuthor("Zainab").assignCategory("functional testcase").assignDevice("Windows");

        try {
            driver.get("https://letcode.in/file");

            // Trigger file downloads
            downloadFile(By.id("pdf"), pdfFileName);
            downloadFile(By.id("txt"), txtFileName);
            downloadFile(By.id("xls"), xlsFileName);

            // Verify files are downloaded
            File pdfFile = new File(downloadDir + "\\" + pdfFileName);
            File txtFile = new File(downloadDir + "\\" + txtFileName);
            File xlsFile = new File(downloadDir + "\\" + xlsFileName);

            Assert.assertTrue(pdfFile.exists(), "PDF file was not downloaded");
            test.pass("PDF file downloaded successfully: " + pdfFileName);
            Assert.assertTrue(txtFile.exists(), "TXT file was not downloaded");
            test.pass("TXT file downloaded successfully: " + txtFileName);
            Assert.assertTrue(xlsFile.exists(), "XLS file was not downloaded");
            test.pass("XLS file downloaded successfully: " + xlsFileName);

        } catch (Exception e) {
            test.fail("Test failed due to exception: " + e.getMessage());
            Assert.fail("Test failed due to exception: " + e.getMessage());
        }
    }

    @Test(priority = 2)
    public void testFileUpload() {
        test = extent.createTest("File Upload Test", "Testing file upload functionality.").assignAuthor("Zainab").assignCategory("functional testcase").assignDevice("Windows");

        try {
            driver.get("https://letcode.in/file");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Handle file upload
            WebElement uploadButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-4")));

            // Make the upload button visible if it's hidden
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", uploadButton);

            // Upload the file
            uploadButton.sendKeys(filePathUpload);

            // Verify file upload
            WebElement fileNameElement = driver.findElement(By.id("input-4"));
            Assert.assertTrue(fileNameElement.getAttribute("value").contains("Abc.txt"), "File upload failed");
            test.pass("File uploaded successfully: Abc.txt");

        } catch (Exception e) {
            test.fail("Test failed due to exception: " + e.getMessage());
            Assert.fail("Test failed due to exception: " + e.getMessage());
        }
    }

    private void downloadFile(By locator, String fileName) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Wait for the button to be visible and clickable
            WebElement downloadButton = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", downloadButton);

            // Wait for the button to be clickable and handle any potential obstructions
            wait.until(ExpectedConditions.elementToBeClickable(downloadButton));

            // Hide any potential overlay
            try {
                WebElement overlay = driver.findElement(By.id("aswift_3_host"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", overlay);
            } catch (NoSuchElementException e) {
                // Overlay not found, proceed
            }

            // Click the button using JavaScript to avoid interception
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", downloadButton);

            // Wait for the file to download
            File file = new File(downloadDir + "\\" + fileName);
            int attempts = 0;
            while (!file.exists() && attempts < 30) {
                Thread.sleep(1000); // Wait for 1 second before checking again
                attempts++;
            }

            if (!file.exists()) {
                Assert.fail("Failed to download " + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Error downloading file: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws IOException {
        if (ITestResult.FAILURE == result.getStatus()) {
            // Capture screenshot for failure and store it in the "images" folder under "src"
            String screenshotPath = captureScreenshot(result.getName());
            // Add the screenshot to Extent Report
            test.addScreenCaptureFromPath(screenshotPath);
            test.fail("Test failed: " + result.getThrowable());
        }

        if (driver != null) {
            driver.quit();
        }

        // Flush the report
        extent.flush();
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
