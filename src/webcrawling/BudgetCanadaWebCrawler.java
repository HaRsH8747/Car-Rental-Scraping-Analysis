package webcrawling;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BudgetCanadaWebCrawler {

    private ChromeOptions chromeOptions = new ChromeOptions();
    private final WebDriver driver;
    private final WebDriverWait wait;
    private boolean findSuggestion = false;

    private String foundLocation = "";

    private String avisUrl = "https://www.avis.ca/en/home";
    private String budgetUrl = "https://www.budget.ca/en/home";

    private String userInput;

    private static Hashtable<String, String> url_Map = new Hashtable<String, String>();


    public BudgetCanadaWebCrawler(WebDriver webDriver, WebDriverWait wait) {
        this.driver = webDriver;
        this.wait = wait;

    }

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "H:\\chromedriver.exe");

        // Set the path to the location of your ChromeDriver executable

//        chromeOptions.addArguments("--headless");
        // Create a new instance of the Chrome driver

        // Navigate to the Avis Canada website
//        driver.get("https://www.avis.ca/en/home");
//        driver.get("https://www.budget.ca/en/home");

        System.out.println("Enter pickup location (city name):");
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

//        checkForPopUp();

//        fetchPickupLocations(userInput);

        // Close the browser
//        driver.quit();
    }

    public void checkForPopUp() {
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

    public Hashtable<String, String> fetchPickupLocations() {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, List<String>> suggestionMap = new HashMap<>();

        WebElement angucompleteResults = driver.findElement(By.className("angucomplete-results"));

        // Find all child elements inside the "angucomplete-results" div
        List<WebElement> childElements = angucompleteResults.findElements(By.xpath("./child::*"));
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
                    if (findSuggestion){
                        locName = div.findElements(By.tagName("span")).stream().map(WebElement::getText).collect(Collectors.joining());
                    }
                    if (locName.toLowerCase().contains("canada")) {
                        divList.add(locName);
                        locationElementMap.put(locName, div);
                    }
                }
                if (childElements.get(childElements.size()-1) == childElement){
                    findSuggestion = false;
                }
//                List<String> divList = divAllLocInCategory.stream()
//                        .map(webElement ->
//                                webElement.findElement(By.tagName("span")).getText())
//                        .filter(s -> s.toLowerCase().contains("canada"))
//                        .toList();
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
            fetchPickupLocations(); // Call the method again
            return url_Map;
        }

        int index = 1;
        List<String> combinedList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : suggestionMap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                System.out.println("\nKey: " + entry.getKey());
                combinedList.addAll(entry.getValue());
                // Iterate and print elements using the iterator
                for (String element : entry.getValue()) {
                    System.out.println(index + ". " + element);
                    index++;
                }
            }
        }

        int selectedIndex;
        do {
            System.out.println("\nPlease select any one locations from the suggested pickup locations above: ");
            Scanner locationSelection = new Scanner(System.in);
            selectedIndex = locationSelection.nextInt();
        }while (selectedIndex > combinedList.size());
//        System.out.println(combinedList.get(selectedIndex-1));
//        inputField.clear();
//        inputField.sendKeys(combinedList.get(selectedIndex - 1));
        String selectedLoc = combinedList.get(selectedIndex - 1);
        foundLocation = selectedLoc;
        locationElementMap.get(selectedLoc).click();
        if (findSuggestion){
            fetchPickupLocations();
        }else {
            fetchCarDeals(driver);
        }
        return url_Map;
    }

    private Hashtable<String, String> fetchCarDeals(WebDriver driver) {
        driver.findElement(By.id("res-home-select-car")).click();

        checkForPopUp();

        scrollDownToLoadAllElements(driver);

        List<WebElement> carDivs = driver.findElements(By.className("step2dtl-avilablecar-section"));
        for (WebElement carDiv : carDivs) {
            try {
                WebElement carGroup = carDiv.findElement(By.cssSelector("h3[ng-bind='car.carGroup']"));
                WebElement carFeatures = carDiv.findElement(By.cssSelector("p.featurecartxt.similar-car"));
                WebElement counterPrice = carDiv.findElement(By.cssSelector("p.payamntp"));  // Adjust the selector accordingly
                WebElement payNowPrice = carDiv.findElement(By.cssSelector("p.payamntr"));  // Adjust the selector accordingly

                // Print the extracted data (you can save it to CSV here)
//            System.out.println("Car Group: " + carGroup.getText());
//            System.out.println("Features: " + carFeatures.getText());
//            System.out.println("Pay at counter Price: " + counterPrice.getText());
//            System.out.println("Pay Now Price: " + payNowPrice.getText());
//            System.out.println();

                // Save data to CSV (append to the file)
                saveToTXT(carGroup.getText(), carFeatures.getText(), counterPrice.getText(), payNowPrice.getText());
            } catch (Exception ex) {
//                ex.getMessage();
            }
        }

        ////////////////////////////////////////////////////////////
        //Save Html Page
        ////////////////////////////////////////////////////////////
        String content = driver.getPageSource();


        url_Map.putAll(WebCrawler.createFile(url, content, fileName, folderName));
//        url_Map.putAll(WebCrawler.createFile(budgetUrl, content, fileName, folderName));

        System.out.println("Data extracted and saved...");
        return url_Map;
    }

    private static void saveToTXT(String carGroup, String features, String counterPrice, String payNowPrice) {
        try {
            FileWriter txtWriter = new FileWriter("avis_car_data.txt", true);
            // Use "|" as the separator
            txtWriter.append(carGroup).append("|").append(features).append("|").append(counterPrice).append("|").append(payNowPrice).append("\n");
            txtWriter.flush();
            txtWriter.close();
//            System.out.println("all data saved...");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String url = "";
    public String fileName = "";
    public String folderName = "";
    public Hashtable<String, String> scrapData(String url, String fileName, String folderName, String pickupLocation, String dropOffLocation, String pickupDate, String dropOffDate) {
        this.url = url;
        this.fileName = fileName;
        this.folderName = folderName;

        Scanner scanner = new Scanner(System.in);
        if (!foundLocation.isEmpty()){
            userInput = foundLocation;
        }else {
            System.out.println("Enter pickup location (city name or zip-code):");
            userInput = scanner.nextLine();
        }

        driver.get(url);
//        driver.get("https://www.budget.ca/en/home");

        checkForPopUp();

        WebElement inputField = driver.findElement(By.id("PicLoc_value"));
        inputField.clear();
        inputField.sendKeys(userInput);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

//        driver.get("https://www.avis.ca/en/home");
//        driver.get("https://www.budget.ca/en/home");

        return fetchPickupLocations();
    }

}