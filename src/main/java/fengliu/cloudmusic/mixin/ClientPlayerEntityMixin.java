/*
 * Copyright © 2025 RTAkland
 * Author: RTAkland
 * Date: 2025/2/6
 */


package fengliu.cloudmusic.mixin;

import com.mojang.authlib.GameProfile;
import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.util.MusicPlayer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Shadow public float prevNauseaIntensity;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Unique
    private boolean isVolumeAdjusted = false;

    @Unique
    private int previousVolume = 0;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        if (Configs.ENABLE.ENABLE_NEARBY_MONSTER_DECREASE_VOLUME.getBooleanValue()) {
            var player = (ClientPlayerEntity) (Object) this;
            var world = player.getWorld();
            var nearbyMobs = world.getEntitiesByClass(
                    LivingEntity.class,
                    player.getBoundingBox().expand(Configs.ALL.NEARBY_MONSTER_DECREASE_VOLUME_RADIUS.getIntegerValue()),
                    entity -> entity instanceof Monster
            );
            MusicPlayer musicPlayer = MusicCommand.getPlayer();
            IMusic playingMusic = musicPlayer.getPlayingMusic();
            if (playingMusic == null) {
                return;
            }
            if (!nearbyMobs.isEmpty() && !isVolumeAdjusted) {
                var currentVolume = musicPlayer.getVolumePercentage();
                this.previousVolume = currentVolume;
                musicPlayer.volumeSet(currentVolume - Configs.ALL.NEARBY_MONSTER_DECREASE_VOLUME_VALUE.getIntegerValue());
                isVolumeAdjusted = true;
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (Configs.ENABLE.ENABLE_NEARBY_MONSTER_DECREASE_VOLUME.getBooleanValue()) {
            var player = (ClientPlayerEntity) (Object) this;
            var world = player.getWorld();
            var nearbyMobs = world.getEntitiesByClass(
                    LivingEntity.class,
                    player.getBoundingBox().expand(10),
                    entity -> entity instanceof Monster
            );
            if (nearbyMobs.isEmpty() && isVolumeAdjusted) {
                isVolumeAdjusted = false;
                MusicPlayer musicPlayer = MusicCommand.getPlayer();
                IMusic playingMusic = musicPlayer.getPlayingMusic();
                if (playingMusic == null) {
                    return;
                }
                musicPlayer.volumeSet(this.previousVolume);
            }
        }
    }
}
