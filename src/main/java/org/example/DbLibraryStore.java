package org.example;

import java.sql.*;
import java.util.*;
import java.sql.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DbLibraryStore implements ILibraryStore {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "QAProject123";
    private static final Logger logger = LogManager.getLogger(DbLibraryStore.class);

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found.", e);
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

            logger.info("Book '{}' by {} added successfully.", newBook.title, newBook.author);
        } catch (SQLException e) {
            logger.error("Failed to add book '{}'", newBook.title, e);
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

                logger.info("Book found: ISBN={}, Title={}", book.ISBN, book.title);
                return book;
            } else {
                logger.warn("No book found with ISBN: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Database error while retrieving book with ISBN: {}", id, e);
        }
        return null;
    }

    @Override
    public Member getMember(String id) {
        return null;
    }

    @Override
    public boolean isSuspendedMember(String id) {
        return false;
    }

    @Override
    public void suspendMember(String id) {

    }

    @Override
    public void addMember(Member newMember) {
        String sql = "INSERT INTO Members (first_name, last_name, member_id, personal_number, level) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            newMember.id = String.format("%04d", new Random().nextInt(10000));
            stmt.setString(1, newMember.firstName);
            stmt.setString(2, newMember.lastName);
            stmt.setString(3, newMember.id);
            stmt.setString(4, newMember.personalNumber);
            stmt.setInt(5, newMember.level);

            stmt.executeUpdate();
            logger.info("Member '{}' {} added with ID {}", newMember.firstName, newMember.lastName, newMember.id);
        } catch (SQLException e) {
            logger.error("Failed to add member '{} {}'", newMember.firstName, newMember.lastName, e);
        }
    }

    @Override
    public void deleteMember(String memberId) {
        String deleteBorrowingsSQL = "DELETE FROM Borrowings WHERE member_id = ?";
        String deleteMemberSQL = "DELETE FROM Members WHERE member_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt1 = conn.prepareStatement(deleteBorrowingsSQL)) {
                stmt1.setString(1, memberId);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(deleteMemberSQL)) {
                stmt2.setString(1, memberId);
                stmt2.executeUpdate();
            }

            conn.commit();
            logger.info("Member {} has been deleted successfully.", memberId);
        } catch (SQLException e) {
            logger.error("Failed to delete member {}", memberId, e);
        }
    }

    @Override
    public int getBorrowedItemsCount(String memberId) {
        return 0;
    }

    @Override
    public boolean borrowItem(String isbn, String memberId) {
        String checkBookSql = "SELECT * FROM Books WHERE ISBN = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement checkBookStmt = conn.prepareStatement(checkBookSql)) {

            checkBookStmt.setInt(1, Integer.parseInt(isbn));
            ResultSet bookRs = checkBookStmt.executeQuery();

            if (!bookRs.next()) {
                logger.warn("Attempt to borrow non-existent book with ISBN: {}", isbn);
                return false;
            }

            String checkItemSql = "SELECT * FROM LibraryItems WHERE ISBN = ? AND available = TRUE LIMIT 1";
            try (PreparedStatement checkItemStmt = conn.prepareStatement(checkItemSql)) {
                checkItemStmt.setInt(1, Integer.parseInt(isbn));
                ResultSet itemRs = checkItemStmt.executeQuery();

                if (!itemRs.next()) {
                    logger.warn("No available copies of ISBN: {} for borrowing.", isbn);
                    return false;
                }

                int itemId = itemRs.getInt("item_id");

                String borrowSql = "INSERT INTO Borrowings (member_id, item_id, borrowed_date) VALUES (?, ?, CURDATE())";
                try (PreparedStatement borrowStmt = conn.prepareStatement(borrowSql)) {
                    borrowStmt.setString(1, memberId);
                    borrowStmt.setInt(2, itemId);
                    borrowStmt.executeUpdate();
                }

                String updateSql = "UPDATE LibraryItems SET available = FALSE WHERE item_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, itemId);
                    updateStmt.executeUpdate();
                }

                logger.info("Member {} borrowed book with ISBN: {}", memberId, isbn);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error processing book borrowing (ISBN: {}, MemberID: {})", isbn, memberId, e);
            return false;
        }
    }

    @Override
    public boolean returnItem(String itemId, String memberId) {
        return false;
    }

    @Override
    public int getSuspensionCount(String memberId) {
        String sql = "SELECT COUNT(*) FROM suspensions WHERE member_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                logger.info("Member {} has {} suspensions.", memberId, count);
                return count;
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch suspension count for member {}", memberId, e);
        }
        return 0;
    }

    @Override
    public void recordSuspension(String memberId) {
        String sql = "INSERT INTO suspensions (member_id, suspension_start, suspension_end) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, memberId);
            stmt.setDate(2, new Date(System.currentTimeMillis())); // Suspension starts now
            stmt.setDate(3, new Date(System.currentTimeMillis() + (15L * 24 * 60 * 60 * 1000))); // Ends in 15 days
            stmt.executeUpdate();

            logger.info("Recorded suspension for member {} from today for 15 days.", memberId);
        } catch (SQLException e) {
            logger.error("Failed to record suspension for member {}", memberId, e);
        }
    }
}
