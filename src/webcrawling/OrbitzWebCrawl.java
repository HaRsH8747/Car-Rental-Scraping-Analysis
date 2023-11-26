package webcrawling;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;

public class OrbitzWebCrawl {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "H:\\chromedriver.exe");

        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--headless");

        WebDriver driver = new ChromeDriver(chromeOptions);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("https://www.orbitz.com/Cars");
        driver.navigate().refresh();
        try {
            while (wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("button.uitk-fake-input")))) {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("button.uitk-button"))).click();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        } catch (Exception e) {
            // If the button is not present after 3 seconds, do nothing
            System.out.println("Pop-up button not found after waiting for 1 seconds. Continuing without clicking.");
        }


        System.out.println("Enter your city:");
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

        fetchDeals(userInput, driver, wait);

        // Close the browser
        driver.quit();
    }

    private static void fetchDeals(String inputQuery, WebDriver driver, WebDriverWait wait) {
        driver.manage().window().maximize();
        wait.until(drive -> ((JavascriptExecutor) drive).executeScript("return document.readyState").equals("complete"));

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"Rental-cars-transportation\"]/div[1]/div/div/div/div/div/div[2]/div[1]/button"))).click();
        WebElement inputField = driver.findElement(By.xpath("//*[@id=\"location-field-locn\"]"));
        inputField.sendKeys(inputQuery);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement locationResults = driver.findElement(By.cssSelector("ul[data-stid='location-field-locn-results']"));

        // Find all list items containing location suggestions within the parent element
        List<WebElement> locationItems = locationResults.findElements(By.cssSelector("li[data-stid='location-field-locn-result-item']"));
        int a = 1;
        // Loop through each list item and extract the location suggestion
        System.out.println("Location Suggestions:");
        for (WebElement locationItem : locationItems) {
            // Find the button element within each list item
            WebElement buttonElement = locationItem.findElement(By.cssSelector("button[data-stid='location-field-locn-result-item-button']"));

            // Get the aria-label attribute containing the location suggestion
            String locationSuggestion = buttonElement.getAttribute("aria-label");

            // Print each location suggestion
            System.out.println(a + ". " + locationSuggestion);
            a++;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select your location: ");
        String userInput = scanner.nextLine();
        List<WebElement> buttons = driver.findElements(By.cssSelector("ul[data-stid='location-field-locn-results'] button[data-stid='location-field-locn-result-item-button']"));

        int indexToClick = Integer.parseInt(userInput); // Change the index based on your requirement (0-based index)

        // Check if the index is valid
        if (indexToClick >= 0 && indexToClick < buttons.size()) {
            // Click the button at the specified index
            buttons.get(indexToClick).click();

        } else {
            System.out.println("Invalid index or element not found.");
        }

//        System.out.println("Please enter your pick up date: ");
//        String date = scanner.nextLine();
//        String inputDate = date;
//
//        // Split the date into day, month, and year
//        String[] parts = inputDate.split("/");
//        int day = Integer.parseInt(parts[0]);
//        int month = Integer.parseInt(parts[1]);
//        int year = Integer.parseInt(parts[2]);
//
//        // Click the button corresponding to the given date
//        String buttonXPath = String.format("//button[@aria-label='%s %d, %d']", getMonthName(month), day, year);
//        System.out.println("Please enter your drop off date: ");
//        String enddate = scanner.nextLine();
//        String inputendDate = enddate;
//
//        // Split the date into day, month, and year
//
//        try {
//            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("d1-btn")));
//            button.click();
//            String[] eparts = inputendDate.split("/");
//            int eday = Integer.parseInt(eparts[0]);
//            int emonth = Integer.parseInt(eparts[1]);
//            int eyear = Integer.parseInt(eparts[2]);
//
//            String endDatebuttonXPath = String.format("//button[@aria-label='%s %d, %d']", getMonthName(emonth), eday, eyear);
//            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(endDatebuttonXPath)));
//            WebElement endDateButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(endDatebuttonXPath)));
//            endDateButton.click();
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"Rental-cars-transportation\"]/div[2]/div[1]/div/div/div[1]/div/div[2]/div/div[2]/div/button")));
//            // Find the element using a CSS selector
//            WebElement doneButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-stid='apply-date-picker'][aria-label='Save changes and close the date picker.']")));
//            doneButton.click();
//
//        } catch (org.openqa.selenium.NoSuchElementException e) {
//            System.out.println("Element not found: " + e.getMessage());
//        }
//
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"Rental-cars-transportation\"]/div[2]/div[2]/div/select"))).click();
//        System.out.println("Enter Pickup Time (HH:MM-AM/PM)");
//        String pickupTime = scanner.nextLine(); // Change this variable to the time you want to select
//        System.out.println("Enter Drop off Time (HH:MM-AM/PM)");
//        String dropOffTime = scanner.nextLine(); // Change this variable to the time you want to select
//
//        selectTime(driver, pickupTime, ".uitk-field-select"); // Replace ".pickup-time-select" with the selector for pickup time
//        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"Rental-cars-transportation\"]/div[2]/div[3]/div/select"))).click();
//
//        selectTime(driver, dropOffTime, ".uitk-field-select[aria-label='Drop-off time']"); // Replace ".drop-off-time-select" with the selector for drop-off time

        WebElement searchButton = driver.findElement(By.cssSelector("button[data-testid='submit-button']"));
        searchButton.click();

        WebDriverWait wait2 = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait2.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

        WebCrawler.createFile("orbitzUrl", driver.getPageSource(), "orbitz_car_deals", "OrbitzFiles/");

    }

    public static void selectTime(WebDriver driver, String inputTime, String selectElementSelector) {
        // Extract hour and minute from the input time
        String[] splitTime = inputTime.split(":");
        int hour = Integer.parseInt(splitTime[0]);
        int minute = Integer.parseInt(splitTime[1].substring(0, 2));
        boolean isPM = splitTime[1].substring(2).equalsIgnoreCase("PM");

        int timeInMinutes = hour * 60 + minute;

        // Find the select element for time
        WebElement timeSelect = driver.findElement(By.cssSelector(selectElementSelector));

        // Loop through options and select the closest time
        for (WebElement option : timeSelect.findElements(By.tagName("option"))) {
            int optionTime = Integer.parseInt(option.getAttribute("data-time"));
            if (optionTime >= timeInMinutes) {
                option.click();
                break;
            }
        }
    }

    private static String getMonthName(int monthNumber) {
        String[] months = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        return months[monthNumber - 1];
    }
}