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
}

