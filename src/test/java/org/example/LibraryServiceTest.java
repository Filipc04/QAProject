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
    private ILibraryStore mockStore; // Mocking the database interactions

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        libraryService = new LibraryService(mockStore);
    }

    @Test
    void testBorrowBook_FailedSuspended() {
        when(mockStore.isSuspendedMember("1234")).thenReturn(true);

        boolean result = libraryService.borrow("678901", "1234");

        assertFalse(result, "Suspended member should not borrow books.");
        verify(mockStore, never()).borrowItem(anyString(), anyString()); // Ensure borrowItem() was NOT called
    }

    @Test
    void testReturnBook_Success() {
        when(mockStore.returnItem("678901", "1234")).thenReturn(true);

        boolean result = libraryService.returnBook("678901", "1234");

        assertTrue(result, "Book should be returned successfully.");
        verify(mockStore).returnItem("678901", "1234"); // Ensure returnItem() was called
    }

    @Test
    void testMemberExists_ValidUser() {
        Member testMember = new Member(); // Create an empty Member object
        testMember.id = "1234"; // Assign values manually
        testMember.firstName = "John";
        testMember.lastName = "Doe";
        testMember.personalNumber = "123456789";
        testMember.level = 1;

        when(mockStore.getMember("1234")).thenReturn(testMember);

        boolean exists = libraryService.memberExists("1234");

        assertTrue(exists, "Member should exist.");
    }

    @Test
    void testMemberExists_InvalidUser() {
        when(mockStore.getMember("9999")).thenReturn(null);

        boolean exists = libraryService.memberExists("9999");

        assertFalse(exists, "Member should not exist.");
    }

    @Test
    void testMemberGetsSuspendedAfterTwoLateReturns() {
        when(mockStore.getMember("1234")).thenReturn(new Member());
        when(mockStore.getSuspensionCount("1234")).thenReturn(0); // No prior suspensions

        // Simulate two late returns
        libraryService.checkLateReturnsAndSuspend("1234");
        libraryService.checkLateReturnsAndSuspend("1234");

        // Verify that the member was suspended
        verify(mockStore).recordSuspension("1234");
    }

    @Test
    void testMemberGetsDeletedAfterTwoSuspensions() {
        Member testMember = new Member();
        testMember.id = "5678";
        testMember.lateReturns = 2; // Simulate that the user already has 2 late returns

        when(mockStore.getMember("5678")).thenReturn(testMember);
        when(mockStore.getSuspensionCount("5678")).thenReturn(1); // Simulate 1 existing suspension

        libraryService.checkLateReturnsAndSuspend("5678");

        verify(mockStore).deleteMember("5678"); // ✅ This should now be called
    }

}

