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

class GCNStarterInitialStep extends StarterInitialStep {

    private GraphProperty<GcnProjectType> gcnTypeProperty;
    private GraphProperty<Integer> gcnSourceLevel;
    private List<GraphProperty<Boolean>> selectedCloudProps;

    GCNStarterInitialStep(StarterContextProvider contextProvider) {
        super(contextProvider);
        selectedCloudProps = new ArrayList<>();
        gcnTypeProperty = getPropertyGraph().property(GcnProjectType.DEFAULT_OPTION);
        gcnSourceLevel = getPropertyGraph().property(GcnUtils.SUPPORTED_JDKS.get(0));
    }

    @Override
    protected void addFieldsBefore(Panel layout) {
        layout.row(GCNBundle.message("gcn.create.new.micronaut.version"), row -> {
            row.segmentedButton(Collections.singletonList(GcnUtils.getMicronautVersion()), (pi, ver) -> {
                pi.setText(ver);
                return null;
            }).setSelectedItem(GcnUtils.getMicronautVersion());
            row.bottomGap(BottomGap.SMALL);
            return null;
        });
        layout.row(GCNBundle.message("gcn.create.new.application.type"), row -> {
            row.segmentedButton(Arrays.asList(GcnProjectType.values()), (pi, type) -> {
                pi.setText(type.getTitle());
                return null;
            }).maxButtonsCount(3).bind(gcnTypeProperty);
            row.bottomGap(BottomGap.SMALL);
            return null;
        });
        layout.row(GCNBundle.message("gcn.create.new.application.source"), row -> {
            row.segmentedButton(new ArrayList<>(GcnUtils.SUPPORTED_JDKS), (pi, type) -> {
                pi.setText("JDK "+type);
                return null;
            }).bind(gcnSourceLevel);
            row.bottomGap(BottomGap.SMALL);
            return null;
        });
    }

    @Override
    protected void addFieldsAfter(Panel layout) {
        layout.buttonsGroup(null, false, panel -> {
            panel.row(GCNBundle.message("gcn.create.new.cloud.provides"), r -> {
                for (GcnCloud c : GcnCloud.supportedValues()) {
                    GraphProperty<Boolean> gcnCloudProperty = getPropertyGraph().property(GcnCloud.OCI == c);
                    selectedCloudProps.add(gcnCloudProperty);
                    ButtonKt.bindSelected(r.checkBox(c.getTitle()), gcnCloudProperty);
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
        List<GcnCloud> selClouds = new ArrayList<>();
        Iterator<GraphProperty<Boolean>> sit = selectedCloudProps.iterator();
        for (GcnCloud c : GcnCloud.supportedValues()) {
            if (sit.next().get()) selClouds.add(c);
        }
        getStarterContext().putUserData(GCNModuleBuilder.GCN_CLOUDS_KEY, selClouds);
        getStarterContext().putUserData(GCNModuleBuilder.GCN_TYPE_KEY, gcnTypeProperty.get());
        getStarterContext().putUserData(GCNModuleBuilder.GCN_SOURCE_KEY, gcnSourceLevel.get());
    }

    private boolean validateUI() {
        Sdk sdk = getSdkProperty().get();
        if (sdk != null) {
            JavaVersion jver = JavaVersion.tryParse(sdk.getVersionString());
            if (jver != null) {
                int sourceLevel = gcnSourceLevel.get();
                if (!jver.isAtLeast(sourceLevel)) {
                    String message = GCNBundle.message("gcn.create.new.source.not.supported", jver, sourceLevel);
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
