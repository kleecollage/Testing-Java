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
        // Using lambdas in messages saves memory, since it doesn't create all of them.
        assertNotNull(actual, () -> "Account cannot be null");
        assertTrue(actual.equals("John Doe"), () -> "Account name must be equal to actual");
        assertEquals(expected, actual, () -> "Expected " + expected + " but got " + actual);
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

    @Test
    void testTransferMoneyAccounts() {
        Account account1 = new Account("John Doe", new BigDecimal("2500"));
        Account account2 = new Account("Jane Smith", new BigDecimal("1500.8989"));
        var bank = new Bank();
        bank.setName("Santander");
        bank.transfer(account2, account1, new BigDecimal("500"));
        assertEquals("1000.8989", account2.getBalance().toPlainString());
        assertEquals("3000", account1.getBalance().toPlainString());
    }

    @Test
    void testRelationBankAccounts() {
        Account account1 = new Account("John Doe", new BigDecimal("2500"));
        Account account2 = new Account("Jane Smith", new BigDecimal("1500.8989"));
        var bank = new Bank();
        bank.addAccount(account1);
        bank.addAccount(account2);
        bank.setName("Santander");

        bank.transfer(account2, account1, new BigDecimal("500"));
        assertAll(
                () -> assertEquals("1000.8989", account2.getBalance().toPlainString(),
                        () -> "Balance value in account2 is not the expected"),
                () -> assertEquals("3000", account1.getBalance().toPlainString(),
                        () -> "Balance value in account1 is not the expected"),
                () -> assertEquals(2, bank.getAccounts().size(),
                        () -> "Bank does not have the expected number of accounts"),
                () -> assertEquals("Santander", account1.getBank().getName()),
                // Relation getPerson from Bank (2 methods)
                () -> assertEquals("John Doe", bank.getAccounts().stream()
                            .filter(a -> a.getPerson().equals("John Doe"))
                            .findFirst()
                            .get().getPerson()),
                () -> assertTrue(bank.getAccounts().stream()
                        // .filter(a -> a.getPerson().equals("John Doe"))
                        // .findFirst().isPresent()
                        .anyMatch(a -> a.getPerson().equals("Jane Smith")))
        );
    }
}

