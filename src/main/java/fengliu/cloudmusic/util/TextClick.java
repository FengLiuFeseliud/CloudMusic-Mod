package fengliu.cloudmusic.util;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.Map;
import java.util.Map.Entry;

/**
 * 自定义事件文本对象
 */
public class TextClick {

    /**
     * 设置一个点击后命令提议的文本对象 (net.minecraft.text)
     * @param text 文本字符串
     * @param suggest 命令提议
     * @return 文本对象
     */
    public static MutableText suggestText(String text, String suggest){
        return Text.literal(text).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, suggest)));
    }

    /**
     * 设置一个可译文本在前, 点击后命令提议的文本对象 (net.minecraft.text)
     * @param key 可译文本 key
     * @param text 文本字符串
     * @param suggest 命令提议
     * @return 文本对象
     */
    public static MutableText suggestText(String key, String text, String suggest){
        return Text.translatable(key, TextClick.suggestText(text, suggest));
    }

    /**
     * 设置一个有多个点击后命令提议的文本对象 (net.minecraft.text)
     * @param textAndSuggest 文本对 (key 文本字符串, value 命令提议)
     * @param sign 分割符
     * @return 文本对象
     */
    public static MutableText suggestTextMap(Map<String, String> textAndSuggest, String sign){
        MutableText text = Text.empty();
        int index = 0;

        for (Entry<String, String> data : textAndSuggest.entrySet()) {
            text.append(TextClick.suggestText(data.getKey(), data.getValue()));

            if(index < textAndSuggest.size() - 1){
                text.append(sign);
                index ++;
            }
        }

        return text;
    }

    /**
     * 设置一个可译文本在前, 有多个点击后命令提议的文本对象 (net.minecraft.text)
     * @param key 可译文本 key
     * @param textAndSuggest 文本对 (key 文本字符串, value 命令提议)
     * @param sign 分割符
     * @return 文本对象
     */
    public static MutableText suggestTextMap(String key, Map<String, String> textAndSuggest, String sign){
        return Text.translatable(key, TextClick.suggestTextMap(textAndSuggest, sign));
    }
}
