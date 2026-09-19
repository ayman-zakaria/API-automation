package BasesAndConfig;

import Models.Book;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

// Reads test payloads out of testdata/books.json instead of typing them inline in
// test methods. Loaded once and cached - the file isn't going to change mid-run.
public final class TestDataLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonNode root;

    private TestDataLoader() {
    }

    private static JsonNode root() {
        if (root == null) {
            try (InputStream input = TestDataLoader.class.getClassLoader()
                    .getResourceAsStream("testdata/books.json")) {
                if (input == null) {
                    throw new IllegalStateException("testdata/books.json not found on classpath");
                }
                root = MAPPER.readTree(input);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to load testdata/books.json", e);
            }
        }
        return root;
    }

    public static Book newBook() {
        return MAPPER.convertValue(root().get("newBook"), Book.class);
    }

    public static Book updatedBook() {
        return MAPPER.convertValue(root().get("updatedBook"), Book.class);
    }

    public static Book invalidBook() {
        return MAPPER.convertValue(root().get("invalidBook"), Book.class);
    }
}
