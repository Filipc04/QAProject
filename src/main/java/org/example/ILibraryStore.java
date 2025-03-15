package org.example;

public interface ILibraryStore {
    void addBook(Book newBook);
    void addMember(Member newMember);
    Book getBook(String id);
    Member getMember(String id);
    boolean isSuspendedMember(String id);
    void removeMember(String id);
    void suspendMember(String id);
    boolean borrowItem(String itemId, String memberId); // Add this method
    boolean returnItem(String itemId, String memberId); // Add this method
}