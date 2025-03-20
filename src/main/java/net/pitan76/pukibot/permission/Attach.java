package net.pitan76.pukibot.permission;

import java.util.Map;

public class Attach {

    public static boolean canGetTotal(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("attach")).get("total");
    }

    public static boolean canRead(Map<String, Object> permission) {
        return (boolean) ((Map<String, Object>) permission.get("attach")).get("read");
    }
}
