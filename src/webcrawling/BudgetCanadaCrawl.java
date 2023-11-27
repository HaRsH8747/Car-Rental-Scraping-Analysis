package webcrawling;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class BudgetCanadaCrawl {

    private static String budgetUrl = "https://www.budget.ca/en/home";

    private static Hashtable<String, String> url_Map = new Hashtable<String, String>();

    public static void main(String[] args) {

    }

    public static void checkForPopUp() {
        By popupButtonLocator = By.cssSelector(".bx-row.bx-row-submit button[data-click='close']");
        try {
            WebElement popupButton = wait.until(ExpectedConditions.presenceOfElementLocated(popupButtonLocator));

            // If the button is present, click it
            popupButton.click();
//            System.out.println("Clicked the 'Continue without discount' button.");
        } catch (Exception e) {
            // If the button is not present after 3 seconds, do nothing
//            System.out.println("Pop-up button not found after waiting for 3 seconds. Continuing without clicking.");
        }
    }

    static ChromeOptions chromeOptions = new ChromeOptions();
    //        chromeOptions.addArguments("--headless");
    static WebDriver driver;
    static WebDriverWait wait;

    public static void initDriver() {
//        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver(chromeOptions);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get(budgetUrl);
    }

    public static String userPickupLoc = "";

    public static String resolveLocation(String pickupLocation, String picLoc_dropdown, String suggestionBox) {
        checkForPopUp();
        userPickupLoc = pickupLocation;
        WebElement inputPickUpField = driver.findElement(By.id(picLoc_dropdown));
        inputPickUpField.clear();
        inputPickUpField.sendKeys(pickupLocation);

        return fetchPickupLocations(driver, suggestionBox);
    }

    static boolean findSuggestion = false;

    public static String fetchPickupLocations(WebDriver driver, String suggestionId) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, List<String>> suggestionMap = new HashMap<>();

//        WebElement angucompleteResults = driver.findElement(By.className("angucomplete-results"));
        // Find all child elements inside the "angucomplete-results" div
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement suggestionDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(suggestionId)));
//        WebElement angucompleteResults = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("angucomplete-results")));
        WebElement angucompleteResults = suggestionDiv.findElement(By.className("angucomplete-results"));
        List<WebElement> childElements = angucompleteResults.findElements(By.xpath("./div"));
        HashMap<String, WebElement> locationElementMap = new HashMap<>();

        // Loop through each child element
        for (WebElement childElement : childElements) {
            WebElement divWebelement = childElement.findElement(By.xpath(".//div[contains(@class, 'angucomplete-selection-category')]"));
            String pString = divWebelement.findElement(By.tagName("p")).getText();
            if (pString.contains("Airport Rental Locations") || pString.contains("Neighborhood Rental Locations")) {
                String spanString = divWebelement.findElement(By.tagName("span")).getText();
                String categoryName = pString.replace(spanString, "").trim();
                List<String> divList = new ArrayList<>();
                List<WebElement> divAllLocInCategory = childElement.findElements(By.cssSelector("div.angucomplete-description"));
                for (WebElement div : divAllLocInCategory) {
                    String locName = div.findElement(By.tagName("span")).getText();
                    if (findSuggestion) {
                        locName = div.findElements(By.tagName("span")).stream().map(WebElement::getText).collect(Collectors.joining());
                    }
                    divList.add(locName);
                    locationElementMap.put(locName, div);
//                    if (locName.toLowerCase().contains("canada")) {
//                    }
                }
                if (childElements.get(childElements.size()-1) == childElement) {
                    findSuggestion = false;
                }
                suggestionMap.put(categoryName, divList);
            } else if (findSuggestion) {
                String spanString = divWebelement.findElement(By.tagName("span")).getText();
                String categoryName = pString.replace(spanString, "").trim();

                List<String> divList = new ArrayList<>();
                List<WebElement> divAllLocInCategory = childElement.findElements(By.cssSelector("div.angucomplete-description"));
                for (WebElement div : divAllLocInCategory) {
                    String locName = div.findElements(By.tagName("span")).stream().map(WebElement::getText).collect(Collectors.joining());
//                    if (locName.toLowerCase().contains("canada")) {
//
//                    }
                    divList.add(locName);
                    locationElementMap.put(locName, div);
                }
                suggestionMap.put(categoryName, divList);
            }
        }


        if (suggestionMap.isEmpty()) {
            findSuggestion = true;
            fetchPickupLocations(driver, suggestionId); // Call the method again
            return "";
        }

        int index = 1;
        List<String> combinedList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : suggestionMap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
//                System.out.println("\nKey: " + entry.getKey());
                combinedList.addAll(entry.getValue());
                // Iterate and print elements using the iterator
                for (String element : entry.getValue()) {
//                    System.out.println(index + ". " + element);
                    index++;
                }
            }
        }

        int selectedIndex;
        do {
//            System.out.println("\nPlease select any one locations from the suggested pickup locations above: ");
            Scanner locationSelection = new Scanner(System.in);
//            selectedIndex = locationSelection.nextInt();
            selectedIndex = 1;
        } while (selectedIndex > combinedList.size());
