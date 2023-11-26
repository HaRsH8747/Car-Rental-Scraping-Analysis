package htmlparser;

import com.fasterxml.jackson.databind.ObjectMapper;
import features.FrequencyCount;
import features.PageRanking;
import features.SpellChecking;
import features.WordCompletion;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CarRentalParser {

    public static void main(String[] args) {

        List<CarInfo> combinedCarInfoList = new ArrayList<>();

        String[] folderPaths = {"AvisFiles/", "BudgetFiles/" };

        for (String folderPath : folderPaths) {
            // Create a File object for the folder
            File folder = new File(folderPath);

            // Check if the path is a directory
            if (folder.isDirectory()) {
                // List all files in the directory
                File[] files = folder.listFiles();

                if (files != null) {
                    for (File file : files) {
                        System.out.println(file.getName());
                        combinedCarInfoList.addAll(parseCarRentalWebsite(file.getAbsolutePath()));
                    }
                } else {
                    System.out.println("The folder is empty.");
                }
            } else {
                System.out.println("The specified path is not a directory.");
            }
        }
        saveCarInfoToJson(combinedCarInfoList, "filtered_car_deals");

        // Filter the car deals based on user preferences
        List<CarInfo> filteredCarInfoList = filterCarDeals(combinedCarInfoList);

        // Save the filtered car deals in a JSON file

        // Display the refined list
//        System.out.println("Refined Selection:");
//        displayCarList(filteredCarInfoList);
    }

    private static List<CarInfo> parseCarRentalWebsite(String filePath) {
        List<CarInfo> carInfoList = new ArrayList<>();

        try {
            // Parse local HTML file
            File input = new File(filePath);
            Document document = Jsoup.parse(input, "UTF-8");

            // Example: Extract car name, price, passenger capacity, etc.
            Elements carElements = document.select(".step2dtl-avilablecar-section");


            for (Element carElement : carElements) {
//                Element aElement = carElement.select("a.vehicleinfo");

                String carName = carElement.select("p.featurecartxt.similar-car").text();
                if (carName.contains("Mystery Car")) {
                    continue;
                }
//                System.out.println(carName);
                double carPrice = Double.parseDouble(carElement.select("p.payamntp").text().replaceAll("[^0-9.]", ""));
//                System.out.println(carElement.select("span.four-seats-feat").text());
                int passengerCapacity = Integer.parseInt(fetchInt(carElement.select("span.four-seats-feat").first().text()));
                String carGroup = carElement.select("h3[ng-bind='car.carGroup']").text();
                String transmissionType = carElement.select("span.four-automatic-feat").text();
                int largeBag = Integer.parseInt(fetchInt(carElement.select("span.four-bags-feat").first().text()));
                int smallBag = Integer.parseInt(fetchInt(carElement.select("span.four-bags-feat-small").first().text()));

                // Create a CarInfo object and add it to the list
                CarInfo carInfo = new CarInfo(carName, carPrice, passengerCapacity, carGroup, transmissionType, largeBag, smallBag);
                carInfoList.add(carInfo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return carInfoList;
    }

    public static String fetchInt(String string) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            return matcher.group();
        }
        return string;
    }

    private static List<CarInfo> filterCarDeals(List<CarInfo> carInfoList) {
        // Filtering logic goes here

        // You may choose to sort the list by price if needed
//        List<CarInfo> processFilter = new ArrayList<>(carInfoList);
//        processFilter.sort(Comparator.comparingDouble(CarInfo::getPrice));

        // Example: Display the default selection
//        if (!carInfoList.isEmpty()) {
//            CarInfo defaultSelection = carInfoList.get(0);
//            System.out.println("Default Selection: " + defaultSelection.getName() + " - $" + defaultSelection.getPrice());
//        } else {
//            System.out.println("No cars found.");
//        }

        // Allow the user to refine their selection based on preferences
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to refine your selection? (yes/no): ");
        String refineSelection = scanner.next().toLowerCase();

        List<CarInfo> processFilter = new ArrayList<>();
        while (refineSelection.equals("yes")) {
//            processFilter.addAll(carInfoList);
//            processFilter.sort(Comparator.comparingDouble(CarInfo::getPrice));
            System.out.println("Select option to refine:\n1. Display all deals\n2. Car Name\n3. Car Price\n4. Transmission Type\n5. Passenger Capacity\n6. Luggage Capacity\n7. Show Car Analysis\n8. Check Page Ranking\n9. Exit");
            int option = scanner.nextInt();

            switch (option) {
                case 1:
//                    System.out.println("Enter preferred Car Name: ");
                    processFilter = carInfoList;
                    break;
                case 2:
                    System.out.println("Enter preferred Car Name: ");
                    String preferredCarName = scanner.next().toLowerCase();

                    processFilter = filterByCarName(carInfoList, preferredCarName);
                    break;
                case 3:
                    System.out.println("Enter preferred price range type (40-100): ");
                    String preferredPriceRange = scanner.next().toLowerCase();
                    processFilter = filterByPriceRange(carInfoList, preferredPriceRange);
                    break;
                case 4:
                    System.out.println("Enter preferred transmission type (Automatic or Manual): ");
                    String preferredTransmission = scanner.next().toLowerCase();
                    processFilter = filterByTransmission(carInfoList, preferredTransmission);
                    break;
                case 5:
                    System.out.println("Enter preferred passenger capacity: ");
                    int preferredPassengerCapacity = scanner.nextInt();
                    processFilter = filterByPassengerCapacity(carInfoList, preferredPassengerCapacity);
                    break;
                case 6:
                    System.out.println("Enter preferred luggage capacity: ");
                    int preferredLuggageCapacity = scanner.nextInt();
                    processFilter = filterByLuggageCapacity(carInfoList, preferredLuggageCapacity);
                    break;
                case 7:
                    fetchCarAnalysis(carInfoList);
//                    processFilter = filterByLuggageCapacity(carInfoList, preferredLuggageCapacity);
                    break;
                case 8:
                    refineSelection = "no";
                    break;
                default:
                    System.out.println("Invalid option. Please enter a valid option.");
            }


            if (processFilter.isEmpty()) {
                System.out.println("No cars match the refined criteria.");
                refineSelection = "no";
            } else {
                // Display the refined list
//                System.out.println("Refined Selection:");
                displayCarList(processFilter);
            }

            if (!refineSelection.equals("no")) {
                System.out.println("Do you want to further refine your selection? (yes/no): ");
                refineSelection = scanner.next().toLowerCase();
            }
        }

        return processFilter;
    }

    private static void fetchCarAnalysis(List<CarInfo> carInfoList) {
//        FrequencyCount.initializeFile("JsonData/filtered_car_deals.json");
        Map<String, Integer> frequencyMap = FrequencyCount.getFrequencyCount("JsonData/filtered_car_deals.json");

        // Print the frequency count
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
            System.out.println("Total Available \"" + entry.getKey() + "\" cars: \"" + entry.getValue() + "\"");
        }
    }

    private static List<CarInfo> filterByPriceRange(List<CarInfo> carInfoList, String preferredPriceRange) {
        String[] priceRange = preferredPriceRange.split("-");

        if (priceRange.length != 2) {
            // Handle invalid price range input
            throw new IllegalArgumentException("Invalid price range format");
        }

        int minPrice = Integer.parseInt(priceRange[0].trim());
        int maxPrice = Integer.parseInt(priceRange[1].trim());

        return carInfoList.stream()
                .filter(car -> {
                    try {
                        double carPrice = car.getPrice();
                        return carPrice >= minPrice && carPrice <= maxPrice;
                    } catch (NumberFormatException e) {
                        // Handle invalid price format for a car
                        return false;
                    }
                })
                .sorted(Comparator.comparingDouble(CarInfo::getPrice))
                .collect(Collectors.toList());
    }

    private static int getUserSelection(int maxOption) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Select an option (1-" + maxOption + "): ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        }
        int selectedOption = scanner.nextInt();
        return selectedOption;
    }

    private static List<CarInfo> filterByCarName(List<CarInfo> carInfoList, String preferredCarName) {
        try {
            SpellChecking.initializeDictionary("JsonData/filtered_car_deals.json");
            WordCompletion.initializeDictionaryFromJsonFile("JsonData/filtered_car_deals.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean check = SpellChecking.checkSpelling(preferredCarName);

        List<String> suggestions = WordCompletion.getSuggestions(preferredCarName.toLowerCase());

        if (!suggestions.isEmpty()) {
            System.out.println("Suggestions:");
            for (int i = 0; i < suggestions.size(); i++) {
                System.out.println((i + 1) + ". " + suggestions.get(i));
            }

            // Assuming you have a method to get user input, e.g., getUserSelection
            int selectedOption = getUserSelection(suggestions.size());

            // Check if the selected option is valid
            if (selectedOption >= 1 && selectedOption <= suggestions.size()) {
                preferredCarName = suggestions.get(selectedOption - 1);
            } else if (selectedOption == 0) {
//                System.out.println(preferredCarName);
                // User selected all options, so no need to change preferredCarName
            } else {
                System.out.println("Invalid selection. Using original input.");
            }
        }

        try {
            PageRanking.pageRank(preferredCarName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        if (!check) {
//        }

        String finalPreferredCarName = preferredCarName;
        return carInfoList.stream()
                .filter(car -> car.getName().equalsIgnoreCase(finalPreferredCarName) || car.getName().toLowerCase().contains(finalPreferredCarName))
                .sorted(Comparator.comparingDouble(CarInfo::getPrice))
                .toList();
    }

    private static List<CarInfo> filterByTransmission(List<CarInfo> carInfoList, String preferredTransmission) {
        return carInfoList.stream()
                .filter(car -> car.getTransmissionType().equalsIgnoreCase(preferredTransmission))
                .sorted(Comparator.comparingDouble(CarInfo::getPrice))
                .toList();
    }

    private static List<CarInfo> filterByPassengerCapacity(List<CarInfo> carInfoList, int preferredPassengerCapacity) {
        return carInfoList.stream()
                .filter(car -> car.getPassengerCapacity() >= preferredPassengerCapacity)
                .sorted(Comparator.comparingDouble(CarInfo::getPrice))
                .toList();
    }

    private static List<CarInfo> filterByLuggageCapacity(List<CarInfo> carInfoList, int preferredLuggageCapacity) {
//        Optional<CarInfo> maxLarge = carInfoList.stream().max(Comparator.comparingInt(CarInfo::getLargeBag));
//        Optional<CarInfo> maxSmall = carInfoList.stream().max(Comparator.comparingInt(CarInfo::getSmallBag));

        Optional<CarInfo> maxTotalCar = carInfoList.stream()
                .max(Comparator.comparingInt(car -> car.getLargeBag() + car.getSmallBag()));

        if (preferredLuggageCapacity > maxTotalCar.get().getLargeBag() + maxTotalCar.get().getSmallBag()) {
            preferredLuggageCapacity = maxTotalCar.get().getLargeBag() + maxTotalCar.get().getSmallBag();
        }
        int finalPreferredLuggageCapacity = preferredLuggageCapacity;
        return carInfoList.stream()
                .filter(car -> car.getLargeBag() + car.getSmallBag() >= finalPreferredLuggageCapacity)
                .sorted(Comparator.comparingDouble(CarInfo::getPrice))
                .toList();
    }

    private static void displayCarList(List<CarInfo> carInfoList) {
        System.out.format("%-25s%-40s%-20s%-25s%-25s%-25s%n",
                "Car Group", "Car Model", "Counter Price", "Passenger Capacity", "Luggage Capacity", "Transmission Type");

        for (CarInfo carInfo : carInfoList) {
            System.out.format("%-25s%-44s%-25s%-24s%-21s%-25s%n",
                    carInfo.carGroup, carInfo.name, carInfo.price,
                    carInfo.passengerCapacity, carInfo.largeBag + carInfo.smallBag, carInfo.transmissionType);
        }


//        carInfoList.forEach(car -> System.out.println(car.getName() + " - $" + car.getPrice()));
    }

    private static void saveCarInfoToJson(List<CarInfo> carInfoList, String filename) {
        ObjectMapper objectMapper = new ObjectMapper();
        String directoryPath = "JsonData/";

        try {
            File directory = new File(directoryPath);

            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create the file in the specified directory with the provided filename
            File file = new File(directory, filename + ".json");

            // Write carInfoList to JSON file
            try {
                objectMapper.writeValue(file, carInfoList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            System.out.println("Filtered car deals saved to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class CarInfo {
        private String name;
        private double price;
        private int passengerCapacity;
        private String carGroup;
        private String transmissionType;
        private int largeBag;
        private int smallBag;

        public CarInfo(String name, double price, int passengerCapacity, String carGroup, String transmissionType, int largeBag, int smallBag) {
            this.name = name;
            this.price = price;
            this.passengerCapacity = passengerCapacity;
            this.carGroup = carGroup;
            this.transmissionType = transmissionType;
            this.largeBag = largeBag;
            this.smallBag = smallBag;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getPassengerCapacity() {
            return passengerCapacity;
        }

        public String getTransmissionType() {
            return transmissionType;
        }

        public String getCarGroup() {
            return carGroup;
        }

        public int getLargeBag() {
            return largeBag;
        }

        public int getSmallBag() {
            return smallBag;
        }
    }
}