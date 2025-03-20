package net.pitan76.pukibot.permission;

import java.util.Map;

public class Backup {

    public static boolean canRead(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("backup")).get("read");
    }

    public static boolean canGetTotal(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("backup")).get("total");
    }
}
