package features;

import java.util.Scanner;
import java.util.regex.Pattern;

public class DataValidation {

    private static final Pattern CAR_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s]+$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$"); // Updated date format
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9] (AM|PM)$"); // HH:MM format
    private static final Pattern CITY_NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");

    public static boolean validateCarName(String carName) {
        boolean check = CAR_NAME_PATTERN.matcher(carName).matches();
        if (!check){
            System.out.println("Invalid car name!!! Please use valid characters and spaces. Sorry... try again.");
        }
        return check;
    }

    public static boolean validateDate(String date) {
        boolean check = DATE_PATTERN.matcher(date).matches();
        if (!check){
            System.out.println("Invalid date format. Please use DD/MM/YYYY. Please try again.");
        }
        return check;
    }

    public static boolean validateTime(String time) {
        boolean check = TIME_PATTERN.matcher(time).matches();
        if (!check){
            System.out.println("Invalid time format. Please use HH:MM. Please try again.");
        }
        return check;
    }

    public static boolean validateCityName(String cityName) {
        boolean check = CITY_NAME_PATTERN.matcher(cityName).matches();
        if (!check){
            System.out.println("Invalid city name. Please use alphabetical characters and spaces. Sorry ...Try again.");
        }
        return check;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Car Rental CLI");

        System.out.print("Enter car name: ");
        String carName = scanner.nextLine();
        while (!validateCarName(carName)) {
            System.out.println("Invalid car name!!! Please use valid characters and spaces. Sorry... try again.");
            System.out.print("Enter car name: ");
            carName = scanner.nextLine();
        }

        System.out.print("Enter date (DD/MM/YYYY): ");
        String date = scanner.nextLine();
        while (!validateDate(date)) {
            System.out.println("Invalid date format. Please use DD/MM/YYYY. Please try again.");
            System.out.print("Enter date (DD/MM/YYYY): ");
            date = scanner.nextLine();
        }

        System.out.print("Enter time (HH:MM): ");
        String time = scanner.nextLine();
        while (!validateTime(time)) {
            System.out.println("Invalid time format. Please use HH:MM. Please try again.");
            System.out.print("Enter time (HH:MM): ");
            time = scanner.nextLine();
        }

        System.out.print("Enter city name: ");
        String cityName = scanner.nextLine();
        while (!validateCityName(cityName)) {
            System.out.println("Invalid city name. Please use alphabetical characters and spaces. Sorry ...Try again.");
            System.out.print("Enter city name: ");
            cityName = scanner.nextLine();
        }

        // Process the validated input (you can replace this with actual processing logic)
        System.out.printf("Success! Car Name: %s, Date: %s, Time: %s, City Name: %s%n", carName, date, time, cityName);

        scanner.close();
    }
}

