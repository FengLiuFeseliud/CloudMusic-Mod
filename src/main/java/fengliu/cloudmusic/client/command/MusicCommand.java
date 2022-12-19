package fengliu.cloudmusic.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.util.MusicPlayer;
import fengliu.cloudmusic.util.MusicPlayer.MusicPlayList;
import fengliu.cloudmusic.util.music163.*;

public class MusicCommand {
    private static Music163 music163 = new Music163(CloudMusicClient.CONFIG.getOrDefault( "cookie", ""));
    private static int volumePercentage = CloudMusicClient.CONFIG.getOrDefault("volume", 80);
    private static MusicPlayer player;
    private static Object data = null;
    private static My my = null;

    private static boolean getMy(boolean reset){
        if(my == null || reset){
            my = music163.my();
        }
        return true;
    }

    private static void resetPlayer(MusicPlayer newPlayer){
        if(player != null){
            player.stop();
        }
        player = newPlayer;
    }

    private static void resetConfig(){
        CloudMusicClient.resetConfig();
        music163 = new Music163(CloudMusicClient.CONFIG.getOrDefault( "cookie", ""));
        getMy(true);
    }

    private interface Job{
        void fun(CommandContext<FabricClientCommandSource> context) throws Exception;
    }

    private static void runCommand(CommandContext<FabricClientCommandSource> context, Job job){
        Thread commandThread = new Thread(){
            @Override
            public void run() {
                try {
                    job.fun(context);
                } catch (Exception err) {
                    context.getSource().sendFeedback(Text.literal(err.getMessage()));
                }
            }
        };
        commandThread.setDaemon(true);
        commandThread.setName("CloudMusic Thread");
        commandThread.start();
    }

    public static void registerAll(){
        LiteralArgumentBuilder<FabricClientCommandSource> CloudMusic = literal("cloudmusic");
        LiteralArgumentBuilder<FabricClientCommandSource> Music = literal("music");
        LiteralArgumentBuilder<FabricClientCommandSource> PlayList = literal("playlist");
        LiteralArgumentBuilder<FabricClientCommandSource> Artist = literal("artist");
        LiteralArgumentBuilder<FabricClientCommandSource> Album = literal("album");
        LiteralArgumentBuilder<FabricClientCommandSource> My = literal("my");
        LiteralArgumentBuilder<FabricClientCommandSource> Volume = literal("volume");

        CloudMusic.executes(context -> {
            return Command.SINGLE_SUCCESS;
        });

        // cloudmusic music id
        CloudMusic.then(Music.then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    data = music163.music(LongArgumentType.getLong(context, "id"));
                    ((Music) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));
        
        // cloudmusic music play id
        CloudMusic.then(Music.then(literal("play").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    resetPlayer((new MusicPlayList(music163.music(LongArgumentType.getLong(context, "id")))).createMusicPlayer(false));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })
        )));
    
        // cloudmusic music like id
        CloudMusic.then(Music.then(literal("like").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    music163.music(LongArgumentType.getLong(context, "id")).like(true);
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic playlist id
        CloudMusic.then(PlayList.then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    data = music163.playlist(LongArgumentType.getLong(context, "id"));
                    ((PlayList) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));
        
        // cloudmusic playlist play id
        CloudMusic.then(PlayList.then(literal("play").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    resetPlayer((new MusicPlayList(music163.playlist(LongArgumentType.getLong(context, "id")).getMusics())).createMusicPlayer(false));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic artist id
        CloudMusic.then(Artist.then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    music163.artist(LongArgumentType.getLong(context, "id"));
                });
                return Command.SINGLE_SUCCESS;
            })
        ));

        // cloudmusic album id
        CloudMusic.then(Album.then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    data = music163.album(LongArgumentType.getLong(context, "id"));
                    ((Album) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));
        
        // cloudmusic album play id
        CloudMusic.then(Album.then(literal("play").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    resetPlayer((new MusicPlayList(music163.album(LongArgumentType.getLong(context, "id")).getMusics())).createMusicPlayer(false));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })
        )));
        
        // cloudmusic my
        CloudMusic.then(My.executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(true);
                my.printToChatHud(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }));

        // cloudmusic my like
        CloudMusic.then(My.then(literal("like").executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(true);
                resetPlayer((new MusicPlayList(my.likeMusicPlayList().getMusics())).createMusicPlayer(false));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic my recommend
        CloudMusic.then(My.then(literal("recommend").executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(true);
                resetPlayer((new MusicPlayList(my.recommend_songs())).createMusicPlayer(false));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic volume
        CloudMusic.then(Volume.executes(context -> {
            context.getSource().sendFeedback(Text.translatable("cloudmusic.info.volume", volumePercentage));
            return Command.SINGLE_SUCCESS;
        }));

        // cloudmusic volume volume
        CloudMusic.then(Volume.then(
            argument("volume", IntegerArgumentType.integer()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    volumePercentage = IntegerArgumentType.getInteger(context, "volume");
                    if(player != null){
                        player.volumeSet(MusicPlayer.toVolume(volumePercentage));
                    }
                    CloudMusicClient.setConfigValue("volume", volumePercentage);
                });
                return Command.SINGLE_SUCCESS;
            }))
        );

        ClientCommandRegistrationCallback.EVENT.register((  dispatcher, registryAccess) -> {
            dispatcher.register(
                CloudMusic
                    .then(Music)
                    .then(PlayList)
                    .then(Artist)
                    .then(Album)
                    .then(My)
                    .then(Volume)
                    .then(
                        // cloudmusic stop
                        literal("stop").executes(context -> {
                            player.stop();
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic up
                        literal("up").executes(context -> {
                            player.up();
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic down
                        literal("down").executes(context -> {
                            player.down();
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic playing
                        literal("playing").executes(context -> {
                            player.playing().printToChatHud(context.getSource());
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic reset
                        literal("reset").executes(context -> {
                            resetConfig();
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic help
                        literal("help").executes(context -> {
                            return Command.SINGLE_SUCCESS;
                        })
                    )
            );
        });
    }
}
