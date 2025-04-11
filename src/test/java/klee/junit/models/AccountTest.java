package klee.junit.models;

import klee.junit.exceptions.InsufficientMoneyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

// @TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountTest {
    // Global vars
    Account account;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void initMethodTest(TestInfo info, TestReporter reporter) {
        this.account = new Account("John Doe", new BigDecimal("1000.12345"));
        this.testInfo = info;
        this.testReporter = reporter;
        System.out.println("starting test method");
        reporter.publishEntry("Executing: " + info.getDisplayName() + ". Method: " +
                info.getTestMethod().orElse(null).getName() + " with tags " + info.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("finishing test method");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("starting tests");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("finishing tests");
    }

    @Tag("account")
    @Nested
    @DisplayName("testing account attributes")
    class AccountTestNameAndBalance {
        @Test
        @DisplayName("testing name")
        void testNameAccount() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("account"))
                testReporter.publishEntry("do something with tag account");
            // account.setPerson("John Doe");
            String expected = "John Doe";
            String actual = account.getPerson();
            // Using lambdas in messages saves memory, since it doesn't create all of them.
            assertNotNull(actual, () -> "Account cannot be null");
            assertTrue(actual.equals("John Doe"), () -> "Account name must be equal to actual");
            assertEquals(expected, actual, () -> "Expected " + expected + " but got " + actual);
        }

        @Test
        @DisplayName("testing balance should not be null or lower than zero")
        void testAccountBalance() {
            assertNotNull(account.getBalance());
            assertEquals(1000.12345, account.getBalance().doubleValue());
            assertFalse(account.getBalance().compareTo(BigDecimal.ZERO) < 0); // -1 = False
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0); // 1 = True
        }

        @Test
        @DisplayName("testing references to be equals with equal method")
        void testAccountReference() {
            Account account = new Account("John Doe", new BigDecimal("8900.9997"));
            Account account2 = new Account("John Doe", new BigDecimal("8900.9997"));
            // assertNotEquals(account2, account);
            assertEquals(account2, account); // Override equals method on model
        }
    }

    @Nested
    class AccountOperationsTests {
        @Tag("account")
        @Test
        void testDebitAccount() {
            account.debit(new BigDecimal(100));
            assertNotNull(account.getBalance());
            assertEquals(900, account.getBalance().intValue());
            assertEquals("900.12345", account.getBalance().toPlainString());
        }

        @Tag("account")
        @Test
        void testCreditAccount() {
            account.credit(new BigDecimal(100));
            assertNotNull(account.getBalance());
            assertEquals(1100, account.getBalance().intValue());
            assertEquals("1100.12345", account.getBalance().toPlainString());
        }

        @Tag("account")
        @Tag("bank")
        @Test
        @Disabled // Skip Test
        void testTransferMoneyAccounts() {
            fail(); // We ensure the test fails
            Account account1 = new Account("John Doe", new BigDecimal("2500"));
            Account account2 = new Account("Jane Smith", new BigDecimal("1500.8989"));
            var bank = new Bank();
            bank.setName("Santander");
            bank.transfer(account2, account1, new BigDecimal("500"));
            assertEquals("1000.8989", account2.getBalance().toPlainString());
            assertEquals("3000", account1.getBalance().toPlainString());
        }
    }

    @Test
    @Tag("account")
    @Tag("error")
    void testInsufficientMoneyException() {
        Exception e = assertThrows(InsufficientMoneyException.class, () -> {
            account.debit(new BigDecimal(1500));
        });
        String expected = "Insufficient Money";
        String actual = e.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    @Tag("account")
    @Tag("bank")
    @DisplayName("testing relations between accounts and bank with assertAll")
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

    /* ####################   NON-BANK TESTS (MACHINE TEST)   #################### */
    @Nested
    class OperativeSystemTests {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testOnlyWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        void testOnlyLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }

        @Test
        @DisabledOnOs({OS.LINUX, OS.MAC})
        void testNoLinuxMac() {
        }
    }

    @Nested
    class JavaVersionTests {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testOnlyJdk8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_21)
        void testOnlyJdk21() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_21)
        void testNoJdk21() {
        }
    }

    @Nested
    class SystemPropertiesTests {
        @Test
        void printSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = ".*21.*")
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testOnly64x() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNo64x() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "mrrobot")
        void testUserName() {
        }
    }

    @Nested
    class EnvironmentVariablesTests {
        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }

        @Test
        void printEnvironmentVariables() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " = " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "~/.sdkman/candidates/java/current")
        void testJavaHome() {}

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void testProcessors() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "dev")
        void testEnv() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled() {
        }
    }

    @Test
    void testTransferMoneyAccountsDev() {
        boolean isDev = "dev".equals(System.getProperty("ENV"));
        // assumeTrue(isDev);
        assumingThat(isDev, () -> {
            Account account1 = new Account("John Doe", new BigDecimal("2500"));
            Account account2 = new Account("Jane Smith", new BigDecimal("1500.8989"));
            var bank = new Bank();
            bank.setName("Santander");
            bank.transfer(account2, account1, new BigDecimal("500"));
            assertEquals("1000.8989", account2.getBalance().toPlainString());
            assertEquals("3000", account1.getBalance().toPlainString());
        });
        // Here can go more asserts
    }

    @DisplayName("Multiple Tests on Debit Account")
    @RepeatedTest(value = 5, name = "{displayName} - Repetition no. {currentRepetition} of {totalRepetitions}" )
    void testDebitAccountRepeat(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            System.out.println("We are on repetition " + info.getCurrentRepetition());
        }
        account.debit(new BigDecimal(100));
        assertNotNull(account.getBalance());
        assertEquals(900, account.getBalance().intValue());
        assertEquals("900.12345", account.getBalance().toPlainString());
    }

    @Tag("param")
    @Nested
    class ParameterizedTests {
        @ParameterizedTest(name = "no. {index} executing with value {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000.12345"})
        void testDebitAccountValueSource(String amount) {
            account.debit(new BigDecimal(amount));
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "no. {index} executing with {argumentsWithNames}")
        @ValueSource(doubles = {100, 200, 300, 500, 700, 1000})
        void testDebitAccountValueSource(double amount) {
            account.debit(new BigDecimal(amount));
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "no. {index} executing with {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345"})
        void testDebitAccountCsvSource(String index, String amount) {
            System.out.println(index + " -> " + amount);
            account.debit(new BigDecimal(amount));
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "no. {index} executing with {argumentsWithNames}")
        @CsvSource({"200,100,John,Jane", "250,200,Jack,Jack", "301,300,chris,Chris",
                "510,500,Sarah,Sarah", "750,700,Jame,James", "1000.12345,1000.12345,Anna,Anna"})
        void testDebitAccountCsvSource2(String balance, String amount, String expected, String actual) {
            System.out.println(balance + " -> " + amount);
            account.setPerson(actual);
            account.setBalance(new BigDecimal(balance));
            account.debit(new BigDecimal(amount));

            assertNotNull(account.getBalance());
            assertNotNull(account.getPerson());
            assertEquals(expected, actual);
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "no. {index} executing with {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitAccountCsvFileSource(String amount) {
            account.debit(new BigDecimal(amount));
            assertNotNull(account.getBalance());
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "no. {index} executing with {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitAccountCsvFileSource2(String balance, String amount, String expected, String actual) {
            account.setPerson(actual);
            account.setBalance(new BigDecimal(balance));
            account.debit(new BigDecimal(amount));

            assertNotNull(account.getBalance());
            assertNotNull(account.getPerson());
            assertEquals(expected, actual);
            assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
        }
    }

    @Tag("param")
    @ParameterizedTest(name = "no. {index} executing with {argumentsWithNames}")
    @MethodSource("amountList")
    void testDebitAccountMethodSource(String amount) {
        account.debit(new BigDecimal(amount));
        assertNotNull(account.getBalance());
        assertTrue(account.getBalance().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> amountList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }
}















