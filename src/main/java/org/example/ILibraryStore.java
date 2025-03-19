package org.example;

public interface ILibraryStore {
    void addBook(Book newBook);
    void addMember(Member newMember);
    Book getBook(String id);
    Member getMember(String id);
    boolean isSuspendedMember(String id);
    void suspendMember(String id);
    boolean borrowItem(String itemId, String memberId); // Add this method
    boolean returnItem(String itemId, String memberId); // Add this method
    int getSuspensionCount(String memberId); // Returns how many times a member was suspended
    void recordSuspension(String memberId); // Logs a suspension in the database
    void deleteMember(String memberId); // Deletes the member after multiple suspensions

}