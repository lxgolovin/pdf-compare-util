package com.lxgolovin.sandbox.pdf.compare.report;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CompareReport {

    @Setter
    private boolean hasErrors;

    private final List<CompareError> compareErrors = new ArrayList<>();

    public void add(CompareError error) {
        compareErrors.add(error);
        setHasErrors(true);
    }
}
