package net.md_5;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class VoidTP extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		getConfig().addDefault("command", "home");
		getConfig().options().copyDefaults(true);
		saveConfig();

		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler(ignoreCancelled = true)
	public void damage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
			getServer().dispatchCommand(((Player) event.getEntity()), getConfig().getString("command"));
		}
	}
}
