package org.example;

import java.sql.Date;

public class Member {
    public String id; // Unique identifier for the member
    public String firstName; // First name of the member
    public String lastName; // Last name of the member
    public String personalNumber; // Unique personal number of the member
    public int level; // 1 = Undergraduate, 2 = Postgraduate, 3 = PhD, 4 = Teacher
    public Date suspendedUntil; // Date until which the member is suspended (null if not suspended)
    public int borrowedBooksCount = 0; // Number of books the member has borrowed
    public int lateReturns = 0; // Counter for late returns
    public static boolean suspended = false; // Om medlemmen är suspenderad

    public Member(String firstName, String id, int level) {
    }

    public Member() {

    }


    public abstract Class<?> getDeclaringClass();

    public abstract String getName();

    public abstract int getModifiers();

    public abstract boolean isSynthetic();

}