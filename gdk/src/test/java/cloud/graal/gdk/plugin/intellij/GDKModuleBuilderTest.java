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

import cloud.graal.gdk.model.GdkCloud;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GDKModuleBuilderTest {

    private static final String TEST_PREFIX = "GDK-test";

    @org.junit.jupiter.api.Test
    void createEmptyProject(@org.junit.jupiter.api.io.TempDir Path tempDir) {
        String baseDir = tempDir.resolve(TEST_PREFIX).toString();
        ProjectDetails projectDetails = ProjectDetails.getDefaultProject(baseDir);

        GDKModuleBuilder.createProject(projectDetails);

        boolean projectCreated = Files.exists( Path.of(baseDir) );
        assertTrue(projectCreated, "Could not find the created project");

        boolean gradlewCreated = Files.exists( Path.of(baseDir).resolve("gradlew") );
        assertTrue(gradlewCreated, "Gradle has not been created in the project directory");

        boolean buildSrcCreated = Files.exists( Path.of(baseDir).resolve("buildSrc") );
        assertTrue(buildSrcCreated, "BuildSrc directory not been created in the project directory");
    }

    @org.junit.jupiter.api.Test
    void createProjectWithProviders(@org.junit.jupiter.api.io.TempDir Path tempDir) {
        String baseDir = tempDir.resolve(TEST_PREFIX).toString();
        ProjectDetails projectDetails = ProjectDetails.getDefaultProject(baseDir);
        projectDetails.providers = List.of(GdkCloud.AWS, GdkCloud.OCI);

        GDKModuleBuilder.createProject(projectDetails);

        boolean projectCreated = Files.exists( Path.of(baseDir) );
        assertTrue(projectCreated, "Could not find the created project");

        boolean awsCreated = Files.exists( Path.of(baseDir).resolve("aws") );
        assertTrue(awsCreated, "AWS support has not been created in the project directory");

        boolean ociCreated = Files.exists( Path.of(baseDir).resolve("oci") );
        assertTrue(ociCreated, "OCI support not been created in the project directory");
    }

    @org.junit.jupiter.api.Test
    void createProjectWithCorrectPackage(@org.junit.jupiter.api.io.TempDir Path tempDir) {
        String baseDir = tempDir.resolve(TEST_PREFIX).toString();
        ProjectDetails projectDetails = ProjectDetails.getDefaultProject(baseDir);

        GDKModuleBuilder.createProject(projectDetails);

        Path buildGradleFilePath = Path.of(baseDir).resolve("build.gradle");
        boolean projectCreated = Files.exists( buildGradleFilePath );
        assertTrue(projectCreated, "Could not find the build gradle file");

        try {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( new FileInputStream(buildGradleFilePath.toString())));
            String line;
            boolean foundPackage = false;
            while ((line = bufferedReader.readLine())!= null) {
                if (line.indexOf("com.example") != -1) {
                    foundPackage = true;
                }
            }
            assertTrue(foundPackage, "Package com.example was not found in build gradle file");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
