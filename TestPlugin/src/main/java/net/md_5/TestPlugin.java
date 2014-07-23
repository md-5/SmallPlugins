package net.md_5;

import org.bukkit.Location;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class TestPlugin extends JavaPlugin
{

    private Location last;

    @Override
    public void onEnable()
    {
        getServer().getScheduler().runTaskTimer( this, new Runnable()
        {

            @Override
            public void run()
            {
                for ( Player player : getServer().getOnlinePlayers() )
                {
                    if ( last == null )
                    {
                        last = player.getLocation();
                    }

                    Location playerLocation = player.getLocation();

                    // Vector relativeAddition = posMinecraft( 15, playerLocation.getYaw(), playerLocation.getPitch() );
                    Vector relativeAddition = rotateLocation( playerLocation.toVector(), playerLocation.clone().add( 5, 5, 5 ).toVector(), 50, 50 );

                    System.out.println( relativeAddition );
                    playerLocation.getWorld().spawn( playerLocation.add( relativeAddition ), Ocelot.class );
                }
            }
        }, 5, 5 );
    }

    private static Vector rotateLocation(Vector origin, Vector location, double yaw, double pitch)
    {
        Vector diff = location.clone().subtract( origin );

        double radius = diff.length();

        System.out.println( "Radius: " + radius );

        double oldYaw = Math.toDegrees( Math.atan2( diff.getX(), diff.getY() ) );

        System.out.println( "Yaw: " + oldYaw );

        double oldPitch = Math.toDegrees( Math.atan2( Math.sqrt( ( diff.getX() * diff.getX() ) + ( diff.getY() * diff.getY() ) ), diff.getZ() ) );
        System.out.println( "Pitch: " + oldPitch );

        return pos(radius, oldYaw + yaw, oldPitch + pitch );
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
