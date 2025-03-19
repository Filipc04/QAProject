package org.example;

import java.util.UUID;
import java.sql.Date;


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
        store.deleteMember(memberId);
        System.out.println("Member deleted successfully.");
        return true;
    }

    public boolean searchMember(String memberId) {
        Member member = store.getMember(memberId);
        if (member != null) {
            System.out.println("Member found");
            System.out.println("Id:" + memberId);
            System.out.println("Name" + member.firstName + " " + member.lastName);
            System.out.println("Suspended until:" + (member.suspendedUntil != null ? member.suspendedUntil : "Not suspended"));
            return true;
        } else {
            System.out.println("No member found with Id:" + memberId);
        }
        return false;
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
        return store.getBorrowedItemsCount(memberId); // ✅ Calls database method
    }

    public boolean memberExists(String userId) {
        return store.getMember(userId) != null; // Check if user exists in the database
    }

    public void checkLateReturnsAndSuspend(String memberId) {
        Member member = store.getMember(memberId);
        if (member == null) return;

        member.lateReturns++;

        System.out.println("Late returns for member " + memberId + ": " + member.lateReturns); // ✅ Debugging line

        if (member.lateReturns >= 2) {
            member.suspendedUntil = new Date(System.currentTimeMillis() + (15L * 24 * 60 * 60 * 1000));
            member.lateReturns = 0; // Reset late return count

            int suspensions = store.getSuspensionCount(memberId);
            System.out.println("Current suspensions: " + suspensions); // ✅ Debugging line

            store.recordSuspension(memberId);

            if (suspensions + 1 >= 2) {
                store.deleteMember(memberId);
                System.out.println("Member " + memberId + " has been deleted due to repeated suspensions."); // ✅ Debugging line
            } else {
                System.out.println("Member " + memberId + " has been suspended for 15 days due to late returns.");
            }
        }
    }

}