package features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FrequencyCount {

    public static void main(String[] args) {
        String filePath = "JsonData/filtered_car_deals.json";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(filePath);

            // Read the JSON file into a JsonNode
            JsonNode jsonNode = objectMapper.readTree(file);

            // Process the JsonNode to get the frequency count
            Map<String, Integer> frequencyMap = getFrequencyCount(filePath);

            // Print the frequency count
            for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
                System.out.println("Name: " + entry.getKey() + ", Frequency: " + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error reading the JSON file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> getFrequencyCount(String filePath) {

        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(filePath);

        // Read the JSON file into a JsonNode
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Map<String, Integer> frequencyMap = new HashMap<>();

        // Iterate through the array in the JSON file
        Iterator<JsonNode> elements = jsonNode.elements();
        while (elements.hasNext()) {
            JsonNode element = elements.next();
            String name = element.get("name").asText();
            frequencyMap.put(name, frequencyMap.getOrDefault(name, 0) + 1);
        }

        return frequencyMap;
    }
}