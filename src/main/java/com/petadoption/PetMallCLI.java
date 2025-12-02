package com.petadoption;

import java.util.Scanner;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.net.http.*;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petadoption.dto.ApplicationResponseDto;
import com.petadoption.dto.ShelterResponseDto;
import com.petadoption.dto.StaffDto;

public class PetMallCLI {

    private static String jwtToken = null;
    private static final Scanner scanner = new Scanner(System.in);
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.println("\n--- PetMall CLI ---");
            if (jwtToken == null) {
                System.out.println("1. Login");
                System.out.println("2. Signup");
                System.out.println("3. Exit");
                System.out.print("Choose: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> login();
                    case "2" -> signup();
                    case "3" -> {
                        System.out.println("Bye!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice!");
                }
            } else {
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. List Pets");
                System.out.println("4. Apply for Adoption");
                System.out.println("5. Change Password");
                System.out.println("6. See Shelters");
                System.out.println("7. See Applications");
                System.out.println("8. Logout");
                System.out.println("9. Exit");
                System.out.print("Choose: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> viewProfile();
                    case "2" -> updateProfile();
                    case "3" -> listPets();
                    case "4" -> applyAdoption();
                    case "5" -> changePassword();
                    case "6" -> seeShelters();
                    case "7" -> seeApplications();
                    case "8" -> logout();
                    case "9" -> {
                        System.out.println("Bye!");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice!");
                }
            }
        }
    }

