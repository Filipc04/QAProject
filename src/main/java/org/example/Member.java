package org.example;
public abstract class Member implements IMember {
    protected int id;
    protected String firstName;
    protected String lastName;
    protected String personalNumber;
    protected int borrowedCount;
    protected int maxBorrowLimit;
    protected int violations;

    public Member(int id, String firstName, String lastName, String personalNumber, int maxBorrowLimit) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.personalNumber = personalNumber;
        this.borrowedCount = 0;
        this.maxBorrowLimit = maxBorrowLimit;
        this.violations = 0;
    }

    public boolean canBorrow() {
        return borrowedCount < maxBorrowLimit;
    }

    @Override
    public boolean borrowBook(Book book) {
        if (canBorrow() && book.isAvailable()) {
            borrowedCount++;
            book.decreaseAvailableCopies();
            return true;
        }
        return false;
    }

    @Override
    public boolean returnBook(Book book) {
        if (borrowedCount > 0) {
            borrowedCount--;
            book.increaseAvailableCopies();
            return true;
        }
        return false;
    }

    public void addViolation() {
        violations++;
    }

    public int getViolations() {
        return violations;
    }
}
