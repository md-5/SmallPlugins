package net.md_5;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class StickyMob extends JavaPlugin implements Runnable
{

    @Override
    public void onEnable()
    {
        getConfig().addDefault( "mobs.wither-gate.world", "world" );
        getConfig().addDefault( "mobs.wither-gate.mob", "WITHER" );
        getConfig().addDefault( "mobs.wither-gate.x", 0 );
        getConfig().addDefault( "mobs.wither-gate.y", 64 );
        getConfig().addDefault( "mobs.wither-gate.z", 0 );
        getConfig().addDefault( "mobs.wither-gate.yaw", 0 );
        getConfig().addDefault( "mobs.wither-gate.pitch", 0 );
        getConfig().addDefault( "interval", 30 );

        getConfig().options().copyDefaults( true );
        saveConfig();

        int interval = getConfig().getInt( "interval" ) * 20;
        getServer().getScheduler().scheduleSyncRepeatingTask( this, this, interval, interval );
    }

    @Override
    public void run()
    {
        ConfigurationSection mobs = getConfig().getConfigurationSection( "mobs" );
        outer:
        for ( String sectionName : mobs.getKeys( false ) )
        {
            ConfigurationSection mobSection = mobs.getConfigurationSection( sectionName );

            String entityName = mobSection.getString( "mob" );
            EntityType entityType = EntityType.fromName( entityName );
            if ( entityType == null )
            {
                getLogger().warning( "Tried to spawn mob entry " + sectionName + " of type " + entityName + " but type does not exist" );
                continue;
            }

            World world = getServer().getWorld( mobSection.getString( "world" ) );
            if ( world == null )
            {
                getLogger().warning( "Tried to spawn mob entry " + sectionName + " in world " + world + " which is not loaded" );
                continue;
            }

            String uuid = mobSection.getString( "uuid" );
            if ( uuid != null )
            {
                for ( Entity e : world.getEntitiesByClass( entityType.getEntityClass() ) )
                {
                    if ( e.getUniqueId().toString().equals( uuid ) )
                    {
                        continue outer;
                    }
                }
            }

            Location loc = new Location( world, mobSection.getDouble( "x" ), mobSection.getDouble( "y" ), mobSection.getDouble( "z" ),
                    (float) mobSection.getDouble( "yaw" ), (float) mobSection.getDouble( "pitch" ) );

            Entity spawned = world.spawnEntity( loc, entityType );
            String spawnedId = spawned.getUniqueId().toString();
            mobSection.set( "uuid", spawnedId );
            saveConfig();

            getLogger().info( "Successfully spawned a " + entityType + " with UUID " + spawnedId + " at " + loc );
        }
    }
}
