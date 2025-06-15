package com.example.usermanagement.ui.screens;

import com.example.usermanagement.data.User;
import com.example.usermanagement.filter.UserFilter;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserFilterTest {

    private List<User> users;

    @Before
    public void setup() {
        users = new ArrayList<>();
        
        // Create users with explicit field values and verify them
        User user1 = new User(1, "John", "Doe", "john.doe@example.com", "1234567890");
        User user2 = new User(2, "Jane", "Smith", "jane.smith@example.com", "0987654321");
        User user3 = new User(3, "Jonathan", "Davis", "jonathan.davis@example.com", "1122334455");
        User user4 = new User(4, "Alice", "Wonder", "alice.wonder@example.com", "5544332211");
        
        // Verify the data is set correctly
        System.out.println("\nVerifying test data setup:");
        System.out.println("User1 firstName: '" + user1.firstName + "'");
        System.out.println("User2 firstName: '" + user2.firstName + "'");
        System.out.println("User3 firstName: '" + user3.firstName + "'");
        System.out.println("User4 firstName: '" + user4.firstName + "'");
        
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        
        // Print initial test data
        System.out.println("\nTest data setup complete:");
        System.out.println("Total users: " + users.size());
        for (User user : users) {
            System.out.println(String.format("User %d: firstName='%s', lastName='%s'", 
                user.id, user.firstName, user.lastName));
        }
    }

    @Test
    public void testFilterByFirstName_exactMatch() {
        List<User> filtered = UserFilter.filterByFirstName(users, "John");
        assertEquals(1, filtered.size()); // Only John should match for exact match
        assertTrue(filtered.stream().anyMatch(user -> user.getFirstName().equals("John")));
    }

    @Test
    public void testFilterByFirstName_partialMatch() {
        System.out.println("\nRunning testFilterByFirstName_partialMatch");
        System.out.println("Searching for 'jo'");
        
        // Verify the search term
        String searchTerm = "jo";
        System.out.println("Search term: '" + searchTerm + "'");
        System.out.println("Search term length: " + searchTerm.length());
        System.out.println("Search term character codes: " + getCharCodes(searchTerm));
        
        List<User> filtered = UserFilter.filterByFirstName(users, searchTerm);
        
        System.out.println("\nFiltered results:");
        for (User user : filtered) {
            System.out.println("Found user: " + user.firstName);
        }
        
        System.out.println("\nExpected: 2 results (John and Jonathan)");
        System.out.println("Actual: " + filtered.size() + " results");
        
        assertEquals(2, filtered.size()); // John and Jonathan
        assertTrue(filtered.stream().anyMatch(user -> user.firstName.equals("John")));
        assertTrue(filtered.stream().anyMatch(user -> user.firstName.equals("Jonathan")));
    }

    @Test
    public void testFilterByFirstName_caseInsensitive() {
        List<User> filtered = UserFilter.filterByFirstName(users, "john");
        assertEquals(1, filtered.size()); // Only John should match
        assertTrue(filtered.stream().anyMatch(user -> user.getFirstName().equals("John")));
    }

    @Test
    public void testFilterByFirstName_noMatch() {
        List<User> filtered = UserFilter.filterByFirstName(users, "Robert");
        assertTrue(filtered.isEmpty());
    }

    @Test
    public void testFilterByFirstName_emptyFilter() {
        List<User> filtered = UserFilter.filterByFirstName(users, "");
        assertEquals(4, filtered.size()); // All users should be returned for empty filter
    }

    @Test
    public void testFilterByFirstName_nullFilter() {
        List<User> filtered = UserFilter.filterByFirstName(users, "");
        assertEquals(4, filtered.size());
    }

    @Test
    public void testFilterByFirstName_emptyUserList() {
        List<User> emptyUsers = new ArrayList<>();
        List<User> filtered = UserFilter.filterByFirstName(emptyUsers, "John");
        assertTrue(filtered.isEmpty());
    }

    @Test
    public void testFilterByLastName() {
        List<User> filtered = UserFilter.filterByLastName(users, "Smith");
        assertEquals(1, filtered.size());
        assertEquals("Jane", filtered.get(0).getFirstName());
    }

    @Test
    public void testFilterByEmail() {
        List<User> filtered = UserFilter.filterByEmail(users, "john.doe@example.com");
        assertEquals(1, filtered.size());
        assertEquals("John", filtered.get(0).getFirstName());
    }

    @Test
    public void testFilterByPhone() {
        List<User> filtered = UserFilter.filterByPhone(users, "12345");
        assertEquals(1, filtered.size());
        assertEquals("John", filtered.get(0).getFirstName());
    }

    @Test
    public void testFilterUsers_multipleCriteria() {
        List<User> filtered = UserFilter.filterUsers(users, "John", "Doe", null, null);
        assertEquals(1, filtered.size());
        assertEquals("John", filtered.get(0).getFirstName());
    }

    @Test
    public void testFilterUsers_noCriteria() {
        List<User> filtered = UserFilter.filterUsers(users, null, null, null, null);
        assertEquals(4, filtered.size());
    }

    @Test
    public void testFilterUsers_someEmptyCriteria() {
        System.out.println("\nRunning testFilterUsers_someEmptyCriteria");
        System.out.println("Initial users:");
        for (User user : users) {
            System.out.println("- " + user.firstName + " " + user.lastName);
        }
        
        // Test parameters
        String firstName = "JO";
        String lastName = "";
        String email = null;
        String phone = "";
        
        System.out.println("\nFilter criteria:");
        System.out.println("firstName: '" + firstName + "'");
        System.out.println("lastName: '" + lastName + "'");
        System.out.println("email: " + email);
        System.out.println("phone: '" + phone + "'");
        
        List<User> filtered = UserFilter.filterUsers(users, firstName, lastName, email, phone);
        
        System.out.println("\nFiltered results:");
        for (User user : filtered) {
            System.out.println("- " + user.firstName + " " + user.lastName);
        }
        
        System.out.println("\nExpected: 2 results (John and Jonathan)");
        System.out.println("Actual: " + filtered.size() + " results");
        
        assertEquals(2, filtered.size()); // John, Jonathan
        assertTrue(filtered.stream().anyMatch(user -> user.firstName.equals("John")));
        assertTrue(filtered.stream().anyMatch(user -> user.firstName.equals("Jonathan")));
    }

    private String getCharCodes(String str) {
        StringBuilder codes = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            codes.append((int)str.charAt(i)).append(" ");
        }
        return codes.toString();
    }
} 