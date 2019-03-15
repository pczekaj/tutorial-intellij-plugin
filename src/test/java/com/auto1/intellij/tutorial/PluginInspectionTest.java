package com.auto1.intellij.tutorial;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.JavaAwareProjectJdkTableImpl;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.LanguageLevelModuleExtension;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class PluginInspectionTest extends LightPlatformCodeInsightFixtureTestCase {

    @NotNull
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor() {
            @Nullable
            @Override
            public Sdk getSdk() {
                return JavaAwareProjectJdkTableImpl.getInstanceEx().getInternalJdk();
            }

            @Override
            protected void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ContentEntry contentEntry) {

                LanguageLevelModuleExtension extension = model.getModuleExtension(LanguageLevelModuleExtension.class);
                if (extension != null) {
                    extension.setLanguageLevel(LanguageLevel.JDK_1_8);
                }

            }
        };
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    protected List<HighlightInfo> doTestFile(String testName, LocalInspectionTool inspectionTool, String intentionHint) {
        ensureValidJdk();

        myFixture.configureByFile(testName + ".java");

        return runInspection(testName, inspectionTool, intentionHint);
    }

    @NotNull
    private List<HighlightInfo> runInspection(String directoryName, LocalInspectionTool inspectionTool, String intentionHint) {
        myFixture.enableInspections(inspectionTool);
        List<HighlightInfo> highlightInfos = myFixture.doHighlighting();

        assertThat(highlightInfos).isNotEmpty();

        if (intentionHint != null) {
            IntentionAction quickFixAction = myFixture.findSingleIntention(intentionHint);
            assertThat(quickFixAction).isNotNull();

            myFixture.launchAction(quickFixAction);
            myFixture.checkResultByFile(directoryName + ".after.java");
        }

        return highlightInfos;
    }

    private void ensureValidJdk() {
        PsiClass stringClass = findClass("java.lang.String");
        assertThat(stringClass)
                .describedAs("If String is unknown class this means that proper JDK wasn't configured")
                .isNotNull();
    }

    protected PsiClass findClass(String qualifiedName) {
        return JavaPsiFacade.getInstance(getProject()).findClass(qualifiedName, GlobalSearchScope.allScope(getProject()));
    }

    protected void attachMavenLibrary(String mavenArtifact) {
        File[] jars = getMavenArtifacts(mavenArtifact);
        Arrays.stream(jars).forEach(jar -> {
            String name = jar.getName();
            PsiTestUtil.addLibrary(myFixture.getProjectDisposable(), myModule, name, jar.getParent(), name);
        });
    }

    private File[] getMavenArtifacts(String... mavenArtifacts) {
        File[] files = Maven.resolver().resolve(mavenArtifacts).withoutTransitivity().asFile();
        if (files.length == 0) {
            throw new IllegalArgumentException("Failed to resolve artifacts " + Arrays.toString(mavenArtifacts));
        }
        return files;
    }
}
