package com.lxgolovin.sandbox.pdf.compare.util;

import lombok.Getter;
import lombok.NonNull;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Getter
public final class PdfDocument implements AutoCloseable {

    private final PDDocument document;

    private final int pageCount;

    public PdfDocument(@NonNull Path path) throws IOException {
        this.document = PDDocument.load(path.toFile());
        this.pageCount = document.getNumberOfPages();
    }

    public PdfDocument(@NonNull InputStream is) throws IOException {
        this.document = PDDocument.load(is);
        this.pageCount = document.getNumberOfPages();
    }

    @Override
    public void close() throws IOException {
        if (document != null) {
            document.close();
        }
    }
}
