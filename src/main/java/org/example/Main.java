package org.example;

import java.util.Scanner;
public class Main {

    public static void main(String[] args) {

        //ILibraryStore store = new FileLibraryStore("myfilename.txt");
        ILibraryStore store = new DbLibraryStore();
        LibraryService svc = new LibraryService(store);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Library System!");
        System.out.println("Enter your user id:");
        String userId = scanner.nextLine();

        boolean done = false;
        int selection = 0;
        while (!done) {
            System.out.println("\nMenu:");
            System.out.println("1. Lend item.");
            System.out.println("2. Return item.");
            // And some more menu choices.
            System.out.println("9. Quit.");
            System.out.println("Select (1-9):");
            selection = Integer.parseInt(scanner.nextLine());

            switch (selection) {
                case 1: {
                    System.out.println("Enter book id:");
                    String bookId = scanner.nextLine();
                    svc.borrow(bookId, userId);
                }
                break;

                case 2: {
                    // Return an item.
                }
                break;

                // More cases here!

                case 9: {
                    done = true;
                }
                break;

                default:
                    System.out.println(String.format("%d is not valid.", selection));
            }
        }

        System.out.println("Bye.");
    }
}
