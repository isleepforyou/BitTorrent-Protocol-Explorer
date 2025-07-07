package main.java;

import java.util.ArrayList;

public class Main {
  public static void main(String[] args) throws Exception {
    
    String command = args[0];
    if("decode".equals(command)) {
        String bencodedValue = args[1]; 
        try {
            Object decoded = BencodeDecoder.decodeBencode(bencodedValue);
            System.out.println(decoded);
        } catch(RuntimeException e) {
          System.out.println(e.getMessage());
          return;
        }
    }
  }
}

