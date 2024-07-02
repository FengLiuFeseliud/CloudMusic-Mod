package fengliu.cloudmusic.util.click;

import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.util.IdUtil;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.function.Function;

public class TextClickItem {
    public final Text text;
    public final Text show;
    public final String commandSuggest;

    public TextClickItem(MutableText text, MutableText show, String commandSuggest) {
        this.text = text;
        this.show = show;
        this.commandSuggest = commandSuggest;
    }

    public TextClickItem(MutableText text, String commandSuggest) {
        this(text, Text.translatable(IdUtil.getOptionsShow("default")), commandSuggest);
    }

    public TextClickItem(String textId, String showId, String commandSuggest) {
        this(Text.translatable(textId), Text.translatable(showId), commandSuggest);
    }

    public TextClickItem(String textId, String commandSuggest) {
        this(IdUtil.getOptions(textId), IdUtil.getOptionsShow(textId), commandSuggest);
    }

    public static MutableText combine(String sign, Function<MutableText, MutableText> setText, TextClickItem... items) {
        MutableText text = Text.empty();
        for (int index = 0; index < items.length; index++) {
            text.append(setText.apply(items[index].build()));
            if (index + 1 != items.length) {
                text.append(sign);
            }
        }
        return text;
    }

    public static MutableText combine(String sign, TextClickItem... items) {
        return combine(sign, mutableText -> mutableText.setStyle(mutableText.getStyle().withBold(true).withColor(Formatting.RED)), items);
    }

    public ClickEvent.Action getAction() {
        if (Configs.COMMAND.CLICK_RUN_COMMAND.getBooleanValue()) {
            return ClickEvent.Action.RUN_COMMAND;
        }
        return ClickEvent.Action.SUGGEST_COMMAND;
    }

    public MutableText build() {
        return ((MutableText) this.text).setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(this.getAction(), this.commandSuggest))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this.show)));
    }
}
