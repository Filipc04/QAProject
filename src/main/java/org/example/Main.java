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
            System.out.println("2. Lend item.");
            System.out.println("3. Return item.");
            System.out.println("4. Unsubscribe/Delete account.");
            System.out.println("5. Suspend member.");
            System.out.println("9. Quit.");
            System.out.println("Select (1-9):");
            int selection = Integer.parseInt(scanner.nextLine());

            switch (selection) {
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
                    // Lend an item
                    System.out.println("Enter your user ID:");
                    String userId = scanner.nextLine();
                    System.out.println("Enter book ISBN:");

                    String bookId = scanner.nextLine();
                    if (svc.borrow(bookId, userId)) {
                        System.out.println("Boken lånad framgångsrikt!");
                    }
                }
                break;

                case 3: {
                    // Return an item
                    System.out.println("Enter your user ID:");
                    String userId = scanner.nextLine();
                    System.out.println("Enter book ISBN:");
                    String bookId = scanner.nextLine();
                    svc.returnBook(bookId, userId);


                case 4: {
                    // Unsubscribe/Delete account
                    System.out.println("Enter your user ID:");
                    String userId = scanner.nextLine();
                    svc.deleteMember(userId);
                }
                break;

                case 5: {
                    // Suspend a member
                    System.out.println("Enter the user ID to suspend:");
                    String userId = scanner.nextLine();
                    svc.suspendMember(userId);
                }
                break;

                    Member newMember = new Member(firstName, id, level) {
                        @Override
                        public Class<?> getDeclaringClass() {
                            return null;
                        }


                        @Override
                        public String getName() {
                            return null;
                        }

                        @Override
                        public int getModifiers() {
                            return 0;
                        }


                default:
                    System.out.println(String.format("%d is not a valid option.", selection));

                        @Override
                        public boolean isSynthetic() {
                            return false;
                        }
                    };
                    store.addMember(newMember);
                    System.out.println("Medlem registrerad!");
                }
               
                case 9 -> done = true;
                default -> System.out.println(selection + " är inte ett giltigt val.");

            }
        }

        scanner.close();
    }
}