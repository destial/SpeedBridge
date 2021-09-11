package me.tofpu.speedbridge.util;

import org.bukkit.Location;

public class Cuboid {
    private final int xMin;
    private final int xMax;
    private final int yMin;
    private final int yMax;
    private final int zMin;
    private final int zMax;

    public Cuboid(final Location point1, final Location point2) {
        xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
    }

    public static Cuboid of(final Location point1, final Location point2) {
        return new Cuboid(point1, point2);
    }

    public boolean isIn(final Location loc) {
        return loc.getBlockX() >= xMin && loc.getBlockX() <= xMax && loc.getBlockY() >= yMin && loc.getBlockY() <= yMax && loc
                .getBlockZ() >= zMin && loc.getBlockZ() <= zMax;
    }
}
