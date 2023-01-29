package fengliu.cloudmusic.mixin;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.data.DjMusic;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.util.MusicPlayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.systems.RenderSystem;

import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.render.MusicIconTexture;
import fengliu.cloudmusic.music163.data.Music;

@Mixin(InGameHud.class)
public class MixinInGameHub {
    private final MinecraftClient client = MinecraftClient.getInstance();
    
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo info){
        if(MusicCommand.loadQRCode){
            int width = this.client.getWindow().getScaledWidth();

            RenderSystem.setShader(GameRenderer::getPositionShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, MusicIconTexture.QR_CODE_ID);

            MatrixStack imgMatrices =new MatrixStack();
            imgMatrices.translate(width - 64, 74, 0);
            imgMatrices.scale(0.50f,0.50f,0.50f);
            DrawableHelper.drawTexture(imgMatrices, 0, 0, 0, 0, 128, 128, 128, 128);
        }

        MusicPlayer player = MusicCommand.getPlayer();
        IMusic playingMusic = player.getPlayingMusic();

        if(playingMusic == null){
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int width = this.client.getWindow().getScaledWidth();

        if(Configs.GUI.LYRIC.getBooleanValue()){
            float lyricScale = (float) Configs.GUI.LYRIC_SCALE.getDoubleValue();
            int lyricY = Configs.GUI.LYRIC_Y.getIntegerValue();
            int lyricX = Configs.GUI.LYRIC_X.getIntegerValue();

            int lyricColor = Configs.GUI.LYRIC_COLOR.getIntegerValue();
            for(String lyric: MusicCommand.getPlayer().getLyric()){
                MatrixStack lyricMatrices =new MatrixStack();
                lyricMatrices.scale(lyricScale, lyricScale, lyricScale);
                client.textRenderer.draw(lyricMatrices, lyric, lyricX, lyricY, lyricColor);
                lyricY += 10;
            }
        }

        if(!Configs.GUI.MUSIC_INFO.getBooleanValue()){
            return;
        }

        int y = Configs.GUI.MUSIC_INFO_Y.getIntegerValue();
        int x = Configs.GUI.MUSIC_INFO_X.getIntegerValue();

        DrawableHelper.fill(matrices, width - 175 - x, y, width - x,  48 + y, Configs.GUI.MUSIC_INFO_COLOR.getIntegerValue());
        DrawableHelper.fill(matrices, width - 145 - x, 40 + y, width - 30 - x,  43 + y, Configs.GUI.MUSIC_PROGRESS_BAR_COLOR.getIntegerValue());
        int progress = Math.round((115 / (float) playingMusic.getDurationSecond()) * player.getPlayingProgressSecond());
        if (progress > 115){
            progress = 115;
        }
        DrawableHelper.fill(matrices, width - 145 - x, 40 + y, width - 145 + progress - x,  43 + y, Configs.GUI.MUSIC_PLAYED_PROGRESS_BAR_COLOR.getIntegerValue());
        RenderSystem.setShaderTexture(0, MusicIconTexture.MUSIC_ICON_ID);

        MatrixStack imgMatrices =new MatrixStack();
        imgMatrices.translate(width - 172 - x, 2.5f + y, 0);
        imgMatrices.scale(0.25f,0.25f,0.25f);
        DrawableHelper.drawTexture(imgMatrices, 0, 0, 0, 0, 128, 128, 128, 128);
        client.textRenderer.draw(matrices, playingMusic.getName().length() > 16 ? playingMusic.getName().substring(0, 16) + "...": playingMusic.getName(), width - 135 - x, 4 + y, Configs.GUI.MUSIC_INFO_TITLE_FONT_COLOR.getIntegerValue());

        int progressFontColor = Configs.GUI.MUSIC_PROGRESS_FONT_COLOR.getIntegerValue();
        client.textRenderer.draw(matrices, player.getPlayingProgressToString(), width - 172 - x, 38 + y, progressFontColor);
        client.textRenderer.draw(matrices, playingMusic.getDurationToString(), width - 28 - x, 38 + y, progressFontColor);
        int musicFontColor = Configs.GUI.MUSIC_INFO_FONT_COLOR.getIntegerValue();
        if (playingMusic instanceof DjMusic music){
            client.textRenderer.draw(matrices, new TranslatableText("cloudmusic.info.dj.creator", music.dj.get("nickname").getAsString()), width - 135 - x, 14 + y, musicFontColor);
            client.textRenderer.draw(matrices, new TranslatableText("cloudmusic.info.dj.music.count", music.listenerCount, music.likedCount), width - 135 - x, 24 + y, musicFontColor);
            return;
        }

        Music music = (Music) playingMusic;
        if(!music.aliasName.isEmpty()){
            client.textRenderer.draw(matrices, music.aliasName.length() > 16 ? music.aliasName.substring(0, 16) + "...": music.aliasName, width - 135 - x, 14 + y, musicFontColor);
        }else{
            String album = music.album.get("name").getAsString();
            client.textRenderer.draw(matrices, album.length() > 16 ? album.substring(0, 16) + "...": album, width - 135 - x, 14 + y, musicFontColor);
        }

        StringBuilder artist = new StringBuilder();
        for (JsonElement artistData : music.artists) {
            artist.append(artistData.getAsJsonObject().get("name").getAsString()).append("/");
        }
        artist = new StringBuilder(artist.substring(0, artist.length() - 1));
        client.textRenderer.draw(matrices, artist.length() > 16 ? artist.substring(0, 16) + "...": artist.toString(), width - 135 - x, 24 + y, musicFontColor);
    }

}
