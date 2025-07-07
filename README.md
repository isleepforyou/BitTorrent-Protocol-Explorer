# BitTorrent Protocol Explorer

A hands-on project to understand and implement the BitTorrent protocol from scratch.

## Features

- **Bencode Decoder**: Decodes all four Bencode data types (strings, integers, lists, dictionaries)
- **Torrent Parser**: Extracts tracker URL, file information, and metadata from single-file torrents
- **Test Suite**: Comprehensive tests to verify functionality and catch regressions

## Usage

```bash
# Compile
javac src/main/java/*.java src/test/java/*.java

# Decode Bencode data
java -cp src main.java.Main decode "5:hello"
java -cp src main.java.Main decode "i52e"
java -cp src main.java.Main decode "l5:helloi52ee"

# Parse torrent file
java -cp src main.java.Main info sample.torrent

# Run tests
java -cp src main.java.Main test
java -cp src main.java.Main test quick
```

## Project Structure

```
src/
├── main/java/
│   ├── Main.java             # Command-line interface
│   ├── BencodeDecoder.java   # Bencode decoding
│   └── TorrentParser.java    # Torrent file parsing
└── test/java/
    ├── TestFramework.java      # Simple test framework
    ├── BencodeDecoderTest.java # Tests for Bencode decoder
    ├── TorrentParserTest.java  # Tests for Torrent parser
    └── TestRunner.java         # Test suite runner
```

## Testing

The project includes comprehensive tests for all functionality:

**Test Coverage:**
- **BencodeDecoder**: String, integer, list, dictionary decoding + error handling + nested structures
- **TorrentParser**: Valid torrent parsing, invalid torrent handling, file I/O errors

**Running Tests:**
```bash
# Run all tests
java -cp src main.java.Main test

# Run quick smoke test
java -cp src main.java.Main test quick

# Or run tests directly
java -cp src test.java.TestRunner
```

## Next Steps

- ✅ Torrent file parsing (completed)
- ✅ Test suite (completed)
- Info hash calculation
- Peer discovery and communication
- Piece downloading and verification
- Multi-file torrent support
