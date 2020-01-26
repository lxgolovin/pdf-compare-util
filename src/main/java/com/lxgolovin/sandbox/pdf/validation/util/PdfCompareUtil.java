package com.lxgolovin.sandbox.pdf.validation.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.util.Optional;

@Slf4j
public final class PdfCompareUtil {

    public static int getPageCount(PdfDocument document) {
        return Optional.ofNullable(document)
                .map(PdfDocument::getPageCount)
                .orElseThrow(() -> new IllegalArgumentException("Pdf document should not be null"));
    }

    public static boolean compare(PdfDocument source, PdfDocument target) {
        String sourceText = getTextFromPdf(source);
        String targetText = getTextFromPdf(target);

        return (sourceText.equals(targetText));
    }

    private static String getTextFromPdf(PdfDocument document) {
        return Optional.ofNullable(document)
                .map(PdfDocument::getDocument)
                .map(d -> {
                    try {
                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.setStartPage(-1);
                        stripper.setEndPage(-1);
                        String text = stripper.getText(d);
                        return text;
                    } catch (Exception e) {
                        //
                        return null;
                    }
                })
                .orElseThrow(() -> new IllegalArgumentException("Pdf document should not be null"));
    }
}
