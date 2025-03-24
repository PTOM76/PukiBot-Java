package net.pitan76.pukibot;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class PukiBot {

    private String url = null;
    private String token = null;
    private String userAgent = null;

    private JsonObject cacheInfo = null;
    private JsonObject cachePermission = null;

    public static void main(String[] args) {
        String URL = "https://pukiwiki.example.com/";
        PukiBot bot = new PukiBot(URL, "MDhEOTQ2MjE4NjJDRjAwRjdGNzhCNDlEQTgxN0RBMzk");

        // キャッシュされたデータの表示
        System.out.println(bot.getPage("FrontPage").get("source"));
    }

    private static Map<String, Object> toMap(JsonObject json) {
        if (json == null)
            return null;

        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            map.put(entry.getKey(), convertJsonElement(entry.getValue()));
        }
        return map;
    }

    private static Object convertJsonElement(JsonElement element) {
        if (element.isJsonNull())
            return null;

        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean())
                return primitive.getAsBoolean();

            if (primitive.isNumber())
                return primitive.getAsNumber();

            if (primitive.isString())
                return primitive.getAsString();
        }

        if (element.isJsonArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonElement arrayElement : element.getAsJsonArray()) {
                list.add(convertJsonElement(arrayElement));
            }
            return list;
        }

        if (element.isJsonObject()) {
            return toMap(element.getAsJsonObject());
        }

        return null;
    }

    public PukiBot(String url, String token) {
        this.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");
        if (url != null) {
            this.setUrl(url);
        }
        if (token != null) {
            this.setToken(token);
        }
    }

    // ユーザーエージェントを設定
    public void setUserAgent(String agent) {
        this.userAgent = agent;
    }

    // ユーザーエージェントを取得
    public String getUserAgent() {
        return this.userAgent;
    }

    // URLを設定
    public void setUrl(String url) {
        this.url = url;
    }

    // URLを取得
    public String getUrl() {
        return this.url;
    }

    // トークンを設定
    public void setToken(String token) {
        this.token = token;
    }

    // トークンを取得
    public String getToken() {
        return this.token;
    }

    private static Map<String, String> headers(String headers) {
        Map<String, String> map = new HashMap<>();
        String[] headerList = headers.split("\n");
        for (String header : headerList) {
            String[] headerData = header.split(":");
            map.put(headerData[0], headerData[1]);
        }
        return map;
    }

    // Infoデータ取得
    public Map<String, Object> getInfo(boolean dict, boolean cache) throws IOException {
        String authorization = "";
        if (this.getToken() != null) {
            authorization = "&authorization=" + this.getToken();
        }
        String url = this.getUrl() + "?cmd=bot&api=info" + authorization;
        if (dict) {
            // JSONデータをMap形式で取得
            JsonObject data = HttpHelper.getResponseAsJsonMap(url, headers("User-Agent: " + this.getUserAgent()), null, "GET");
            if (cache) {
                this.cacheInfo = data;
            }
            return toMap(data);
        } else {
            // String型で取得
            String data = HttpHelper.getResponse(url, headers("User-Agent: " + this.getUserAgent()), null, "GET");
            if (cache) {
                this.cacheInfo = HttpHelper.toMap(data);
            }
            return toMap(HttpHelper.toMap(data));
        }
    }

    // PukiWikiのバージョンを取得
    public String getPukiWikiVersion() throws IOException {
        return (String) this.getInfo(true, true).get("pukiwiki.version");
    }

    // Wikiのタイトルを取得
    public String getWikiTitle() throws IOException {
        return (String) this.getInfo(true, true).get("page_title");
    }

    // 管理人の名前を取得
    public String getWikiAdmin() throws IOException {
        return (String) this.getInfo(true, true).get("modifier");
    }

    // 管理人のサイトを取得
    public String getWikiAdminSite() throws IOException {
        return (String) this.getInfo(true, true).get("modifierlink");
    }

    // パーミッションのデータ取得
    public Map<String, Object> getPermission(boolean cache, boolean dict) {
        String authorization = "";
        if (this.getToken() != null) {
            authorization = "&authorization=" + this.getToken();
        }
        String url = this.getUrl() + "?cmd=bot&api=permission" + authorization;
        if (dict) {
            JsonObject data = HttpHelper.getResponseAsJsonMap(url, headers("User-Agent: " + this.getUserAgent()), null, "GET");
            if (cache) {
                this.cachePermission = data;
            }
            return toMap(data);
        } else {
            String data = HttpHelper.getResponse(url, headers("User-Agent: " + this.getUserAgent()), null, "GET");
            if (cache) {
                this.cachePermission = HttpHelper.toMap(data);
            }
            return toMap(HttpHelper.toMap(data));
        }
    }

    // ページのデータ取得
    public Map<String, Object> getPage(String page, boolean dict, boolean permitCheck) {
        if (permitCheck) {
            if (this.cachePermission != null) {
                if (!(boolean) this.cachePermission.get("permission.page.canRead").getAsBoolean()) {
                    return null;
                }
            } else if (this.getPermission(true, true) != null) {
                if (!(boolean) this.cachePermission.get("permission.page.canRead").getAsBoolean()) {
                    return null;
                }
            }
        }
        String authorization = "";
        if (this.getToken() != null) {
            authorization = "&authorization=" + this.getToken();
        }
        String url = this.getUrl() + "?cmd=bot&api=page" + authorization + "&name=" + page;
        if (dict) {
            return toMap(HttpHelper.getResponseAsJsonMap(url, headers("User-Agent: " + this.getUserAgent()), null, "GET"));
        } else {
            String data = HttpHelper.getResponse(url, headers("User-Agent: " + this.getUserAgent()), null, "GET");
            return toMap(HttpHelper.toMap(data));
        }
    }

    public Map<String, Object> getPage(String page) {
        return getPage(page, false, false);
    }

    // ページの書き込み
    public Map<String, Object> writePage(String page, String source, boolean permitCheck, boolean notimestamp, boolean dict) throws IOException {
        if (permitCheck) {
            if (this.cachePermission != null) {
                if (!(boolean) this.cachePermission.get("permission.page.canEdit").getAsBoolean()) {
                    return null;
                }
            } else if (this.getPermission(true, true) != null) {
                if (!(boolean) this.cachePermission.get("permission.page.canEdit").getAsBoolean()) {
                    return null;
                }
            }
        }
        Map<String, String> data = new HashMap<>();
        data.put("name", page);
        data.put("source", source);
        data.put("notimestamp", String.valueOf(notimestamp));

        String authorization = "";
        if (this.getToken() != null) {
            authorization = "&authorization=" + this.getToken();
        }

        String url = this.getUrl() + "?cmd=bot&api=page" + authorization;
        if (dict) {
            return toMap(HttpHelper.getResponseAsJsonMap(url, headers("User-Agent: " + this.getUserAgent()), data, "PUT"));
        } else {
            String response = HttpHelper.getResponse(url, headers("User-Agent: " + this.getUserAgent()), data, "PUT");
            return toMap(HttpHelper.toMap(response));
        }
    }

    // ページの削除
    public Map<String, Object> deletePage(String page, boolean permitCheck, boolean dict) throws IOException {
        if (permitCheck) {
            if (this.cachePermission != null) {
                if (!(boolean) this.cachePermission.get("permission.page.canEdit").getAsBoolean()) {
                    return null;
                }
            } else if (this.getPermission(true, true) != null) {
                if (!(boolean) this.cachePermission.get("permission.page.canEdit").getAsBoolean()) {
                    return null;
                }
            }
        }
        Map<String, String> data = new HashMap<>();
        data.put("name", page);

        String authorization = "";
        if (this.getToken() != null) {
            authorization = "&authorization=" + this.getToken();
        }

        String url = this.getUrl() + "?cmd=bot&api=page" + authorization;
        if (dict) {
            return toMap(HttpHelper.getResponseAsJsonMap(url, headers("User-Agent: " + this.getUserAgent()), data, "DELETE"));
        } else {
            String response = HttpHelper.getResponse(url, headers("User-Agent: " + this.getUserAgent()), data, "DELETE");
            return toMap(HttpHelper.toMap(response));
        }
    }

    // ページの存在チェック
    public boolean existPage(String page, boolean permitCheck) throws IOException {
        return (boolean) this.getExistPageData(page, permitCheck, true).get("exist");
    }

    public Map<String, Object> getExistPageData(String page, boolean permitCheck, boolean dict) throws IOException {
        if (permitCheck) {
            if (this.cachePermission != null) {
                if (!(boolean) this.cachePermission.get("permission.page.canExistCheck").getAsBoolean()) {
                    return null;
                }
            } else if (this.getPermission(true, true) != null) {
                if (!(boolean) this.cachePermission.get("permission.page.canExistCheck").getAsBoolean()) {
                    return null;
                }
            }
        }

        String authorization = "";
        if (this.getToken() != null) {
            authorization = "&authorization=" + this.getToken();
        }

        String url = this.getUrl() + "?cmd=bot&api=exist" + authorization + "&type=page&name=" + page;
        if (dict) {
            return toMap(HttpHelper.getResponseAsJsonMap(url, headers("User-Agent: " + this.getUserAgent()), null, "GET"));
        } else {
            String data = HttpHelper.getResponse(url, headers("User-Agent: " + this.getUserAgent()), null, "GET");
            return toMap(HttpHelper.toMap(data));
        }
    }
}


