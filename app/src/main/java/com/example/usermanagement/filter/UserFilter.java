package com.example.usermanagement.filter;

import com.example.usermanagement.data.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Legacy-style filter class for User objects
 */
public class UserFilter {
    private UserFilter() {
        // Private constructor to prevent instantiation
    }

    /**
     * Filters users by first name
     * @param users List of users to filter
     * @param firstName First name to filter by
     * @return Filtered list of users
     */
    public static List<User> filterByFirstName(List<User> users, String firstName) {
        List<User> filteredUsers = new ArrayList<>();
        String searchTerm = firstName.toLowerCase().trim();
        System.out.println("\nFiltering by first name:");
        System.out.println("Search term: '" + searchTerm + "'");
        System.out.println("Total users to check: " + users.size());
        
        for (User user : users) {
            String userFirstName = user.firstName.toLowerCase().trim();
            System.out.println("\nChecking user: '" + userFirstName + "'");
            boolean startsWith = userFirstName.startsWith(searchTerm);
            System.out.println("StartsWith check: '" + userFirstName + "' starts with '" + searchTerm + "' = " + startsWith);
            
            if (startsWith) {
                System.out.println("✓ Match found: " + userFirstName);
                filteredUsers.add(user);
            } else {
                System.out.println("✗ No match: " + userFirstName);
            }
        }
        
        System.out.println("\nFinal results:");
        for (User user : filteredUsers) {
            System.out.println("- " + user.firstName);
        }
        System.out.println("Total matches: " + filteredUsers.size());
        
        return filteredUsers;
    }

