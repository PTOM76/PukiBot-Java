package net.pitan76.pukibot.permission;

import java.util.Map;

public class Permission {
    public static boolean canGetInfo(Map<String, Object> permission) {
        return (boolean) permission.get("info");
    }
}