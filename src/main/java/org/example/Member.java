package org.example;

public class Member {
    public String firstName;
    public String id;
    public static int level;  // 1 = undergraduate, 2 = postgraduate, 3 = PhD, 4 = teacher
    public static int borrowedBooksCount = 0; // Antal böcker medlemmen har lånat
    public int lateReturns = 0; // Räknare för förseningar
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