//        System.out.println(combinedList.get(selectedIndex-1));
//        inputField.clear();
//        inputField.sendKeys(combinedList.get(selectedIndex - 1));
        String selectedLoc = combinedList.get(selectedIndex - 1);
//        foundLocation = selectedLoc;
        locationElementMap.get(selectedLoc).click();
        if (findSuggestion) {
            fetchPickupLocations(driver, suggestionId);
        } else {
//            fetchCarDeals(driver);
        }
        return selectedLoc;
//        return url_Map;
    }

    public static void resolveDate(String pickupDate, String returnDate) {
        WebElement inputPickUpDateField = driver.findElement(By.id("from"));
        WebElement inputReturnDateField = driver.findElement(By.id("to"));
        inputPickUpDateField.clear();
        inputReturnDateField.clear();
//        String[] pickupSplit = pickupDate.split("/");
//        for (String s: pickupSplit){
//            inputPickUpDateField.sendKeys(s);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        String[] returnSplit = returnDate.split("/");
//        for (String s: returnSplit){
//            inputReturnDateField.sendKeys(s);
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
        inputPickUpDateField.sendKeys(pickupDate);
        inputReturnDateField.sendKeys(returnDate);
    }

    private static void scrollDownToLoadAllElements(WebDriver driver) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        int currentHeight, newHeight = 0;

        do {
            currentHeight = newHeight;
            jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try {
                Thread.sleep(2000); // Adjust sleep time if necessary
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            newHeight = Integer.parseInt(jsExecutor.executeScript("return document.body.scrollHeight").toString());
        } while (newHeight > currentHeight);
    }

    public static Hashtable<String, String> fetchCarDeals() {
        driver.findElement(By.id("res-home-select-car")).click();

        if (checkForWarning()) {
            return null;
        }

        checkForPopUp();

        scrollDownToLoadAllElements(driver);

//        List<WebElement> carDivs = driver.findElements(By.className("step2dtl-avilablecar-section"));
//        for (WebElement carDiv : carDivs) {
//            try {
//                WebElement carGroup = carDiv.findElement(By.cssSelector("h3[ng-bind='car.carGroup']"));
//                WebElement carFeatures = carDiv.findElement(By.cssSelector("p.featurecartxt.similar-car"));
//                WebElement counterPrice = carDiv.findElement(By.cssSelector("p.payamntp"));  // Adjust the selector accordingly
//                WebElement payNowPrice = carDiv.findElement(By.cssSelector("p.payamntr"));  // Adjust the selector accordingly
//
//                // Print the extracted data (you can save it to CSV here)
////            System.out.println("Car Group: " + carGroup.getText());
////            System.out.println("Features: " + carFeatures.getText());
////            System.out.println("Pay at counter Price: " + counterPrice.getText());
////            System.out.println("Pay Now Price: " + payNowPrice.getText());
////            System.out.println();
//
//                // Save data to CSV (append to the file)
////                saveToTXT(carGroup.getText(), carFeatures.getText(), counterPrice.getText(), payNowPrice.getText());
//            } catch (Exception ex) {
////                ex.getMessage();
//            }
//        }

        ////////////////////////////////////////////////////////////
        //Save Html Page
        ////////////////////////////////////////////////////////////
        String content = driver.getPageSource();

        String folderList[] = {"AvisHtml", "BudgetHtml"};
        File webPageFolder = new File("AvisHtml");
        int fileCounter = 1;
        if (webPageFolder.exists()){
            fileCounter = webPageFolder.listFiles().length;
        }

//        url_Map.putAll(WebCrawler.createFile(avisUrl, content, userPickupLoc+"_"+fileCounter+"_avis_car_deals", "AvisFiles/"));
        url_Map.putAll(WebCrawler.createFile(budgetUrl, content, "budget_deals", "BudgetFiles/"));
//        url_Map.putAll(WebCrawler.createFile(budgetUrl, content, fileName, folderName));

        System.out.println("Data extracted and saved...");
        return url_Map;
    }

    private static boolean checkForWarning() {
        try {
//            WebElement element = driver.findElement(By.id("warning-msg-err"));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("warning-msg-err")));
            if (element.isDisplayed()) {
                System.out.println(element.getText());
                driver.get(budgetUrl);
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    public static void resolveTime(String pickupTime, String returnTime) {

        // Find the select element using XPath
        WebElement selectPickUpTimeElement = driver.findElement(By.xpath("//select[@ng-model='vm.reservationModel.pickUpTime']"));

        // Create a Select object
        Select selectPickupTime = new Select(selectPickUpTimeElement);

        // Format the time as it appears in the HTML options
        String formattedPickupTime = String.format("string:%s", pickupTime);

        // Select the option by its value
        selectPickupTime.selectByValue(formattedPickupTime);

        // Find the select element using XPath
        WebElement selectDropOffTimeElement = driver.findElement(By.xpath("//select[@ng-model='vm.reservationModel.dropTime']"));

        // Create a Select object
        Select selectDropOffTime = new Select(selectDropOffTimeElement);

        // Format the time as it appears in the HTML options
        String formattedDropOffTime = String.format("string:%s", returnTime);

        // Select the option by its value
        selectDropOffTime.selectByValue(formattedDropOffTime);

    }

    public static void closeDriver() {
        driver.quit();
    }
}