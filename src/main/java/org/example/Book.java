package org.example;
public class Book {
    private int id;
    private String title;
    private int isbn;
    private int totalCopies;
    private int availableCopies;

    public Book(int id, String title, int isbn, int totalCopies) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void decreaseAvailableCopies() {
        if (availableCopies > 0) {
            availableCopies--;
        }
    }

    public void increaseAvailableCopies() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }
}
