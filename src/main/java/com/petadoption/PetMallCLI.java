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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.petadoption.dto.*;

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
                System.out.println("7. See Available Animals by Shelter");
                System.out.println("8. See Applications");
                System.out.println("9. See Adoptions");
                System.out.println("10. Logout");
                System.out.println("11. Exit");
                System.out.print("Choose: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> viewProfile();
                    case "2" -> updateProfile();
                    case "3" -> listPets();
                    case "4" -> applyAdoption();
                    case "5" -> changePassword();
                    case "6" -> seeShelters();
                    case "7" -> seeAvailableAnimals();
                    case "8" -> seeApplications();
                    case "9" -> seeAdoptions();
                    case "10" -> logout();
                    case "11" -> {
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
            while (true) {
                System.out.print("Enter Pet ID to adopt: ");
                String petIdStr = scanner.nextLine();
                Long petId;
                try {
                    petId = Long.parseLong(petIdStr);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID. Try again.");
                    continue;
                }

                // STEP 1: Check availability
                HttpRequest availabilityRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/animals/" + petId + "/availability"))
                        .header("Authorization", "Bearer " + jwtToken)
                        .GET()
                        .build();

                HttpResponse<String> availabilityResponse = client.send(availabilityRequest, HttpResponse.BodyHandlers.ofString());

                if (availabilityResponse.statusCode() != 200) {
                    System.out.println("Failed to check availability: " + availabilityResponse.body());
                    continue;
                }

                boolean available = Boolean.parseBoolean(availabilityResponse.body());

                if (!available) {
                    System.out.println("This animal is not available. Please choose another ID.");
                    continue;
                }

                // STEP 2: Apply for adoption
                HttpRequest applyRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/application?animalId=" + petId))
                        .header("Authorization", "Bearer " + jwtToken)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                HttpResponse<String> applyResponse = client.send(applyRequest, HttpResponse.BodyHandlers.ofString());

                if (applyResponse.statusCode() == 200) {
                    System.out.println("Adoption request sent successfully!");
                    break; // exit loop after success
                } else {
                    System.out.println("Failed to apply for adoption: " + applyResponse.body());
                }
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
                // Create ObjectMapper with JavaTimeModule to handle LocalTime
                ObjectMapper mapperWithTime = new ObjectMapper();
                mapperWithTime.registerModule(new JavaTimeModule());
                mapperWithTime.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                // Deserialize JSON into ShelterResponseDto array
                ShelterResponseDto[] shelters = mapperWithTime.readValue(response.body(), ShelterResponseDto[].class);

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
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Create ObjectMapper with JavaTimeModule to handle LocalDate/LocalDateTime
                ObjectMapper mapperWithTime = new ObjectMapper();
                mapperWithTime.registerModule(new JavaTimeModule());
                mapperWithTime.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                // Parse JSON into list of ApplicationResponseDto
                List<ApplicationResponseDto> applications = mapperWithTime.readValue(
                        response.body(),
                        mapperWithTime.getTypeFactory().constructCollectionType(List.class, ApplicationResponseDto.class)
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

    private static void seeAdoptions() {
        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                System.out.println("You must login first.");
                return;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/adopter/getAdoptions")) // your endpoint
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Create ObjectMapper with JavaTimeModule to handle LocalDate
                ObjectMapper mapperWithTime = new ObjectMapper();
                mapperWithTime.registerModule(new JavaTimeModule());
                mapperWithTime.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                // Parse JSON into list of AdoptionDto
                List<AdoptionDto> adoptions = mapperWithTime.readValue(
                        response.body(),
                        mapperWithTime.getTypeFactory().constructCollectionType(List.class, AdoptionDto.class)
                );

                if (adoptions.isEmpty()) {
                    System.out.println("No adoptions found.");
                } else {
                    System.out.println("=== Adoptions ===");
                    for (AdoptionDto adoption : adoptions) {
                        System.out.println("----------------------------");
                        System.out.println("Adoption ID:      " + adoption.getId());
                        System.out.println("Application ID:   " + adoption.getApplicationId());
                        System.out.println("Date:             " + adoption.getDate());
                        System.out.println("Fee:              " + adoption.getFee());
                    }
                    System.out.println("============================");
                }

            } else {
                System.out.println("Failed to fetch adoptions: " + response.statusCode());
                System.out.println(response.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seeAvailableAnimals() {
        try {
            if (jwtToken == null || jwtToken.isEmpty()) {
                System.out.println("You must login first.");
                return;
            }

            // Fetch shelters
            HttpRequest shelterRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/shelter/getAll"))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> shelterResponse = client.send(shelterRequest, BodyHandlers.ofString());
            if (shelterResponse.statusCode() != 200) {
                System.out.println("Failed to fetch shelters: " + shelterResponse.body());
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            ShelterResponseDto[] shelters = mapper.readValue(shelterResponse.body(), ShelterResponseDto[].class);

            System.out.println("\n=== Select Shelter ===");
            for (ShelterResponseDto s : shelters) {
                System.out.println(s.getId() + ". " + s.getName());
            }
            System.out.print("Enter Shelter ID: ");
            Long shelterId = Long.parseLong(scanner.nextLine());

            System.out.print("Enter statuses (comma separated, e.g., AVAILABLE,ADOPTED) or leave empty: ");
            String statusInput = scanner.nextLine();
            List<String> statuses = null;
            if (!statusInput.isBlank()) {
                statuses = List.of(statusInput.split(",")).stream()
                        .map(String::trim)
                        .toList();
            }

            System.out.print("Filter by Name (leave empty if not): ");
            String name = scanner.nextLine();
            if (name.isBlank()) name = null;

            System.out.print("Filter by Species (leave empty if not): ");
            String species = scanner.nextLine();
            if (species.isBlank()) species = null;

            System.out.print("Filter by Age (leave empty if not): ");
            String ageStr = scanner.nextLine();
            Integer age = ageStr.isBlank() ? null : Integer.parseInt(ageStr);

            // Fetch animals
            StringBuilder url = new StringBuilder("http://localhost:8080/animals?shelterId=" + shelterId);
            if (statuses != null) {
                for (String s : statuses) {
                    url.append("&status=").append(s);
                }
            }
            if (name != null) url.append("&name=").append(name);
            if (species != null) url.append("&species=").append(species);
            if (age != null) url.append("&age=").append(age);

            HttpRequest animalRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url.toString()))
                    .header("Authorization", "Bearer " + jwtToken)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> animalResponse = client.send(animalRequest, BodyHandlers.ofString());
            if (animalResponse.statusCode() == 200) {
                List<AnimalDto> animals = mapper.readValue(
                        animalResponse.body(),
                        mapper.getTypeFactory().constructCollectionType(List.class, AnimalDto.class)
                );

                if (animals.isEmpty()) {
                    System.out.println("No animals found for the selected filters.");
                } else {
                    System.out.println("\n=== Animals ===");
                    for (AnimalDto a : animals) {
                        System.out.println("ID: " + a.getId());
                        System.out.println("Name: " + a.getName());
                        System.out.println("Species: " + a.getSpecies());
                        System.out.println("Breed: " + a.getBreed());
                        System.out.println("Sex: " + a.getSex());
                        System.out.println("Age: " + a.getAge());
                        System.out.println("Weight: " + a.getWeight());
                        System.out.println("Color: " + a.getColor());
                        System.out.println("Intake Date: " + a.getIntakeDate());
                        System.out.println("----------------------");
                    }
                }
            } else {
                System.out.println("Failed to fetch animals: " + animalResponse.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
