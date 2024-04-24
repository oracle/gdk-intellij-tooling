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

package cloud.graal.gcn.plugin.intellij;

import cloud.graal.gcn.GcnUtils;
import cloud.graal.gcn.model.GcnCloud;
import cloud.graal.gcn.model.GcnProjectType;
import cloud.graal.gcn.model.GcnService;
import io.micronaut.starter.options.BuildTool;
import io.micronaut.starter.options.JdkVersion;
import io.micronaut.starter.options.Language;
import io.micronaut.starter.options.TestFramework;
import java.util.Collections;
import java.util.List;

public class ProjectDetails {
    String micronautVersion, defaultPackage, appName, baseDir;
    GcnProjectType type;
    JdkVersion javaVersion;
    boolean generateCode;
    BuildTool build;
    TestFramework test;
    Language lang;
    List<GcnCloud> providers;
    List<GcnService> services;
    List<String> features;

    private ProjectDetails() {}

    public static ProjectDetails extractFromUI(GCNModuleBuilder gcnBuilder) {
        ProjectDetails project = new ProjectDetails();
        // Micronaut version
//        project.micronautVersion = firstStep.micronautVersion.getSelectedItem().toString();
        project.micronautVersion = GcnUtils.getMicronautVersion();

        // Application type
        project.type = gcnBuilder.getGcnProjectType();

        // Java version
        project.javaVersion = JdkVersion.valueOf(gcnBuilder.getSourceLevel()); // gcnBuilder.getJavaVersion();

        // Language
        project.lang = gcnBuilder.getSelectedLanguage();

        // Application package & name
        project.defaultPackage = gcnBuilder.getPackageName();
        project.appName = gcnBuilder.getAppName();

        // Generate example code
        project.generateCode = gcnBuilder.getIncludeExamples();

        // Test framework
        project.test = gcnBuilder.getSelectedTest();

        // Build tools
        project.build = gcnBuilder.getSelectedType();

        // Cloud providers
        project.providers = gcnBuilder.getProviders();

        // Services
        project.services = gcnBuilder.getServices();

        // Features
        project.features = gcnBuilder.getSelectedFeatures();

        return project;
    }

    public static ProjectDetails getDefaultProject(String baseDir) {
        ProjectDetails project = new ProjectDetails();

        project.baseDir = baseDir;
        project.micronautVersion = GcnUtils.getMicronautVersion();
        project.type = GcnProjectType.DEFAULT_OPTION;
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
