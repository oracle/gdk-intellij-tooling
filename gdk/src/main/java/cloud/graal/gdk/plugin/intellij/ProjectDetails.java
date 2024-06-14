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

import cloud.graal.gdk.GdkUtils;
import cloud.graal.gdk.model.GdkCloud;
import cloud.graal.gdk.model.GdkProjectType;
import cloud.graal.gdk.model.GdkService;
import io.micronaut.starter.options.BuildTool;
import io.micronaut.starter.options.JdkVersion;
import io.micronaut.starter.options.Language;
import io.micronaut.starter.options.TestFramework;
import java.util.Collections;
import java.util.List;

public class ProjectDetails {
    String micronautVersion, defaultPackage, appName, baseDir;
    GdkProjectType type;
    JdkVersion javaVersion;
    boolean generateCode;
    BuildTool build;
    TestFramework test;
    Language lang;
    List<GdkCloud> providers;
    List<GdkService> services;
    List<String> features;

    private ProjectDetails() {}

    public static ProjectDetails extractFromUI(GDKModuleBuilder gdkBuilder) {
        ProjectDetails project = new ProjectDetails();
        // Micronaut version
//        project.micronautVersion = firstStep.micronautVersion.getSelectedItem().toString();
        project.micronautVersion = GdkUtils.getMicronautVersion();

        // Application type
        project.type = gdkBuilder.getGdkProjectType();

        // Java version
        project.javaVersion = JdkVersion.valueOf(gdkBuilder.getSourceLevel()); // gdkBuilder.getJavaVersion();

        // Language
        project.lang = gdkBuilder.getSelectedLanguage();

        // Application package & name
        project.defaultPackage = gdkBuilder.getPackageName();
        project.appName = gdkBuilder.getAppName();

        // Generate example code
        project.generateCode = gdkBuilder.getIncludeExamples();

        // Test framework
        project.test = gdkBuilder.getSelectedTest();

        // Build tools
        project.build = gdkBuilder.getSelectedType();

        // Cloud providers
        project.providers = gdkBuilder.getProviders();

        // Services
        project.services = gdkBuilder.getServices();

        // Features
        project.features = gdkBuilder.getSelectedFeatures();

        return project;
    }

    public static ProjectDetails getDefaultProject(String baseDir) {
        ProjectDetails project = new ProjectDetails();

        project.baseDir = baseDir;
        project.micronautVersion = GdkUtils.getMicronautVersion();
        project.type = GdkProjectType.DEFAULT_OPTION;
        project.javaVersion = JdkVersion.JDK_17;
        project.generateCode = Boolean.FALSE;
        project.defaultPackage = "com.example";
        project.appName = "demo";
        project.test = TestFramework.DEFAULT_OPTION;
        project.build = BuildTool.GRADLE;
        project.providers = Collections.emptyList();
        project.services = Collections.emptyList();
        project.features = Collections.emptyList();

        return project;
    }
}
