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

import static cloud.graal.gcn.GcnGeneratorContext.EXAMPLE_CODE;
import cloud.graal.gcn.GcnProjectCreator;
import cloud.graal.gcn.feature.GcnTestedFeatures;
import cloud.graal.gcn.model.GcnCloud;
import cloud.graal.gcn.model.GcnProjectType;
import cloud.graal.gcn.model.GcnService;
import com.intellij.ide.starters.local.GeneratorAsset;
import com.intellij.ide.starters.local.Library;
import com.intellij.ide.starters.local.LibraryCategory;
import com.intellij.ide.starters.local.Starter;
import com.intellij.ide.starters.local.StarterContext;
import com.intellij.ide.starters.local.StarterContextProvider;
import com.intellij.ide.starters.local.StarterModuleBuilder;
import com.intellij.ide.starters.local.StarterPack;
import com.intellij.ide.starters.local.wizard.StarterInitialStep;
import com.intellij.ide.starters.local.wizard.StarterLibrariesStep;
import com.intellij.ide.starters.shared.CustomizedMessages;
import com.intellij.ide.starters.shared.LibraryLink;
import com.intellij.ide.starters.shared.LibraryLinkType;
import com.intellij.ide.starters.shared.StarterLanguage;
import com.intellij.ide.starters.shared.StarterProjectType;
import com.intellij.ide.starters.shared.StarterTestRunner;
import com.intellij.ide.starters.shared.StarterWizardSettings;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.projectRoots.impl.DependentSdkType;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.util.lang.JavaVersion;
import icons.SdkIcons;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.starter.application.OperatingSystem;
import io.micronaut.starter.feature.Feature;
import io.micronaut.starter.io.ConsoleOutput;
import io.micronaut.starter.io.FileSystemOutputHandler;
import io.micronaut.starter.options.BuildTool;
import io.micronaut.starter.options.JdkVersion;
import io.micronaut.starter.options.Language;
import io.micronaut.starter.options.TestFramework;
import io.micronaut.starter.util.NameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.Icon;
import org.jetbrains.annotations.NonNls;


public class GCNModuleBuilder extends StarterModuleBuilder {

    static final Key<GcnProjectType> GCN_TYPE_KEY = Key.create(GcnProjectType.class.getName());
    static final Key<List<GcnCloud>> GCN_CLOUDS_KEY = Key.create(GcnCloud.class.getName());
    static final Key<Integer> GCN_SOURCE_KEY = Key.create(Integer.class.getName());
    private static final @NonNls String GUIDE_URL = "https://graal.cloud/gcn/modules/";

    private StarterContextProvider internalCp;
    private StarterContext featuresContext;
    private Collection<Feature> features;
    public GCNModuleBuilder() {
    }

