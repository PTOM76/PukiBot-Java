package net.pitan76.pukibot.permission;

import java.util.Map;

public class Plugin {

    public static boolean canExecute(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("plugin")).get("execute");
    }

    public static boolean canGetList(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("plugin")).get("list");
    }

    public static boolean canExistCheck(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("plugin")).get("exist");
    }

    public static boolean canGetTotal(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("plugin")).get("total");
    }
}
