package de.edittrich.spike.spigot;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpigotSpikeEventListenerOnPlayerJoin implements Listener {
	private final SpigotSpikePlugin plugin;
	private final FileConfiguration config;
	private final Logger logger;

	public SpigotSpikeEventListenerOnPlayerJoin(SpigotSpikePlugin plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		this.logger = plugin.getLogger();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		logger.info("Event: onPlayerJoin");

		if (config.getBoolean("youAreAwesome")) {
			player.sendMessage("You are awesome!");
		} else {
			player.sendMessage("You are not awesome...");
		}
	}
}