    private static void login() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            Map<String, String> payload = new HashMap<>();
            payload.put("email", email);
            payload.put("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Map<String, Object> res = mapper.readValue(response.body(), Map.class);
                jwtToken = (String) res.get("token");
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed: " + response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void signup() {
        try {
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Address: ");
            String address = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Number of Adults: ");
            int adults = Integer.parseInt(scanner.nextLine());
            System.out.print("Number of Children: ");
            int children = Integer.parseInt(scanner.nextLine());
            System.out.print("Has Other Pets? (yes/no): ");
            boolean hasOtherPets = scanner.nextLine().equalsIgnoreCase("yes");

            Map<String, Object> payload = new HashMap<>();
            payload.put("name", name);
            payload.put("address", address);
            payload.put("email", email);
            payload.put("password", password);
            payload.put("numberOfAdults", adults);
            payload.put("numberOfChildren", children);
            payload.put("hasOtherPets", hasOtherPets);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/auth/signup"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Map<String, Object> res = mapper.readValue(response.body(), Map.class);
                jwtToken = (String) res.get("token");
                System.out.println("Signup successful! Logged in.");
            } else {
                System.out.println("Signup failed: " + response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void viewProfile() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/adopter/me"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Map<String, Object> profile = mapper.readValue(response.body(), Map.class);
                System.out.println("\n--- Profile ---");
                profile.forEach((k, v) -> System.out.println(k + ": " + v));
            } else {
                System.out.println("Failed to fetch profile.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateProfile() {
        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                System.out.println("You must login first.");
                return;
            }

            Map<String, Object> payload = new HashMap<>();
            System.out.print("New Address: ");
            payload.put("address", scanner.nextLine());
            System.out.print("New Number of Adults: ");
            payload.put("numberOfAdults", Integer.parseInt(scanner.nextLine()));
            System.out.print("New Number of Children: ");
            payload.put("numberOfChildren", Integer.parseInt(scanner.nextLine()));
            System.out.print("Has Other Pets? (yes/no): ");
            payload.put("hasOtherPets", scanner.nextLine().equalsIgnoreCase("yes"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/adopter/update"))
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
            e.printStackTrace();
        }
    }


    private static void listPets() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/animals"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                List<Map<String, Object>> pets = mapper.readValue(response.body(), List.class);
                System.out.println("\n--- Pets ---");
                for (Map<String, Object> pet : pets) {
                    System.out.println(pet.get("id") + ". " + pet.get("name") + " (" + pet.get("species") + ")");
                }
            } else {
                System.out.println("Failed to fetch pets.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void applyAdoption() {
        try {
            System.out.print("Enter Pet ID to adopt: ");
            String petId = scanner.nextLine();

            Map<String, Object> payload = new HashMap<>();
            payload.put("petId", petId);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/adoption"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Adoption request sent!");
            } else {
                System.out.println("Failed to apply for adoption.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void logout() {
        jwtToken = null;
        System.out.println("Logged out successfully.");
    }

    private static void changePassword() {

        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                System.out.println("You must login first.");
                return;
            }

            String password;
            String password1;

            // Loop until passwords match
            while (true) {
                System.out.print("New Password: ");
                password = scanner.nextLine();
                System.out.print("Enter again: ");
                password1 = scanner.nextLine();

                if (password.equals(password1)) {
                    break; // passwords match
                } else {
                    System.out.println("Passwords do not match. Please try again.\n");
                }
            }

            // Send password as plain string in body
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/adopter/updatePassword"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "text/plain") // important: plain text
                    .PUT(HttpRequest.BodyPublishers.ofString(password))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Password updated successfully!");
            } else {
                System.out.println("Failed to update password: " + response.body() + "\n" +  + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seeShelters() {
        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                System.out.println("You must login first.");
                return;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/shelter/getAll")) // adjust endpoint if needed
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Deserialize JSON into ShelterResponseDto array
                ShelterResponseDto[] shelters = mapper.readValue(response.body(), ShelterResponseDto[].class);

                System.out.println("=== Shelters List ===\n");

                for (ShelterResponseDto shelter : shelters) {
                    System.out.println("ID: " + shelter.getId());
                    System.out.println("Name: " + shelter.getName());
                    System.out.println("Address: " + shelter.getAddress());
                    System.out.println("Capacity: " + shelter.getCapacity());

                    System.out.println("\nContacts:");
                    if (shelter.getShelterContacts() != null && !shelter.getShelterContacts().isEmpty()) {
                        for (ShelterResponseDto.ShelterContact contact : shelter.getShelterContacts()) {
                            System.out.println(" - " + contact.getContactType() + ": " + contact.getValue());
                        }
                    } else {
                        System.out.println(" - No contacts available");
                    }

                    System.out.println("\nWorking Hours:");
                    if (shelter.getShelterWorkingHours() != null && !shelter.getShelterWorkingHours().isEmpty()) {
                        for (ShelterResponseDto.ShelterWorkingHour wh : shelter.getShelterWorkingHours()) {
                            System.out.println(" - " + wh.getDayOfWeek() + ": " +
                                    wh.getOpeningTime() + " - " + wh.getClosingTime());
                        }
                    } else {
                        System.out.println(" - No working hours available");
                    }

                    System.out.println("\nStaff:");
                    if (shelter.getStaffs() != null && !shelter.getStaffs().isEmpty()) {
                        for (StaffDto staff : shelter.getStaffs()) {
                            System.out.println(" - " + staff.getName() + " (ID: " + staff.getId() +
                                    ", Address: " + staff.getAddress() + ", UserID: " + staff.getUserId() + ")");
                        }
                    } else {
                        System.out.println(" - No staff available");
                    }

                    System.out.println("\n--------------------------\n");
                }

            } else {
                System.out.println("Failed to fetch shelters: " + response.statusCode() + "\n" + response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seeApplications() {
        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                System.out.println("You must login first.");
                return;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/application/getAll"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json") // GET usually uses JSON
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse JSON into list of ApplicationResponseDto
                List<ApplicationResponseDto> applications = mapper.readValue(
                        response.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, ApplicationResponseDto.class)
                );

                if (applications.isEmpty()) {
                    System.out.println("No applications found.");
                } else {
                    System.out.println("=== Applications ===");
                    for (ApplicationResponseDto app : applications) {
                        System.out.println("----------------------------");
                        System.out.println("Application ID:      " + app.getId());
                        System.out.println("Animal Name:         " + app.getAnimalName());
                        System.out.println("Animal Species:      " + app.getAnimalSpecies());
                        System.out.println("Submission Date:     " + app.getSubmissionDate());
                        System.out.println("Status:              " + app.getStatus());
                        System.out.println("Status Updated Date: " + app.getStatusUpdatedDate());
                    }
                    System.out.println("============================");
                }

            } else {
                System.out.println("Failed to fetch applications: " + response.statusCode());
                System.out.println(response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
