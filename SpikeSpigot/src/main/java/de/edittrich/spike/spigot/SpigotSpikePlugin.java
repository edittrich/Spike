package de.edittrich.spike.spigot;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotSpikePlugin extends JavaPlugin {
	private final FileConfiguration config = getConfig();
	private final Logger logger = getLogger();

	@Override
	public void onEnable() {
		config.addDefault("buildingfile", "building");
		config.addDefault("shift", 3);
		config.options().copyDefaults(true);
		saveConfig();

		logger.info("onEnable has been invoked with buildingfile: " + config.getString("buildingfile"));

		this.getCommand("basic").setExecutor(new SpigotSpikeCommandExecutorBasic(this));
		this.getCommand("cube").setExecutor(new SpigotSpikeCommandExecutorCube(this));
		this.getCommand("bauen").setExecutor(new SpigotSpikeCommandExecutorBuild(this));

		getServer().getPluginManager().registerEvents(new SpigotSpikeEventListenerOnPlayerJoin(this), this);
	}
}