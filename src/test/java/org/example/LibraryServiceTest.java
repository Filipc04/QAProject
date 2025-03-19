package org.example;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LibraryServiceTest {

    private LibraryService libraryService;

    @Mock
    private ILibraryStore mockStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        libraryService = new LibraryService(mockStore);
    }

    //Member Registration
    @Test
    void registeringNewMemberShouldGenerateUniqueIdAndStoreMember() {
        Member newMember = new Member();
        doNothing().when(mockStore).addMember(any(Member.class));

        boolean result = libraryService.registerMember(newMember);

        assertTrue(result, "New member should be registered successfully.");
        verify(mockStore).addMember(newMember);
    }

    //Borrowing Books
    @Test
    void borrowingBookShouldFailIfMemberIsSuspended() {
        when(mockStore.isSuspendedMember("1234")).thenReturn(true);

        boolean result = libraryService.borrow("678901", "1234");

        assertFalse(result, "Suspended member should not be able to borrow books.");
        verify(mockStore, never()).borrowItem(anyString(), anyString());
    }

    @Test
    void borrowingBookShouldFailIfMemberReachesBorrowLimit() {
        when(mockStore.isSuspendedMember("1234")).thenReturn(false);
        when(mockStore.getBorrowedItemsCount("1234")).thenReturn(3);

        boolean result = libraryService.borrow("678901", "1234");

        assertFalse(result, "Member should not be able to borrow more books than their limit.");
        verify(mockStore, never()).borrowItem(anyString(), anyString());
    }

    @Test
    void borrowingNonExistentBookShouldFail() {
        when(mockStore.isSuspendedMember("1234")).thenReturn(false);
        Member testMember = new Member();
        testMember.id = "1234";
        testMember.level = 1;
        when(mockStore.getMember("1234")).thenReturn(testMember);
        when(mockStore.getBorrowedItemsCount("1234")).thenReturn(0);
        when(mockStore.borrowItem("999999", "1234")).thenReturn(false);

        boolean result = libraryService.borrow("999999", "1234");

        assertFalse(result, "Borrowing a non-existent book should fail.");
        verify(mockStore).borrowItem("999999", "1234");
    }

    //Returning Books
    @Test
    void returningBookShouldSucceedIfBookWasBorrowed() {
        when(mockStore.returnItem("678901", "1234")).thenReturn(true);

        boolean result = libraryService.returnBook("678901", "1234");

        assertTrue(result, "Book should be returned successfully.");
        verify(mockStore).returnItem("678901", "1234");
    }

    @Test
    void returningBookShouldFailIfBookWasNotBorrowed() {
        when(mockStore.returnItem("678901", "1234")).thenReturn(false);

        boolean result = libraryService.returnBook("678901", "1234");

        assertFalse(result, "Returning a book should fail if the book was not borrowed.");
    }

    //Searching Members
    @Test
    void searchingForExistingMemberShouldReturnTrue() {
        Member testMember = new Member();
        testMember.id = "1234";
        testMember.firstName = "John";
        testMember.lastName = "Doe";

        when(mockStore.getMember("1234")).thenReturn(testMember);

        boolean exists = libraryService.searchMember("1234");

        assertTrue(exists, "Searching for an existing member should return true.");
    }

    @Test
    void searchingForNonExistingMemberShouldReturnFalse() {
        when(mockStore.getMember("9999")).thenReturn(null);

        boolean exists = libraryService.searchMember("9999");

        assertFalse(exists, "Searching for a non-existing member should return false.");
    }

    //Member Suspension and Deletion
    @Test
    void suspendingMemberShouldCallStoreSuspendMethod() {
        doNothing().when(mockStore).suspendMember("1234");

        boolean result = libraryService.suspendMember("1234");

        assertTrue(result, "Suspending a member should call the suspend method.");
        verify(mockStore).suspendMember("1234");
    }

    @Test
    void memberShouldBeDeletedIfTheyReachSuspensionLimit() {
        Member testMember = new Member();
        testMember.id = "5678";
        testMember.lateReturns = 2;

        when(mockStore.getMember("5678")).thenReturn(testMember);
        when(mockStore.getSuspensionCount("5678")).thenReturn(1);

        libraryService.checkLateReturnsAndSuspend("5678");

        verify(mockStore).deleteMember("5678");
    }

    //Borrowing Limits Based on role
    @Test
    void undergraduateMemberShouldBeAbleToBorrowWithinLimit() {
        Member member = new Member();
        member.id = "1001";
        member.level = 1;

        when(mockStore.getMember("1001")).thenReturn(member);
        when(mockStore.getBorrowedItemsCount("1001")).thenReturn(2); // Borrowed 2 books

        boolean result = libraryService.canBorrowMoreItems("1001");

        assertTrue(result, "Undergraduate should be able to borrow one more book.");
    }

    @Test
    void undergraduateMemberShouldNotBeAbleToBorrowIfAtLimit() {
        Member member = new Member();
        member.id = "1001";
        member.level = 1; // Undergraduate

        when(mockStore.getMember("1001")).thenReturn(member);
        when(mockStore.getBorrowedItemsCount("1001")).thenReturn(3); // Borrowed max (3)

        boolean result = libraryService.canBorrowMoreItems("1001");

        assertFalse(result, "Undergraduate should not be able to borrow more than 3 books.");
    }

    @Test
    void teacherShouldBeAbleToBorrowWithinLimit() {
        Member member = new Member();
        member.id = "2002";
        member.level = 4;

        when(mockStore.getMember("2002")).thenReturn(member);
        when(mockStore.getBorrowedItemsCount("2002")).thenReturn(9); // Borrowed 9 books

        boolean result = libraryService.canBorrowMoreItems("2002");

        assertTrue(result, "Teacher should be able to borrow one more book.");
    }

    @Test
    void teacherShouldNotBeAbleToBorrowIfAtLimit() {
        Member member = new Member();
        member.id = "2002";
        member.level = 4;

        when(mockStore.getMember("2002")).thenReturn(member);
        when(mockStore.getBorrowedItemsCount("2002")).thenReturn(10); // Borrowed max (10)

        boolean result = libraryService.canBorrowMoreItems("2002");

        assertFalse(result, "Teacher should not be able to borrow more than 10 books.");
    }

    @Test
    void nonExistentMemberShouldNotBeAbleToBorrow() {
        when(mockStore.getMember("9999")).thenReturn(null); // Non-existent member

        boolean result = libraryService.canBorrowMoreItems("9999");

        assertFalse(result, "Invalid member should not be able to borrow.");
    }
}

