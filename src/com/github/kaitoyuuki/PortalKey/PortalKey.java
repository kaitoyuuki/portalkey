package com.github.kaitoyuuki.PortalKey;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PortalKey extends JavaPlugin {
	
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		getConfig();
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new PortalListener(this), this);
		getCommand("pk").setExecutor(new PortalKeyCommands(this));
	}
	
	@Override
	public void onDisable() {
		HandlerList.unregisterAll();
	}
}
