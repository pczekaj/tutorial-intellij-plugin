package com.auto1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wkda.common.tests.annotation.WkdaIntegrationTest;

import static org.hamcrest.Matchers.*;
import static wkda.wiremock1.com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@WkdaIntegrationTest
public class OnClassLevelTest extends BaseIntegrationTest {

    @Test
    public void checkSomething() {
        //some assertions
    }

}
