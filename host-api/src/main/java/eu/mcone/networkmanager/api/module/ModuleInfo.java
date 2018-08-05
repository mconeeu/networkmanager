/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.api.module;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class ModuleInfo {

    @Getter @Setter
    private String moduleClass;
    @Getter @Setter
    private String moduleName;
    @Getter @Setter
    private boolean running;
    @Getter @Setter
    private File file;

    public ModuleInfo(final String moduleClass, final String moduleName, final boolean running, final File file) {
        this.moduleClass = moduleClass;
        this.moduleName = moduleName;
        this.running = running;
        this.file = file;
    }

}
