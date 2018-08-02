/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.core.module;

import lombok.Getter;
import lombok.Setter;

public class ModuleInfo {

    @Getter @Setter
    private String moduleClass;
    @Getter @Setter
    private String moduleName;
    @Getter @Setter
    private Boolean running;

    public ModuleInfo(final String moduleClass, final String moduleName, final Boolean running) {
        this.moduleClass = moduleClass;
        this.moduleName = moduleName;
        this.running = running;
    }
}
