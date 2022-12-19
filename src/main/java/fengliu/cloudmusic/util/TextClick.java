package fengliu.cloudmusic.util;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.ClickEvent.Action;

public class TextClick {

    public static MutableText suggestText(String text, String suggest){
        return Text.literal(text).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, suggest)));
    }

    public static MutableText suggestText(String key, String text, String suggest){
        return Text.translatable(key).append(TextClick.suggestText(text, suggest));
    }

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

    public static MutableText suggestTextMap(String key, Map<String, String> textAndSuggest, String sign){
        return Text.translatable(key).append(TextClick.suggestTextMap(textAndSuggest, sign));
    }
}
