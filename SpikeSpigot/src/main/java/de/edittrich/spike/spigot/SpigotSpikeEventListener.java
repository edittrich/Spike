package de.edittrich.spike.spigot;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SpigotSpikeEventListener implements Listener {
	private final SpigotSpikePlugin plugin;

	public SpigotSpikeEventListener(SpigotSpikePlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		plugin.getLogger().info("Event: onPlayerJoin");

		if (plugin.getConfig().getBoolean("youAreAwesome")) {
			player.sendMessage("You are awesome!");
		} else {
			player.sendMessage("You are not awesome...");
		}
	}
}