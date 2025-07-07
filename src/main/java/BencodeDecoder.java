package main.java;

import java.util.ArrayList;

public class BencodeDecoder {

    static Object decodeBencode(String bencodedString) {
        if (bencodedString == null || bencodedString.isEmpty()) {
          throw new RuntimeException("Invalid bencoded value");
        }
    
        char firstChar = bencodedString.charAt(0);
    
        if (Character.isDigit(firstChar)) {
          return stringDecodeBencode(bencodedString);
        } else if (firstChar == 'i') {
          return integerDecodeBencode(bencodedString);
        } else if (firstChar == 'l') {
          return listDecodeBencode(bencodedString);
        } else if (firstChar == 'd') {
          return dictionaryDecodeBencode(bencodedString);
        }
    
        throw new RuntimeException("Invalid bencoded value");
      }
    
      static String stringDecodeBencode(String bencodedString) {
        if(Character.isDigit(bencodedString.charAt(0))){ //String decodes always start with digits
          int locationOfColon = 0;
          for(int i = 1; i < bencodedString.length(); i++){
            if(bencodedString.charAt(i) == ':'){  
              locationOfColon = i;
              break;
            }
          }
          int lengthOfString = Integer.parseInt(bencodedString.substring(0, locationOfColon));
          if (locationOfColon + 1 + lengthOfString > bencodedString.length()) {
            throw new RuntimeException("Invalid bencoded value");
          }
          return bencodedString.substring(locationOfColon + 1, locationOfColon + 1 + lengthOfString);
        }
        throw new RuntimeException("Invalid bencoded value");
      }
    
      static int integerDecodeBencode(String bencodedString){
        if (bencodedString.charAt(0) != 'i') {
          throw new RuntimeException("Invalid bencoded value");
        }
        
        int locationOfE = 0;
        for(int i = 1; i < bencodedString.length(); i++){
          if(bencodedString.charAt(i) == 'e'){
            locationOfE = i; 
            break;
          }
        }
        if (locationOfE == 0) {
          throw new RuntimeException("Invalid bencoded value");
        }
        try {
          return Integer.parseInt(bencodedString.substring(1, locationOfE));
        } catch (NumberFormatException e) {
          throw new RuntimeException("Invalid bencoded value");
        }
      }
    
      static ArrayList<Object> listDecodeBencode(String bencodedString){
        if (bencodedString.charAt(0) != 'l') {
          throw new RuntimeException("Invalid bencoded value");
        }
    
        ArrayList<Object> list = new ArrayList<>(); 
        int i = 1;
    
        while (i < bencodedString.length()) {
          char currentChar = bencodedString.charAt(i);
          
          if (currentChar == 'e') {
            return list;
          }
    
          String remainingString = bencodedString.substring(i);
          Object decodedValue = decodeBencode(remainingString);
          list.add(decodedValue);
    
          // Skip past the decoded value
          if (currentChar == 'i') {
            i += findBencodedIntegerLength(remainingString) + 1;
          } else if (Character.isDigit(currentChar)) {
            String strValue = (String)decodedValue;
            i += strValue.length() + String.valueOf(strValue.length()).length() + 1;
          } else if (currentChar == 'l') {
            i += findBencodedListLength(remainingString) + 1;
          } else if (currentChar == 'd') {
            i += findBencodedDictLength(remainingString) + 1;
          }
        }
        
        throw new RuntimeException("Invalid bencoded value");
      }
    
      static java.util.LinkedHashMap<String, Object> dictionaryDecodeBencode(String bencodedString) {
        if (bencodedString.charAt(0) != 'd') {
          throw new RuntimeException("Invalid bencoded value");
        }
    
        java.util.LinkedHashMap<String, Object> dict = new java.util.LinkedHashMap<>();
        int i = 1;
    
        while (i < bencodedString.length()) {
          char currentChar = bencodedString.charAt(i);
          
          if (currentChar == 'e') {
            return dict;
          }
    
          if (!Character.isDigit(currentChar)) {
            throw new RuntimeException("Invalid bencoded value");
          }
    
          // Decode key (must be a string)
          String key = stringDecodeBencode(bencodedString.substring(i));
          i += key.length() + String.valueOf(key.length()).length() + 1;
    
          if (i >= bencodedString.length()) {
            throw new RuntimeException("Invalid bencoded value");
          }
    
          // Decode value
          Object value = decodeBencode(bencodedString.substring(i));
          dict.put(key, value);
    
          // Skip past the decoded value
          currentChar = bencodedString.charAt(i);
          if (currentChar == 'i') {
            i += findBencodedIntegerLength(bencodedString.substring(i)) + 1;
          } else if (Character.isDigit(currentChar)) {
            String strValue = (String)value;
            i += strValue.length() + String.valueOf(strValue.length()).length() + 1;
          } else if (currentChar == 'l') {
            i += findBencodedListLength(bencodedString.substring(i)) + 1;
          } else if (currentChar == 'd') {
            i += findBencodedDictLength(bencodedString.substring(i)) + 1;
          }
        }
    
        throw new RuntimeException("Invalid bencoded value");
      }
    
      static int findBencodedIntegerLength(String bencodedString){
        int i = 0;
        while(i < bencodedString.length() && bencodedString.charAt(i) != 'e'){
          i++;
        }
        return i;
      }
    
      static int findBencodedListLength(String bencodedString) {
        int i = 0;
        int depth = 0;
        
        while (i < bencodedString.length()) {
          char c = bencodedString.charAt(i);
          if (c == 'l') depth++;
          else if (c == 'e') {
            depth--;
            if (depth == 0) return i;
          }
          i++;
        }
        return i;
      }
    
      static int findBencodedDictLength(String bencodedString) {
        int i = 0;
        int depth = 0;
        
        while (i < bencodedString.length()) {
          char c = bencodedString.charAt(i);
          if (c == 'd') depth++;
          else if (c == 'e') {
            depth--;
            if (depth == 0) return i;
          }
          i++;
        }
        return i;
      }
    }
    
