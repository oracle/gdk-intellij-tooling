/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cloud.graal.gdk.plugin.intellij;

import cloud.graal.gdk.GdkProjectCreator;
import cloud.graal.gdk.model.GdkProjectType;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.starter.options.BuildTool;
import io.micronaut.starter.options.TestFramework;

import static org.junit.jupiter.api.Assertions.*;

public class GDKCoreIntegrationTest {
    @org.junit.jupiter.api.Test
    void applicationContextCreated() {
        ApplicationContextBuilder contextBuilder = ApplicationContext.builder();
        contextBuilder.deduceEnvironment(false);
        ApplicationContext context = contextBuilder.start();

        GdkProjectCreator projectCreator = context.getBean(GdkProjectCreator.class);
        assertNotNull(projectCreator, "Failed to get GdkProjectCreator instance");
    }

    @org.junit.jupiter.api.Test
    void listAllTestFrameworks() {
        TestFramework[] testFrameworks = TestFramework.values();

        assertNotNull(testFrameworks, "Test framework list is null");
        assertNotEquals(0, testFrameworks.length, "No test frameworks found");

        String mustContain = "junit";
        boolean contains = false;
        for (TestFramework testFramework : testFrameworks) {
            if (testFramework.getName().equals(mustContain)) {
                contains = true;
            }
        }
        assertTrue(contains, "JUnit test not present in list of test frameworks");
    }

    @org.junit.jupiter.api.Test
    void listApplicationTypes() {
        GdkProjectType[] projectTypes = GdkProjectType.values();

        assertNotNull(projectTypes, "Project type list is null");
        assertNotEquals(0, projectTypes.length, "No project types found");

        String mustContain = "application";
        boolean contains = false;
        for (GdkProjectType applicationType : projectTypes) {
            if (applicationType.getName().equals(mustContain)) {
                contains = true;
            }
        }
        assertTrue(contains, "Application project type not present in list of project types");
    }

    @org.junit.jupiter.api.Test
    void listAllBuildTools() {
        BuildTool[] buildTools = BuildTool.values();

        assertNotNull(buildTools, "Build tools list is null");
        assertNotEquals(0, buildTools.length, "No build tools found");
    }
}
