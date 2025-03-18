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
    void testBorrowBook_Success() {
        when(mockStore.isSuspendedMember("1234")).thenReturn(false);
        when(mockStore.borrowItem("678901", "1234")).thenReturn(true);

        boolean result = libraryService.borrow("678901", "1234");

        assertTrue(result, "Book should be borrowed successfully.");
        verify(mockStore).borrowItem("678901", "1234"); // Ensure borrowItem() was called
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
}

