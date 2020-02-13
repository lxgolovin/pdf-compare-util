package com.lxgolovin.sandbox.pdf.compare.util;

import com.lxgolovin.sandbox.file.util.TextToFile;
import com.lxgolovin.sandbox.pdf.compare.report.CompareReport;
import com.lxgolovin.sandbox.pdf.compare.report.ErrorType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PdfCompareUtilTest {

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
    void compareNullPdf() {
        assertThrows(IllegalArgumentException.class, () -> PdfCompareUtil.compare("", null));
    }

    @Test
    void compareEqualPdfByText() throws URISyntaxException, IOException {
        Path sample1Path = getFilePath("sample_text_compare_equal_1.pdf");
        Path sample2Path = getFilePath("sample_text_compare_equal_2.pdf");

        try (PdfDocument document1 = new PdfDocument(sample1Path);
             PdfDocument document2 = new PdfDocument(sample2Path)) {

            CompareReport report = PdfCompareUtil.compare(document1, document2);

            assertFalse(report.isHasErrors());
            assertTrue(report.getCompareErrors().isEmpty());
        }
    }

    @Test
    void compareChangedPdfByText() throws URISyntaxException, IOException {
        Path sample1Path = getFilePath("sample_text_compare_equal_1.pdf");
        Path sample2Path = getFilePath("sample_text_compare_differ_change.pdf");

        try (PdfDocument document1 = new PdfDocument(sample1Path);
             PdfDocument document2 = new PdfDocument(sample2Path)) {

            CompareReport report = PdfCompareUtil.compare(document1, document2);

            assertTrue(report.isHasErrors());
            assertFalse(report.getCompareErrors().isEmpty());
            boolean isNotOnlyChanged = report.getCompareErrors().stream()
                    .anyMatch(e -> e.getErrorType() != ErrorType.LINE_CHANGED);
            assertFalse(isNotOnlyChanged);
        }
    }

    @Test
    void compareInsertedPdfByText() throws URISyntaxException, IOException {
        Path sample1Path = getFilePath("sample_text_compare_equal_1.pdf");
        Path sample2Path = getFilePath("sample_text_compare_differ_insert.pdf");

        try (PdfDocument document1 = new PdfDocument(sample1Path);
             PdfDocument document2 = new PdfDocument(sample2Path)) {

            CompareReport report = PdfCompareUtil.compare(document1, document2);

            assertTrue(report.isHasErrors());
            assertFalse(report.getCompareErrors().isEmpty());
            boolean isNotOnlyInserted = report.getCompareErrors().stream()
                    .anyMatch(e -> e.getErrorType() != ErrorType.LINE_INSERTED);
            assertFalse(isNotOnlyInserted);
        }
    }

    @Test
    void compareDeletedPdfByText() throws URISyntaxException, IOException {
        Path sample1Path = getFilePath("sample_text_compare_equal_1.pdf");
        Path sample2Path = getFilePath("sample_text_compare_differ_delete.pdf");

        try (PdfDocument document1 = new PdfDocument(sample1Path);
             PdfDocument document2 = new PdfDocument(sample2Path)) {

            CompareReport report = PdfCompareUtil.compare(document1, document2);

            assertTrue(report.isHasErrors());
            assertFalse(report.getCompareErrors().isEmpty());
            boolean isNotOnlyDeleted = report.getCompareErrors().stream()
                    .anyMatch(e -> e.getErrorType() != ErrorType.LINE_DELETED);
            assertFalse(isNotOnlyDeleted);
        }
    }

    @Test
    void comparePdfPagesNumberMismatch() throws URISyntaxException, IOException {
        Path sample1Path = getFilePath("sample_text_compare_equal_1.pdf");
        Path sample2Path = getFilePath("sample_text_compare_differ_pages.pdf");

        try (PdfDocument document1 = new PdfDocument(sample1Path);
             PdfDocument document2 = new PdfDocument(sample2Path)) {

            CompareReport report = PdfCompareUtil.compare(document1, document2);

            assertTrue(report.isHasErrors());
            assertFalse(report.getCompareErrors().isEmpty());
            boolean notOnlyPagesMismatch = report.getCompareErrors().stream()
                    .anyMatch(e -> e.getErrorType() != ErrorType.WRONG_SIZE);
            assertFalse(notOnlyPagesMismatch);
        }
    }

    @Test
    void writeToTextFile() throws URISyntaxException, IOException {
        Path samplePath = getFilePath("sample_text_compare_equal_1.pdf");
        Path tempFile = Paths.get("text_file.txt");
        tempFile.toFile().deleteOnExit();

        try (PdfDocument document = new PdfDocument(samplePath)) {
            String text = PdfCompareUtil.getTextFromPdf(document);
            TextToFile.write(tempFile, text);
        }

        assertTrue(Files.exists(tempFile));
        assertTrue(tempFile.toFile().length() > 0);
    }

    @Test
    void compareChangedPdfVsTextByText() throws URISyntaxException, IOException {
        Path sample1Path = getFilePath("sample_text_compare_equal_1.txt");
        Path sample2Path = getFilePath("sample_text_compare_differ_change.pdf");
        String originalText = new String(Files.readAllBytes(sample1Path));

        try (PdfDocument document2 = new PdfDocument(sample2Path)) {
            CompareReport report = PdfCompareUtil.compare(originalText, document2);

            assertTrue(report.isHasErrors());
            assertFalse(report.getCompareErrors().isEmpty());
            boolean isNotOnlyChanged = report.getCompareErrors().stream()
                    .anyMatch(e -> e.getErrorType() != ErrorType.LINE_CHANGED);
            assertFalse(isNotOnlyChanged);
        }
    }

    @Test
    void compareInsertedPdfVsTextByText() throws URISyntaxException, IOException {
        Path sample1Path = getFilePath("sample_text_compare_equal_1.txt");
        Path sample2Path = getFilePath("sample_text_compare_differ_insert.pdf");
        String originalText = new String(Files.readAllBytes(sample1Path));

        try (PdfDocument document2 = new PdfDocument(sample2Path)) {
            CompareReport report = PdfCompareUtil.compare(originalText, document2);

            assertTrue(report.isHasErrors());
            assertFalse(report.getCompareErrors().isEmpty());
            boolean isNotOnlyInserted = report.getCompareErrors().stream()
                    .anyMatch(e -> e.getErrorType() != ErrorType.LINE_INSERTED);
            assertFalse(isNotOnlyInserted);
        }
    }

    @Test
    void compareDeletedPdfVsTextByText() throws URISyntaxException, IOException {
        Path sample2Path = getFilePath("sample_text_compare_differ_delete.pdf");
        Path sample1Path = getFilePath("sample_text_compare_equal_1.txt");
        String originalText = new String(Files.readAllBytes(sample1Path));

        try (PdfDocument document2 = new PdfDocument(sample2Path)) {
            CompareReport report = PdfCompareUtil.compare(originalText, document2);

            assertTrue(report.isHasErrors());
            assertFalse(report.getCompareErrors().isEmpty());
            boolean isNotOnlyDeleted = report.getCompareErrors().stream()
                    .anyMatch(e -> e.getErrorType() != ErrorType.LINE_DELETED);
            assertFalse(isNotOnlyDeleted);
        }
    }

    private Path getFilePath(String filename) throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).toURI());
    }
}