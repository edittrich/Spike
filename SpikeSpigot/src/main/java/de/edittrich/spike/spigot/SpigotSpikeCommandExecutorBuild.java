package de.edittrich.spike.spigot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpigotSpikeCommandExecutorBuild implements CommandExecutor {
	private final SpigotSpikePlugin plugin;
	private final FileConfiguration config;
	private final Logger logger;

	public SpigotSpikeCommandExecutorBuild(SpigotSpikePlugin plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();
		this.logger = plugin.getLogger();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("bauen")) {
			logger.info("Command: bauen");

			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");

			} else {
				int i = 1;
				String buildingFile = "building";
				String line = null;
				String[] segments = null;

				Block currentBlock = null;
				Player player = (Player) sender;
				Location loc = player.getLocation();
				int x1 = loc.getBlockX() + config.getInt("shift", 3);
				int y1 = loc.getBlockY();
				int z1 = loc.getBlockZ();
				int xPoint = x1;
				int yPoint = y1;
				int zPoint = z1;
				World world = loc.getWorld();

				File file = new File(buildingFile + i);
				logger.info("building file " + buildingFile);
				while (file.exists() && !file.isDirectory()) {
					logger.info("Level " + i + " & yPoint " + yPoint);

					try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
						zPoint = z1;
						while ((line = bufferedReader.readLine()) != null) {
							logger.info("Line " + line + " & zPoint " + zPoint);
							segments = line.split(Pattern.quote(" "));
							xPoint = x1;
							for (int c = 0; c < segments.length; c++) {
								logger.info("c " + c + " & xPoint " + xPoint);
								currentBlock = world.getBlockAt(xPoint, yPoint, zPoint);
								switch (segments[c]) {
								case "Di":
									System.out.println("Diamant");
									currentBlock.setType(Material.DIAMOND_BLOCK);
									break;
								case "..":
									System.out.println("Luft");
									currentBlock.setType(Material.AIR);
									break;
								default:
									break;
								}
								xPoint++;
							}
							zPoint++;
						}
					} catch (IOException e) {
						logger.severe("IO Exception!");
					}

					file = new File(buildingFile + ++i);
					yPoint++;
				}

			}
			return true;
		} else {
			return false;
		}
	}
}