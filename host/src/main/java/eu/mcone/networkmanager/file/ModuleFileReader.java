/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager.file;

import eu.mcone.networkmanager.core.module.ModuleInfo;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ModuleFileReader {

    @Getter
    private InputStream inputStream;
    @Getter
    private ModuleInfo moduleInfo;
    private String moduleMainClass;
    private String moduleName;

    public ModuleFileReader(final InputStream inputStream) {
        try {
            this.inputStream = inputStream;
            loadModuleYML();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadModuleYML() throws IOException {
        Properties properties = new Properties();
        properties.load(this.inputStream);
        this.moduleMainClass = properties.getProperty("main: ");
        this.moduleName = properties.getProperty("name: ");
        this.moduleInfo = new ModuleInfo(moduleMainClass, moduleName, null);
    }
}
