package com.github.kaitoyuuki.PortalKey;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PortalListener implements Listener {
	
	PortalKey plugin;
	Logger logger;
	
	public PortalListener(PortalKey plugin) {
		this.plugin = plugin;
		logger = plugin.getLogger();
	}
	
	@EventHandler
	public void onPortalKeyEvent(PlayerPortalEvent event) {

		Player player = event.getPlayer();
		ItemStack heldItem = player.getInventory().getItemInHand();
		Integer slot = player.getInventory().getHeldItemSlot();
		List<Integer> keys = plugin.getConfig().getIntegerList("PortalKey.keys");
		List<String> worlds = plugin.getConfig().getStringList("PortalKey.worlds");
		
		World spawn = Bukkit.getWorld("world");
		World world = null;
		String worldname = "";
		
		if (worlds.contains(player.getWorld().getName())) {
			Location to = event.getFrom();
			to.setWorld(spawn);
			event.getPortalTravelAgent().createPortal(to);
			event.setTo(to);
			player.sendMessage("§dReturning to the normal world");
			logger.info("Sending player " + player.getName() + " to world " + to.getWorld().getName());
			player.teleport(to, event.getCause());
			event.setCancelled(true);
			return;
		}
		if (!(player.hasPermission("portalkey.use.all")) && !(player.hasPermission("portalkey.use." + heldItem.getTypeId()))) {
			return;
		}
		if (player.getWorld() != spawn) {
			return;
		}
		if (heldItem.getType() == Material.AIR){
			return;
		}
		if (!keys.contains(heldItem.getTypeId())) {
			return;
		}
		for (Integer key : keys) {
			if (heldItem.getTypeId() == key) {
				int index = keys.indexOf(heldItem.getTypeId());
				worldname = worlds.get(index);
				world = Bukkit.getServer().getWorld(worldname);
				if (world == spawn) {
					return;
				}
				if (world == null) {
					WorldCreator wc = new WorldCreator(worldname);
					wc.copy(spawn);
					Random r = new Random();
					Long seed = r.nextLong();
					wc.seed(seed);
					Bukkit.createWorld(wc);
					world = Bukkit.getServer().getWorld(worldname);
				}
			}
		}
		if (world != player.getWorld()) {
			Location to = event.getFrom().clone();
			to.setWorld(world);
			event.getPortalTravelAgent().setSearchRadius(128);
			event.getPortalTravelAgent().setCreationRadius(32);
			Location portal = event.getPortalTravelAgent().findOrCreate(to);
			event.setTo(to);
			player.sendMessage("§dYour key disappears into the portal, and a door opens to " + to.getWorld().getName());
			logger.info("Sending player " + player.getName() + " to world " + to.getWorld().getName());
			player.teleport(portal, event.getCause());
			PlayerInventory inventory = player.getInventory();
			ItemStack stack = inventory.getItem(slot);
			int amt = stack.getAmount();
			stack.setAmount(amt - 1);
			inventory.setItem(slot, stack);
			event.setCancelled(true);
		}
	}
	
	
}
