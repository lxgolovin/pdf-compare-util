package com.lxgolovin.sandbox.pdf.compare.report;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CompareReportTest {

    @Test
    void checkArgumentIsNull() {
        CompareReport report = new CompareReport();
        assertThrows(NullPointerException.class, () -> report.add(null));
        assertThrows(NullPointerException.class, () -> report.addAll(null));
    }
}