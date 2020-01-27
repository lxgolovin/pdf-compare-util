package com.lxgolovin.sandbox.pdf.compare.util;

import lombok.Getter;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public final class PdfDocument implements AutoCloseable {

    @Getter
    private final PDDocument document;

    @Getter
    private final int pageCount;

    public PdfDocument(Path path) throws IOException {
        Optional.ofNullable(path)
                .orElseThrow(() -> new IllegalArgumentException("Path to the file cannot null"));
        this.document = PDDocument.load(path.toFile());
        this.pageCount = document.getNumberOfPages();
    }

    @Override
    public void close() throws IOException {
        if (document != null) {
            document.close();
        }
    }
}
