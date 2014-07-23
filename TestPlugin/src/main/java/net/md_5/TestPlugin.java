package net.md_5;

import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;
import com.google.common.collect.Iterables;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class TestPlugin extends JavaPlugin implements Listener
{

    private static final String META_KEY = "asgyuhkdf";

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents( this, this );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player = (Player) sender;

        Hologram holo = new HologramFactory( this ).withText( ChatColor.RED + "This is a really nice test. asuilhdasd." ).withLocation( locate( player ) ).build();
        holo.show( player );

        player.setMetadata( META_KEY, new FixedMetadataValue( this, holo ) );

        return true;
    }

    private Location locate(Player player)
    {
        Location inFront = player.getLocation();
        inFront.add( 0, player.getEyeHeight(), 0 );
        inFront.add( posMinecraft( 5, inFront.getYaw(), inFront.getPitch() ) );

        return inFront;
    }

    @EventHandler
    public void move(PlayerMoveEvent event)
    {
        update( event.getPlayer() );
    }

    private void update(Player player)
    {
        List<MetadataValue> meta = player.getMetadata( META_KEY );
        if ( meta == null || meta.isEmpty() )
        {
            return;
        }

        Hologram holo = (Hologram) Iterables.getOnlyElement( meta ).value();
        Location inFront = locate( player );
        player.sendMessage( inFront.toString() );
        holo.move( inFront );
    }

    /**
     * This function converts the Minecraft yaw and pitch to proper Euler
     * angles, passes it into {@link #pos(double, double, double)} and then
     * transforms the result back into Minecraft geometry.
     *
     * @param radius
     * @param yaw
     * @param pitch
     * @return
     */
    public static Vector posMinecraft(double radius, double yaw, double pitch)
    {
        Vector eulerPos = pos( radius, -yaw, pitch + 90 );

        return new Vector( eulerPos.getY(), eulerPos.getZ(), eulerPos.getX() );
    }

    /**
     * This is the mathematically correct method to return a 3d vector which is
     * radius out from the origin, rotated by yaw and inclined by pitch.
     *
     * A yaw of 0 would be (x, 0, z) whilst a pitch of 0 would be (0, 0, r).
     *
     * @param radius the radius out from the origin.
     * @param yaw the yaw from the Y axis
     * @param pitch the pitch from the Z axis where 0 is vertical.
     * @return the transformed vector
     */
    public static Vector pos(double radius, double yaw, double pitch)
    {
        double sRadians = Math.toRadians( yaw );
        double tRadians = Math.toRadians( pitch );

        double x = radius * Math.cos( sRadians ) * Math.sin( tRadians );
        double y = radius * Math.sin( sRadians ) * Math.sin( tRadians );
        double z = radius * Math.cos( tRadians );

        return new Vector( x, y, z );
    }
}
