package com.auto1.intellij.tutorial;

public class DirtiesContextInspectionTest extends PluginInspectionTest {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        attachMavenLibrary("org.springframework:spring-test:5.1.5.RELEASE");
    }

    public void testOnClassLevelTest() {
        doTest();
    }

    private void doTest() {
        String quickFix = PluginBundle.message("inspection.dirties.context.use.quickfix");
        doTestFile("DirtiesContextInspectionTest/" + getTestName(false),
                new DirtiesContextInspection(),
                quickFix);
    }
}
