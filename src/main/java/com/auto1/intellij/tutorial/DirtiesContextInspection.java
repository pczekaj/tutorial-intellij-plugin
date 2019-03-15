package com.auto1.intellij.tutorial;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class DirtiesContextInspection extends AbstractBaseJavaLocalInspectionTool {
    private static final Logger LOG = Logger.getInstance(DirtiesContextInspection.class);
    private static final String DIRTIES_CONTEXT = "org.springframework.test.annotation.DirtiesContext";

    private final LocalQuickFix deleteQuickFix = new DeleteQuickFix();

    @NonNls
    private static final String DESCRIPTION_TEMPLATE =
            PluginBundle.message("inspection.dirties.context.problem.descriptor");

    @NotNull
    @Override
    public String getDisplayName() {
        return PluginBundle.message("inspection.dirties.context.display.name");
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

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            @Override
            public void visitAnnotation(PsiAnnotation annotation) {
                super.visitAnnotation(annotation);

                String qualifiedName = annotation.getQualifiedName();

                if (DIRTIES_CONTEXT.equals(qualifiedName)) {
                    holder.registerProblem(annotation, DESCRIPTION_TEMPLATE, deleteQuickFix);
                }
            }
        };
    }

    private static class DeleteQuickFix implements LocalQuickFix {
        @NotNull
        public String getName() {
            return PluginBundle.message("inspection.dirties.context.use.quickfix");
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            try {
                descriptor.getPsiElement().delete();
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }

        @NotNull
        public String getFamilyName() {
            return getName();
        }
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}

