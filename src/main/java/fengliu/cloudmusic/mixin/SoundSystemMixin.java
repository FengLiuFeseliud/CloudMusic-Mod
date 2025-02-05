package fengliu.cloudmusic.mixin;

import fengliu.cloudmusic.command.MusicCommand;
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

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    public void play(SoundInstance soundInstance, CallbackInfo ci) {
        currentCategory = soundInstance.getCategory();
        MusicPlayer player = MusicCommand.getPlayer();
        IMusic playingMusic = player.getPlayingMusic();
        if (playingMusic != null) {
            if (currentCategory == SoundCategory.MUSIC) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"), cancellable = true)
    public void tick(CallbackInfo ci) {
        MusicPlayer player = MusicCommand.getPlayer();
        IMusic playingMusic = player.getPlayingMusic();
        if (playingMusic != null) {
            if (currentCategory == SoundCategory.MUSIC) {
                ci.cancel();
            }
        }
    }
}
