package main.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class TorrentParser {
    
    private String announce;
    private LinkedHashMap<String, Object> info;
    private long length;
    private String name;
    private long pieceLength;
    private byte[] pieces;
    
    public TorrentParser(String filePath) throws IOException {
        byte[] torrentData = Files.readAllBytes(Paths.get(filePath));
        parseTorrent(torrentData);
    }
    
    private void parseTorrent(byte[] torrentData) {
        // Convert bytes to String for bencode decoding
        // Note: This assumes the torrent file uses ISO-8859-1 encoding to preserve byte values
        String bencodedString = new String(torrentData, java.nio.charset.StandardCharsets.ISO_8859_1);
        
        // Parse the bencoded torrent file
        Object decoded = BencodeDecoder.decodeBencode(bencodedString);
        
        if (!(decoded instanceof LinkedHashMap)) {
            throw new RuntimeException("Invalid torrent file: root must be a dictionary");
        }
        
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> torrentDict = (LinkedHashMap<String, Object>) decoded;
        
        // Extract announce URL
        Object announceObj = torrentDict.get("announce");
        if (announceObj instanceof String) {
            this.announce = (String) announceObj;
        } else {
            throw new RuntimeException("Invalid torrent file: missing or invalid announce");
        }
        
        // Extract info dictionary
        Object infoObj = torrentDict.get("info");
        if (!(infoObj instanceof LinkedHashMap)) {
            throw new RuntimeException("Invalid torrent file: missing or invalid info dictionary");
        }
        
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> infoDict = (LinkedHashMap<String, Object>) infoObj;
        this.info = infoDict;
        
        // Extract length (file size in bytes)
        Object lengthObj = infoDict.get("length");
        if (lengthObj instanceof Integer) {
            this.length = ((Integer) lengthObj).longValue();
        } else {
            throw new RuntimeException("Invalid torrent file: missing or invalid length in info dictionary");
        }
        
        // Extract name (suggested filename)
        Object nameObj = infoDict.get("name");
        if (nameObj instanceof String) {
            this.name = (String) nameObj;
        } else {
            throw new RuntimeException("Invalid torrent file: missing or invalid name in info dictionary");
        }
        
        // Extract piece length
        Object pieceLengthObj = infoDict.get("piece length");
        if (pieceLengthObj instanceof Integer) {
            this.pieceLength = ((Integer) pieceLengthObj).longValue();
        } else {
            throw new RuntimeException("Invalid torrent file: missing or invalid piece length in info dictionary");
        }
        
        // Extract pieces (SHA-1 hashes)
        Object piecesObj = infoDict.get("pieces");
        if (piecesObj instanceof String) {
            this.pieces = ((String) piecesObj).getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        } else {
            throw new RuntimeException("Invalid torrent file: missing or invalid pieces in info dictionary");
        }
    }
    
    public String getAnnounce() {
        return announce;
    }
    
    public long getLength() {
        return length;
    }
    
    public String getName() {
        return name;
    }
    
    public long getPieceLength() {
        return pieceLength;
    }
    
    public byte[] getPieces() {
        return pieces;
    }
    
    public LinkedHashMap<String, Object> getInfo() {
        return info;
    }
    
    public void printTorrentInfo() {
        System.out.println("Tracker URL: " + announce);
        System.out.println("Length: " + length);
        System.out.println("Name: " + name);
        System.out.println("Piece Length: " + pieceLength);
        System.out.println("Number of pieces: " + (pieces.length / 20)); // Each SHA-1 hash is 20 bytes
    }
} 