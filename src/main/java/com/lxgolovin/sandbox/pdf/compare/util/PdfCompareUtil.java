package com.lxgolovin.sandbox.pdf.compare.util;

import com.lxgolovin.sandbox.pdf.compare.report.CompareError;
import com.lxgolovin.sandbox.pdf.compare.report.CompareReport;
import com.lxgolovin.sandbox.pdf.compare.report.ErrorType;
import difflib.*;
import lombok.experimental.UtilityClass;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public final class PdfCompareUtil {

    public static int getPageCount(PdfDocument document) {
        return Optional.ofNullable(document)
                .map(PdfDocument::getPageCount)
                .orElseThrow(() -> new IllegalArgumentException("Pdf document should not be null"));
    }

    public static CompareReport compare(PdfDocument source, PdfDocument target) throws IOException {
        Optional.ofNullable(source).orElseThrow(() -> new IllegalArgumentException("Source pdf document should not be null"));
        Optional.ofNullable(target).orElseThrow(() -> new IllegalArgumentException("Target pdf document should not be null"));

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

    private static List<CompareError> reportDifference(String sourceText, String targetText) {
        List<String> sourceLines = Arrays.asList(sourceText.split("\\r?\\n"));
        List<String> targetLines = Arrays.asList(targetText.split("\\r?\\n"));
        Patch<String> patch = DiffUtils.diff(sourceLines, targetLines);

        return Stream.concat(
                Stream.concat(
                        getInsertedCompareError(patch),
                        getDeletedCompareError(patch)),
                getChangedCompareError(patch))
                .collect(Collectors.toList());
    }

    private static Stream<CompareError> getDeletedCompareError(Patch<String> patch) {
        return patch.getDeltas().stream()
                .filter(d -> d.getType().equals(Delta.TYPE.CHANGE))
                .map(d -> new CompareError(ErrorType.LINE_CHANGED, d.toString()));
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

    private static String getTextFromPdf(PdfDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(document.getPageCount());
        return stripper.getText(document.getDocument());
    }
}
