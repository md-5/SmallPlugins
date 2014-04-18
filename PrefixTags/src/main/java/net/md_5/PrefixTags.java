package net.md_5;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PrefixTags extends JavaPlugin implements Listener
{

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents( this, this );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        Team team = player.getScoreboard().getTeam( player.getName() );
        if ( team == null )
        {
            team = player.getScoreboard().registerNewTeam( player.getName() );
        }

        String prefix = PermissionsEx.getUser( player ).getPrefix();
        if ( prefix != null )
        {
            team.setPrefix( ChatColor.translateAlternateColorCodes( '&', prefix.substring( 0, Math.min( prefix.length(), 16 ) ) ) );
        }
        team.addPlayer( player );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        Team team = player.getScoreboard().getTeam( player.getName() );
        if ( team != null )
        {
            team.removePlayer( player );
            team.unregister();
        }
    }
}
