package de.edittrich.spike.spigot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotBlankPlugin extends JavaPlugin implements Listener {
	FileConfiguration config = getConfig();

	public void onEnable() {
		config.addDefault("youAreAwesome", true);
		config.options().copyDefaults(true);
		saveConfig();
		
		getLogger().info("onEnable has been invoked with youAreAwesome: " + config.getBoolean("youAreAwesome"));

		// Enable our class to check for new players using onPlayerJoin()
		getServer().getPluginManager().registerEvents(this, this);
	}

	// This method checks for incoming players and sends them a message
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		if (config.getBoolean("youAreAwesome")) {
			player.sendMessage("You are awesome!");
		} else {
			player.sendMessage("You are not awesome...");
		}
	}

}