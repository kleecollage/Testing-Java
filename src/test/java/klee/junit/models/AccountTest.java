package klee.junit.models;

import klee.junit.exceptions.InsufficientMoneyException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    @Test
    void test_name_account() {
        var account = new Account("John Doe", new BigDecimal("1000.12345"));
        // account.setPerson("John Doe");
        String expected = "John Doe";
        String actual = account.getPerson();
        assertNotNull(actual);
        assertEquals(expected, actual);
        assertTrue(actual.equals("John Doe"));
    }

    @Test
    void testAccountBBalance() {
        Account account = new Account("John Doe", new BigDecimal("1000.12345"));
        assertNotNull(account.getBalance());
        assertEquals(1000.12345, account.getBalance().doubleValue());
        assertFalse(account.getBalance().compareTo(BigDecimal.ZERO) < 0); // -1 = False
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0); // 1 = True
    }

    @Test
    void testAccountReference() {
        Account account = new Account("John Doe", new BigDecimal("8900.9997"));
        Account account2 = new Account("John Doe", new BigDecimal("8900.9997"));
        // assertNotEquals(account2, account);
        assertEquals(account2, account); // Override equals method on model
    }

    @Test
    void testDebitAccount() {
        Account account = new Account("John Doe", new BigDecimal("1000.12345"));
        account.debit(new BigDecimal(100));
        assertNotNull(account.getBalance());
        assertEquals(900, account.getBalance().intValue());
        assertEquals("900.12345", account.getBalance().toPlainString());
    }

    @Test
    void testCreditAccount() {
        Account account = new Account("John Doe", new BigDecimal("1000.12345"));
        account.credit(new BigDecimal(100));
        assertNotNull(account.getBalance());
        assertEquals(1100, account.getBalance().intValue());
        assertEquals("1100.12345", account.getBalance().toPlainString());
    }

    @Test
    void testInsufficientMoneyException() {
        Account account = new Account("John Doe", new BigDecimal("1000.12345"));
        Exception e = assertThrows(InsufficientMoneyException.class, () -> {
            account.debit(new BigDecimal(1500));
        });
        String expected = "Insufficient Money";
        String actual = e.getMessage();
        assertEquals(expected, actual);
    }
}

