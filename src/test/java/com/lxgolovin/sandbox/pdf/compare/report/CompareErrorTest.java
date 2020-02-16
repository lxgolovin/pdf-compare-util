package com.lxgolovin.sandbox.pdf.compare.report;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompareErrorTest {

    @Test
    void checkArgumentIsNull() {
        assertThrows(NullPointerException.class, () -> new CompareError(ErrorType.LINE_INSERTED, null));
        assertThrows(NullPointerException.class, () -> new CompareError(null, ErrorType.LINE_INSERTED.name()));
    }
}