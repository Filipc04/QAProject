package org.example;

public interface ILibraryStore {
    void addBook(Book newBook);
    void addMember(Member newMember);
    Book getBook(String id);
    Member getMember(String id);
    boolean isSuspendedMember(String id);
    void suspendMember(String id);
    boolean borrowItem(String itemId, String memberId);
    boolean returnItem(String itemId, String memberId);
    int getSuspensionCount(String memberId); //returns how many times a member was suspended
    void recordSuspension(String memberId); //logs a suspension in the database
    void deleteMember(String memberId); //delete after multiple suspensions
    int getBorrowedItemsCount(String memberId);

}