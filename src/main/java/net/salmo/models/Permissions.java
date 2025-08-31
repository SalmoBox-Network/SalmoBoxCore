package net.salmo.models;

public enum Permissions {
    COMMAND_DISPOSAL("disposal"),
    COMMAND_FLY("fly"),
    COMMAND_FLY_SPEED("fly.speed"),
    COMMAND_FLY_OTHERS("fly.others"),
    COMMAND_SPAWN("spawn"),
    COMMAND_PLAYTIME("playtime"),
    COMMAND_ENDERCHEST("enderchest"),
    COMMAND_FEED("feed"),
    COMMAND_NEAR("near"),
    COMMAND_WARP("warp"),

    // BYPASSES
    FEED_COOLDOWN_BYPASS("feed.cooldown.bypass"),
    NEAR_COOLDOWN_BYPASS("near.cooldown.bypass"),

    // ADMIN PERMISSIONS
    COMMAND_FEED_OTHERS("admin.feed.others"),
    COMMAND_SETSPAWN("admin.setspawn"),
    COMMAND_SEEN("admin.seen"),
    COMMAND_SETWARP("admin.setwarp"),
    COMMAND_DELWARP("admin.delwarp"),
    COMMAND_RELOAD("admin.reload");

    private final String perm;

    Permissions(String perm) {
        this.perm = perm;
    }

    public final String getPermission() {
        return "salmoboxcore." + this.perm;
    }
}
