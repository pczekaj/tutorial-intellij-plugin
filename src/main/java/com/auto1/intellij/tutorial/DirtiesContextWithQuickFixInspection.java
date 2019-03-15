package com.auto1.intellij.tutorial;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class DirtiesContextWithQuickFixInspection extends AbstractBaseJavaLocalInspectionTool {
    private static final String DIRTIES_CONTEXT = "org.springframework.test.annotation.DirtiesContext";

    @NonNls
    private static final String DESCRIPTION_TEMPLATE = "Usage of @DirtiesContext makes integration tests slower";

    @NotNull
    @Override
    public String getDisplayName() {
        return "Usage of @DirtiesContext is not recommended";
    }

    @NotNull
    @Override
    public String getGroupDisplayName() {
        return GroupNames.PERFORMANCE_GROUP_NAME;
    }

    @NotNull
    @Override
    public String getShortName() {
        return "DirtiesContext";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitAnnotation(PsiAnnotation annotation) {
                super.visitAnnotation(annotation);

                String qualifiedName = annotation.getQualifiedName();

                if (DIRTIES_CONTEXT.equals(qualifiedName)) {
                    holder.registerProblem(annotation, DESCRIPTION_TEMPLATE, new DeleteQuickFix());
                }
            }
        };
    }

    private static class DeleteQuickFix implements LocalQuickFix {
        public String getName() {
            return "Removes usage of @DirtiesContext";
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            descriptor.getPsiElement().delete();
        }

        public String getFamilyName() {
            return getName();
        }
    }
}
