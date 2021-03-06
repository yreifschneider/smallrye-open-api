/*
 * Copyright 2018 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smallrye.openapi.tck.extra;

import io.restassured.response.ValidatableResponse;
import org.eclipse.microprofile.openapi.tck.AppTestBase;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;
import test.io.smallrye.openapi.tck.BaseTckTest;
import test.io.smallrye.openapi.tck.TckTest;

import static org.hamcrest.Matchers.*;

/**
 * NOTE: It's not a TCK test, it only leverages the TCK test setup
 * 
 */
@TckTest
public class IgnoreJsonPropertiesTest extends BaseTckTest<IgnoreJsonPropertiesTest.ExtensionsTestArquillian> {

    public static class ExtensionsTestArquillian extends AppTestBase {
        @Deployment(name = "jsonignoreproperties")
        public static WebArchive createDeployment() {
            return ShrinkWrap.create(WebArchive.class, "airlines.war")
                    .addPackages(true, new String[] { "io.smallrye.openapi.tck.extra.jsonignoreproperties" })
                    .addAsManifestResource("openapi.yaml", "openapi.yaml");
        }

        @RunAsClient
        @Test(dataProvider = "formatProvider")
        public void testDirectIgnore(String type) {
            ValidatableResponse vr = this.callEndpoint(type);
            String schemaPath = "components.schemas.DirectIgnore.properties";
            vr.body(schemaPath + ".dontIgnoreMe", notNullValue());
            vr.body(schemaPath + ".ignoreMe", nullValue());
        }

        @RunAsClient
        @Test(dataProvider = "formatProvider")
        public void testInheritedIgnore(String type) {
            ValidatableResponse vr = this.callEndpoint(type);
            String schemaPath = "components.schemas.InheritIgnore.properties";
            vr.body(schemaPath + ".dontIgnoreMe", notNullValue());
            vr.body(schemaPath + ".ignoreMe", nullValue());
        }

        @RunAsClient
        @Test(dataProvider = "formatProvider")
        public void testInheritedIgnoreOverride(String type) {
            ValidatableResponse vr = this.callEndpoint(type);
            String schemaPath = "components.schemas.InheritIgnoreOverride.properties";
            vr.body(schemaPath + ".dontIgnoreMe", nullValue());
            vr.body(schemaPath + ".ignoreMe", notNullValue());
        }
    }
}
