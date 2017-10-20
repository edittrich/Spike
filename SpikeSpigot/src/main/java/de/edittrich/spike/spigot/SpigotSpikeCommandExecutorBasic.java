package de.edittrich.spike.spigot;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpigotSpikeCommandExecutorBasic implements CommandExecutor {
	private final SpigotSpikePlugin plugin;

	public SpigotSpikeCommandExecutorBasic(SpigotSpikePlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("basic")) {
			plugin.getLogger().info("Command: Basic");

			return true;

		} else if (cmd.getName().equalsIgnoreCase("cube")) {
			plugin.getLogger().info("Command: Cube");

			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");

			} else {
				Player player = (Player) sender;
				Location loc = player.getLocation();
				int length = 5;

				int x1 = loc.getBlockX() + plugin.getConfig().getInt("shift", 1);
				int y1 = loc.getBlockY();
				int z1 = loc.getBlockZ();

				int x2 = x1 + length;
				int y2 = y1 + length;
				int z2 = z1 + length;

				World world = loc.getWorld();

				for (int xPoint = x1; xPoint <= x2; xPoint++) {
					for (int yPoint = y1; yPoint <= y2; yPoint++) {
						for (int zPoint = z1; zPoint <= z2; zPoint++) {
							Block currentBlock = world.getBlockAt(xPoint, yPoint, zPoint);
							currentBlock.setType(Material.DIAMOND_BLOCK);
						}
					}
				}
			}
			return true;
		}
		return false;
	}
}