    public static void createProject(ProjectDetails project) {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(GCNModuleBuilder.class.getClassLoader());
            ApplicationContextBuilder ctxBuilder = ApplicationContext.builder();
            ctxBuilder.deduceEnvironment(false);
            ApplicationContext ctx = ctxBuilder.start();
            GcnProjectCreator projectCreator = ctx.getBean(GcnProjectCreator.class);

            // app name + package
            String name = project.defaultPackage + '.' + project.appName;

            // detect operating system
            OperatingSystem os;
            if (SystemInfo.isWindows) os = OperatingSystem.WINDOWS;
            else if (SystemInfo.isMac) os = OperatingSystem.MACOS;
            else if (SystemInfo.isLinux) os = OperatingSystem.LINUX;
            else if (SystemInfo.isSolaris) os = OperatingSystem.SOLARIS;
            else os = OperatingSystem.LINUX; // fallback

            File appDirectory = new File(project.baseDir);

            projectCreator.create(project.type, NameUtils.parse(name), project.lang,
                    project.test, project.build, project.providers, project.services,
                    project.features, project.javaVersion.majorVersion(), os,
                    Map.of(EXAMPLE_CODE, project.generateCode),
                    new FileSystemOutputHandler(appDirectory, ConsoleOutput.NOOP), ConsoleOutput.NOOP);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    @Override
    protected List<GeneratorAsset> getAssets(Starter strtr) {
        return Collections.emptyList();
    }

    @Override
    protected StarterPack getStarterPack() {
        List<Library> libs = new ArrayList<>();
        for (GcnService service : GcnService.values()) {
            String guideUrl = GUIDE_URL+"#"+service.getDocumentationModuleName();
            LibraryLink libraryLink = new LibraryLink(LibraryLinkType.GUIDE, guideUrl, null);
            libs.add(new Library(service.name(), null, service.getTitle(),
                    service.getDescription(),null, null, Collections.singletonList(libraryLink),
                    null, false, false, Collections.emptySet()));
        }
        return new StarterPack("GCN project", Collections.singletonList(new Starter("GCNS","GCN Service", getDependencyConfig("/starters/gcn.xml"), libs)));
    }

    StarterPack getFeaturesStarterPack() {
        List<Starter> starters = new ArrayList<>();
        List<Library> libs = new ArrayList<>();
        Map<String, LibraryCategory> categories = new HashMap<>();
        features = GCNModuleBuilder.getFeatures();
        for (Feature f : features) {
            if (GcnTestedFeatures.isFeatureGcnTested(f) && f.getTitle() != null && f.getDescription() != null) {
                String cat = f.getCategory();
                LibraryCategory category = new LibraryCategory(cat, null, cat, cat);
                categories.putIfAbsent(cat, category);
                List<LibraryLink> docs = getLibraryLink(f);
                libs.add(new Library(f.getName(), null, f.getTitle(), f.getDescription(),null, null, docs, categories.get(cat), false, false, Collections.emptySet()));
            }
        }
        libs.sort(new Comparator<Library>() {
            @Override
            public int compare(Library o1, Library o2) {
                int r = o1.getCategory().getTitle().compareTo(o2.getCategory().getTitle());
                if (r == 0) r = o1.getTitle().compareTo(o2.getTitle());
                return r;
            }
        });
        Starter s = new Starter("GCNF", "GCN Feature", getDependencyConfig("/starters/gcn.xml"), libs);
        starters.add(s);
        return new StarterPack("GCN project", starters);
    }

    @Override
    public void applyAdditionalChanges(Module module) {
        ProjectDetails projectDetails = ProjectDetails.extractFromUI(this);

        projectDetails.baseDir = module.getProject().getBasePath();
        createProject(projectDetails);
        fixGradleProject(projectDetails);
    }

    List<GcnService> getServices() {
        List<GcnService> services = new ArrayList<>();
        for (String libId : getStarterContext().getLibraryIds()) {
            services.add(GcnService.valueOf(libId));
        }
        return services;
    }

    List<String> getSelectedFeatures() {
        return new ArrayList<>(featuresContext.getLibraryIds());
    }

    @Override
    protected List<StarterLanguage> getLanguages() {
       List<StarterLanguage> languages = new ArrayList<>();
        for (Language l : Language.values()) {
            String name = l.getName();
            String capName = name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
            StarterLanguage type = new StarterLanguage(l.name(), capName, name, true, null);
            if (l.equals(Language.DEFAULT_OPTION)) {
                languages.add(0, type);
            } else {
                //languages.add(type);
            }
        }
        return languages;
    }

    Language getSelectedLanguage() {
        StarterLanguage language = getStarterContext().getLanguage();
        return Language.valueOf(language.getId());
    }

    @Override
    protected boolean isExampleCodeProvided() {
        return true;
    }

    boolean getIncludeExamples() {
        return getStarterContext().getIncludeExamples();
    }

    @Override
    protected CustomizedMessages getCustomizedMessages() {
        CustomizedMessages msg = new CustomizedMessages();
        msg.setDependenciesLabel(GCNBundle.message("gcn.create.new.services"));
        msg.setNoDependenciesSelectedLabel(GCNBundle.message("gcn.create.new.no.services"));
        msg.setSelectedDependenciesLabel(GCNBundle.message("gcn.create.new.added.services"));
        return msg;
    }

    @Override
    protected List<StarterProjectType> getProjectTypes() {
        BuildTool GCN_DEFAULT = BuildTool.GRADLE; // Micronaut switched to Kotlin DSL, but GCN sticking with Groovy for now (see GcnProjectCreator)
        List<StarterProjectType> builds = new ArrayList<>();
        for (BuildTool b : BuildTool.values()) {
            StarterProjectType type = new StarterProjectType(b.name(), b.getTitle(), b.getTitle());
            if (b.equals(GCN_DEFAULT)) {
                builds.add(0, type);
            } else {
                builds.add(type);
            }
        }
        return builds;
    }

    BuildTool getSelectedType() {
        StarterProjectType projectType = getStarterContext().getProjectType();
        return BuildTool.valueOf(projectType.getId());
    }

    @Override
    protected List<StarterTestRunner> getTestFrameworks() {
       List<StarterTestRunner> tests = new ArrayList<>();
        for (TestFramework t : TestFramework.values()) {
            StarterTestRunner type = new StarterTestRunner(t.name(), t.getTitle());
            if (t.equals(TestFramework.DEFAULT_OPTION)) {
                tests.add(0, type);
            } else {
                tests.add(type);
            }
        }
        return tests;
    }

    TestFramework getSelectedTest() {
        StarterTestRunner testFramework = getStarterContext().getTestFramework();
        return TestFramework.valueOf(testFramework.getId());
    }

    JdkVersion getJavaVersion() {
        JavaVersion jver = JavaVersion.tryParse(getModuleJdk().getVersionString());
        if (jver != null) {
            try {
                return JdkVersion.valueOf(jver.feature);
            } catch (IllegalArgumentException ex) {
                // unsupported JDK
                return JdkVersion.JDK_17;
            }
        }
        return JdkVersion.JDK_17;
    }

    GcnProjectType getGcnProjectType() {
        return getStarterContext().getUserData(GCN_TYPE_KEY);
    }

    List<GcnCloud> getProviders() {
        return getStarterContext().getUserData(GCN_CLOUDS_KEY);
    }

    String getPackageName() {
        return getStarterContext().getGroup();
    }

    String getAppName() {
        return getStarterContext().getName();
    }

    Integer getSourceLevel() {
        return getStarterContext().getUserData(GCN_SOURCE_KEY);
    }

    @Override
    public String getDescription() {
        return "Graal cloud native module type";
    }

    @Override
    public String getPresentableName() {
        return "Graal Cloud Native";
    }

    @Override
    public Icon getNodeIcon() {
        return SdkIcons.gcn_default_icon;
    }

    @Override
    public String getBuilderId() {
        return "GCN_MODULE_TYPE";
    }

    public static Collection<Feature> getFeatures() {
        try {
            ApplicationContextBuilder ctxBuilder = ApplicationContext.builder();
            ctxBuilder.deduceEnvironment(false);
            ApplicationContext ctx = ctxBuilder.start();
            return ctx.getBeansOfType(Feature.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<LibraryLink> getLibraryLink(Feature f) {
        String docs = f.getMicronautDocumentation();
        if (docs != null) {
            LibraryLink libraryLink = new LibraryLink(LibraryLinkType.GUIDE, docs, null);
            return Collections.singletonList(libraryLink);
        }
        return Collections.emptyList();
    }

    @Override
    public ModuleType<?> getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    protected StarterInitialStep createOptionsStep(StarterContextProvider contextProvider) {
        return new GCNStarterInitialStep(contextProvider);
    }

    @Override
    protected StarterLibrariesStep createLibrariesStep(StarterContextProvider contextProvider) {
        internalCp = contextProvider;
        return super.createLibrariesStep(contextProvider);
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(WizardContext context, ModulesProvider modulesProvider) {
        ModuleWizardStep[] createWizardSteps = super.createWizardSteps(context, modulesProvider);
        ModuleWizardStep[] steps = Arrays.copyOf(createWizardSteps, createWizardSteps.length+1);
        steps[createWizardSteps.length] = createFeaturesStep(context);
        return steps;
    }

    private StarterLibrariesStep createFeaturesStep(WizardContext context) {
        Disposable disposable = context.getDisposable();
        featuresContext = new StarterContext();
        featuresContext.setStarterPack(getFeaturesStarterPack());
        StarterWizardSettings s = internalCp.getSettings();
        CustomizedMessages msg = new CustomizedMessages();
        msg.setDependenciesLabel(GCNBundle.message("gcn.create.new.features"));
        msg.setNoDependenciesSelectedLabel(GCNBundle.message("gcn.create.new.no.features"));
        msg.setSelectedDependenciesLabel(GCNBundle.message("gcn.create.new.added.features"));
        StarterWizardSettings featuresSettings = new StarterWizardSettings(s.getProjectTypes(),
                s.getLanguages(), s.isExampleCodeProvided(), s.isPackageNameEditable(),
                s.getLanguageLevels(), s.getDefaultLanguageLevel(),
                s.getPackagingTypes(), s.getApplicationTypes(),
                s.getTestFrameworks(), msg, s.getShowProjectTypes());
        StarterContextProvider sc = new StarterContextProvider(this, disposable, featuresContext, context, featuresSettings, this::getFeaturesStarterPack);
        return new StarterLibrariesStep(sc);
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdk) {
        return sdk instanceof JavaSdkType && !(sdk instanceof DependentSdkType);
    }

    @Override
    protected JavaVersion getMinJavaVersion() {
        return LanguageLevel.JDK_17.toJavaVersion();
    }

    @Override
    public String getParentGroup() {
      return JavaModuleType.JAVA_GROUP;
    }

    @Override
    public int getWeight() {
      return JavaModuleBuilder.BUILD_SYSTEM_WEIGHT;
    }

    private void fixGradleProject(ProjectDetails projectDetails) {
        try {
            switch (projectDetails.build) {
                case GRADLE:
                    File gradleFile = new File(projectDetails.baseDir, "build.gradle");
                    if (!gradleFile.exists()) gradleFile.createNewFile();
                    break;
                case GRADLE_KOTLIN:
                    File gradleKFile = new File(projectDetails.baseDir, "build.gradle.kts");
                    if (!gradleKFile.exists()) gradleKFile.createNewFile();
                    break;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}