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
import com.intellij.ide.starters.local.StarterContextProvider;
import com.intellij.ide.starters.local.wizard.StarterInitialStep;
import com.intellij.openapi.observable.properties.GraphProperty;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.dsl.builder.BottomGap;
import com.intellij.ui.dsl.builder.ButtonKt;
import com.intellij.ui.dsl.builder.Panel;
import com.intellij.util.lang.JavaVersion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class GDKStarterInitialStep extends StarterInitialStep {

    private GraphProperty<GdkProjectType> gdkTypeProperty;
    private GraphProperty<Integer> gdkSourceLevel;
    private List<GraphProperty<Boolean>> selectedCloudProps;

    GDKStarterInitialStep(StarterContextProvider contextProvider) {
        super(contextProvider);
        selectedCloudProps = new ArrayList<>();
        gdkTypeProperty = getPropertyGraph().property(GdkProjectType.DEFAULT_OPTION);
        gdkSourceLevel = getPropertyGraph().property(GdkUtils.SUPPORTED_JDKS.get(0));
    }

    @Override
    protected void addFieldsBefore(Panel layout) {
        layout.row(GDKBundle.message("gdk.create.new.micronaut.version"), row -> {
            row.segmentedButton(Collections.singletonList(GdkUtils.getMicronautVersion()), (pi, ver) -> {
                pi.setText(ver);
                return null;
            }).setSelectedItem(GdkUtils.getMicronautVersion());
            row.bottomGap(BottomGap.SMALL);
            return null;
        });
        layout.row(GDKBundle.message("gdk.create.new.application.type"), row -> {
            row.segmentedButton(Arrays.asList(GdkProjectType.values()), (pi, type) -> {
                pi.setText(type.getTitle());
                return null;
            }).maxButtonsCount(3).bind(gdkTypeProperty);
            row.bottomGap(BottomGap.SMALL);
            return null;
        });
        layout.row(GDKBundle.message("gdk.create.new.application.source"), row -> {
            row.segmentedButton(new ArrayList<>(GdkUtils.SUPPORTED_JDKS), (pi, type) -> {
                pi.setText("JDK "+type);
                return null;
            }).bind(gdkSourceLevel);
            row.bottomGap(BottomGap.SMALL);
            return null;
        });
    }

    @Override
    protected void addFieldsAfter(Panel layout) {
        layout.buttonsGroup(null, false, panel -> {
            panel.row(GDKBundle.message("gdk.create.new.cloud.provides"), r -> {
                for (GdkCloud c : GdkCloud.supportedValues()) {
                    GraphProperty<Boolean> gdkCloudProperty = getPropertyGraph().property(GdkCloud.OCI == c);
                    selectedCloudProps.add(gdkCloudProperty);
                    ButtonKt.bindSelected(r.checkBox(c.getTitle()),gdkCloudProperty);
                }
                r.bottomGap(BottomGap.SMALL);
                return null;
            });
            return null;
        });
    }

    @Override
    public void updateDataModel() {
        super.updateDataModel();
        List<GdkCloud> selClouds = new ArrayList<>();
        Iterator<GraphProperty<Boolean>> sit = selectedCloudProps.iterator();
        for (GdkCloud c : GdkCloud.supportedValues()) {
            if (sit.next().get()) selClouds.add(c);
        }
        getStarterContext().putUserData(GDKModuleBuilder.GDK_CLOUDS_KEY, selClouds);
        getStarterContext().putUserData(GDKModuleBuilder.GDK_TYPE_KEY, gdkTypeProperty.get());
        getStarterContext().putUserData(GDKModuleBuilder.GDK_SOURCE_KEY, gdkSourceLevel.get());
    }

    private boolean validateUI() {
        Sdk sdk = getSdkProperty().get();
        if (sdk != null) {
            JavaVersion jver = JavaVersion.tryParse(sdk.getVersionString());
            if (jver != null) {
                int sourceLevel = gdkSourceLevel.get();
                if (!jver.isAtLeast(sourceLevel)) {
                    String message = GDKBundle.message("gdk.create.new.source.not.supported", jver, sourceLevel);
                    Messages.showErrorDialog(message, "Error");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean validate() {
        return super.validate() && validateUI();
    }
}
