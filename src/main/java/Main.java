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
    } else if("info".equals(command)) {
        String torrentFilePath = args[1];
        try {
            TorrentParser parser = new TorrentParser(torrentFilePath);
            parser.printTorrentInfo();
        } catch(Exception e) {
            System.out.println("Error parsing torrent file: " + e.getMessage());
            return;
        }
    } else if("test".equals(command)) {
        // Run tests
        if (args.length > 1 && "quick".equals(args[1])) {
            test.java.TestRunner.runQuickTest();
        } else {
            test.java.TestRunner.main(new String[]{});
        }
    } else {
        System.out.println("Unknown command: " + command);
        System.out.println("Usage:");
        System.out.println("  java -cp src main.java.Main decode <bencoded_value>");
        System.out.println("  java -cp src main.java.Main info <torrent_file_path>");
        System.out.println("  java -cp src main.java.Main test [quick]");
    }
  }
}

