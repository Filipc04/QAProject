package org.example;

import java.util.*;
import java.sql.*;

public class DbLibraryStore implements ILibraryStore {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "QAProject123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addBook(Book newBook) {
        String sql = "INSERT INTO Books (ISBN, title, author, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newBook.ISBN);
            stmt.setString(2, newBook.title);
            stmt.setString(3, newBook.author);
            stmt.setInt(4, newBook.year);
            stmt.executeUpdate();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Book getBook(String id) {
        String sql = "SELECT * FROM Books WHERE ISBN = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Book book = new Book();
                book.ISBN = rs.getString("ISBN");
                book.title = rs.getString("title");
                book.author = rs.getString("author");
                book.year = rs.getInt("year");
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addMember(Member newMember) {
    }

    @Override
    public Member getMember(String id) {
        System.out.println("DbLibraryStore::getMember()");
        return new Member() {
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

            @Override
            public boolean isSynthetic() {
                return false;
            }
        };
    }

    @Override
    public boolean isSuspendedMember(String id) {
        return false;
    }

    @Override
    public void removeMember(String id) {
    }

    @Override
    public void suspendMember(String id) {
    }
}