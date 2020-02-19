package com.lxgolovin.sandbox.pdf.compare.util;

import com.lxgolovin.sandbox.file.util.TextToFile;
import com.lxgolovin.sandbox.pdf.compare.report.CompareReport;
import com.lxgolovin.sandbox.pdf.compare.report.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("pdf-compare-util-test")
class PdfCompareUtilTest {
    private static Stream<Arguments> providePdfTestsData() {
        return Stream.of(
                Arguments.of("sample_text_compare_differ_change.pdf", ErrorType.LINE_CHANGED),
                Arguments.of("sample_text_compare_differ_delete.pdf", ErrorType.LINE_DELETED),
                Arguments.of("sample_text_compare_differ_insert.pdf", ErrorType.LINE_INSERTED),
                Arguments.of("sample_text_compare_differ_pages.pdf", ErrorType.WRONG_SIZE)
        );
    }

    private static Stream<Arguments> providePdfForTextTestsData() {
        return Stream.of(
                Arguments.of("sample_text_compare_differ_change.pdf", ErrorType.LINE_CHANGED),
                Arguments.of("sample_text_compare_differ_delete.pdf", ErrorType.LINE_DELETED),
                Arguments.of("sample_text_compare_differ_insert.pdf", ErrorType.LINE_INSERTED)
        );
    }

    @Test
    void getPageCount() throws IOException, URISyntaxException {
        Path samplePath = getFilePath("sample_text_compare_equal_1.pdf");
        try (PdfDocument document = new PdfDocument(samplePath)) {
            assertEquals(5, document.getPageCount());
            assertEquals(5, PdfCompareUtil.getPageCount(document));
        }
    }

    @ParameterizedTest
    @NullSource
    void compareNullPdf(PdfDocument document) {
        assertThrows(NullPointerException.class, () -> PdfCompareUtil.getPageCount(document));
        assertThrows(NullPointerException.class, () -> PdfCompareUtil.compare("", document));
    }

    @Test
    void compareEqualPdfByText() throws URISyntaxException, IOException {
        Path originalFile = getFilePath("sample_text_compare_equal_1.pdf");
        Path targetFile = getFilePath("sample_text_compare_equal_2.pdf");

        CompareReport report = comparePdfVsPdf(originalFile, targetFile);
        assertFalse(report.isHasErrors());
        assertTrue(report.getCompareErrors().isEmpty());
    }

    @ParameterizedTest(name = "#{index} - compare pdf VS pdf {1}")
    @MethodSource("providePdfTestsData")
    void comparePdfVsPdfByText(String targetFileName, ErrorType errorType) throws URISyntaxException, IOException {
        Path originalFile = getFilePath("sample_text_compare_equal_1.pdf");
        Path targetFile = getFilePath(targetFileName);

        CompareReport report = comparePdfVsPdf(originalFile, targetFile);
        assertTrue(report.isHasErrors());
        assertFalse(report.getCompareErrors().isEmpty());
        boolean isNotOnlyChanged = report.getCompareErrors().stream()
                .anyMatch(e -> e.getErrorType() != errorType);
        assertFalse(isNotOnlyChanged);
    }

    @ParameterizedTest(name = "#{index} - compare pdf VS text {1}")
    @MethodSource("providePdfForTextTestsData")
    void compareChangedPdfVsTextByText(String targetFileName, ErrorType errorType) throws URISyntaxException, IOException {
        Path originalFile = getFilePath("sample_text_compare_equal_1.txt");
        String originalText = TextToFile.read(originalFile);
        Path targetFile = getFilePath(targetFileName);

        CompareReport report = comparePdfVsText(originalText, targetFile);
        assertTrue(report.isHasErrors());
        assertFalse(report.getCompareErrors().isEmpty());
        boolean isNotOnlyChanged = report.getCompareErrors().stream()
                .anyMatch(e -> e.getErrorType() != errorType);
        assertFalse(isNotOnlyChanged);
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

    private CompareReport comparePdfVsPdf(Path original, Path target) throws IOException {
        try (PdfDocument originalPdf = new PdfDocument(original);
             PdfDocument targetPdf = new PdfDocument(target)) {
            return PdfCompareUtil.compare(originalPdf, targetPdf);
        }
    }

    private CompareReport comparePdfVsText(String originalText, Path targetFile) throws IOException {
        try (PdfDocument targetPdf = new PdfDocument(targetFile)) {
            return PdfCompareUtil.compare(originalText, targetPdf);
        }
    }

    private Path getFilePath(String filename) throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(filename)).toURI());
    }
}