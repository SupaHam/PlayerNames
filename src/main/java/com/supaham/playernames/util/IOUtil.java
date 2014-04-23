package com.supaham.playernames.util;

import com.google.common.io.CharStreams;
import com.supaham.playernames.PlayerNamesPlugin;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * IOUtil class contains utility methods for IO related tasks.
 */
public class IOUtil {

    /**
     * Gets a String of a resource in the plugin jar file.
     *
     * @param fileName resource name to retrieve
     * @return String of the resource contents
     */
    public static String resourceToString(String fileName) {
        Plugin plugin = PlayerNamesPlugin.getInstance();
        InputStream is = plugin.getResource(fileName);
        try {
            if (is == null) {
                throw new NullPointerException("resource '" + fileName + "' not found.");
            }
            return CharStreams.toString(new InputStreamReader(is));
        } catch (IOException e) {
            plugin.getLogger().severe("Error occurred while reading '" + fileName + "'.");
            e.printStackTrace();
            return null;
        }
    }
}
