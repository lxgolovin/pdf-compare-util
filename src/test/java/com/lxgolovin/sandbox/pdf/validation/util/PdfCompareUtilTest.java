package com.lxgolovin.sandbox.pdf.validation.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PdfCompareUtilTest {

    @Test
    void getPageCount() throws IOException, URISyntaxException {
        assertThrows(IllegalArgumentException.class, () -> PdfCompareUtil.getPageCount(null));

        Path samplePath = getFilePath("sample_get_pages.pdf");
        try (PdfDocument document = new PdfDocument(samplePath)) {
            assertEquals(9, PdfCompareUtil.getPageCount(document));
        }
    }

    @Test
    void compareEqualPdfByText() throws URISyntaxException, IOException {
        assertThrows(IllegalArgumentException.class, () -> PdfCompareUtil.compare(null, null));

        Path sample1Path = getFilePath("sample_get_pages.pdf");
        Path sample2Path = getFilePath("sample_text_compare_1.pdf");

        try (PdfDocument document1 = new PdfDocument(sample1Path);
             PdfDocument document2 = new PdfDocument(sample2Path)) {

            boolean isEqual = PdfCompareUtil.compare(document1, document2);
            assertTrue(isEqual);
        }
    }

    private Path getFilePath(String filename) throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).toURI());
    }

}