package org.example;

public class LibraryItem {
    public Book book;
    public boolean isAvailable; // Om kopian är tillgänglig för lån

    public LibraryItem(Book book) {
        this.book = book;
        this.isAvailable = true;  // Vid skapande är kopian tillgänglig
    }
}
