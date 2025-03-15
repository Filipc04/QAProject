package org.example;

import org.example.Member;


public interface ILibraryStore {
    public void addBook(Book newBook);
    public void addMember(Member newMember);
    public Book getBook(String id);
    public Member getMember(String id);
    public boolean isSuspendedMember(String id);
    public void removeMember(String id);
    public void suspendMember(String id);
}
