import features.DataValidation;
import webcrawling.AvisCanadaCrawl;
import webcrawling.BudgetCanadaCrawl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Scanner;

public class Group4 {

    private static Hashtable<String, String> url_Map = new Hashtable<String, String>();
    private static String avisUrl = "https://www.avis.ca/en/home";
    private static String budgetUrl = "https://www.budget.ca/en/home";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AvisCanadaCrawl.initDriver();
//        BudgetCanadaCrawl.initDriver();

//        ChromeOptions chromeOptions = new ChromeOptions();
////        chromeOptions.addArguments("--headless");
//        WebDriver driver = new ChromeDriver(chromeOptions);
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
//        do {
//            // Set up WebDriver, navigate to the website, etc.
//
//            AvisCanadaWebScraping avisCanadaWebScraping = new AvisCanadaWebScraping(driver,wait);
//
//            url_Map.putAll(avisCanadaWebScraping.scrapData(avisUrl, "Avis", "AvisHtml/", pickupLocation, dropOffLocation, pickupDate, dropOffDate));
//            url_Map.putAll(avisCanadaWebScraping.scrapData(budgetUrl, "Budget", "BudgetHtml/", pickupLocation, dropOffLocation, pickupDate, dropOffDate));
//
//            System.out.println("\nFrequency Count:");
//            Scanner inp = new Scanner(System.in);
//            String product = inp.nextLine();
//
//            InvertedIndexing.Indexing(url_Map, product);
//
//            // Close the browser
//
//            System.out.println("Do you want to continue? (yes/no):");
//        } while (scanner.nextLine().equalsIgnoreCase("yes"));
        do {
            // Ask if pickup and drop-off locations are the same
            System.out.print("Are pickup and drop-off locations the same? (yes/no): ");
            String sameLocationResponse = scanner.nextLine().toLowerCase();

            // Get pickup location
            String pickupLocation;
            do {
                System.out.print("Enter pickup location: ");
                pickupLocation = scanner.nextLine();
            }while (!DataValidation.validateCityName(pickupLocation));

            String finalSelectedPickupLoc = AvisCanadaCrawl.resolveLocation(pickupLocation,"PicLoc_value", "PicLoc_dropdown");
//            BudgetCanadaCrawl.resolveLocation(pickupLocation,"PicLoc_value", "PicLoc_dropdown");

//             Get drop-off location if locations are different
            String dropOffLocation;
            do {
                dropOffLocation = sameLocationResponse.equals("no") ? getDropOffLocation(scanner) : pickupLocation;
            }while (!DataValidation.validateCityName(dropOffLocation));

            String finalSelectedDropOffLoc = AvisCanadaCrawl.resolveLocation(dropOffLocation,"DropLoc_value", "DropLoc_dropdown");

            // Get pickup date
            String pickupDate;
            do {
                System.out.print("Enter pickup date (DD/MM/YYYY): ");
                pickupDate = scanner.nextLine();
            }while (!DataValidation.validateDate(pickupDate));
            pickupDate = convertDateFormat(pickupDate);

            // Get drop-off date
            String returnDate;
            do {
                System.out.print("Enter return date (DD/MM/YYYY): ");
                returnDate = scanner.nextLine();
            }while (!DataValidation.validateDate(returnDate));
            returnDate = convertDateFormat(returnDate);

            String pickupTime;
            do {
                System.out.print("Enter pickup time (HH:MM AM/PM): ");
                pickupTime = scanner.nextLine();
            }while (!DataValidation.validateTime(pickupTime));

            // Get drop-off date
            String returnTime;
            do {
                System.out.print("Enter return time (HH:MM AM/PM): ");
                returnTime = scanner.nextLine();
            }while (!DataValidation.validateTime(returnTime));

            AvisCanadaCrawl.resolveDate(pickupDate,returnDate);
            AvisCanadaCrawl.resolveTime(pickupTime,returnTime);
            Hashtable<String, String> hashtable = AvisCanadaCrawl.fetchCarDeals();

            // Perform web scraping actions (replace with your actual scraping logic)
//            AvisCanadaWebCrawler avisCanadaWebCrawler = new AvisCanadaWebCrawler(driver,wait);
//            BudgetCanadaWebCrawler budgetCanadaWebCrawler = new BudgetCanadaWebCrawler(driver,wait);

//            url_Map.putAll(avisCanadaWebCrawler.scrapData(avisUrl, "Avis", "AvisHtml/", pickupLocation, dropOffLocation, pickupDate, returnDate));
//            url_Map.putAll(budgetCanadaWebCrawler.scrapData(budgetUrl, "Budget", "BudgetHtml/", pickupLocation, dropOffLocation, pickupDate, returnDate));

            // Ask if the user wants to continue
            System.out.print("Do you want to continue? (yes/no): ");
        } while (scanner.nextLine().equalsIgnoreCase("yes"));

//        driver.quit();
    }

    public static String convertDateFormat(String inputDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date date = originalFormat.parse(inputDate);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the ParseException as needed
            return null; // Return null or throw an exception based on your error handling strategy
        }
    }

    private static String getDropOffLocation(Scanner scanner) {
        System.out.print("Enter drop-off location: ");
        return scanner.nextLine();
    }
}