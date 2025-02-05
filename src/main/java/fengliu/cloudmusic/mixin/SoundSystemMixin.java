package fengliu.cloudmusic.mixin;

import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.util.MusicPlayer;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在播放音乐的时候如果MC需要播放背景音乐的话
 * 就取消播放背景音乐的事件
 */
@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {

    @Unique
    public SoundCategory currentCategory;

    /**
     * 判断是否需要停止播放背景音乐
     * @param soundCategory 音乐类
     * @return false 不播放
     */
    private static boolean canStopGameMusic(SoundCategory soundCategory){
        if (!Configs.PLAY.NOT_PLAY_GAME_MUSIC.getBooleanValue()){
            return false;
        }

        MusicPlayer player = MusicCommand.getPlayer();
        IMusic playingMusic = player.getPlayingMusic();
        if (playingMusic == null) {
            return false;
        }

        return soundCategory == SoundCategory.MUSIC;
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance soundInstance, CallbackInfo ci) {
        if (!canStopGameMusic(soundInstance.getCategory())){
            return;
        }
        ci.cancel();
    }

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        if (!canStopGameMusic(currentCategory)){
            return;
        }
        ci.cancel();
    }
}
