package com.github.kaitoyuuki.PortalKey;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//@SuppressWarnings("unused")
public class PortalKeyCommands implements CommandExecutor {
	
	private final PortalKey plugin;
	private List<Integer> keys;
	private List<String> worlds;
	
	public PortalKeyCommands(PortalKey plugin) {
		this.plugin = plugin;
		keys = plugin.getConfig().getIntegerList("PortalKey.keys");
		worlds = plugin.getConfig().getStringList("PortalKey.worlds");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pk")) {
			if (args.length == 0) {
				sender.sendMessage("");
				sender.sendMessage("§2PortalKey §av" + plugin.getDescription().getVersion() + " §2by §akaitoyuuki");
				sender.sendMessage("");
				sender.sendMessage("§d/pk §3Base command for PortalKey plugin.");
				sender.sendMessage("§d/pk set <key> <destination> §3creates or modifies a key pair");
				sender.sendMessage("§d/pk del <key> §3deletes the specified key pair");
				sender.sendMessage("§d/pk keys §3Lists all keys and their destinations.");
				sender.sendMessage("");
				return true;
			}
			else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("set")) {
					sender.sendMessage("§d/pk set <key> <destination> §3Creates or modifies the given key to send players to the specified world.");
					return true;
				}
				if (args[0].equalsIgnoreCase("del")) {
					sender.sendMessage("§d/pk del <key> §3Deletes the specified key pair.");
					return true;
				}
				else if (args[0].equalsIgnoreCase("keys")) {
					sender.sendMessage("");
					sender.sendMessage("§9Keys:");
					sender.sendMessage("");
					for (Integer key : keys) {
						String world = worlds.get(keys.indexOf(key));
						if (world != null) {
							String item = Material.getMaterial(key).name().toLowerCase();
							item = item.replaceAll("_", " ");
							sender.sendMessage("    §3" + item + "  -  " + world);
						}
					}
					
					return true;
				}
				else {
					return false;
				}
			}
			else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("set")) {
					sender.sendMessage("§cNot enough arguments.");
					return true;
				}
				else if (args[0].equalsIgnoreCase("del")) {
					if (sender instanceof Player) {
						if (!((Player) sender).hasPermission("portalkey.set")) {
							sender.sendMessage("§cYou do not have permission to perform this command.");
							return true;
						}
					}
					Material material = Material.getMaterial(args[1]);
					if (material == null) {
						try {
							Integer matID = Integer.parseInt(args[1]);
							material = Material.getMaterial(matID);
							if (material == null) {
								sender.sendMessage("§c" + args[1] + " is not a valid key.");
								return true;
							}
						} catch (NumberFormatException e) {
							sender.sendMessage("§c" + args[1] + " is not a valid key.");
							return true;
						}

					}
					Integer key = material.getId();
					if (keys.contains(key)) {
						Integer index = keys.indexOf(key);
						keys.remove(key);
						worlds.remove(worlds.get(index));
						plugin.getConfig().set("PortalKey.keys", keys);
						plugin.getConfig().set("PortalKey.worlds", worlds);
						plugin.saveConfig();
						sender.sendMessage("§9Deleted key pair for §3" + material.name());
						return true;
					}
				}
				else {
					return false;
				}
			}
			else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("set")) {
					if (sender instanceof Player) {
						if (!((Player) sender).hasPermission("portalkey.set")) {
							sender.sendMessage("§cYou do not have permission to perform this command.");
							return true;
						}
					}
					Material material = Material.getMaterial(args[1]);
					if (material == null) {
						try {
							Integer matID = Integer.parseInt(args[1]);
							material = Material.getMaterial(matID);
							if (material == null) {
								sender.sendMessage("§c" + args[1] + " is not a valid key.");
								return true;
							}
						} catch (NumberFormatException e) {
							sender.sendMessage("§c" + args[1] + " is not a valid key.");
							return true;
						}

					}
					Integer key = material.getId();
					if (keys.contains(key)) {
						Integer index = keys.indexOf(key);
						worlds.add(index, args[2]);
						plugin.getConfig().set("PortalKey.keys", keys);
						plugin.getConfig().set("PortalKey.worlds", worlds);
						plugin.saveConfig();
						sender.sendMessage("§9Changed key world pair: §3" + material.name() + " " + args[2]);
						return true;
					}
					else {
						keys.add(key);
						worlds.add(keys.indexOf(key), args[2]);
						plugin.getConfig().set("PortalKey.keys", keys);
						plugin.getConfig().set("PortalKey.worlds", worlds);
						plugin.saveConfig();
						sender.sendMessage("§9Created new key world pair: §3" + material.name() + " " + args[2]);
						return true;
					}
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		
		return false;
	}
}
