package test.java;

import main.java.BencodeDecoder;

public class TestRunner {
    
    public static void main(String[] args) {
        System.out.println("BitTorrent Protocol Explorer - Test Suite");
        System.out.println("=========================================");
        
        int totalFailures = 0;
        
        // Reset test framework stats
        TestFramework.resetStats();
        
        // Run BencodeDecoder tests
        BencodeDecoderTest.runTests();
        int bencodeFailures = TestFramework.getFailedCount();
        totalFailures += bencodeFailures;
        
        // Reset stats for next test suite
        TestFramework.resetStats();
        
        // Run TorrentParser tests
        TorrentParserTest.runTests();
        int torrentFailures = TestFramework.getFailedCount();
        totalFailures += torrentFailures;
        
        // Print overall summary
        printOverallSummary(totalFailures, bencodeFailures, torrentFailures);
        
        // Exit with appropriate code
        System.exit(totalFailures == 0 ? 0 : 1);
    }
    
    private static void printOverallSummary(int totalFailures, int bencodeFailures, int torrentFailures) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("OVERALL TEST SUMMARY");
        System.out.println("=".repeat(60));
        
        System.out.println("BencodeDecoder Tests: " + (bencodeFailures == 0 ? "PASSED ✓" : "FAILED ✗ (" + bencodeFailures + " failures)"));
        System.out.println("TorrentParser Tests:  " + (torrentFailures == 0 ? "PASSED ✓" : "FAILED ✗ (" + torrentFailures + " failures)"));
        
        System.out.println("\nTotal Failures: " + totalFailures);
        
        if (totalFailures == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED! 🎉");
            System.out.println("Your BitTorrent Protocol Explorer is working correctly!");
        } else {
            System.out.println("\n❌ SOME TESTS FAILED");
            System.out.println("Please review the failures above and fix the issues.");
        }
        
        System.out.println("=".repeat(60));
    }
    
    public static void runQuickTest() {
        System.out.println("Running Quick Test Suite...");
        System.out.println("---------------------------");
        
        // Quick smoke test for basic functionality
        try {
            // Test basic bencode decoding
            String result = (String) BencodeDecoder.decodeBencode("5:hello");
            if (!"hello".equals(result)) {
                System.out.println("❌ Basic bencode decoding failed");
                return;
            }
            
            // Test integer decoding
            int intResult = (int) BencodeDecoder.decodeBencode("i42e");
            if (intResult != 42) {
                System.out.println("❌ Integer bencode decoding failed");
                return;
            }
            
            System.out.println("✅ Quick test passed - basic functionality working");
            
        } catch (Exception e) {
            System.out.println("❌ Quick test failed with exception: " + e.getMessage());
        }
    }
} 