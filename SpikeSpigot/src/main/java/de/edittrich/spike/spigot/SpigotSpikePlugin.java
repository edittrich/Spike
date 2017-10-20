package de.edittrich.spike.spigot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotSpikePlugin extends JavaPlugin {
	private final FileConfiguration config = getConfig();

	public void onEnable() {
		config.addDefault("youAreAwesome", true);
		config.addDefault("shift", 1);
		config.options().copyDefaults(true);
		saveConfig();

		getLogger().info("onEnable has been invoked with youAreAwesome: " + config.getBoolean("youAreAwesome"));

		this.getCommand("basic").setExecutor(new SpigotSpikeCommandExecutorBasic(this));
		this.getCommand("cube").setExecutor(new SpigotSpikeCommandExecutorCube(this));

		getServer().getPluginManager().registerEvents(new SpigotSpikeEventListenerOnPlayerJoin(this), this);
	}
}