package com.lxgolovin.sandbox.pdf.compare.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PdfCompareUtilTest {

    PdfCompareUtil pdfCompareUtil = new PdfCompareUtil();

    @Test
    void getPageCount() throws IOException, URISyntaxException {
        assertThrows(IllegalArgumentException.class, () -> PdfCompareUtil.getPageCount(null));

        Path samplePath = getFilePath("sample_text_compare_equal_1.pdf");
        try (PdfDocument document = new PdfDocument(samplePath)) {
            assertEquals(5, document.getPageCount());
            assertEquals(5, PdfCompareUtil.getPageCount(document));
        }
    }

    @Test
    void compareEntireDocumentsEqualPdfByText() throws URISyntaxException, IOException {
        assertThrows(IllegalArgumentException.class, () -> pdfCompareUtil.compare(null, null));

        Path sample1Path = getFilePath("sample_text_compare_equal_1.pdf");
        Path sample2Path = getFilePath("sample_text_compare_equal_2.pdf");

        try (PdfDocument document1 = new PdfDocument(sample1Path);
             PdfDocument document2 = new PdfDocument(sample2Path)) {

            boolean notEqual = pdfCompareUtil.compare(document1, document2).isHasErrors();
            assertFalse(notEqual);
        }
    }

    @Test
    void compareEntireDocumentsNotEqualPdfByText() throws URISyntaxException, IOException {
        assertThrows(IllegalArgumentException.class, () -> pdfCompareUtil.compare(null, null));

        Path sample1Path = getFilePath("sample_text_compare_equal_1.pdf");
        Path sample2Path = getFilePath("sample_text_compare_differ_1.pdf");

        try (PdfDocument document1 = new PdfDocument(sample1Path);
             PdfDocument document2 = new PdfDocument(sample2Path)) {

            boolean notEqual = pdfCompareUtil.compare(document1, document2).isHasErrors();
            assertTrue(notEqual);
        }
    }

    private Path getFilePath(String filename) throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).toURI());
    }

}