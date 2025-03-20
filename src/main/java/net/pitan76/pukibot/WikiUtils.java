package net.pitan76.pukibot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiUtils {

    /**
     * 正規表現を使ってパターンマッチを行う共通メソッド
     *
     * @param source ソースコード
     * @param regex 正規表現
     * @return マッチした文字列
     */
    private static String extractFromSource(String source, String regex) {
        Pattern ptn = Pattern.compile(regex);
        Matcher m = ptn.matcher(source);
        if (m.find())
            return m.group(1);

        return null; // 一致しない場合はnullを返す
    }

    // おそらく使わなくなった関数
    /**
     * ソースコードの#author(...)から最終更新日時を取り出します (形式: xxxx-xx-xxTxx:xx:xx+xx:xx)
     *
     * @param source ソースコード
     * @return 最終更新日時
     */
    public static String getLastModifiedTime(String source) {
        return extractFromSource(source, "^#author\\(\"(.*?)(?:;.*?)?\",");
    }

    /**
     * ソースコードの#author(...)からユーザーネームを取り出します
     *
     * @param source ソースコード
     * @return ユーザーネーム
     */
    public static String getAuthorName(String source) {
        return extractFromSource(source, "^#author\\(\".*?\",\"(.*?)\"");
    }

    /**
     * ソースコードの#author(...)からフルネームを取り出します
     *
     * @param source ソースコード
     * @return フルネーム
     */
    public static String getAuthorFullName(String source) {
        return extractFromSource(source, "^#author\\(\".*?\",\".*?\",\"(.*?)\"");
    }

    /**
     * HTMLエンティティを元に戻します
     *
     * @param str ソースコード
     * @return 変換後のソースコード
     */
    public static String convertSourceFromPreSource(String str) {
        // HTMLエンティティの置換マッピング
        String[][] entities = {
                {"&amp;", "&"},
                {"&lt;", "<"},
                {"&gt;", ">"},
                {"&quot;", "\""}
        };

        for (String[] entity : entities) {
            str = str.replace(entity[0], entity[1]);
        }

        return str;
    }
}
