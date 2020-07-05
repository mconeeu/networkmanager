package group.onegaming.networkmanager.host.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import group.onegaming.networkmanager.host.api.ModuleHost;
import group.onegaming.networkmanager.host.api.module.ModuleInfo;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

@Log
public class JsonConfig<T> {

    public final static Gson PRETTY_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public final static File MODULE_DIR = new File(System.getProperty("user.dir"), "modules");

    private final ModuleInfo info;
    private final File file;
    private final Class<T> tClass;

    @Getter
    private JsonElement json;

    /**
     * construct a JSON config from the 'plugins/plugin-name' directory
     *
     * @param info     plugin
     * @param fileName filename i.e. config.json
     */
    public JsonConfig(ModuleInfo info, String fileName) {
        this(info, null, MODULE_DIR + info.getName(), fileName);
    }

    /**
     * construct the JSON config with a custom path
     *
     * @param info     plugin
     * @param path     custom file path
     * @param fileName filename i.e config.json
     */
    public JsonConfig(ModuleInfo info, String path, String fileName) {
        this(info, null, path, fileName);
    }

    /**
     * construct a JSON config from the 'plugins/plugin-name' directory
     *
     * @param info     plugin
     * @param tClass   the class to|from where this json config is (de-)serializable (might be null)
     * @param fileName filename i.e. config.json
     */
    public JsonConfig(ModuleInfo info, Class<T> tClass, String fileName) {
        this(info, tClass, MODULE_DIR + "/" + info.getName(), fileName);
    }

    /**
     * construct the JSON config with a custom path
     *
     * @param info     plugin
     * @param tClass   the class to|from where this json config is (de-)serializable (might be null)
     * @param path     custom file path
     * @param fileName filename i.e config.json
     */
    public JsonConfig(ModuleInfo info, Class<T> tClass, String path, String fileName) {
        this.info = info;
        this.tClass = tClass;

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }

        this.file = new File(dir, fileName);
        try {
            if (!file.exists()) {
                this.file.createNewFile();
                try {
                    updateConfig(tClass.newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            reloadFile();
            log.log(Level.INFO, "§aLoaded Config §f" + fileName);
        } catch (Exception e) {
            log.log(Level.SEVERE, "§cError loading Config §f" + fileName);
            e.printStackTrace();
        }
    }

    /**
     * reloads the json packets from file
     * Attention! this overrides all existing changes on the CoreJsonConfig#getJson() object
     *
     * @throws Exception thrown if an IOException occurs
     */
    public void reloadFile() throws Exception {
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            json = ModuleHost.getInstance().getJsonParser().parse(reader);

            reader.close();
            fis.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "§cError reloading Config §f" + file.getName());
            throw new Exception("Cannot reload JsonConfig " + file.getName(), e);
        }
    }

    /**
     * saves the Config to file
     */
    public void save() throws Exception {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer fw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            PRETTY_GSON.toJson(json, fw);

            fw.close();
            fos.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, "§cError saving Config §f" + file.getName());
            throw new Exception("Cannot save JsonConfig " + file.getName());
        }
    }

    /**
     * parses the json object to the given Type. Only works if a type was set via constructor
     *
     * @return serialized object of given type
     * @throws IllegalStateException thrown if type was not set via constructor
     */
    public T parseConfig() throws IllegalStateException {
        if (tClass != null) {
            return PRETTY_GSON.fromJson(json, tClass);
        } else {
            throw new IllegalStateException("Config must be instantiated with a class if using POJO");
        }
    }

    /**
     * parses an object of a specific type if type was set via constructor
     *
     * @param config serialized object of given type
     * @throws IllegalStateException thrown if type was not set via constructor
     */
    public void updateConfig(T config) throws Exception {
        if (tClass != null) {
            this.json = PRETTY_GSON.toJsonTree(config, tClass);
            save();
        } else {
            throw new IllegalStateException("Config must be instantiated with a class if using POJO");
        }
    }
}