    private static String getCharCodes(String str) {
        StringBuilder codes = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            codes.append((int)str.charAt(i)).append(" ");
        }
        return codes.toString();
    }

    /**
     * Filters users by last name
     * @param users List of users to filter
     * @param lastName Last name to filter by
     * @return Filtered list of users
     */
    public static List<User> filterByLastName(List<User> users, String lastName) {
        List<User> filteredUsers = new ArrayList<>();
        
        // Handle null or empty lastName parameter
        if (lastName == null || lastName.trim().isEmpty()) {
            System.out.println("\nNo last name filter provided, returning all users");
            return new ArrayList<>(users);
        }
        
        String searchTerm = lastName.toLowerCase().trim();
        System.out.println("\nFiltering by last name:");
        System.out.println("Search term: '" + searchTerm + "'");
        System.out.println("Total users to check: " + users.size());
        
        for (User user : users) {
            // Skip users with null lastName
            if (user.lastName == null) {
                System.out.println("✗ Skipping user with null last name: " + user.firstName);
                continue;
            }
            
            String userLastName = user.lastName.toLowerCase().trim();
            System.out.println("\nChecking user: '" + user.firstName + " " + userLastName + "'");
            boolean startsWith = userLastName.startsWith(searchTerm);
            System.out.println("StartsWith check: '" + userLastName + "' starts with '" + searchTerm + "' = " + startsWith);
            
            if (startsWith) {
                System.out.println("✓ Match found: " + user.firstName + " " + userLastName);
                filteredUsers.add(user);
            } else {
                System.out.println("✗ No match: " + user.firstName + " " + userLastName);
            }
        }
        
        System.out.println("\nLast name filter results:");
        for (User user : filteredUsers) {
            System.out.println("- " + user.firstName + " " + user.lastName);
        }
        System.out.println("Total matches: " + filteredUsers.size());
        
        return filteredUsers;
    }

    /**
     * Filters users by email
     * @param users List of users to filter
     * @param email Email to filter by
     * @return Filtered list of users
     */
    public static List<User> filterByEmail(List<User> users, String email) {
        List<User> filteredUsers = new ArrayList<>();
        String searchTerm = email.toLowerCase().trim();
        System.out.println("\nFiltering by email:");
        System.out.println("Search term: '" + searchTerm + "'");
        System.out.println("Total users to check: " + users.size());
        
        for (User user : users) {
            String userEmail = user.email.toLowerCase().trim();
            System.out.println("\nChecking user: '" + user.firstName + " " + user.lastName + "'");
            System.out.println("Email: '" + userEmail + "'");
            boolean contains = userEmail.contains(searchTerm);
            System.out.println("Contains check: '" + userEmail + "' contains '" + searchTerm + "' = " + contains);
            
            if (contains) {
                System.out.println("✓ Match found: " + user.firstName + " " + user.lastName);
                filteredUsers.add(user);
            } else {
                System.out.println("✗ No match: " + user.firstName + " " + user.lastName);
            }
        }
        
        System.out.println("\nEmail filter results:");
        for (User user : filteredUsers) {
            System.out.println("- " + user.firstName + " " + user.lastName);
        }
        System.out.println("Total matches: " + filteredUsers.size());
        
        return filteredUsers;
    }

    /**
     * Filters users by phone number
     * @param users List of users to filter
     * @param phone Phone number to filter by
     * @return Filtered list of users
     */
    public static List<User> filterByPhone(List<User> users, String phone) {
        List<User> filteredUsers = new ArrayList<>();
        String searchTerm = phone.trim();
        System.out.println("\nFiltering by phone:");
        System.out.println("Search term: '" + searchTerm + "'");
        System.out.println("Total users to check: " + users.size());
        
        for (User user : users) {
            String userPhone = user.phone.trim();
            System.out.println("\nChecking user: '" + user.firstName + " " + user.lastName + "'");
            System.out.println("Phone: '" + userPhone + "'");
            boolean startsWith = userPhone.startsWith(searchTerm);
            System.out.println("StartsWith check: '" + userPhone + "' starts with '" + searchTerm + "' = " + startsWith);
            
            if (startsWith) {
                System.out.println("✓ Match found: " + user.firstName + " " + user.lastName);
                filteredUsers.add(user);
            } else {
                System.out.println("✗ No match: " + user.firstName + " " + user.lastName);
            }
        }
        
        System.out.println("\nPhone filter results:");
        for (User user : filteredUsers) {
            System.out.println("- " + user.firstName + " " + user.lastName);
        }
        System.out.println("Total matches: " + filteredUsers.size());
        
        return filteredUsers;
    }

    /**
     * Filters users by multiple criteria
     * @param users List of users to filter
     * @param firstName First name filter (can be null)
     * @param lastName Last name filter (can be null)
     * @param email Email filter (can be null)
     * @param phone Phone filter (can be null)
     * @return Filtered list of users
     */
    public static List<User> filterUsers(List<User> users, String firstName, String lastName, String email, String phone) {
        if (users == null) {
            return new ArrayList<>();
        }
        List<User> filteredUsers = new ArrayList<>(users);
        System.out.println("\nStarting filterUsers with " + filteredUsers.size() + " users");
        
        // Convert empty strings to null
        firstName = (firstName != null && firstName.trim().isEmpty()) ? null : firstName;
        lastName = (lastName != null && lastName.trim().isEmpty()) ? null : lastName;
        email = (email != null && email.trim().isEmpty()) ? null : email;
        phone = (phone != null && phone.trim().isEmpty()) ? null : phone;
        
        // Only apply filters if they have actual content
        if (firstName != null) {
            System.out.println("\nApplying first name filter: '" + firstName + "'");
            System.out.println("Users before first name filter: " + filteredUsers.size());
            filteredUsers = filterByFirstName(filteredUsers, firstName);
            System.out.println("Users after first name filter: " + filteredUsers.size());
            System.out.println("Filtered users after first name:");
            for (User user : filteredUsers) {
                System.out.println("- " + user.firstName + " " + user.lastName);
            }
        }
        
        if (lastName != null) {
            System.out.println("\nApplying last name filter: '" + lastName + "'");
            System.out.println("Users before last name filter: " + filteredUsers.size());
            filteredUsers = filterByLastName(filteredUsers, lastName);
            System.out.println("Users after last name filter: " + filteredUsers.size());
            System.out.println("Filtered users after last name:");
            for (User user : filteredUsers) {
                System.out.println("- " + user.firstName + " " + user.lastName);
            }
        }
        
        if (email != null) {
            System.out.println("\nApplying email filter: '" + email + "'");
            System.out.println("Users before email filter: " + filteredUsers.size());
            filteredUsers = filterByEmail(filteredUsers, email);
            System.out.println("Users after email filter: " + filteredUsers.size());
            System.out.println("Filtered users after email:");
            for (User user : filteredUsers) {
                System.out.println("- " + user.firstName + " " + user.lastName);
            }
        }
        
        if (phone != null) {
            System.out.println("\nApplying phone filter: '" + phone + "'");
            System.out.println("Users before phone filter: " + filteredUsers.size());
            filteredUsers = filterByPhone(filteredUsers, phone);
            System.out.println("Users after phone filter: " + filteredUsers.size());
            System.out.println("Filtered users after phone:");
            for (User user : filteredUsers) {
                System.out.println("- " + user.firstName + " " + user.lastName);
            }
        }
        
        System.out.println("\nFinal filtered results:");
        for (User user : filteredUsers) {
            System.out.println("- " + user.firstName + " " + user.lastName);
        }
        System.out.println("Total matches: " + filteredUsers.size());
        
        return filteredUsers;
    }
} 

