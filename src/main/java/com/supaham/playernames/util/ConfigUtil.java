package com.supaham.playernames.util;

import com.supaham.playernames.PlayerNamesPlugin;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Class containing {@link FileConfiguration} utility methods.
 */
public class ConfigUtil {

    /**
     * Gets or creates a {@link ConfigurationSection}.
     *
     * @param cs   ConfigurationSection to operate in
     * @param path path of the ConfigurationSection to get
     * @return the newly created ConfigurationSection
     */
    public static ConfigurationSection getOrCreateSection(ConfigurationSection cs, String path) {
        Validate.notNull(cs, "cs can not be null.");
        Validate.notNull(path, "path can not be null.");

        return cs.isConfigurationSection(path) ? cs.getConfigurationSection(path) : cs.createSection(path);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOrSet(ConfigurationSection cs, String path, T value) {
        Validate.notNull(cs, "cs can not be null.");

        if (!cs.isSet(path)) {
            cs.set(path, value);
        }

        return (T) cs.get(path, value);
    }

    /**
     * Create a default configuration file from the jar file.
     *
     * @param destination the destination file
     * @param defaultName the name of the file inside the jar's defaults folder
     * @param replace     whether to replace the old file
     */
    public static boolean createDefaultConfiguration(File destination, String defaultName, boolean replace) {
        Validate.notNull(destination, "destination can not be null.");
        Validate.notNull(defaultName, "defaultName can not be null.");

        PlayerNamesPlugin plugin = PlayerNamesPlugin.getInstance();

        // Make parent directories
        File parent = destination.getParentFile();

        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (!replace && destination.exists()) {
            return false;
        }

        InputStream input = null;
        try {
            JarFile file = new JarFile(plugin.getPluginFile());
            ZipEntry copy = file.getEntry("defaults/" + defaultName);
            if (copy == null) {
                throw new FileNotFoundException();
            }
            input = file.getInputStream(copy);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to read default configuration: " + defaultName);
        }

        boolean created = false;
        if (input != null) {

            try (FileOutputStream output = new FileOutputStream(destination)) {
                byte[] buf = new byte[8192];
                int length;
                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }

                plugin.getLogger().info("Default configuration file written: " + destination.getAbsolutePath());
                created = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (IOException ignore) {
                }
            }
        }
        return created;
    }
}
