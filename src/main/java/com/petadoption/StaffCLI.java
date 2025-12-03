package com.petadoption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.petadoption.dto.AnimalDto;
import com.petadoption.dto.ApplicationResponseDtoStaff;
import com.petadoption.dto.StaffDto;
import com.petadoption.enums.AnimalEventType;
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
            System.out.println("5. View All Animals");
            System.out.println("6. Update Animal");
            System.out.println("7. Add Animal");
            System.out.println("8. Add Event To Animal");

            System.out.println("9. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> viewProfile();
                case "2" -> updateProfile();
                case "3" -> viewAllApplications();
                case "4" -> updateApplicationStatus();
                case "5" -> viewAllAnimals();
                case "6" -> updateAnimal();
                case "7" -> addAnimal();
                case "8" -> addAnimalEvent();

                case "9" -> {
                    System.out.println("Exiting Staff CLI...");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void addAnimal() {
        try {
            Map<String, Object> payload = new HashMap<>();

            System.out.print("Name: ");
            payload.put("name", scanner.nextLine());

            System.out.print("Species: ");
            payload.put("species", scanner.nextLine());

            System.out.print("Breed: ");
            payload.put("breed", scanner.nextLine());

            System.out.print("Sex: ");
            payload.put("sex", scanner.nextLine());

            System.out.print("Age: ");
            payload.put("age", Integer.parseInt(scanner.nextLine()));

            System.out.print("Weight: ");
            payload.put("weight", Double.parseDouble(scanner.nextLine()));

            System.out.print("Color: ");
            payload.put("color", scanner.nextLine());

            System.out.print("Intake Date (YYYY-MM-DD): ");
            payload.put("intakeDate", scanner.nextLine());

            // Show status options
            System.out.println("Select Status:");
            System.out.println("1. AVAILABLE");
            System.out.println("2. PENDING");
            System.out.println("3. ADOPTED");
            System.out.print("Status ID: ");
            payload.put("statusId", Long.parseLong(scanner.nextLine()));

            // Show shelter options dynamically (example hardcoded, ideally fetch from API)
            System.out.print("Shelter ID: ");
            payload.put("shelterId", Long.parseLong(scanner.nextLine()));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/animals/add"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("Animal added successfully!");
            } else {
                System.out.println("Failed to add animal: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error adding animal: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void updateAnimal() {
        try {
            System.out.print("Enter Animal ID to update: ");
            Long animalId = Long.parseLong(scanner.nextLine());

            Map<String, Object> payload = new HashMap<>();

            System.out.print("New Name (leave blank to keep current): ");
            String name = scanner.nextLine();
            if (!name.isBlank()) payload.put("name", name);

            System.out.print("New Species (leave blank to keep current): ");
            String species = scanner.nextLine();
            if (!species.isBlank()) payload.put("species", species);

            System.out.print("New Breed (leave blank to keep current): ");
            String breed = scanner.nextLine();
            if (!breed.isBlank()) payload.put("breed", breed);

            System.out.print("New Sex (leave blank to keep current): ");
            String sex = scanner.nextLine();
            if (!sex.isBlank()) payload.put("sex", sex);

            System.out.print("New Age (leave blank to keep current): ");
            String ageInput = scanner.nextLine();
            if (!ageInput.isBlank()) payload.put("age", Integer.parseInt(ageInput));

            System.out.print("New Weight (leave blank to keep current): ");
            String weightInput = scanner.nextLine();
            if (!weightInput.isBlank()) payload.put("weight", Double.parseDouble(weightInput));

            System.out.print("New Color (leave blank to keep current): ");
            String color = scanner.nextLine();
            if (!color.isBlank()) payload.put("color", color);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/animals/update/" + animalId))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Animal updated successfully!");
            } else {
                System.out.println("Failed to update animal: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error updating animal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void viewAllAnimals() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/animals/all"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                AnimalDto[] animals = mapper.readValue(response.body(), AnimalDto[].class);

                System.out.println("\n--- All Animals ---");
                for (AnimalDto a : animals) {
                    System.out.printf("ID: %d | Name: %s | Species: %s | Breed: %s | Sex: %s | Age: %d | Weight: %.2f | Color: %s | Intake: %s%n",
                            a.getId(),
                            a.getName(),
                            a.getSpecies(),
                            a.getBreed(),
                            a.getSex(),
                            a.getAge(),
                            a.getWeight(),
                            a.getColor(),
                            a.getIntakeDate());
                }
            } else {
                System.out.println("Failed to fetch animals: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Error fetching animals: " + e.getMessage());
            e.printStackTrace();
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

    private static void addAnimalEvent() {
        try {
            Map<String, Object> payload = new HashMap<>();

            // Select Animal
            System.out.println("Select Animal ID for the event:");
            // Ideally, fetch all animals from API dynamically
            HttpRequest animalsRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/animals/all"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .GET()
                    .build();

            HttpResponse<String> animalsResponse = client.send(animalsRequest, HttpResponse.BodyHandlers.ofString());
            if (animalsResponse.statusCode() == 200) {
                AnimalDto[] animals = mapper.readValue(animalsResponse.body(), AnimalDto[].class);
                for (AnimalDto a : animals) {
                    System.out.printf("ID: %d | Name: %s | Species: %s%n", a.getId(), a.getName(), a.getSpecies());
                }
            } else {
                System.out.println("Failed to fetch animals: " + animalsResponse.body());
                return;
            }

            System.out.print("Animal ID: ");
            payload.put("animalId", Long.parseLong(scanner.nextLine()));

            // Select Event Type
            System.out.println("Select Event Type:");
            for (AnimalEventType type : AnimalEventType.values()) {
                System.out.println(type.ordinal() + 1 + ". " + type);
            }
            System.out.print("Event Type: ");
            int typeChoice = Integer.parseInt(scanner.nextLine());
            payload.put("eventType", AnimalEventType.values()[typeChoice - 1]);

            // Event Date
            System.out.print("Event Date and Time (YYYY-MM-DDTHH:MM, e.g., 2025-12-03T14:30): ");
            payload.put("eventDate", scanner.nextLine());

            // Details
            System.out.print("Details (optional): ");
            String details = scanner.nextLine();
            if (!details.isBlank()) {
                payload.put("details", details);
            }

            // Send POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/animal-events/add"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                System.out.println("Animal Event added successfully!");
            } else {
                System.out.println("Failed to add Animal Event: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Error adding Animal Event: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
