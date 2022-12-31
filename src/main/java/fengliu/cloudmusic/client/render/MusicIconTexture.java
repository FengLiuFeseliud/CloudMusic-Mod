package fengliu.cloudmusic.client.render;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.music163.Music;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.QRCode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileInputStream;

public class MusicIconTexture {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static Identifier MUSIC_ICON_ID = new Identifier(CloudMusicClient.MOD_ID, "music.icon");
    public static Identifier QR_CODE_ID = new Identifier(CloudMusicClient.MOD_ID, "qr.code");
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

    public static void getQRCode(String QRCodeData){
        NativeImage img;
        try {
            File QRCodeFile = QRCode.generateQRCode(QRCodeData, CloudMusicClient.MC_PATH +"/cloud_music_qrcode.png", 128, 128, "png");
            img = NativeImage.read(new FileInputStream(QRCodeFile));
        } catch (Exception err) {
            err.printStackTrace();
            return;
        }

        NativeImageBackedTexture imageTexture = new NativeImageBackedTexture(img);
        client.execute(
                () -> client.getTextureManager().registerTexture(QR_CODE_ID, imageTexture)
        );
    }

    public static boolean canUseIcon(){
        return canUseIcon;
    }
}
