package test.java;

import java.util.ArrayList;
import java.util.List;

public class TestFramework {
    
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static List<String> failures = new ArrayList<>();
    
    public static void assertTrue(boolean condition, String message) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println("✓ " + message);
        } else {
            failedTests++;
            failures.add(message);
            System.out.println("✗ " + message);
        }
    }
    
    public static void assertEquals(Object expected, Object actual, String message) {
        totalTests++;
        boolean equal = (expected == null && actual == null) || 
                       (expected != null && expected.equals(actual));
        
        if (equal) {
            passedTests++;
            System.out.println("✓ " + message);
        } else {
            failedTests++;
            String failure = message + " (Expected: " + expected + ", Actual: " + actual + ")";
            failures.add(failure);
            System.out.println("✗ " + failure);
        }
    }
    
    public static void assertThrows(Class<? extends Exception> expectedType, Runnable test, String message) {
        totalTests++;
        try {
            test.run();
            failedTests++;
            String failure = message + " (Expected exception " + expectedType.getSimpleName() + " but none was thrown)";
            failures.add(failure);
            System.out.println("✗ " + failure);
        } catch (Exception e) {
            if (expectedType.isInstance(e)) {
                passedTests++;
                System.out.println("✓ " + message);
            } else {
                failedTests++;
                String failure = message + " (Expected " + expectedType.getSimpleName() + " but got " + e.getClass().getSimpleName() + ")";
                failures.add(failure);
                System.out.println("✗ " + failure);
            }
        }
    }
    
    public static void startTestSuite(String suiteName) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Running " + suiteName);
        System.out.println("=".repeat(50));
    }
    
    public static void endTestSuite() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("Tests completed: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        
        if (failedTests > 0) {
            System.out.println("\nFailures:");
            for (String failure : failures) {
                System.out.println("  - " + failure);
            }
        }
        
        System.out.println("-".repeat(50));
    }
    
    public static void resetStats() {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        failures.clear();
    }
    
    public static boolean allTestsPassed() {
        return failedTests == 0;
    }
    
    public static int getFailedCount() {
        return failedTests;
    }
} 