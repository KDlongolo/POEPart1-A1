
package com.mycompany.prog5121a1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Scanner;

public class Prog5121A1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Login> users = new ArrayList<>();

        System.out.println("\nWelcome to our Chat App 2026! Select from the options below");

        while (true) {
            // Display Menu
            System.out.println("\nMain Menu");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {

                case 1: // REGISTRATION
                    System.out.println("Enter Username: ");
                    String username = scanner.nextLine();

                    boolean validUsername = username.contains("_") && username.length() >= 5;

                    if (!validUsername) {
                        System.out.println("Username must contain (_) and be at least 5 characters long!");
                        break;
                    }

                    System.out.println("Enter Password:");
                    String password = scanner.nextLine();

                    boolean validPassword = password.length() >= 8
                            && password.matches(".*[A-Z].*")
                            && password.matches(".*\\d.*")
                            && password.matches(".*[!@#$%^&*(),.?\":<>].*");

                    if (!validPassword) {
                        System.out.println("Password must be 8+ chars, include uppercase, number & special character.");
                        break;
                    }

                    System.out.println("Enter SA phone number (e.g. +27841234567): ");
                    String phone = scanner.nextLine();

                    if (!phone.matches("\\+27[6-8]\\d{8}")) {
                        System.out.println("Invalid phone number format!");
                        break;
                    }

                    // Check duplicate username
                    boolean userExists = users.stream().anyMatch(u -> u.getUsername().equals(username));

                    if (userExists) {
                        System.out.println("Username already exists!");
                        break;
                    }

                    users.add(new Login(username, password, phone));

                    System.out.println("✅ User Registered Successfully!");
                    System.out.println("Welcome " + username + "! You may now log in.");
                    break;

                case 2: // LOGIN
                    if (users.isEmpty()) {
                        System.out.println("No users registered yet. Please register first.");
                        break;
                    }

                    promptLogin(scanner, users);
                    break;

                case 3: // EXIT
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Please choose 1, 2 or 3.");
            }
        }
    }

    // LOGIN LOGIC
    private static void promptLogin(Scanner scanner, ArrayList<Login> users) {
        System.out.println("Enter Username:");
        String enteredUsername = scanner.nextLine();

        System.out.println("Enter Password:");
        String enteredPassword = scanner.nextLine();

        Login user = users.stream()
                .filter(u -> u.getUsername().equals(enteredUsername))
                .findFirst()
                .orElse(null);

        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        boolean status = user.loginUser(enteredPassword);
        System.out.println(user.returnLoginStatus(status));
    }
}

// LOGIN CLASS
class Login {

    private String username;
    private String passwordHash;
    private String phoneNumber;
    private byte[] salt;

    public Login(String username, String password, String phoneNumber) {
        this.username = username;
        this.salt = generateSalt();
        this.passwordHash = hashPassword(password, salt);
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public boolean loginUser(String enteredPassword) {
        return this.passwordHash.equals(hashPassword(enteredPassword, salt));
    }

    public String returnLoginStatus(boolean status) {
        return status ? "✅ Login Successful!" : "❌ Invalid Username or Password";
    }

    // HASH PASSWORD WITH SHA‑256
    private String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hash = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // GENERATE RANDOM SALT
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return saltBytes;
    }
}
