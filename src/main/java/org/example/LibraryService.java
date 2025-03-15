package org.example;

import java.util.UUID;

public class LibraryService {

    ILibraryStore store;

    public LibraryService(ILibraryStore store) {
        this.store = store;
    }

    public boolean borrow(String bookId, String memberId) {
        // Check if the member is suspended
        if (store.isSuspendedMember(memberId)) {
            System.out.println("This member is suspended and cannot borrow books.");
            return false;
        }

        // Check if the member has reached their borrowing limit
        if (!canBorrowMoreItems(memberId)) {
            System.out.println("This member has reached their borrowing limit.");
            return false;
        }

        // Borrow the book
        return store.borrowItem(bookId, memberId);
    }

    public boolean returnBook(String isbn, String memberId) {
        // Return the book
        boolean success = store.returnItem(isbn, memberId);

        if (success) {
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Failed to return the book.");
        }

        return success;
    }

    public boolean registerMember(Member newMember) {
        // Generate a unique ID for the member (e.g., using UUID or a sequence)
        newMember.id = UUID.randomUUID().toString().substring(0, 4); // Example: 4-character ID

        // Add the member to the database
        store.addMember(newMember);
        System.out.println("Registration successful! Your user ID is: " + newMember.id);
        return true;
    }

    public boolean deleteMember(String memberId) {
        // Delete the member
        store.removeMember(memberId);
        System.out.println("Member deleted successfully.");
        return true;
    }

    public boolean suspendMember(String memberId) {
        // Suspend the member
        store.suspendMember(memberId);
        System.out.println("Member suspended successfully.");
        return true;
    }

    private boolean canBorrowMoreItems(String memberId) {
        Member member = store.getMember(memberId);
        if (member == null) {
            return false; // Member not found
        }

        int maxItems;
        switch (member.level) {
            case 1: // Undergraduate
                maxItems = 3;
                break;
            case 2: // Postgraduate
                maxItems = 5;
                break;
            case 3: // PhD
                maxItems = 7;
                break;
            case 4: // Teacher
                maxItems = 10;
                break;
            default:
                return false; // Invalid level
        }

        int currentItems = getBorrowedItemsCount(memberId);
        return currentItems < maxItems;
    }

    private int getBorrowedItemsCount(String memberId) {
        // Implement logic to count the number of items borrowed by the member
        // For now, return 0 (you can implement this later)
        return 0;
    }
}