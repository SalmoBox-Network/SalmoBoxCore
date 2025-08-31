package net.salmo.database;

import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class WarpData {
    private String name;
    @Setter
    private String permission;
    @Setter
    private String worldName;
    @Setter
    private double x;
    private double y;
    @Setter
    private double z;
    private float yaw;
    @Setter
    private float pitch;

    public WarpData() {}

    public WarpData(String name, Location location, String permission) {
        this.name = name.toLowerCase();
        this.permission = permission;
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name.toLowerCase(); }

    public String getPermission() { return permission; }

    public String getWorldName() { return worldName; }

    public double getX() { return x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }

    public float getYaw() { return yaw; }
    public void setYaw(float yaw) { this.yaw = yaw; }

    public float getPitch() { return pitch; }
}