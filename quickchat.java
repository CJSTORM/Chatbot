import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.concurrent.atomic.AtomicInteger;

public class QuickChatApp {

    // A simple flag to simulate a successful login
    private static boolean isLoggedIn = false;
    // Counter for message numbers (Num messages sent)
    private static AtomicInteger messageCounter = new AtomicInteger(1); 
    // List to hold all Message objects created during the session
    private static List<Message> messageLog = new ArrayList<>();

    public static void main(String[] args) {
        // Display welcome message
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.", "Welcome", JOptionPane.INFORMATION_MESSAGE);

        // Simulate a successful login
        loginUser();

        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(null, "Login failed. Exiting application.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        runApplication();
    }

    /**
     * Simple method to simulate user login.
     * The application only continues if this is successful.
     */
    private static void loginUser() {
        // In a real app, this would involve password validation, but here, 
        // we'll just check for a non-empty username/password for simplicity.
        String username = JOptionPane.showInputDialog(null, "Enter username:", "Login", JOptionPane.QUESTION_MESSAGE);
        String password = JOptionPane.showInputDialog(null, "Enter password:", "Login", JOptionPane.QUESTION_MESSAGE);

        if (username != null && !username.trim().isEmpty() && 
            password != null && !password.trim().isEmpty()) {
            isLoggedIn = true;
            JOptionPane.showMessageDialog(null, "Login Successful!", "Login Status", JOptionPane.INFORMATION_MESSAGE);
        } else {
            isLoggedIn = false;
        }
    }
    
    /**
     * The main application loop. Runs until the user selects 'Quit'.
     */
    private static void runApplication() {
        int choice = 0;
        
        while (choice != 3) {
            String menu = "Choose a feature:\n" +
                          "1) Send Messages\n" +
                          "2) Show recently sent messages (Coming Soon)\n" +
                          "3) Quit";

            try {
                String input = JOptionPane.showInputDialog(null, menu, "QuickChat Menu", JOptionPane.QUESTION_MESSAGE);
                if (input == null) {
                    choice = 3; // Treat pressing 'Cancel' or 'X' as Quit
                    continue;
                }
                choice = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid choice. Please enter 1, 2, or 3.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            switch (choice) {
                case 1:
                    sendMessagesFeature();
                    break;
                case 2:
                    // Show recently sent messages - still in development
                    JOptionPane.showMessageDialog(null, "Coming Soon.", "Feature Status", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 3:
                    // Quit option
                    JOptionPane.showMessageDialog(null, "Thank you for using QuickChat. Goodbye!", "Exit", JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Please choose 1, 2, or 3.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the logic for sending messages.
     * Prompts the user for the number of messages to enter.
     */
    private static void sendMessagesFeature() {
        int messageCountLimit = 0;
        try {
            String input = JOptionPane.showInputDialog(null, "How many messages do you wish to enter?", "Set Message Limit", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return;
            messageCountLimit = Integer.parseInt(input.trim());
            if (messageCountLimit <= 0) {
                 JOptionPane.showMessageDialog(null, "Please enter a positive number of messages.", "Error", JOptionPane.ERROR_MESSAGE);
                 return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number. Returning to main menu.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // The application should allow the user to enter only the set number of messages
        for (int i = 0; i < messageCountLimit; i++) {
            processSingleMessage();
        }

        // The total number of messages should be accumulated and displayed once all the messages have been sent.
        JOptionPane.showMessageDialog(null, 
            "All messages entered.\nTotal Messages *Sent* during this session: " + Message.returnTotalMessages(), 
            "Session Summary", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handles the creation, input, validation, and action for a single message.
     */
    private static void processSingleMessage() {
        int currentMessageNum = messageCounter.getAndIncrement();
        Message currentMessage = new Message(currentMessageNum);
        
        JOptionPane.showMessageDialog(null, "Entering message #" + currentMessageNum + ".", "New Message", JOptionPane.INFORMATION_MESSAGE);

        // --- 1. Recipient Cell Input and Validation ---
        String recipient = JOptionPane.showInputDialog(null, "Enter Recipient Cell Number (max 10 chars, include international code if possible):");
        if (recipient == null) {
            JOptionPane.showMessageDialog(null, "Message creation cancelled.", "Cancelled", JOptionPane.WARNING_MESSAGE);
            messageCounter.decrementAndGet();
            return;
        }
        currentMessage.setRecipientCell(recipient);
        
        if (currentMessage.checkRecipientCell() != 1) {
            JOptionPane.showMessageDialog(null, "Recipient cell number validation failed.", "Error", JOptionPane.ERROR_MESSAGE);
            messageCounter.decrementAndGet();
            return;
        }

        // --- 2. Message Content Input and Validation ---
        String messageContent = JOptionPane.showInputDialog(null, "Enter Message (max 250 characters):");
        if (messageContent == null) {
            JOptionPane.showMessageDialog(null, "Message creation cancelled.", "Cancelled", JOptionPane.WARNING_MESSAGE);
            messageCounter.decrementAndGet();
            return;
        }
        currentMessage.setMessageContent(messageContent);
        
        // Short message, which should not exceed 250 characters
        if (messageContent.length() > 250) {
            // Display error message if too long
            JOptionPane.showMessageDialog(null, "Please enter a message of less than 250 characters.", "Message Error", JOptionPane.ERROR_MESSAGE);
            messageCounter.decrementAndGet();
            return; 
        } else {
            // Message sent if it meets the requirement
            JOptionPane.showMessageDialog(null, "Message content validated.", "Validation Success", JOptionPane.INFORMATION_MESSAGE);
        }

        // --- 3. Create Message Hash ---
        currentMessage.createMessageHash();
        
        // --- 4. Choose Action (Send, Disregard, Store) ---
        String status = currentMessage.SentMessage();
        
        messageLog.add(currentMessage);

        // --- 5. Final Actions and Display ---
        if (status.equals("Stored")) {
            currentMessage.storeMessage();
        }

        // Display full details using JOptionPane after it has been sent
        JOptionPane.showMessageDialog(null, 
            currentMessage.getDisplayDetails(), 
            "Message Sent/Processed Details", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}