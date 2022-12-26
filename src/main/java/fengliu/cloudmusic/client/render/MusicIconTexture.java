package fengliu.cloudmusic.client.render;

import fengliu.cloudmusic.CloudMusicMod;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.music163.Music;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class MusicIconTexture {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static Identifier MUSIC_ICON_ID = new Identifier(CloudMusicMod.MOD_ID, "music.icon");
    private static boolean canUseIcon = false;

    /**
     * 获取封面并注册材质
     * @param music 音乐
     */
    public static void getMusicIcon(Music music){
        Thread commandThread = new Thread(){
            @Override
            public void run() {
                NativeImage img;
                try {
                    img = NativeImage.read(HttpClient.downloadStream(music.picUrl + "?param=128y128"));
                } catch (Exception err) {
                    err.printStackTrace();
                    return;
                }

                NativeImageBackedTexture imageTexture = new NativeImageBackedTexture(img);
                client.execute(
                    () -> client.getTextureManager().registerTexture(MUSIC_ICON_ID, imageTexture)
                );

                canUseIcon = true;
            }
        };
        commandThread.setDaemon(true);
        commandThread.setName("CloudMusic getMusicIcon Thread");
        commandThread.start();
    }

    public static void nullIcon(){
        NativeImageBackedTexture imageTexture = new NativeImageBackedTexture(null);
        client.execute(
            () -> client.getTextureManager().registerTexture(MUSIC_ICON_ID, imageTexture)
        );
    }

    public static boolean canUseIcon(){
        return canUseIcon;
    }
}
