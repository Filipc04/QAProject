package org.example;

import java.sql.*;

public class DbLibraryStore implements ILibraryStore {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "QAProject123"; // Replace with your MySQL password

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
        String sql = "INSERT INTO Members (member_id, first_name, last_name, personal_number, level) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newMember.id);
            stmt.setString(2, newMember.firstName);
            stmt.setString(3, newMember.lastName);
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
    public Member getMemberByPersonalNumber(String personalNumber) {
        String sql = "SELECT * FROM Members WHERE personal_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personalNumber);
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
                Date suspendedUntil = rs.getDate("suspended_until");
                return suspendedUntil != null && suspendedUntil.after(new Date(System.currentTimeMillis()));
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
    public boolean borrowItem(String itemId, String memberId) {
        String sql = "INSERT INTO Borrowings (member_id, item_id, borrowed_date) VALUES (?, ?, CURDATE())";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            stmt.setString(2, itemId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean returnItem(String itemId, String memberId) {
        String sql = "UPDATE Borrowings SET returned_date = CURDATE() WHERE member_id = ? AND item_id = ? AND returned_date IS NULL";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            stmt.setString(2, itemId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int getBorrowedItemsCount(String memberId) {
        String sql = "SELECT COUNT(*) AS borrowed_count FROM Borrowings WHERE member_id = ? AND returned_date IS NULL";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("borrowed_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean isBookAvailable(String bookId) {
        String sql = "SELECT available FROM LibraryItems WHERE ISBN = ? AND available = TRUE";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Returns true if at least one available copy exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}