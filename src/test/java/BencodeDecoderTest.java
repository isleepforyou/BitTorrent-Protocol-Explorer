package test.java;

import main.java.BencodeDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BencodeDecoderTest {
    
    public static void runTests() {
        TestFramework.startTestSuite("BencodeDecoder Tests");
        
        testStringDecoding();
        testIntegerDecoding();
        testListDecoding();
        testDictionaryDecoding();
        testErrorHandling();
        testNestedStructures();
        
        TestFramework.endTestSuite();
    }
    
    private static void testStringDecoding() {
        System.out.println("\n--- String Decoding Tests ---");
        
        // Basic string decoding
        TestFramework.assertEquals("hello", BencodeDecoder.decodeBencode("5:hello"), 
            "Should decode simple string");
        TestFramework.assertEquals("world", BencodeDecoder.decodeBencode("5:world"), 
            "Should decode another simple string");
        
        // Empty string
        TestFramework.assertEquals("", BencodeDecoder.decodeBencode("0:"), 
            "Should decode empty string");
        
        // String with special characters
        TestFramework.assertEquals("test:string", BencodeDecoder.decodeBencode("11:test:string"), 
            "Should decode string with colon");
        
        // Longer string
        TestFramework.assertEquals("this is a longer test string", 
            BencodeDecoder.decodeBencode("28:this is a longer test string"), 
            "Should decode longer string");
        
        // String with numbers
        TestFramework.assertEquals("123", BencodeDecoder.decodeBencode("3:123"), 
            "Should decode string containing numbers");
    }
    
    private static void testIntegerDecoding() {
        System.out.println("\n--- Integer Decoding Tests ---");
        
        // Basic integer decoding
        TestFramework.assertEquals(42, BencodeDecoder.decodeBencode("i42e"), 
            "Should decode positive integer");
        TestFramework.assertEquals(-42, BencodeDecoder.decodeBencode("i-42e"), 
            "Should decode negative integer");
        TestFramework.assertEquals(0, BencodeDecoder.decodeBencode("i0e"), 
            "Should decode zero");
        
        // Large numbers
        TestFramework.assertEquals(1000000, BencodeDecoder.decodeBencode("i1000000e"), 
            "Should decode large positive integer");
        TestFramework.assertEquals(-1000000, BencodeDecoder.decodeBencode("i-1000000e"), 
            "Should decode large negative integer");
    }
    
    private static void testListDecoding() {
        System.out.println("\n--- List Decoding Tests ---");
        
        // Empty list
        ArrayList<Object> result = (ArrayList<Object>) BencodeDecoder.decodeBencode("le");
        TestFramework.assertTrue(result.isEmpty(), "Should decode empty list");
        
        // List with one string
        result = (ArrayList<Object>) BencodeDecoder.decodeBencode("l5:helloe");
        TestFramework.assertEquals(1, result.size(), "Should have one element");
        TestFramework.assertEquals("hello", result.get(0), "Should contain correct string");
        
        // List with multiple strings
        result = (ArrayList<Object>) BencodeDecoder.decodeBencode("l5:hello5:worlde");
        TestFramework.assertEquals(2, result.size(), "Should have two elements");
        TestFramework.assertEquals("hello", result.get(0), "First element should be 'hello'");
        TestFramework.assertEquals("world", result.get(1), "Second element should be 'world'");
        
        // List with mixed types
        result = (ArrayList<Object>) BencodeDecoder.decodeBencode("l5:helloi42ee");
        TestFramework.assertEquals(2, result.size(), "Should have two elements");
        TestFramework.assertEquals("hello", result.get(0), "First element should be string");
        TestFramework.assertEquals(42, result.get(1), "Second element should be integer");
        
        // List with integers
        result = (ArrayList<Object>) BencodeDecoder.decodeBencode("li42ei-42ei0ee");
        TestFramework.assertEquals(3, result.size(), "Should have three elements");
        TestFramework.assertEquals(42, result.get(0), "First element should be 42");
        TestFramework.assertEquals(-42, result.get(1), "Second element should be -42");
        TestFramework.assertEquals(0, result.get(2), "Third element should be 0");
    }
    
    private static void testDictionaryDecoding() {
        System.out.println("\n--- Dictionary Decoding Tests ---");
        
        // Empty dictionary
        LinkedHashMap<String, Object> result = (LinkedHashMap<String, Object>) BencodeDecoder.decodeBencode("de");
        TestFramework.assertTrue(result.isEmpty(), "Should decode empty dictionary");
        
        // Dictionary with one key-value pair
        result = (LinkedHashMap<String, Object>) BencodeDecoder.decodeBencode("d3:cow3:mooe");
        TestFramework.assertEquals(1, result.size(), "Should have one key-value pair");
        TestFramework.assertEquals("moo", result.get("cow"), "Should contain correct mapping");
        
        // Dictionary with multiple key-value pairs
        result = (LinkedHashMap<String, Object>) BencodeDecoder.decodeBencode("d3:cow3:moo4:spam4:eggse");
        TestFramework.assertEquals(2, result.size(), "Should have two key-value pairs");
        TestFramework.assertEquals("moo", result.get("cow"), "Should contain cow->moo mapping");
        TestFramework.assertEquals("eggs", result.get("spam"), "Should contain spam->eggs mapping");
        
        // Dictionary with mixed value types
        result = (LinkedHashMap<String, Object>) BencodeDecoder.decodeBencode("d3:agei25e4:name4:johne");
        TestFramework.assertEquals(2, result.size(), "Should have two key-value pairs");
        TestFramework.assertEquals(25, result.get("age"), "Should contain age->25 mapping");
        TestFramework.assertEquals("john", result.get("name"), "Should contain name->john mapping");
    }
    
    private static void testErrorHandling() {
        System.out.println("\n--- Error Handling Tests ---");
        
        // Test invalid inputs
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode(""), 
            "Should throw exception for empty string");
        
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode(null), 
            "Should throw exception for null input");
        
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode("x"), 
            "Should throw exception for invalid character");
        
        // Test invalid string format
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode("5hello"), 
            "Should throw exception for missing colon in string");
        
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode("10:short"), 
            "Should throw exception for string shorter than declared length");
        
        // Test invalid integer format
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode("i42"), 
            "Should throw exception for integer missing end marker");
        
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode("iabce"), 
            "Should throw exception for non-numeric integer");
        
        // Test invalid list format
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode("l5:hello"), 
            "Should throw exception for list missing end marker");
        
        // Test invalid dictionary format
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode("d3:cow"), 
            "Should throw exception for dictionary missing value");
        
        TestFramework.assertThrows(RuntimeException.class, 
            () -> BencodeDecoder.decodeBencode("di42e3:mooe"), 
            "Should throw exception for dictionary with non-string key");
    }
    
    private static void testNestedStructures() {
        System.out.println("\n--- Nested Structure Tests ---");
        
        // List containing a list
        ArrayList<Object> result = (ArrayList<Object>) BencodeDecoder.decodeBencode("ll5:helloeee");
        TestFramework.assertEquals(1, result.size(), "Should have one element");
        TestFramework.assertTrue(result.get(0) instanceof ArrayList, "Element should be a list");
        
        ArrayList<Object> innerList = (ArrayList<Object>) result.get(0);
        TestFramework.assertEquals(1, innerList.size(), "Inner list should have one element");
        TestFramework.assertEquals("hello", innerList.get(0), "Inner list should contain 'hello'");
        
        // Dictionary containing a list
        LinkedHashMap<String, Object> dictResult = (LinkedHashMap<String, Object>) 
            BencodeDecoder.decodeBencode("d4:itemsl5:helloi42eee");
        TestFramework.assertEquals(1, dictResult.size(), "Should have one key-value pair");
        TestFramework.assertTrue(dictResult.get("items") instanceof ArrayList, "Value should be a list");
        
        ArrayList<Object> itemsList = (ArrayList<Object>) dictResult.get("items");
        TestFramework.assertEquals(2, itemsList.size(), "List should have two elements");
        TestFramework.assertEquals("hello", itemsList.get(0), "First item should be 'hello'");
        TestFramework.assertEquals(42, itemsList.get(1), "Second item should be 42");
        
        // List containing a dictionary
        result = (ArrayList<Object>) BencodeDecoder.decodeBencode("ld3:cow3:mooee");
        TestFramework.assertEquals(1, result.size(), "Should have one element");
        TestFramework.assertTrue(result.get(0) instanceof LinkedHashMap, "Element should be a dictionary");
        
        LinkedHashMap<String, Object> innerDict = (LinkedHashMap<String, Object>) result.get(0);
        TestFramework.assertEquals(1, innerDict.size(), "Inner dictionary should have one key-value pair");
        TestFramework.assertEquals("moo", innerDict.get("cow"), "Inner dictionary should contain cow->moo");
    }
} 