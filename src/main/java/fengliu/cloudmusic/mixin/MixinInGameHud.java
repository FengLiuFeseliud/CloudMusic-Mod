package fengliu.cloudmusic.mixin;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.systems.RenderSystem;
import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.music163.data.DjMusic;
import fengliu.cloudmusic.music163.data.Music;
import fengliu.cloudmusic.render.MusicIconTexture;
import fengliu.cloudmusic.util.MusicPlayer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    static {
        final MinecraftClient client = MinecraftClient.getInstance();
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            MusicPlayer player = MusicCommand.getPlayer();
            IMusic playingMusic = player.getPlayingMusic();
            if (playingMusic == null) {
                return;
            }
            float lyricScale = (float) Configs.GUI.LYRIC_SCALE.getDoubleValue();
            int lyricY = Configs.GUI.LYRIC_Y.getIntegerValue();
            int lyricX = Configs.GUI.LYRIC_X.getIntegerValue();
            int lyricColor = Configs.GUI.LYRIC_COLOR.getIntegerValue();
            for (String lyric : MusicCommand.getPlayer().getLyric()) {
                MatrixStack lyricMatrices = new MatrixStack();
                lyricMatrices.scale(lyricScale, lyricScale, lyricScale);
                drawContext.drawText(client.textRenderer, lyric, lyricX, lyricY, lyricColor, true);
                lyricY += 10;
            }
        });
    }

    @Unique
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Unique
    public void renderLoginQrCode(DrawContext context) {
        if (!MusicCommand.loadQRCode) {
            return;
        }
        Function<Identifier, RenderLayer> renderLayer = RenderLayer::getGuiTexturedOverlay;
        context.drawTexture(renderLayer, MusicIconTexture.QR_CODE_ID, this.client.getWindow().getScaledWidth() - 64, 72, 64, 64, 0, 0, 128, 128, 128, 128);
    }

    @Unique
    public int[] getMusicInfoPos() {
        int y = Configs.GUI.MUSIC_INFO_Y.getIntegerValue();
        int x = Configs.GUI.MUSIC_INFO_X.getIntegerValue();
        if (this.client.player == null || !Configs.GUI.MUSIC_INFO_EFFECT_OFFSET.getBooleanValue()) {
            return new int[]{y, x};
        }

        int offset = 0;
        for (StatusEffectInstance statusEffect : this.client.player.getStatusEffects()) {
            if (statusEffect.getEffectType().value().isBeneficial()) {
                offset = 1;
            } else {
                offset = 2;
                break;
            }
        }

        if (offset == 0) {
            return new int[]{y, x};
        }
        return new int[]{y + Configs.GUI.MUSIC_INFO_EFFECT_OFFSET_Y.getIntegerValue() * offset, x + Configs.GUI.MUSIC_INFO_EFFECT_OFFSET_X.getIntegerValue()};
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        this.renderLoginQrCode(context);

        MusicPlayer player = MusicCommand.getPlayer();
        IMusic playingMusic = player.getPlayingMusic();

        if (playingMusic == null) {
            return;
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (!Configs.GUI.MUSIC_INFO.getBooleanValue()) {
            return;
        }

        int width = this.client.getWindow().getScaledWidth();

        int[] pos = this.getMusicInfoPos();
        int y = pos[0];
        int x = pos[1];

        context.fill(width - 175 - x, y, width - x, 48 + y, Configs.GUI.MUSIC_INFO_COLOR.getIntegerValue());
        context.fill(width - 145 - x, 40 + y, width - 30 - x, 43 + y, Configs.GUI.MUSIC_PROGRESS_BAR_COLOR.getIntegerValue());
        int progress = Math.round((115 / (float) playingMusic.getDurationSecond()) * player.getPlayingProgressSecond());
        if (progress > 115) {
            progress = 115;
        }
        Function<Identifier, RenderLayer> renderLayer = RenderLayer::getGuiTexturedOverlay;
        context.fill(width - 145 - x, 40 + y, width - 145 + progress - x, 43 + y, Configs.GUI.MUSIC_PLAYED_PROGRESS_BAR_COLOR.getIntegerValue());
        context.drawTexture(renderLayer, MusicIconTexture.MUSIC_ICON_ID, width - 172 - x, (int) (2.5f + y), 32f, 32f, 32, 32, 32, 32);
        context.drawText(this.client.textRenderer, playingMusic.getName().length() > 16 ? playingMusic.getName().substring(0, 16) + "..." : playingMusic.getName(), width - 135 - x, 4 + y, Configs.GUI.MUSIC_INFO_TITLE_FONT_COLOR.getIntegerValue(), true);

        int progressFontColor = Configs.GUI.MUSIC_PROGRESS_FONT_COLOR.getIntegerValue();
        context.drawText(this.client.textRenderer, player.getPlayingProgressToString(), width - 172 - x, 38 + y, progressFontColor, true);
        context.drawText(this.client.textRenderer, playingMusic.getDurationToString(), width - 28 - x, 38 + y, progressFontColor, true);
        int musicFontColor = Configs.GUI.MUSIC_INFO_FONT_COLOR.getIntegerValue();
        if (playingMusic instanceof DjMusic music) {
            context.drawText(this.client.textRenderer, Text.translatable("cloudmusic.info.dj.creator", music.dj.get("nickname").getAsString()), width - 135 - x, 14 + y, musicFontColor, true);
            context.drawText(this.client.textRenderer, Text.translatable("cloudmusic.info.dj.music.count", music.listenerCount, music.likedCount), width - 135 - x, 24 + y, musicFontColor, true);
            return;
        }

        Music music = (Music) playingMusic;
        if (!music.aliasName.isEmpty()) {
            context.drawText(this.client.textRenderer, music.aliasName.length() > 16 ? music.aliasName.substring(0, 16) + "..." : music.aliasName, width - 135 - x, 14 + y, musicFontColor, true);
        } else {
            String album = music.album.get("name").getAsString();
            context.drawText(this.client.textRenderer, album.length() > 16 ? album.substring(0, 16) + "..." : album, width - 135 - x, 14 + y, musicFontColor, true);
        }

        StringBuilder artist = new StringBuilder();
        for (JsonElement artistData : music.artists.asList()) {
            artist.append(artistData.getAsJsonObject().get("name").getAsString()).append("/");
        }
        artist = new StringBuilder(artist.substring(0, artist.length() - 1));
        context.drawText(this.client.textRenderer, artist.length() > 16 ? artist.substring(0, 16) + "..." : artist.toString(), width - 135 - x, 24 + y, musicFontColor, true);

        if (music.freeTrialInfo == null) {
            return;
        }

        int freeTrialEndProgress = Math.round((115 / (float) playingMusic.getDurationSecond()) * music.freeTrialInfo.get("end").getAsInt());
        context.fill(width - 145 + freeTrialEndProgress - 1 - x, 40 + y, width - 145 + freeTrialEndProgress + 1 - x, 43 + y, Configs.GUI.MUSIC_PLAYED_PROGRESS_BAR_COLOR.getIntegerValue());
    }

}
