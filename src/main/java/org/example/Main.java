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
                    String userId = scanner.nextLine();

                    Member member = store.getMember(userId);
                    if (member == null) {
                        System.out.println("Error: No member found with this ID. Please try again.");
                        break;
                    }

                    boolean loggedIn = true;
                    while (loggedIn) {
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
                                boolean lending = true;
                                while (lending) {
                                    if (!svc.canBorrowMoreItems(userId)) { // ✅ Check borrow limit before allowing another book
                                        System.out.println("You have reached your borrowing limit. Returning to the user menu.");
                                        break; // ✅ Exit the loop if limit is reached
                                    }

                                    System.out.println("Enter book ISBN:");
                                    String bookId = scanner.nextLine();

                                    boolean success = svc.borrow(bookId, userId); // ✅ Ensure book was successfully borrowed

                                    if (success) { // ✅ Only ask for another book if borrowing succeeded
                                        System.out.println("\nDo you want to:");
                                        System.out.println("1. Lend another book.");
                                        System.out.println("2. Return to the user menu.");
                                        System.out.print("Select (1-2): ");

                                        int choice = 0;
                                        boolean validChoice = false;
                                        while (!validChoice) {
                                            try {
                                                choice = Integer.parseInt(scanner.nextLine().trim());
                                                if (choice == 1 || choice == 2) {
                                                    validChoice = true;
                                                } else {
                                                    System.out.println("Please enter 1 to lend another book or 2 to return to the user menu.");
                                                }
                                            } catch (NumberFormatException e) {
                                                System.out.println("Invalid input! Please enter 1 or 2.");
                                            }
                                        }

                                        if (choice == 2) {
                                            lending = false; // ✅ Exit loop and return to user menu
                                        }
                                    } else {
                                        System.out.println("Book could not be borrowed. Returning to the user menu.");
                                        lending = false; // ✅ Exit loop if borrowing fails
                                    }
                                }

                            }
                            break;

                            case 2: {
                                // Return an item
                                boolean returning = true;
                                while (returning) {
                                    System.out.println("Enter book ISBN:");
                                    String bookId = scanner.nextLine();
                                    svc.returnBook(bookId, userId);
                                    svc.checkLateReturnsAndSuspend(userId);

                                    System.out.println("\nDo you want to:");
                                    System.out.println("1. Return another book.");
                                    System.out.println("2. Return to the user menu.");
                                    System.out.print("Select (1-2): ");

                                    int choice = 0;
                                    boolean validChoice = false;
                                    while (!validChoice) {
                                        try {
                                            choice = Integer.parseInt(scanner.nextLine().trim());
                                            if (choice == 1 || choice == 2) {
                                                validChoice = true;
                                            } else {
                                                System.out.println("Please enter 1 to return another book or 2 to return to the user menu.");
                                            }
                                        } catch (NumberFormatException e) {
                                            System.out.println("Invalid input! Please enter 1 or 2.");
                                        }
                                    }

                                    if (choice == 2) {
                                        returning = false;
                                    }
                                }
                            }
                            break;

                            case 3: {
                                // Unsubscribe/Delete account
                                svc.deleteMember(userId);
                                loggedIn = false;
                            }
                            break;

                            case 4: {
                                System.out.println("Enter the number of days to suspend the member:");
                                int days = 0;
                                boolean validDays = false;
                                while (!validDays) {
                                    try {
                                        days = Integer.parseInt(scanner.nextLine().trim());
                                        if (days > 0) {
                                            validDays = true;
                                        } else {
                                            System.out.println("Please enter a valid number of days (must be at least 1).");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("Invalid input! Please enter a valid number.");
                                    }
                                }

                                svc.suspendMember(userId, days); // ✅ Now passes the chosen duration
                            }
                            break;

                            case 5: {
                                loggedIn = false;
                            }
                            break;
                        }
                    }
                }
                break;

                case 3: {
                    done = true;
                }
                break;
            }
        }
    }
}
