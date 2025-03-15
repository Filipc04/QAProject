package org.example;

public class LibraryService {

    ILibraryStore store;

    public LibraryService(ILibraryStore store) {
        this.store = store;
    }

    public boolean borrow(String isbn, String memberId) {
        Member memberInfo = store.getMember(memberId);
        Book book = store.getBook(isbn);

        if (memberInfo == null) {
            System.out.println("Medlemmen hittades inte.");
            return false;
        }

        if (book == null) {
            System.out.println("Boken hittades inte.");
            return false;
        }

        if (memberInfo.suspended) {
            System.out.println("Medlemmen är suspenderad och kan inte låna böcker.");
            return false;
        }

        int maxBooks = getMaxBooksForLevel(memberInfo.level);
        if (memberInfo.borrowedBooksCount >= maxBooks) {
            System.out.println("Medlemmen har redan lånat maximalt antal böcker.");
            return false;
        }

        if (book.availableCopies <= 0) {
            System.out.println("Denna bok är inte tillgänglig för lån just nu.");
            return false;
        }

        memberInfo.borrowedBooksCount++;
        book.availableCopies--;
        System.out.println("Boken lånad framgångsrikt!");
        return true;
    }

    private int getMaxBooksForLevel(int level) {
        switch (level) {
            case 1: return 3;
            case 2: return 5;
            case 3: return 7;
            case 4: return 10;
            default: return 0;
        }
    }

    public boolean returnBook(String isbn, String memberId) {
        Member member = store.getMember(memberId);
        Book book = store.getBook(isbn);

        if (member == null) {
            System.out.println("Medlemmen hittades inte.");
            return false;
        }

        if (book == null) {
            System.out.println("Boken hittades inte.");
            return false;
        }

        if (member.borrowedBooksCount > 0) {
            member.borrowedBooksCount--;
            book.availableCopies++;
            System.out.println("Boken returnerades framgångsrikt.");
            return true;
        } else {
            System.out.println("Medlemmen har inga böcker att returnera.");
            return false;
        }
    }

    public void checkLateReturns(Member member) {
        if (member.lateReturns >= 2) {
            member.suspended = true;
            System.out.println("Medlemmen har blivit suspenderad i 15 dagar.");
        }
    }
}
