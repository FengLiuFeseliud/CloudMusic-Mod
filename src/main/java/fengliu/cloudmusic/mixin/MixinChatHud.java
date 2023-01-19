package fengliu.cloudmusic.mixin;

import fengliu.cloudmusic.music163.Shares;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class MixinChatHud {
    private static final Pattern SHAR_PATTERN = Pattern.compile("CloudMusic#.+\\sid:\\s[^\\sid:][1-9][^\\sa-zA-Z]+$", Pattern.CASE_INSENSITIVE);

    /**
     * 判断是否为分享消息
     * @param sharMatcher matcher
     * @return bool
     */
    private boolean isSharMessage(Matcher sharMatcher){
        if (!sharMatcher.find()) {
            return false;
        }

        return sharMatcher.groupCount() <= 1;
    }

    /**
     * 设置分享消息样式
     * @param message 消息
     */
    private void setShar(Text message){
        Matcher sharMatcher = SHAR_PATTERN.matcher(message.getString());
        if (!isSharMessage(sharMatcher)) {
            return;
        }

        String[] keyValuePair = sharMatcher.group(0).replace("CloudMusic# ", "").split(" id: ");
        if (keyValuePair.length > 2){
            return;
        }

        for (Shares shar: Shares.values()) {
            if(!shar.isShar(keyValuePair[0])){
                continue;
            }

            ((MutableText) message).setStyle(
                Style.EMPTY.withClickEvent(new ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        shar.getCommand(Long.parseLong(keyValuePair[1]))
                    ))
                    .withColor(0x87CEEB)
            );
            return;
        }
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    public void addMessage(Text message, CallbackInfo info){
        setShar(message);
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V ", at = @At("HEAD"))
    public void addMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, CallbackInfo info){
        setShar(message);
    }
}
