package net.pitan76.pukibot.permission;

import java.util.Map;

public class Page {

    public static boolean canRead(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("page")).get("read");
    }

    public static boolean canEdit(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("page")).get("edit");
    }

    public static boolean canExistCheck(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("page")).get("exist");
    }

    public static boolean canGetList(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("page")).get("list");
    }

    public static boolean canSearch(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("page")).get("search");
    }

    public static boolean canGetTotal(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("page")).get("total");
    }
}
