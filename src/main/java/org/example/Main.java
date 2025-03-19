package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ILibraryStore store = new DbLibraryStore();
        LibraryService svc = new LibraryService(store);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Library System!");

        boolean done = false;
        while (!done) {
            int selection1 = 0;
            boolean validInput = false;
            while (!validInput) {
                System.out.println("\nMain Menu:");
                System.out.println("1. Register as a new member.");
                System.out.println("2. Login with your ID.");
                System.out.println("3. Quit.");
                System.out.print("Select (1-3): ");
                try {
                    selection1 = Integer.parseInt(scanner.nextLine().trim());
                    if (selection1 >= 1 && selection1 <= 3) {
                        validInput = true;
                    } else {
                        System.out.println("Please input a number between 1 and 3!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a valid number between 1 and 3.");
                }
            }

            switch (selection1) {
                case 1: {
                    // Register a new member
                    System.out.println("Enter your first name:");
                    String firstName = scanner.nextLine();
                    System.out.println("Enter your last name:");
                    String lastName = scanner.nextLine();
                    System.out.println("Enter your personal number:");
                    String personalNumber = scanner.nextLine();
                    System.out.println("Enter your level (1 = Undergraduate, 2 = Postgraduate, 3 = PhD, 4 = Teacher):");

                    // Validate level input
                    int level = 0;
                    boolean validLevel = false;
                    while (!validLevel) {
                        try {
                            level = Integer.parseInt(scanner.nextLine().trim());
                            if (level >= 1 && level <= 4) {
                                validLevel = true;
                            } else {
                                System.out.println("Please input a valid level between 1 and 4.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input! Please enter a number between 1 and 4.");
                        }
                    }

                    Member newMember = new Member();
                    newMember.firstName = firstName;
                    newMember.lastName = lastName;
                    newMember.personalNumber = personalNumber;
                    newMember.level = level;

                    svc.registerMember(newMember);
                }
                break;

                case 2: {
                    // Login process
                    System.out.println("Enter your user ID:");
                    String userId = scanner.nextLine(); // User stays logged in

                    Member member = store.getMember(userId);
                    if (member == null) {
                        System.out.println("Error: No member found with this ID. Please try again.");
                        break;
                    }

                    boolean loggedIn = true;
                    while (loggedIn) {  // Keep user logged in until they choose to log out
                        int selection2 = 0;
                        boolean validSelection = false;
                        while (!validSelection) {
                            System.out.println("\nUser Menu:");
                            System.out.println("1. Lend item.");
                            System.out.println("2. Return item.");
                            System.out.println("3. Unsubscribe/Delete account.");
                            System.out.println("4. Suspend member.");
                            System.out.println("5. Logout and return to Main Menu.");

                            System.out.print("Select (1-5): ");
                            try {
                                selection2 = Integer.parseInt(scanner.nextLine().trim());
                                if (selection2 >= 1 && selection2 <= 5) {
                                    validSelection = true;
                                } else {
                                    System.out.println("Please input a number between 1 and 5!");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input! Please enter a valid number between 1 and 5.");
                            }
                        }

                        switch (selection2) {
                            case 1: {
                                // Lend an item
                                System.out.println("Enter book ISBN:");
                                String bookId = scanner.nextLine();
                                svc.borrow(bookId, userId);
                            }
                            break;

                            case 2: {
                                // Return an item
                                System.out.println("Enter book ISBN:");
                                String bookId = scanner.nextLine();
                                svc.returnBook(bookId, userId);
                            }
                            break;

                            case 3: {
                                // Unsubscribe/Delete account
                                svc.deleteMember(userId);
                                loggedIn = false;  // Log out after deleting account
                            }
                            break;

                            case 4: {
                                // Suspend a member
                                svc.suspendMember(userId);
                            }
                            break;

                            case 5: {
                                loggedIn = false;  // Log out and return to Main Menu
                            }
                            break;
                        }
                    }
                }
                break;

                case 3: {
                    done = true;  // Quit the program
                    System.out.println("Goodbye!");
                }
                break;
            }
        }
    }
}
