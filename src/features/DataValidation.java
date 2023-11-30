package features;

import java.util.Scanner;
import java.util.regex.Pattern;

// DataValidation class to perform input validation for car rental information
public class DataValidation {

    // Regular expressions for validating car name, date, time, and city name
    static Pattern PTRN_FOR_CAR_NAME = Pattern.compile("^[a-zA-Z0-9\\s]+$");
    static Pattern PTRN_FOR_DATE = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$"); //date formate update
    static Pattern PTRN_FOR_TIME = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9] (AM|PM)$");
    static Pattern PTRN_NAME_FOR_CITY = Pattern.compile("^[a-zA-Z\\s]+$");

    // Method to validate car name
    public static boolean validateCarName(String nameOfCar) {
        boolean ck_ptrn = PTRN_FOR_CAR_NAME.matcher(nameOfCar).matches();
        if (!ck_ptrn) {
            System.out.println("Car name os invalid! Enter proper car name.Apologies, please try again.\n");
        }
        return ck_ptrn;
    }

    // Method to validate date
    public static boolean validateDate(String date) {
        boolean ck_ptrn = PTRN_FOR_DATE.matcher(date).matches();
        if (!ck_ptrn) {
            System.out.println("Format of date is invalid. Use the format DD/MM/YYYY and try again.");
        }
        return ck_ptrn;
    }

    // Method to validate time
    public static boolean validateTime(String time) {
        boolean ck_ptrn = PTRN_FOR_TIME.matcher(time).matches();
        if (!ck_ptrn) {
            System.out.println("Time format is invalid. Please use the format HH:MM. Kindly try again.");
        }
        return ck_ptrn;
    }

    // Method to validate city name
    public static boolean validateCityName(String cityName) {
        boolean ck_ptrn = PTRN_NAME_FOR_CITY.matcher(cityName).matches();
        if (!ck_ptrn) {
            System.out.println("City name is invalid. Please use letters and spaces only. Apologies, please try again.");
        }
        return ck_ptrn;
    }

    // Method to validate user response (used in potential future interactions)
    public static boolean validateUserResponse(String input) {
        if (input.length() == 1 && (input.charAt(0) == 'y' || input.charAt(0) == 'n')) {
            return true;
        } else {
            System.out.print("Invalid input. Please enter 'y' or 'n'\n");
            return false;
        }
    }
}
