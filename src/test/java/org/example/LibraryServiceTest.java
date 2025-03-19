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

    //MEMBER REGISTRATION TESTS
    @Test
    void registeringNewMemberShouldGenerateUniqueIdAndStoreMember() {
        Member newMember = new Member();
        doNothing().when(mockStore).addMember(any(Member.class));

        boolean result = libraryService.registerMember(newMember);

        assertTrue(result, "New member should be registered successfully.");
        verify(mockStore).addMember(newMember);
    }

    //BORROWING BOOKS TESTS
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

    //RETURNING BOOKS TESTS

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

    @Test
    void returningBookShouldUpdateAvailability() {
        when(mockStore.returnItem("678901", "1234")).thenReturn(true); // ✅ Simulate successful return

        boolean result = libraryService.returnBook("678901", "1234");

        assertTrue(result, "Returning a book should mark it as available again.");
        verify(mockStore).returnItem("678901", "1234");
    }

    //MEMBER SUSPENSION TESTS
    @Test
    void suspendingMemberShouldStoreCorrectDays() {
        doNothing().when(mockStore).suspendMember("1234", 7); // ✅ Mock 7-day suspension

        boolean result = libraryService.suspendMember("1234", 7);

        assertTrue(result, "Member should be suspended for the correct number of days.");
        verify(mockStore).suspendMember("1234", 7);
    }

    @Test
    void suspendingMemberShouldFailForInvalidDays() {
        boolean result = libraryService.suspendMember("1234", 0); // ❌ Invalid: 0 days

        assertFalse(result, "Suspending a member with 0 days should fail.");
        verify(mockStore, never()).suspendMember(anyString(), anyInt());
    }

    @Test
    void memberShouldBeDeletedIfTheyReachSuspensionLimit() {
        Member testMember = new Member();
        testMember.id = "5678";
        testMember.lateReturns = 2;

        when(mockStore.getMember("5678")).thenReturn(testMember);
        when(mockStore.getSuspensionCount("5678")).thenReturn(1);
        when(mockStore.wasReturnLate("5678")).thenReturn(true);

        libraryService.checkLateReturnsAndSuspend("5678");

        verify(mockStore).deleteMember("5678");
    }

    //LOGIN & MEMBER SEARCH TESTS
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

    @Test
    void memberShouldBeAbleToLoginWithValidId() {
        Member existingMember = new Member();
        existingMember.id = "1234";

        when(mockStore.getMember("1234")).thenReturn(existingMember);

        boolean result = libraryService.memberExists("1234");

        assertTrue(result, "Existing member should be able to log in.");
    }

    @Test
    void memberShouldNotBeAbleToLoginWithInvalidId() {
        when(mockStore.getMember("9999")).thenReturn(null);

        boolean result = libraryService.memberExists("9999");

        assertFalse(result, "Invalid member should not be able to log in.");
    }
}
