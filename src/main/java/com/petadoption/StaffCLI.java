package com.petadoption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.petadoption.dto.ApplicationResponseDtoStaff;
import com.petadoption.dto.StaffDto;
import com.petadoption.enums.ApplicationStatusEnum;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StaffCLI {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static String jwtToken;

    public static void main(String[] args) {
        System.out.println("=== Staff CLI ===");
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
                    .uri(URI.create("http://localhost:8080/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, Object> res = mapper.readValue(response.body(), Map.class);
                jwtToken = (String) res.get("token");
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
            System.out.println("\n--- Staff Menu ---");
            System.out.println("1. View My Profile");
            System.out.println("2. Update My Profile");
            System.out.println("3. View All Applications");
            System.out.println("4. Update Application Status");

            System.out.println("5. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewProfile();
                case "2" -> updateProfile();
                case "3" -> viewAllApplications();
                case "4" -> updateApplicationStatus();
                case "5" -> {
                    System.out.println("Exiting Staff CLI...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void updateApplicationStatus() {
        try {
            System.out.print("Enter Application ID to update: ");
            Long applicationId = Long.parseLong(scanner.nextLine());

            System.out.println("Select new status:");
            for (ApplicationStatusEnum status : ApplicationStatusEnum.values()) {
                System.out.println(status.ordinal() + 1 + ". " + status);
            }
            System.out.print("Choose: ");
            int statusChoice = Integer.parseInt(scanner.nextLine());
            ApplicationStatusEnum newStatus = ApplicationStatusEnum.values()[statusChoice - 1];

            Map<String, Object> payload = new HashMap<>();
            payload.put("applicationId", applicationId);
            payload.put("status", newStatus);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/application/update-status"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Application status updated successfully!");
            } else {
                System.out.println("Failed to update application status: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error updating application status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void viewAllApplications() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/application/getAllApplications"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ApplicationResponseDtoStaff[] applications = mapper.readValue(
                        response.body(),
                        ApplicationResponseDtoStaff[].class
                );

                System.out.println("\n--- All Applications ---");
                for (ApplicationResponseDtoStaff app : applications) {
                    System.out.printf("ID: %d | Animal: %s (%s) | Adopter: %s | Submission: %s | Status: %s | Last Updated: %s%n",
                            app.getId(),
                            app.getAnimalName(),
                            app.getAnimalSpecies(),
                            app.getAdopterName(),
                            app.getSubmissionDate(),
                            app.getStatus(),
                            app.getStatusUpdatedDate());
                }
            } else {
                System.out.println("Failed to fetch applications: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error fetching applications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void viewProfile() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/staff/me"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Your Profile:");
                ObjectMapper mapper = new ObjectMapper();

                StaffDto dto = mapper.readValue(response.body(), StaffDto.class);

                System.out.println(dto.toString());
                } else {
                System.out.println("Failed to fetch profile: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error fetching profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void updateProfile() {
        try {
            Map<String, Object> payload = new HashMap<>();

            System.out.print("New Name (leave blank to keep current): ");
            String name = scanner.nextLine();
            if (!name.isBlank()) payload.put("name", name);

            System.out.print("New Address (leave blank to keep current): ");
            String address = scanner.nextLine();
            if (!address.isBlank()) payload.put("address", address);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/staff/update/profile"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Failed to update profile: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
