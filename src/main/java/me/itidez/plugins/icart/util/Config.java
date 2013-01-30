package me.itidez.plugins.icart.util;

import java.util.ArrayList;
import java.util.List;
import me.itidez.plugins.icart.Icart;

import org.bukkit.configuration.Configuration;

/**
 * Configuration manager for iCart.
 * Any field with the first letter capitalized is a config option
 * @author iTidez
 */
public class Config {

	public static boolean CheckUpdates;
        public static boolean isSimpleTime;
	public static boolean Debug;
	public static Util.DebugLevel DebugLevel;
	public static int LogDelay;
        public static String DatabaseName;
        public static String DatabaseUser;
        public static String DatabasePass;
        public static String DatabaseHost;

	private static Configuration config;

	/**
	 * Loads the config from file and validates the data
	 * @param plugin
	 */
	public Config(Icart plugin) {

		config = plugin.getConfig().getRoot();
		config.options().copyDefaults(true);
		config.set("version", plugin.version);
        plugin.saveConfig();

		//Load values
		CheckUpdates = config.getBoolean("general.check-for-updates");
		Debug = config.getBoolean("general.debug");
		isSimpleTime = config.getBoolean("general.simplify-time");
		LogDelay = config.getInt("general.log-delay");

		try {
			DebugLevel = Util.DebugLevel.valueOf(config.getString("general.debug-level").toUpperCase());
		} catch (Exception ex) {
			DebugLevel = Util.DebugLevel.NONE;
		}

        }
}
