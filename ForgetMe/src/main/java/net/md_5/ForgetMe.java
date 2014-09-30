package net.md_5;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ForgetMe extends JavaPlugin implements Listener, Runnable
{

    private List<String> ignoredList;
    private List<String> forgotten;
    private YamlConfiguration ignored;
    private File ignoredFile;

    @Override
    public void onEnable()
    {
        getConfig().addDefault( "time.7000", Arrays.asList( "say player %s has been gone for 7000 days", "ban %s not welcome :(" ) );
        getConfig().addDefault( "interval", 360 );

        getConfig().options().copyDefaults( true );
        saveConfig();

        ignoredFile = new File( getDataFolder(), "ignored.yml" );

        getServer().getPluginManager().registerEvents( this, this );

        int interval = getConfig().getInt( "interval" ) * 20 * 60;
        getServer().getScheduler().runTaskTimerAsynchronously( this, this, interval, interval );

        try
        {
            ignored = YamlConfiguration.loadConfiguration( ignoredFile );
        } catch ( Exception ex )
        {
            getLogger().log( Level.SEVERE, "Error loading ignored!", ex );
        }

        ignoredList = ignored.getStringList( "players" );
        forgotten = ignored.getStringList( "forgotten" );

        getServer().getScheduler().runTaskTimerAsynchronously( this, new Runnable()
        {

            @Override
            public void run()
            {
                ignored.set( "players", ignoredList );
                ignored.set( "forgotten", forgotten );
                try
                {
                    ignored.save( ignoredFile );
                } catch ( IOException ex )
                {
                    getLogger().log( Level.SEVERE, "Error saving ignored players", ex );
                }
            }
        }, 60 * 20, 60 * 20 );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void playerJoin(PlayerJoinEvent event)
    {
        if ( event.getPlayer().hasPermission( "inactive.ignore" ) )
        {
            String name = event.getPlayer().getName().toLowerCase();
            if ( !ignoredList.contains( name ) )
            {
                ignoredList.add( name );
            }
        }
        forgotten.remove( event.getPlayer().getName().toLowerCase() );
    }

    @Override
    public void run()
    {
        ConfigurationSection timeSection = getConfig().getConfigurationSection( "time" );
        for ( String key : timeSection.getKeys( false ) )
        {
            int elapsed = Integer.parseInt( key );
            Calendar threshold = Calendar.getInstance();
            threshold.add( Calendar.DATE, -elapsed );
            Date thresholdDate = threshold.getTime();

            List<String> commands = timeSection.getStringList( key );

            for ( final OfflinePlayer player : getServer().getOfflinePlayers() )
            {
                String lowerCaseName = player.getName().toLowerCase();
                if ( ignoredList.contains( lowerCaseName ) || forgotten.contains( lowerCaseName ) )
                {
                    continue;
                }

                Date lastPlayed = new Date( player.getLastPlayed() );
                if ( lastPlayed.before( thresholdDate ) )
                {
                    for ( final String command : commands )
                    {
                        getServer().getScheduler().runTask( this, new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                getServer().dispatchCommand( getServer().getConsoleSender(), String.format( command, player.getName() ) );
                            }
                        } );
                    }
                    forgotten.add( lowerCaseName );
                }
            }
        }
    }
}
