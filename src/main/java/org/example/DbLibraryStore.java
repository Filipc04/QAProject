package org.example;

import java.sql.*;
import java.util.*;
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
        String sql = "INSERT INTO Members (first_name, last_name, member_id, personal_number, level) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Generate a 4-digit numeric ID for the member
            newMember.id = String.format("%04d", new Random().nextInt(10000));

            // Set parameters for the SQL statement
            stmt.setString(1, newMember.firstName);
            stmt.setString(2, newMember.lastName);
            stmt.setString(3, newMember.id);
            stmt.setString(4, newMember.personalNumber);
            stmt.setInt(5, newMember.level);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Member getMember(String id) {
        String sql = "SELECT * FROM Members WHERE member_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.id = rs.getString("member_id");
                member.firstName = rs.getString("first_name");
                member.lastName = rs.getString("last_name");
                member.personalNumber = rs.getString("personal_number");
                member.level = rs.getInt("level");
                member.suspendedUntil = rs.getDate("suspended_until");
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isSuspendedMember(String id) {
        String sql = "SELECT suspended_until FROM Members WHERE member_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                java.sql.Date suspendedUntil = rs.getDate("suspended_until");
                return suspendedUntil != null && suspendedUntil.after(new java.sql.Date(System.currentTimeMillis()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void removeMember(String id) {
        String sql = "DELETE FROM Members WHERE member_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void suspendMember(String id) {
        String sql = "UPDATE Members SET suspended_until = DATE_ADD(CURDATE(), INTERVAL 15 DAY) WHERE member_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean borrowItem(String isbn, String memberId) {
        // Step 1: Check if the book exists
        String checkBookSql = "SELECT * FROM Books WHERE ISBN = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement checkBookStmt = conn.prepareStatement(checkBookSql)) {
            checkBookStmt.setInt(1, Integer.parseInt(isbn)); // ISBN is now an integer
            ResultSet bookRs = checkBookStmt.executeQuery();

            if (!bookRs.next()) {
                System.out.println("Book with ISBN " + isbn + " does not exist.");
                return false;
            }

            // Step 2: Check if there is an available item
            String checkItemSql = "SELECT * FROM LibraryItems WHERE ISBN = ? AND available = TRUE LIMIT 1";
            try (PreparedStatement checkItemStmt = conn.prepareStatement(checkItemSql)) {
                checkItemStmt.setInt(1, Integer.parseInt(isbn));
                ResultSet itemRs = checkItemStmt.executeQuery();

                if (!itemRs.next()) {
                    System.out.println("No available items for ISBN " + isbn + ".");
                    return false;
                }

                int itemId = itemRs.getInt("item_id");

                // Step 3: Insert into Borrowings table
                String borrowSql = "INSERT INTO Borrowings (member_id, item_id, borrowed_date) VALUES (?, ?, CURDATE())";
                try (PreparedStatement borrowStmt = conn.prepareStatement(borrowSql)) {
                    borrowStmt.setString(1, memberId);
                    borrowStmt.setInt(2, itemId);
                    borrowStmt.executeUpdate();
                }

                // Step 4: Mark the item as unavailable
                String updateSql = "UPDATE LibraryItems SET available = FALSE WHERE item_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, itemId);
                    updateStmt.executeUpdate();
                }

                System.out.println("Book borrowed successfully!");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean returnItem(String isbn, String memberId) {
        // Step 1: Find the item_id for the given ISBN
        String findItemSql = "SELECT item_id FROM LibraryItems WHERE ISBN = ? LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement findItemStmt = conn.prepareStatement(findItemSql)) {
            findItemStmt.setInt(1, Integer.parseInt(isbn)); // ISBN is now an integer
            ResultSet rs = findItemStmt.executeQuery();

            if (rs.next()) {
                int itemId = rs.getInt("item_id");

                // Step 2: Update the Borrowings table
                String returnSql = "UPDATE Borrowings SET returned_date = CURDATE() " +
                        "WHERE member_id = ? AND item_id = ? AND returned_date IS NULL";
                try (PreparedStatement returnStmt = conn.prepareStatement(returnSql)) {
                    returnStmt.setString(1, memberId);
                    returnStmt.setInt(2, itemId);
                    int rowsUpdated = returnStmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        // Step 3: Mark the item as available
                        String updateSql = "UPDATE LibraryItems SET available = TRUE WHERE item_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, itemId);
                            updateStmt.executeUpdate();
                        }

                        return true;
                    } else {
                        System.out.println("No matching borrowing record found.");
                        return false;
                    }
                }
            } else {
                System.out.println("No items found for the given ISBN.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Member searchMemberById(String memberId) {
        String sql = "SELECT * FROM Members WHERE member_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.id = rs.getString("member_id");
                member.firstName = rs.getString("first_name");
                member.lastName = rs.getString("last_name");
                member.personalNumber = rs.getString("personal_number");
                member.level = rs.getInt("level");
                member.suspendedUntil = rs.getDate("suspended_until");
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Returnera null om medlemmen inte hittas
    }

}