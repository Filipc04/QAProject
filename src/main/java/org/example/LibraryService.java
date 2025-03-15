package org.example;

import java.sql.Date;
import java.util.UUID;

public class LibraryService {

    private ILibraryStore store;

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

        // Check if the book is available
        if (!store.isBookAvailable(bookId)) {
            System.out.println("The book is not available.");
            return false;
        }

        // Borrow the book
        return store.borrowItem(bookId, memberId);
      
    private int getMaxBooksForLevel(int level) {
        switch (level) {
            case 1: return 3;
            case 2: return 5;
            case 3: return 7;
            case 4: return 10;
            default: return 0;
        }
    }


    public boolean returnBook(String bookId, String memberId) {
        // Return the book
        boolean success = store.returnItem(bookId, memberId);

        if (success) {
            System.out.println("Book returned successfully.");

            // Check for late returns and handle suspensions
            handleLateReturns(memberId);
        } else {
            System.out.println("Failed to return the book.");
        }

        return success;
    }

    public boolean registerMember(Member newMember) {
        // Check if the personal number is already registered
        Member existingMember = store.getMemberByPersonalNumber(newMember.personalNumber);
        if (existingMember != null) {
            if (existingMember.suspendedUntil != null && existingMember.suspendedUntil.after(new Date(System.currentTimeMillis()))) {
                System.out.println("Registration not allowed: This member has violated regulations.");
                return false;
            } else {
                System.out.println("Member already registered.");
                return true;
            }
        }

        // Generate a unique ID for the member
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
        // Suspend the member for 15 days
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

        int currentItems = store.getBorrowedItemsCount(memberId);
        return currentItems < maxItems;
    }

    private void handleLateReturns(String memberId) {
        Member member = store.getMember(memberId);
        if (member == null) {
            return; // Member not found
        }

        // Check if the member has delayed returning books more than twice
        if (member.lateReturns > 2) {
            // Suspend the member for 15 days
            suspendMember(memberId);

            // Check if the member has been suspended more than twice
            if (member.lateReturns > 4) {
                // Delete the account
                deleteMember(memberId);
            }
        }
    }
}