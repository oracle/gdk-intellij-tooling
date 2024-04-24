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

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import icons.SdkIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GCNModuleType extends ModuleType<GCNModuleBuilder> {

    private static final String ID = "GCN_MODULE_TYPE";

    public GCNModuleType() {
        super(ID);
    }

    public static GCNModuleType getInstance() {
        return (GCNModuleType) ModuleTypeManager.getInstance().findByID(ID);
    }

    @NotNull
    @Override
    public GCNModuleBuilder createModuleBuilder() {
        return new GCNModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return "Graal Cloud Native";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Graal cloud native module type";
    }

    @NotNull
    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return SdkIcons.gcn_default_icon;
    }

    @Override
    public ModuleWizardStep @NotNull [] createWizardSteps(@NotNull WizardContext wizardContext,
                                                          @NotNull GCNModuleBuilder moduleBuilder,
                                                          @NotNull ModulesProvider modulesProvider) {
        return super.createWizardSteps(wizardContext, moduleBuilder, modulesProvider);
    }

}
