package fengliu.cloudmusic.mixin;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.music163.Lyric;
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

import fengliu.cloudmusic.client.command.MusicCommand;
import fengliu.cloudmusic.client.render.MusicIconTexture;
import fengliu.cloudmusic.music163.Music;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHubMixin {
    private Music oldMusic;
    private final MinecraftClient client = MinecraftClient.getInstance();
    
    @Inject(method = "render", at = @At("HEAD"))
    public void render(MatrixStack matrices, float tickDelta, CallbackInfo info){
        if(MusicCommand.loadQRCode){
            int width = this.client.getWindow().getScaledWidth();

            RenderSystem.setShader(GameRenderer::getPositionProgram);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, MusicIconTexture.QR_CODE_ID);

            MatrixStack imgMatrices =new MatrixStack();
            imgMatrices.translate(width - 64, 64, 0);
            imgMatrices.scale(0.50f,0.50f,0.50f);
            DrawableHelper.drawTexture(imgMatrices, 0, 0, 0, 0, 128, 128, 128, 128);
        }

        Music music = MusicCommand.playing();

        if(music == null){
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionProgram);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        int width = this.client.getWindow().getScaledWidth();

        if(CloudMusicClient.lyric){
            int lyricIn = CloudMusicClient.lyricHeight;
            for(String lyric: MusicCommand.getLyric()){
                MatrixStack lyricMatrices =new MatrixStack();
                lyricMatrices.scale(CloudMusicClient.lyricScale, CloudMusicClient.lyricScale, CloudMusicClient.lyricScale);
                DrawableHelper.drawStringWithShadow(lyricMatrices, client.textRenderer, lyric, CloudMusicClient.lyricWidth, lyricIn, 0xFFFFFF);
                lyricIn += 10;
            }
        }

        if(this.oldMusic == null){
            MusicIconTexture.getMusicIcon(music);
            this.oldMusic = music;
        }

        if(!music.picUrl.equals(oldMusic.picUrl)){
            MusicIconTexture.getMusicIcon(music);
        }

        if(!MusicIconTexture.canUseIcon()){
            return;
        }

        DrawableHelper.fill(matrices, width - 175, 0, width,  38, 0xE41318 + 0x4D000000);
        RenderSystem.setShaderTexture(0, MusicIconTexture.MUSIC_ICON_ID);

        MatrixStack imgMatrices =new MatrixStack();
        imgMatrices.translate(width - 172, 2.5f, 0);
        imgMatrices.scale(0.25f,0.25f,0.25f);
        DrawableHelper.drawTexture(imgMatrices, 0, 0, 0, 0, 128, 128, 128, 128);
        DrawableHelper.drawStringWithShadow(matrices, client.textRenderer, music.name.length() > 16 ? music.name.substring(0, 16) + "...": music.name, width - 135, 4, 0xFFFFFF);
        if(!music.aliasName.isEmpty()){
            DrawableHelper.drawStringWithShadow(matrices, client.textRenderer, music.aliasName.length() > 16 ? music.aliasName.substring(0, 16) + "...": music.aliasName, width - 135, 14, 0x9E9E9E);
        }else{
            String album = music.album.get("name").getAsString();
            DrawableHelper.drawStringWithShadow(matrices, client.textRenderer, album.length() > 16 ? album.substring(0, 16) + "...": album, width - 135, 14, 0x9E9E9E);
        }

        String artist = "";
        for (JsonElement artistData : music.artists.asList()) {
            artist += artistData.getAsJsonObject().get("name").getAsString() + "/";
        }
        artist = artist.substring(0, artist.length() - 1);
        DrawableHelper.drawStringWithShadow(matrices, client.textRenderer, artist.length() > 16 ? artist.substring(0, 16) + "...": artist, width - 135, 24, 0x9E9E9E);
        this.oldMusic = music;
    }

}
