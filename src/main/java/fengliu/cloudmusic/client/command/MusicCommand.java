package fengliu.cloudmusic.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

import java.util.ArrayList;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.util.MusicPlayer;
import fengliu.cloudmusic.util.MusicPlayer.MusicPlayList;
import fengliu.cloudmusic.util.music163.*;
import fengliu.cloudmusic.util.page.Page;

public class MusicCommand {
    private static Music163 music163 = new Music163(CloudMusicClient.CONFIG.getOrDefault( "cookie", ""));
    private static int volumePercentage = CloudMusicClient.CONFIG.getOrDefault("volume", 80);
    private static boolean playUrl = CloudMusicClient.CONFIG.getOrDefault("play.url", false);
    private static MusicPlayer player = new MusicPlayer(new ArrayList<>(), false);
    private static Page page = null;
    private static Object data = null;
    private static My my = null;

    public static boolean isPlayUrl(){
        return playUrl;
    }

    private static void getMy(boolean reset, FabricClientCommandSource source){
        if(my == null || reset){
            my = music163.my();
        }

        if(my == null){
            source.sendFeedback(Text.translatable("cloudmusic.info.not.cookie"));
        }
    }
    
    /**
     * 重置音乐播放器
     * @param newPlayer 新音乐播放对象
     */
    private static void resetPlayer(MusicPlayer newPlayer){
        try {
            player.exit();
        } catch (Exception e) {
            
        }
        player = newPlayer;
    }

    /**
     * 重置配置
     */
    private static void resetConfig(){
        my = null;

        CloudMusicClient.resetConfig();
        music163 = new Music163(CloudMusicClient.CONFIG.getOrDefault( "cookie", ""));
        volumePercentage = CloudMusicClient.CONFIG.getOrDefault("volume", 80);
        playUrl = CloudMusicClient.CONFIG.getOrDefault("play.url", false);
    }

    private interface Job{
        void fun(CommandContext<FabricClientCommandSource> context) throws Exception;
    }

