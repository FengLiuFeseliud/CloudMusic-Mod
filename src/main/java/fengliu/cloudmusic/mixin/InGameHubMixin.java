package fengliu.cloudmusic.mixin;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.DjMusic;
import fengliu.cloudmusic.music163.IMusic;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.systems.RenderSystem;

import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.render.MusicIconTexture;
import fengliu.cloudmusic.music163.Music;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHubMixin {
    private final MinecraftClient client = MinecraftClient.getInstance();
    
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo info){
        if(MusicCommand.loadQRCode){
            int width = this.client.getWindow().getScaledWidth();

            RenderSystem.setShader(GameRenderer::getPositionProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, MusicIconTexture.QR_CODE_ID);

            MatrixStack imgMatrices =new MatrixStack();
            imgMatrices.translate(width - 64, 74, 0);
            imgMatrices.scale(0.50f,0.50f,0.50f);
            DrawableHelper.drawTexture(imgMatrices, 0, 0, 0, 0, 128, 128, 128, 128);
        }

        IMusic playingMusic = MusicCommand.getPlayer().playingMusic();

        if(playingMusic == null){
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int width = this.client.getWindow().getScaledWidth();

        if(Configs.GUI.LYRIC.getBooleanValue()){
            float lyricScale = (float) Configs.GUI.LYRIC_SCALE.getDoubleValue();
            int lyricY = Configs.GUI.LYRIC_Y.getIntegerValue();
            int lyricX = Configs.GUI.LYRIC_X.getIntegerValue();

            int lyriccolor;
            try{
                lyriccolor = Integer.parseInt(Configs.GUI.LYRIC_COLOR.getStringValue(), 16);
            }catch(Exception err){
                lyriccolor = 0xFFFFFF;
            }

            for(String lyric: MusicCommand.getPlayer().getLyric()){
                MatrixStack lyricMatrices =new MatrixStack();
                lyricMatrices.scale(lyricScale, lyricScale, lyricScale);
                client.textRenderer.draw(lyricMatrices, lyric, lyricX, lyricY, lyriccolor);
                lyricY += 10;
            }
        }

        if(!Configs.GUI.MUSIC_INFO.getBooleanValue()){
            return;
        }

        int y = Configs.GUI.MUSIC_INFO_Y.getIntegerValue();
        int x = Configs.GUI.MUSIC_INFO_X.getIntegerValue();

        int musicColor;
        try{
            musicColor = Integer.parseInt(Configs.GUI.MUSIC_INFO_COLOR.getStringValue(), 16);
        }catch(Exception err){
            musicColor = 0x4DE41318;
        }

        int musicTitleColor;
        try{
            musicTitleColor = Integer.parseInt(Configs.GUI.MUSIC_INFO_TITLE_FONT_COLOR.getStringValue(), 16);
        }catch(Exception err){
            musicTitleColor = 0x9E9E9E;
        }

        int musicFontColor;
        try{
            musicFontColor = Integer.parseInt(Configs.GUI.MUSIC_INFO_FONT_COLOR.getStringValue(), 16);
        }catch(Exception err){
            musicFontColor = 0x9E9E9E;
        }

        DrawableHelper.fill(matrices, width - 175 - x, y, width - x,  38 + y, musicColor);
        RenderSystem.setShaderTexture(0, MusicIconTexture.MUSIC_ICON_ID);

        MatrixStack imgMatrices =new MatrixStack();
        imgMatrices.translate(width - 172 - x, 2.5f + y, 0);
        imgMatrices.scale(0.25f,0.25f,0.25f);
        DrawableHelper.drawTexture(imgMatrices, 0, 0, 0, 0, 128, 128, 128, 128);
        client.textRenderer.draw(matrices, playingMusic.getName().length() > 16 ? playingMusic.getName().substring(0, 16) + "...": playingMusic.getName(), width - 135 - x, 4 + y, musicTitleColor);
        if (playingMusic instanceof DjMusic){
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
        for (JsonElement artistData : music.artists.asList()) {
            artist.append(artistData.getAsJsonObject().get("name").getAsString()).append("/");
        }
        artist = new StringBuilder(artist.substring(0, artist.length() - 1));
        client.textRenderer.draw(matrices, artist.length() > 16 ? artist.substring(0, 16) + "...": artist.toString(), width - 135 - x, 24 + y, musicFontColor);
    }

}
