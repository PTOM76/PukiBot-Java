package net.pitan76.pukibot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpHelper {

    private static final Logger logger = Logger.getLogger(HttpHelper.class.getName());

    private static final Gson gson = new Gson();

    /**
     * getResponseで得たものをJsonのマップ(辞書型)で返す
     *
     * @param url URL
     * @param headers ヘッダー
     * @param data POSTデータ
     * @param method GET or POST
     * @return JsonObject
     */
    public static JsonObject getResponseAsJsonMap(String url, Map<String, String> headers, Map<String, String> data, String method) {
        try {
            String res = getResponse(url, headers, data, method);
            if (res == null)
                return null;

            return gson.fromJson(res, JsonObject.class);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in getResponseAsJsonMap: ", e);
            return null;
        }
    }

    /**
     * StringをMap(辞書型)で返す
     *
     * @param str 文字列
     * @return JsonObject
     */
    public static JsonObject toMap(String str) {
        try {
            if (str == null)
                return null;

            return gson.fromJson(str, JsonObject.class);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in toMap: ", e);
            return null;
        }
    }

    /**
     * Bot APIとのやり取りをするための関数
     *
     * @param urlStr URL
     * @param headers ヘッダー
     * @param data POSTデータ
     * @param method GET or POST
     * @return String
     */
    public static String getResponse(String urlStr, Map<String, String> headers, Map<String, String> data, String method) {
        try {
            // URLを指定して接続を開く
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // メソッドの設定 (GET, POST など)
            conn.setRequestMethod(method);

            // ヘッダーの設定
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // POSTデータの送信 (dataがnullでない場合)
            if (data != null) {
                conn.setDoOutput(true);
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    if (postData.length() > 0) postData.append("&");
                    postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    postData.append("=")
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = postData.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // レスポンスの読み取り
            int status = conn.getResponseCode();
            BufferedReader in;
            if (status < HttpURLConnection.HTTP_BAD_REQUEST) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in getResponse: ", e);
            return null;
        }
    }
}
