/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.networkmanager;

import eu.mcone.networkmanager.api.ModuleHost;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class NetworkManager extends ModuleHost {

    public static final File HOME_DIR = new File(System.getProperty("user.dir"));

    @Getter
    private ExecutorService threadPool;

    public NetworkManager() throws IOException, ClassNotFoundException {
        setInstance(this);
        this.threadPool = Executors.newCachedThreadPool();

        File[] modules = HOME_DIR.listFiles();

        if (modules != null) {
            for (File module : modules) {
                JarFile jarFile = new JarFile(module);
                Enumeration<JarEntry> e = jarFile.entries();

                URL[] urls = {new URL("jar:file:" + module.getPath() + "!/")};
                URLClassLoader cl = URLClassLoader.newInstance(urls);

                String mainClass = null;

                while (e.hasMoreElements()) {
                    JarEntry je = e.nextElement();
                    if (je.isDirectory()) {
                        if (je.getName().equals("module.yml")) {

                        } else if (je.getName().endsWith(".class")) {

                        } else {
                            continue;
                        }
                    }

                    // -6 because of .class
                    String className = je.getName().substring(0, je.getName().length() - 6);
                    className = className.replace('/', '.');
                    cl.loadClass(className);
                }


            }
        }
    }

    public static void main(String[] args) {
        try {
            new NetworkManager();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



}
