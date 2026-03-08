/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.loader.impl.bootstrap;

import com.mojang.logging.LogUtils;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforgespi.earlywindow.GraphicsBootstrapper;
import org.slf4j.Logger;

import java.util.List;

public class FabricLoaderBootstrap implements GraphicsBootstrapper {
    private static final String NAME = "fabric_loader_bootstrap";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void bootstrap(String[] arguments) {
        // Load FML mods into Fabric Loader
        LOGGER.info("Propagating FML mod list to Fabric Loader");
        List<ModInfo> mods = FMLLoader.getCurrent().getLoadingModList().getMods();
        FabricLoaderImpl.INSTANCE.addFmlMods(mods);
    }
}
