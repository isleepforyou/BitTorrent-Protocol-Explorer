package test.java;

import main.java.TorrentParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class TorrentParserTest {
    
    public static void runTests() {
        TestFramework.startTestSuite("TorrentParser Tests");
        
        testValidTorrentParsing();
        testInvalidTorrentHandling();
        testGetterMethods();
        testFileNotFound();
        
        TestFramework.endTestSuite();
    }
    
    private static void testValidTorrentParsing() {
        System.out.println("\n--- Valid Torrent Parsing Tests ---");
        
        try {
            // Create a sample torrent file for testing
            String sampleTorrentPath = createSampleTorrentFile();
            
            TorrentParser parser = new TorrentParser(sampleTorrentPath);
            
            // Test basic parsing
            TestFramework.assertEquals("http://tracker.example.com:8080/announce", 
                parser.getAnnounce(), "Should parse announce URL correctly");
            TestFramework.assertEquals(92063L, parser.getLength(), "Should parse file length correctly");
            TestFramework.assertEquals("sample.txt", parser.getName(), "Should parse file name correctly");
            TestFramework.assertEquals(32768L, parser.getPieceLength(), "Should parse piece length correctly");
            
            // Test pieces array
            byte[] pieces = parser.getPieces();
            TestFramework.assertTrue(pieces.length > 0, "Should have pieces data");
            TestFramework.assertTrue(pieces.length % 20 == 0, "Pieces length should be multiple of 20");
            
            // Test info dictionary
            LinkedHashMap<String, Object> info = parser.getInfo();
            TestFramework.assertTrue(info.containsKey("name"), "Info should contain name");
            TestFramework.assertTrue(info.containsKey("length"), "Info should contain length");
            TestFramework.assertTrue(info.containsKey("piece length"), "Info should contain piece length");
            TestFramework.assertTrue(info.containsKey("pieces"), "Info should contain pieces");
            
            // Clean up test file
            Files.deleteIfExists(Paths.get(sampleTorrentPath));
            
        } catch (Exception e) {
            TestFramework.assertTrue(false, "Should not throw exception for valid torrent: " + e.getMessage());
        }
    }
    
    private static void testInvalidTorrentHandling() {
        System.out.println("\n--- Invalid Torrent Handling Tests ---");
        
        try {
            // Test with invalid torrent data (not a dictionary)
            String invalidTorrentPath = createInvalidTorrentFile();
            
            TestFramework.assertThrows(RuntimeException.class, 
                () -> {
                    try {
                        new TorrentParser(invalidTorrentPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, 
                "Should throw exception for invalid torrent structure");
            
            // Clean up test file
            Files.deleteIfExists(Paths.get(invalidTorrentPath));
            
        } catch (Exception e) {
            TestFramework.assertTrue(false, "Test setup failed: " + e.getMessage());
        }
        
        try {
            // Test with missing announce field
            String missingAnnouncePath = createMissingAnnounceFile();
            
            TestFramework.assertThrows(RuntimeException.class, 
                () -> {
                    try {
                        new TorrentParser(missingAnnouncePath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, 
                "Should throw exception for missing announce field");
            
            // Clean up test file
            Files.deleteIfExists(Paths.get(missingAnnouncePath));
            
        } catch (Exception e) {
            TestFramework.assertTrue(false, "Test setup failed: " + e.getMessage());
        }
        
        try {
            // Test with missing info field
            String missingInfoPath = createMissingInfoFile();
            
            TestFramework.assertThrows(RuntimeException.class, 
                () -> {
                    try {
                        new TorrentParser(missingInfoPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, 
                "Should throw exception for missing info field");
            
            // Clean up test file
            Files.deleteIfExists(Paths.get(missingInfoPath));
            
        } catch (Exception e) {
            TestFramework.assertTrue(false, "Test setup failed: " + e.getMessage());
        }
    }
    
    private static void testGetterMethods() {
        System.out.println("\n--- Getter Methods Tests ---");
        
        try {
            String sampleTorrentPath = createSampleTorrentFile();
            TorrentParser parser = new TorrentParser(sampleTorrentPath);
            
            // Test all getter methods return non-null values
            TestFramework.assertTrue(parser.getAnnounce() != null, "getAnnounce should not return null");
            TestFramework.assertTrue(parser.getName() != null, "getName should not return null");
            TestFramework.assertTrue(parser.getLength() > 0, "getLength should return positive value");
            TestFramework.assertTrue(parser.getPieceLength() > 0, "getPieceLength should return positive value");
            TestFramework.assertTrue(parser.getPieces() != null, "getPieces should not return null");
            TestFramework.assertTrue(parser.getInfo() != null, "getInfo should not return null");
            
            // Clean up test file
            Files.deleteIfExists(Paths.get(sampleTorrentPath));
            
        } catch (Exception e) {
            TestFramework.assertTrue(false, "Should not throw exception: " + e.getMessage());
        }
    }
    
    private static void testFileNotFound() {
        System.out.println("\n--- File Not Found Tests ---");
        
        // Test file not found manually since assertThrows doesn't handle checked exceptions well
        try {
            new TorrentParser("non_existent_file.torrent");
            TestFramework.assertTrue(false, "Should throw IOException for non-existent file");
        } catch (IOException e) {
            TestFramework.assertTrue(true, "Should throw IOException for non-existent file");
        }
    }
    
    private static String createSampleTorrentFile() throws IOException {
        // Create a valid torrent file structure in bencode format
        // This represents a torrent file with:
        // - announce: "http://tracker.example.com:8080/announce"
        // - info dictionary with name, length, piece length, and pieces
        
        String torrentData = "d" + // Start dictionary
            "8:announce" + "41:http://tracker.example.com:8080/announce" + // announce field
            "4:info" + // info field
            "d" + // Start info dictionary
                "6:length" + "i92063e" + // length: 92063
                "4:name" + "10:sample.txt" + // name: "sample.txt"
                "12:piece length" + "i32768e" + // piece length: 32768
                "6:pieces" + "60:" + // pieces: 60 bytes (3 * 20-byte SHA1 hashes)
                "12345678901234567890" + // First 20-byte hash (dummy)
                "abcdefghijklmnopqrst" + // Second 20-byte hash (dummy)
                "ABCDEFGHIJKLMNOPQRST" + // Third 20-byte hash (dummy)
            "e" + // End info dictionary
            "e"; // End main dictionary
        
        Path tempFile = Files.createTempFile("test_torrent", ".torrent");
        Files.write(tempFile, torrentData.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        
        return tempFile.toString();
    }
    
    private static String createInvalidTorrentFile() throws IOException {
        // Create an invalid torrent file (not a dictionary)
        String invalidData = "l5:helloe"; // This is a list, not a dictionary
        
        Path tempFile = Files.createTempFile("invalid_torrent", ".torrent");
        Files.write(tempFile, invalidData.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        
        return tempFile.toString();
    }
    
    private static String createMissingAnnounceFile() throws IOException {
        // Create a torrent file missing the announce field
        String torrentData = "d" + // Start dictionary
            "4:info" + // info field (but missing announce)
            "d" + // Start info dictionary
                "6:length" + "i92063e" + // length: 92063
                "4:name" + "10:sample.txt" + // name: "sample.txt"
                "12:piece length" + "i32768e" + // piece length: 32768
                "6:pieces" + "20:" + "12345678901234567890" + // pieces: 20 bytes (1 hash)
            "e" + // End info dictionary
            "e"; // End main dictionary
        
        Path tempFile = Files.createTempFile("missing_announce", ".torrent");
        Files.write(tempFile, torrentData.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        
        return tempFile.toString();
    }
    
    private static String createMissingInfoFile() throws IOException {
        // Create a torrent file missing the info field
        String torrentData = "d" + // Start dictionary
            "8:announce" + "41:http://tracker.example.com:8080/announce" + // announce field (but missing info)
            "e"; // End main dictionary
        
        Path tempFile = Files.createTempFile("missing_info", ".torrent");
        Files.write(tempFile, torrentData.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        
        return tempFile.toString();
    }
} 