package com.auto1.intellij.tutorial;

import com.intellij.codeInspection.InspectionToolProvider;
import org.jetbrains.annotations.NotNull;

public class CodeInspectionProvider implements InspectionToolProvider {
    @Override
    @NotNull
    public Class[] getInspectionClasses() {
        return new Class[]{
                DirtiesContextInspection.class
        };
    }
}
