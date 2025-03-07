package org.example;

import java.lang.reflect.Member;

public class DbLibraryStore implements ILibraryStore {

    @Override
    public void addBook(Book newBook) {
        // SQL code here
    }

    @Override
    public void addMember(Member newMember) {

    }

    @Override
    public void addMember(Member newMember) {
    }

    @Override
    public Book getBook(String id) {
        // SQL code here
        System.out.println("DbLibraryStore::getBook()");
        return new Book();
    }

    @Override
    public Member getMember(String id) {
        System.out.println("DbLibraryStore::getMember()");
        return new Member() {
            @Override
            public Class<?> getDeclaringClass() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public int getModifiers() {
                return 0;
            }

            @Override
            public boolean isSynthetic() {
                return false;
            }
        };
    }

    @Override
    public boolean isSuspendedMember(String id) {

        return false;
    }

    @Override
    public void removeMember(String id) {
    }

    @Override
    public void suspendMember(String id) {
    }
}
