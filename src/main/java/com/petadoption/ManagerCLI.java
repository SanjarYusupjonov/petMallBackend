package com.petadoption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.*;

public class ManagerCLI {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static String jwtToken;

    public static void main(String[] args) {
        System.out.println("=== Manager CLI ===");
        login();
        startCLI();
    }

    private static void login() {
        try {
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            Map<String, String> payload = new HashMap<>();
            payload.put("email", email);
            payload.put("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/auth/login")) // your login endpoint
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, Object> res = mapper.readValue(response.body(), Map.class);
                jwtToken = (String) res.get("token"); // adjust key if different
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed: " + response.body());
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void startCLI() {
        while (true) {
            System.out.println("\n--- Manager Menu ---");
            System.out.println("1. Create Shelter");
            System.out.println("2. Update Shelter");
            System.out.println("3. Create Staff");
            System.out.println("4. Delete Staff");
            System.out.println("5. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> createShelter();
                case "2" -> updateShelter();
                case "3" -> createStaff();
                case "4" -> deleteStaff();
                case "5" -> {
                    System.out.println("Exiting Manager CLI...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void createShelter() {
        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                System.out.println("You must login first.");
                return;
            }

            Scanner scanner = new Scanner(System.in);
            Map<String, Object> shelterPayload = new HashMap<>();

            System.out.print("Shelter Name: ");
            shelterPayload.put("name", scanner.nextLine());

            System.out.print("Shelter Address: ");
            shelterPayload.put("address", scanner.nextLine());

            System.out.print("Shelter Capacity: ");
            shelterPayload.put("capacity", Long.parseLong(scanner.nextLine()));

            // Contacts
            List<Map<String, String>> contacts = new ArrayList<>();
            while (true) {
                System.out.print("Add contact? (yes/no): ");
                String ans = scanner.nextLine();
                if (ans.equalsIgnoreCase("no")) break;

                Map<String, String> contact = new HashMap<>();
                System.out.print("Contact Type (phone/email/fax): ");
                contact.put("contactType", scanner.nextLine());
                System.out.print("Contact Value: ");
                contact.put("value", scanner.nextLine());
                contacts.add(contact);
            }
            shelterPayload.put("contacts", contacts);

            // Working hours
            List<Map<String, String>> workingHours = new ArrayList<>();
            while (true) {
                System.out.print("Add working hour? (yes/no): ");
                String ans = scanner.nextLine();
                if (ans.equalsIgnoreCase("no")) break;

                Map<String, String> hour = new HashMap<>();
                System.out.print("Day of Week: ");
                hour.put("dayOfWeek", scanner.nextLine());
                System.out.print("Opening Time (HH:mm): ");
                hour.put("openingTime", scanner.nextLine());
                System.out.print("Closing Time (HH:mm): ");
                hour.put("closingTime", scanner.nextLine());
                workingHours.add(hour);
            }
            shelterPayload.put("workingHours", workingHours);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/shelter/create"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(shelterPayload)))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("Shelter created successfully!");
            } else {
                System.out.println("Failed to create shelter: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error creating shelter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateShelter() {
        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                System.out.println("You must login first.");
                return;
            }

            System.out.print("Enter Shelter ID to update: ");
            Long shelterId = Long.parseLong(scanner.nextLine());

            Map<String, Object> shelterPayload = new HashMap<>();

            System.out.print("New Shelter Name (leave blank to keep unchanged): ");
            String name = scanner.nextLine();
            if (!name.isBlank()) shelterPayload.put("name", name);

            System.out.print("New Shelter Address (leave blank to keep unchanged): ");
            String address = scanner.nextLine();
            if (!address.isBlank()) shelterPayload.put("address", address);

            System.out.print("New Shelter Capacity (leave blank to keep unchanged): ");
            String capacityInput = scanner.nextLine();
            if (!capacityInput.isBlank()) shelterPayload.put("capacity", Long.parseLong(capacityInput));

            // Contacts
            List<Map<String, String>> contacts = new ArrayList<>();
            while (true) {
                System.out.print("Add/Update contact? (yes/no): ");
                String ans = scanner.nextLine();
                if (ans.equalsIgnoreCase("no")) break;

                Map<String, String> contact = new HashMap<>();
                System.out.print("Contact Type (phone/email/fax): ");
                contact.put("contactType", scanner.nextLine());
                System.out.print("Contact Value: ");
                contact.put("value", scanner.nextLine());
                contacts.add(contact);
            }
            if (!contacts.isEmpty()) shelterPayload.put("contacts", contacts);

            // Working hours
            List<Map<String, String>> workingHours = new ArrayList<>();
            while (true) {
                System.out.print("Add/Update working hour? (yes/no): ");
                String ans = scanner.nextLine();
                if (ans.equalsIgnoreCase("no")) break;

                Map<String, String> hour = new HashMap<>();
                System.out.print("Day of Week: ");
                hour.put("dayOfWeek", scanner.nextLine());
                System.out.print("Opening Time (HH:mm): ");
                hour.put("openingTime", scanner.nextLine());
                System.out.print("Closing Time (HH:mm): ");
                hour.put("closingTime", scanner.nextLine());
                workingHours.add(hour);
            }
            if (!workingHours.isEmpty()) shelterPayload.put("workingHours", workingHours);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/shelter/update/" + shelterId))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(shelterPayload)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Shelter updated successfully!");
            } else {
                System.out.println("Failed to update shelter: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error updating shelter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createStaff() {
        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                System.out.println("You must login first.");
                return;
            }

            Map<String, Object> staffPayload = new HashMap<>();
            System.out.print("Staff Name: ");
            staffPayload.put("name", scanner.nextLine());

            System.out.print("Staff Address: ");
            staffPayload.put("address", scanner.nextLine());

            System.out.print("Staff Email: ");
            staffPayload.put("email", scanner.nextLine());

            System.out.print("Staff Password: ");
            staffPayload.put("password", scanner.nextLine());

            System.out.print("Shelter ID: ");
            staffPayload.put("shelterId", Long.parseLong(scanner.nextLine()));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/staff/create"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(staffPayload)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("Staff created successfully!");
                System.out.println("Response: " + response.body());
            } else {
                System.out.println("Failed to create staff: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error creating staff: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void deleteStaff() { /* same as before */ }
}
