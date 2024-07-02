package fengliu.cloudmusic.util;

import fengliu.cloudmusic.config.Configs;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.function.Function;

public class TextClickItem {
    public final MutableText show;
    protected MutableText text;
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

    public static MutableText combine(TextClickItem... items) {
        return combine(" ", mutableText -> mutableText.setStyle(mutableText.getStyle().withBold(true).withColor(Formatting.RED)), items);
    }

    public TextClickItem appendToStarts(MutableText text) {
        this.text = text.append(this.text);
        return this;
    }

    public TextClickItem appendToStarts(String textId) {
        return this.appendToStarts(Text.translatable(textId));
    }

    public TextClickItem append(MutableText text) {
        this.text.append(text);
        return this;
    }

    public TextClickItem append(String text) {
        this.text.append(text);
        return this;
    }

    public ClickEvent.Action getAction() {
        if (Configs.COMMAND.CLICK_RUN_COMMAND.getBooleanValue() && this.commandSuggest.startsWith("/")) {
            return ClickEvent.Action.RUN_COMMAND;
        }
        return ClickEvent.Action.SUGGEST_COMMAND;
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

    public MutableText build() {
        return this.text.setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(this.getAction(), this.commandSuggest))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this.show)));
    }
}
