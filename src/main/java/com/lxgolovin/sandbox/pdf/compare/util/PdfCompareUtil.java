package com.lxgolovin.sandbox.pdf.compare.util;

import com.lxgolovin.sandbox.pdf.compare.report.CompareError;
import com.lxgolovin.sandbox.pdf.compare.report.CompareReport;
import com.lxgolovin.sandbox.pdf.compare.report.ErrorType;
import difflib.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public final class PdfCompareUtil {

    private final CompareReport report = new CompareReport();

    public static int getPageCount(PdfDocument document) {
        return Optional.ofNullable(document)
                .map(PdfDocument::getPageCount)
                .orElseThrow(() -> new IllegalArgumentException("Pdf document should not be null"));
    }

    public CompareReport compare(PdfDocument source, PdfDocument target) throws IOException {
        if ((source == null) || (target == null)) {
            throw new IllegalArgumentException("Source or target pdf document is null");
        }

        if (!isSameSize(source, target)) {
            report.add(new CompareError(ErrorType.WRONG_SIZE, "Number of pages in the documents differs"));
        }

        String sourceText = getTextFromPdf(source);
        String targetText = getTextFromPdf(target);
        if (!sourceText.equals(targetText)) {
            reportDifference(sourceText, targetText);
        }

        return report;
    }

    private void reportDifference(String sourceText, String targetText) {
        List<String> sourceLines = Arrays.asList(sourceText.split("\\r?\\n"));
        List<String> targetLines = Arrays.asList(targetText.split("\\r?\\n"));
        Patch<String> patch = DiffUtils.diff(sourceLines, targetLines);

        patch.getDeltas().stream()
                .filter(d -> d.getType().equals(Delta.TYPE.INSERT))
                .map(d -> new CompareError(ErrorType.LINE_INSERTED, d.toString()))
                .forEach(report::add);

        patch.getDeltas().stream()
                .filter(d -> d.getType().equals(Delta.TYPE.CHANGE))
                .map(d -> new CompareError(ErrorType.LINE_CHANGED, d.toString()))
                .forEach(report::add);

        patch.getDeltas().stream()
                .filter(d -> d.getType().equals(Delta.TYPE.DELETE))
                .map(d -> new CompareError(ErrorType.LINE_DELETED, d.toString()))
                .forEach(report::add);
    }

    private boolean isSameSize(PdfDocument source, PdfDocument target) {
        return source.getPageCount() == target.getPageCount();
    }

    private String getTextFromPdf(PdfDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(document.getPageCount());
        return stripper.getText(document.getDocument());
    }
}
