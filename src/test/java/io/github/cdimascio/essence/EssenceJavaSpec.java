package io.github.cdimascio.essence;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import static io.github.cdimascio.essence.FixturesKt.parseJson;
import static io.github.cdimascio.essence.FixturesKt.readFileFull;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EssenceJavaSpec {

    @Test
    public void parse() {
        String html = readFileFull("./fixtures/test_mcsweeney.html");
        JsonNode orig = parseJson(readFileFull("./fixtures/test_mcsweeney.json"));
        EssenceResult data = Essence.extract(html);

        JsonNode expected = orig.get("expected");

        String origText = cleanOrigText(expected.get("cleaned_text").asText());
        String newText = cleanTestingTest(data.getText(), origText);
        assertNotEquals("text should not be null", "", newText);
        assertTrue(data.getText().length() >= origText.length());
        assertEquals(origText, newText);
    }

    private String cleanTestingTest(String newText, String originalText) {
        return newText.
            replace("\n\n", " ").
            replace("\\ \\", " ")
            .substring(0, Math.min(newText.length(), originalText.length()));
    }

    private String cleanOrigText(String text) {
        return text.replace("\n\n", " ");
    }
}
