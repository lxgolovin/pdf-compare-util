package com.lxgolovin.sandbox.pdf.compare.util;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import com.lxgolovin.sandbox.pdf.compare.report.CompareError;
import com.lxgolovin.sandbox.pdf.compare.report.CompareReport;
import com.lxgolovin.sandbox.pdf.compare.report.ErrorType;
import difflib.*;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public final class PdfCompareUtil {

    public static int getPageCount(@NonNull PdfDocument document) {
        return Optional.of(document)
                .map(PdfDocument::getPageCount)
                .orElse(0);
    }

    public static CompareReport compare(@NonNull String originalText, @NonNull PdfDocument target) throws IOException {
        CompareReport report = new CompareReport();

        String targetText = getTextFromPdf(target);
        if (!originalText.equals(targetText)) {
            report.addAll(reportDifference(originalText, targetText));
        }

        return report;
    }

    public static CompareReport compare(@NonNull PdfDocument source, @NonNull PdfDocument target) throws IOException {
        CompareReport report = new CompareReport();

        if (!isSameSize(source, target)) {
            report.add(new CompareError(ErrorType.WRONG_SIZE, "Number of pages in the documents differs"));
        }

        String sourceText = getTextFromPdf(source);
        String targetText = getTextFromPdf(target);
        if (!sourceText.equals(targetText)) {
            report.addAll(reportDifference(sourceText, targetText));
        }

        return report;
    }

    public static String getTextFromPdf(PdfDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(document.getPageCount());
        return stripper.getText(document.getDocument());
    }

    private static List<CompareError> reportDifference(String sourceText, String targetText) {
        List<String> sourceLines = Arrays.asList(sourceText.split("\\r?\\n"));
        List<String> targetLines = Arrays.asList(targetText.split("\\r?\\n"));
        Patch<String> patch = DiffUtils.diff(sourceLines, targetLines);

        return Stream.of(
                getInsertedCompareError(patch),
                getChangedCompareError(patch),
                getDeletedCompareError(patch))
                .flatMap(s -> s)
                .collect(Collectors.toList());
    }

    private static Stream<CompareError> getDeletedCompareError(Patch<String> patch) {
        return patch.getDeltas().stream()
                .filter(d -> d.getType().equals(Delta.TYPE.DELETE))
                .map(d -> new CompareError(ErrorType.LINE_DELETED, d.toString()));
    }

    private static Stream<CompareError> getChangedCompareError(Patch<String> patch) {
        return patch.getDeltas().stream()
                .filter(d -> d.getType().equals(Delta.TYPE.CHANGE))
                .map(d -> new CompareError(ErrorType.LINE_CHANGED, d.toString()));
    }

    private static Stream<CompareError> getInsertedCompareError(Patch<String> patch) {
        return patch.getDeltas().stream()
                .filter(d -> d.getType().equals(Delta.TYPE.INSERT))
                .map(d -> new CompareError(ErrorType.LINE_INSERTED, d.toString()));
    }

    private static boolean isSameSize(PdfDocument source, PdfDocument target) {
        return source.getPageCount() == target.getPageCount();
    }
}
