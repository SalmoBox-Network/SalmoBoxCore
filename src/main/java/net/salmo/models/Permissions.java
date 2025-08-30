package net.salmo.models;

public enum Permissions {
    COMMAND_DISPOSAL("disposal");

    private final String perm;

    Permissions(String perm) {
        this.perm = perm;
    }

    public final String getPermission() {
        return "salmoboxcore." + this.perm;
    }
}
