package de.edittrich.spike.spigot;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class SpigotSpikeCommandExecutorBasic implements CommandExecutor {
	private final SpigotSpikePlugin plugin;
	private final FileConfiguration config;
	private final Logger logger;

	public SpigotSpikeCommandExecutorBasic(SpigotSpikePlugin plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		this.logger = plugin.getLogger();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("basic")) {
			logger.info("Command: Basic");
			return true;
		} else {
			return false;
		}
	}
}