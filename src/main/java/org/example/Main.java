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
            System.out.println("\nMain Menu:");
            System.out.println("1. Register as a new member.");
            System.out.println("2. Login with your ID.");
            System.out.println("3. Quit.");
            System.out.println("Select (1-3):");
            int selection1= Integer.parseInt(scanner.nextLine());

            switch (selection1){
                case 1: {
                    // Register a new member
                    System.out.println("Enter your first name:");
                    String firstName = scanner.nextLine();
                    System.out.println("Enter your last name:");
                    String lastName = scanner.nextLine();
                    System.out.println("Enter your personal number:");
                    String personalNumber = scanner.nextLine();
                    System.out.println("Enter your level (1 = Undergraduate, 2 = Postgraduate, 3 = PhD, 4 = Teacher):");
                    int level = Integer.parseInt(scanner.nextLine());

                    Member newMember = new Member();
                    newMember.firstName = firstName;
                    newMember.lastName = lastName;
                    newMember.personalNumber = personalNumber;
                    newMember.level = level;

                    svc.registerMember(newMember);
                }
                break;

                case 2: {
                    System.out.println("Enter your user ID:");
                    String userId = scanner.nextLine();  // Declared once here

                    System.out.println("1. Lend item.");
                    System.out.println("2. Return item.");
                    System.out.println("3. Unsubscribe/Delete account.");
                    System.out.println("4. Suspend member.");
                    System.out.println("5. Quit");

                    System.out.println("Select (1-5):");
                    int selection2 = Integer.parseInt(scanner.nextLine());

                    switch (selection2) {
                        case 1: {
                            // Lend an item
                            System.out.println("Enter book ISBN:");
                            String bookId = scanner.nextLine();
                            svc.borrow(bookId, userId);  // Use existing userId
                        }
                        break;

                        case 2: {
                            // Return an item
                            System.out.println("Enter book ISBN:");
                            String bookId = scanner.nextLine();
                            svc.returnBook(bookId, userId);  // Use existing userId
                        }
                        break;

                        case 3: {
                            // Unsubscribe/Delete account
                            svc.deleteMember(userId);  // Use existing userId
                        }
                        break;

                        case 4: {
                            // Suspend a member
                            svc.suspendMember(userId);  // Use existing userId
                        }
                        break;

                        case 5: {
                            done = true;
                        }
                        break;
                    }
                }
                break;




                default:
                    System.out.println(String.format("%d is not a valid option.", selection1));
            }
            }
        }
    }
