package net.md_5;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathStuff extends JavaPlugin implements Listener
{

    private Material reward;

    @Override
    public void onEnable()
    {
        getConfig().addDefault( "reward", "EMERALD" );
        getConfig().addDefault( "reward_count", 1 );
        getConfig().options().copyDefaults( true );
        saveConfig();

        reward = Material.getMaterial( getConfig().getString( "reward" ) );

        getServer().getPluginManager().registerEvents( this, this );
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event)
    {
        if ( reward != null )
        {
            Player killer = event.getEntity().getKiller();
            if ( killer != null )
            {
                killer.getInventory().addItem( new ItemStack( reward, getConfig().getInt( "reward_count" ) ) );
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void playerHit(EntityDamageByEntityEvent event)
    {
        if ( event.getEntity() instanceof Player && event.getDamager() instanceof Player )
        {
            event.getEntity().getWorld().playEffect( event.getEntity().getLocation(), Effect.SMOKE, 0 );
        }
    }
}
