package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Create an instance of DbLibraryStore
        ILibraryStore store = new DbLibraryStore();
        LibraryService svc = new LibraryService(store);
        Scanner scanner = new Scanner(System.in);

        // Test adding a book
        Book newBook = new Book();
        newBook.ISBN = "1234567890129";
        newBook.title = "Abbes bok";
        newBook.author = "Å. Carl Abbe";
        newBook.year = 2025;

        store.addBook(newBook);

        // Verify the book was added
        Book retrievedBook = store.getBook("1234567890129");
        if (retrievedBook != null) {
            System.out.println("Retrieved Book: " + retrievedBook.title);
        } else {
            System.out.println("Book not found.");
        }
    }
}