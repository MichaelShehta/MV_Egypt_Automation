// java
package MV_Egypt;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class ContactFormTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        String status;
        if (result.getStatus() == ITestResult.SUCCESS) {
            status = "PASS";
        } else if (result.getStatus() == ITestResult.FAILURE) {
            status = "FAIL";
        } else if (result.getStatus() == ITestResult.SKIP) {
            status = "SKIP";
        } else {
            status = "UNKNOWN";
        }

        System.out.println("Test '" + result.getMethod().getMethodName() + "' - " + status);
        if (result.getThrowable() != null) {
            System.out.println("Reason: " + result.getThrowable().getMessage());
        }

        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testSubmitContactForm_ValidScenario() throws InterruptedException {
        driver.get("https://www.mountainviewegypt.com/contact-us");

        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'form-field')][1]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[text()='Complaint']"))).click();

        WebElement fullName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Full name']")));
        fullName.sendKeys("Test User");

        driver.findElement(By.xpath("//input[@placeholder='Email address']"))
                .sendKeys("test@example.com");

        Select countrySelect = new Select(driver.findElement(By.name("mobileNumberCountry")));
        countrySelect.selectByValue("EG");

        driver.findElement(By.xpath("//input[@placeholder='Enter mobile number']"))
                .sendKeys("1211522685");

        // fixed XPath for textarea placeholder
        driver.findElement(By.xpath("//textarea[@placeholder='Write your message (optional)']"))
                .sendKeys("This is a test automation message.");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        WebElement successMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Thank you')]")
                )
        );

        Assert.assertTrue(successMessage.isDisplayed(), "Thank You message should be displayed");
    }

    @Test
    public void testSubmitContactForm_InvalidEmail() throws InterruptedException {
        driver.get("https://www.mountainviewegypt.com/contact-us");
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'form-field')][1]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[text()='Complaint']"))).click();

        driver.findElement(By.xpath("//input[@placeholder='Full name']")).sendKeys("Test User");
        driver.findElement(By.xpath("//input[@placeholder='Email address']")).sendKeys("invalid-email");

        Select countrySelect = new Select(driver.findElement(By.name("mobileNumberCountry")));
        countrySelect.selectByValue("EG");

        driver.findElement(By.xpath("//input[@placeholder='Enter mobile number']")).sendKeys("1211522685");

        driver.findElement(By.xpath("//textarea[@placeholder='Write your message (optional)']"))
                .sendKeys("Test message.");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // ✅ Now wait for error message instead of Thank You
        WebElement errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("/html/body/div/section[2]/div/div/form/div[3]/p")
                )
        );

        Assert.assertTrue(errorMessage.isDisplayed(), "Validation error for invalid email should be displayed");
        System.out.println("Error message text: " + errorMessage.getText());
    }

    @Test
    public void testSubmitContactForm_InvalidName() throws InterruptedException {
        driver.get("https://www.mountainviewegypt.com/contact-us");
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 800);");
        Thread.sleep(1000);

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'form-field')][1]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[text()='Complaint']"))).click();

        // ❌ Invalid name (only numbers to trigger validation)
        driver.findElement(By.xpath("//input[@placeholder='Full name']")).sendKeys("12345");

        driver.findElement(By.xpath("//input[@placeholder='Email address']")).sendKeys("test@example.com");

        Select countrySelect = new Select(driver.findElement(By.name("mobileNumberCountry")));
        countrySelect.selectByValue("EG");

        driver.findElement(By.xpath("//input[@placeholder='Enter mobile number']")).sendKeys("1211522685");

        driver.findElement(By.xpath("//textarea[@placeholder='Write your message (optional)']"))
                .sendKeys("Testing invalid name case.");

        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // ✅ Wait for the Full Name validation error
        WebElement errorMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("/html/body/div/section[2]/div/div/form/div[2]/div/p")
                )
        );

        Assert.assertTrue(errorMessage.isDisplayed(), "Validation error for invalid name should be displayed");
        System.out.println("Full Name error message: " + errorMessage.getText());
    }


}