    /**
     * 新开线程运行指令
     * @param context 指令上下文
     * @param job 任务
     */
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
        LiteralArgumentBuilder<FabricClientCommandSource> User = literal("user");
        LiteralArgumentBuilder<FabricClientCommandSource> My = literal("my");
        LiteralArgumentBuilder<FabricClientCommandSource> Volume = literal("volume");
        LiteralArgumentBuilder<FabricClientCommandSource> Page = literal("page");

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
                    data = music163.artist(LongArgumentType.getLong(context, "id"));
                    ((Artist) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));

        // cloudmusic artist top id
        CloudMusic.then(Artist.then(literal("top").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    resetPlayer((new MusicPlayList(music163.artist(LongArgumentType.getLong(context, "id")).topSong())).createMusicPlayer(false));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic artist album id
        CloudMusic.then(Artist.then(literal("album").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    page = music163.artist(LongArgumentType.getLong(context, "id")).albumPage();
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic artist music id
        // CloudMusic.then(Artist.then(literal("music").then(
        //     argument("id", LongArgumentType.longArg()).executes(contextdata -> {
        //         runCommand(contextdata, context -> {
        //             resetPlayer((new MusicPlayList(music163.artist(LongArgumentType.getLong(context, "id")).music())).createMusicPlayer(false));
        //             player.start();
        //         });
        //         return Command.SINGLE_SUCCESS;
        //     })
        // )));

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

        // cloudmusic user id
        CloudMusic.then(User.then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    data = music163.user(LongArgumentType.getLong(context, "id"));
                    ((User) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));

        // cloudmusic user playlist id
        CloudMusic.then(User.then(literal("playlist").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    page = music163.user(LongArgumentType.getLong(context, "id")).playListsPage();
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            }))
        ));

        // cloudmusic user like id
        CloudMusic.then(User.then(literal("like").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    resetPlayer(new MusicPlayList(music163.user(LongArgumentType.getLong(context, "id")).likeMusicPlayList().getMusics()).createMusicPlayer(false));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            }))
        ));
        
        // cloudmusic my
        CloudMusic.then(My.executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(true, context.getSource());
                my.printToChatHud(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }));

        // cloudmusic my like
        CloudMusic.then(My.then(literal("like").executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(false, context.getSource());
                resetPlayer((new MusicPlayList(my.likeMusicPlayList().getMusics())).createMusicPlayer(false));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic my playlist
        CloudMusic.then(My.then(literal("playlist").executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(false, context.getSource());
                page = my.playListsPage();
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        LiteralArgumentBuilder<FabricClientCommandSource> Recommend = literal("recommend");

        // cloudmusic my recommend music
        CloudMusic.then(My.then(Recommend.then(literal("music").executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(false, context.getSource());
                resetPlayer((new MusicPlayList(my.recommend_songs())).createMusicPlayer(false));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my recommend playlist
        CloudMusic.then(My.then(Recommend.then(literal("playlist").executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(false, context.getSource());
                page = my.recommend_resource();
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        LiteralArgumentBuilder<FabricClientCommandSource> Sublist = literal("sublist");

        // cloudmusic my sublist album
        CloudMusic.then(My.then(Sublist.then(literal("album").executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(false, context.getSource());
                page = my.sublist_album();
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my sublist artist
        CloudMusic.then(My.then(Sublist.then(literal("artist").executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(false, context.getSource());
                page = my.sublist_artist();
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));


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
                    player.volumeSet(MusicPlayer.toVolume(volumePercentage));
                    CloudMusicClient.setConfigValue("volume", volumePercentage);
                });
                return Command.SINGLE_SUCCESS;
            }))
        );
        
        // cloudmusic page up
        CloudMusic.then(Page.then(literal("up").executes(context -> {
            if(page == null){
                return Command.SINGLE_SUCCESS;
            }
            page.up(context.getSource());
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic page down
        CloudMusic.then(Page.then(literal("down").executes(contextdata -> {
            if(page == null){
                return Command.SINGLE_SUCCESS;
            }

            runCommand(contextdata, context -> {
                page.down(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic page to page
        CloudMusic.then(Page.then(literal("to").then(
            argument("page", IntegerArgumentType.integer()).executes(contextdata -> {
                if(page == null){
                    return Command.SINGLE_SUCCESS;
                }

                runCommand(contextdata, context -> {
                    page.to(IntegerArgumentType.getInteger(context, "page") - 1, context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        ClientCommandRegistrationCallback.EVENT.register((  dispatcher, registryAccess) -> {
            dispatcher.register(
                CloudMusic
                    .then(
                        // cloudmusic stop
                        literal("stop").executes(context -> {
                            player.stop();
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic continue
                        literal("continue").executes(context -> {
                            player.continues();
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
                        // cloudmusic exit
                        literal("exit").executes(context -> {
                            player.exit();
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
                        literal("reset").executes(contextdata -> {
                            runCommand(contextdata, context -> {
                                player.stop();
                                FabricClientCommandSource source = context.getSource();
                                source.sendFeedback(Text.translatable("cloudmusic.info.config.reset"));

                                resetConfig();
                                getMy(true, source);

                                source.sendFeedback(Text.translatable("cloudmusic.info.config.complete"));
                                if(my == null){
                                    source.sendFeedback(Text.translatable("cloudmusic.info.config.cookie", "§cnull"));
                                }else{
                                    source.sendFeedback(Text.translatable("cloudmusic.info.config.cookie", "§c" + my.name));
                                }

                                source.sendFeedback(Text.translatable("cloudmusic.info.config.volume", "§c" + volumePercentage));
                                source.sendFeedback(Text.translatable("cloudmusic.info.config.play.url", "§c" + playUrl));
                                CloudMusicClient.cacheHelper.printToChatHud(source);
                            });
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
