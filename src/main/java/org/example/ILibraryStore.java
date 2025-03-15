package org.example;

import org.example.Member;

public interface ILibraryStore {
    void addBook(Book newBook);
    void addMember(Member newMember);
    Book getBook(String id);
    Member getMember(String id);
    Member getMemberByPersonalNumber(String personalNumber);
    boolean isSuspendedMember(String id);
    void removeMember(String id);
    void suspendMember(String id);
    boolean borrowItem(String itemId, String memberId);
    boolean returnItem(String itemId, String memberId);
    int getBorrowedItemsCount(String memberId);
    boolean isBookAvailable(String bookId);
}