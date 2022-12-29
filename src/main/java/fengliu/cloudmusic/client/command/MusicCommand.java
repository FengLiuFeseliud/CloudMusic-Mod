package fengliu.cloudmusic.client.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import java.util.ArrayList;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.music163.*;
import fengliu.cloudmusic.util.MusicPlayer;
import fengliu.cloudmusic.util.page.Page;

public class MusicCommand {
    private static Music163 music163 = new Music163(CloudMusicClient.CONFIG.getOrDefault( "cookie", ""));
    private static int volumePercentage = CloudMusicClient.CONFIG.getOrDefault("volume", 80);
    private static boolean playUrl = CloudMusicClient.CONFIG.getOrDefault("play.url", false);
    private static boolean loopPlay = CloudMusicClient.CONFIG.getOrDefault("play.loop", true);
    private static MusicPlayer player = new MusicPlayer(new ArrayList<>());
    private static Page page = null;
    private static Object data = null;
    private static My my = null;

    public static boolean isPlayUrl(){
        return playUrl;
    }

    public static boolean isLoopPlay(){
        return loopPlay;
    }

    private static My getMy(boolean reset){
        if(my == null || reset){
            my = music163.my();
        }

        return my;
    }
    
    /**
     * 重置音乐播放器
     * @param musics 音乐列表
     */
    private static void resetPlayer(List<Music> musics){
        try {
            player.exit();
        } catch (Exception e) {
            
        }
        player = new MusicPlayer(musics);
    }

    /**
     * 重置音乐播放器
     * @param newPlayer 播放器
     */
    private static void resetPlayer(MusicPlayer newPlayer){
        try {
            player.exit();
        } catch (Exception e) {
            
        }
        player = newPlayer;
    }

    /**
     * 重置音乐播放器
     * @param music 音乐
     */
    private static void resetPlayer(Music music){
        List<Music> musics = new ArrayList<>();
        musics.add(music);

        resetPlayer(musics);
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
        loopPlay = CloudMusicClient.CONFIG.getOrDefault("play.loop", true);
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

    public static Music playing(){
        return player.playing();
    }

    public static void registerAll(){
        LiteralArgumentBuilder<FabricClientCommandSource> CloudMusic = literal("cloudmusic");
        LiteralArgumentBuilder<FabricClientCommandSource> Music = literal("music");
        LiteralArgumentBuilder<FabricClientCommandSource> PlayList = literal("playlist");
        LiteralArgumentBuilder<FabricClientCommandSource> Artist = literal("artist");
        LiteralArgumentBuilder<FabricClientCommandSource> Album = literal("album");
        LiteralArgumentBuilder<FabricClientCommandSource> User = literal("user");
        LiteralArgumentBuilder<FabricClientCommandSource> My = literal("my");
        LiteralArgumentBuilder<FabricClientCommandSource> Playing = literal("playing");
        LiteralArgumentBuilder<FabricClientCommandSource> Search = literal("search");
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
                    Music music = music163.music(LongArgumentType.getLong(context, "id"));
                    resetPlayer(music);
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.music.play", music.name));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })
        )));
    
        // cloudmusic music like id
        CloudMusic.then(Music.then(literal("like").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    Music music = music163.music(LongArgumentType.getLong(context, "id"));
                    music.like();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.music.like", music.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic music unlike id
        CloudMusic.then(Music.then(literal("unlike").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    Music music = music163.music(LongArgumentType.getLong(context, "id"));
                    music.unlike();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.music.unlike", music.name));
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
                    PlayList playList = music163.playlist(LongArgumentType.getLong(context, "id"));
                    resetPlayer(playList.getMusics());
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.playlist.play", playList.name));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic playlist subscribe id
        CloudMusic.then(PlayList.then(literal("subscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    PlayList playList = music163.playlist(LongArgumentType.getLong(context, "id"));
                    playList.subscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.playlist.subscribe", playList.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic playlist unsubscribe id
        CloudMusic.then(PlayList.then(literal("unsubscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    PlayList playList = music163.playlist(LongArgumentType.getLong(context, "id"));
                    playList.unsubscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.playlist.unsubscribe", playList.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic playlist add id musicId
        CloudMusic.then(PlayList.then(literal("add").then(
            argument("id", LongArgumentType.longArg()).then(
                argument("musicId", LongArgumentType.longArg()).executes(contextdata -> {
                    runCommand(contextdata, context -> {
                        music163.playlist(LongArgumentType.getLong(context, "id")).add(LongArgumentType.getLong(context, "musicId"));
                    });
                    return Command.SINGLE_SUCCESS;
                })
            ))
        ));

        // cloudmusic playlist add id musicId
        CloudMusic.then(PlayList.then(literal("del").then(
            argument("id", LongArgumentType.longArg()).then(
                argument("musicId", LongArgumentType.longArg()).executes(contextdata -> {
                    runCommand(contextdata, context -> {
                        music163.playlist(LongArgumentType.getLong(context, "id")).del(LongArgumentType.getLong(context, "musicId"));
                    });
                    return Command.SINGLE_SUCCESS;
                })
            ))
        ));

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
                    Artist artist = music163.artist(LongArgumentType.getLong(context, "id"));
                    resetPlayer(artist.topSong());
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.artist.top.play", artist.name));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic artist album id
        CloudMusic.then(Artist.then(literal("album").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    Artist artist = music163.artist(LongArgumentType.getLong(context, "id"));
                    page = artist.albumPage();
                    page.setInfoText(Text.translatable("cloudmusic.info.page.artist.album", artist.name));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic artist subscribe id
        CloudMusic.then(Artist.then(literal("subscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    Artist artist = music163.artist(LongArgumentType.getLong(context, "id"));
                    artist.subscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.artist.subscribe", artist.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic artist unsubscribe id
        CloudMusic.then(Artist.then(literal("unsubscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    Artist artist = music163.artist(LongArgumentType.getLong(context, "id"));
                    artist.unsubscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.artist.unsubscribe", artist.name));
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
                    Album album = music163.album(LongArgumentType.getLong(context, "id"));
                    resetPlayer(album.getMusics());
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.album.play", album.name));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic album subscribe id
        CloudMusic.then(Album.then(literal("subscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    Album album = music163.album(LongArgumentType.getLong(context, "id"));
                    album.subscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.album.subscribe", album.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic album unsubscribe id
        CloudMusic.then(Album.then(literal("unsubscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    Album album = music163.album(LongArgumentType.getLong(context, "id"));
                    album.unsubscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.album.unsubscribe", album.name));
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
                    User user = music163.user(LongArgumentType.getLong(context, "id"));
                    page = user.playListsPage();
                    page.setInfoText(Text.translatable("cloudmusic.info.page.user.playlist", user.name));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            }))
        ));

        // cloudmusic user like id
        CloudMusic.then(User.then(literal("like").then(
            argument("id", LongArgumentType.longArg()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    User user = music163.user(LongArgumentType.getLong(context, "id"));
                    resetPlayer(user.likeMusicPlayList().getMusics());
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.like", user.name));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            }))
        ));
        
        // cloudmusic my
        CloudMusic.then(My.executes(contextdata -> {
            runCommand(contextdata, context -> {
                getMy(true).printToChatHud(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }));

        // cloudmusic my like
        CloudMusic.then(My.then(literal("like").executes(contextdata -> {
            runCommand(contextdata, context -> {
                resetPlayer(getMy(false).likeMusicPlayList().getMusics());
                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.like", getMy(false).name));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic my fm
        CloudMusic.then(My.then(literal("fm").executes(contextdata -> {
            runCommand(contextdata, context -> {
                resetPlayer(new Fm(getMy(false)));
                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.fm"));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic my playlist
        CloudMusic.then(My.then(literal("playlist").executes(contextdata -> {
            runCommand(contextdata, context -> {
                page = getMy(false).playListsPage();
                page.setInfoText(Text.translatable("cloudmusic.info.page.user.playlist", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        LiteralArgumentBuilder<FabricClientCommandSource> Recommend = literal("recommend");

        // cloudmusic my recommend music
        CloudMusic.then(My.then(Recommend.then(literal("music").executes(contextdata -> {
            runCommand(contextdata, context -> {
                resetPlayer(getMy(false).recommend_songs());
                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.recommend.music"));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my recommend playlist
        CloudMusic.then(My.then(Recommend.then(literal("playlist").executes(contextdata -> {
            runCommand(contextdata, context -> {
                page = getMy(false).recommend_resource();
                page.setInfoText(Text.translatable("cloudmusic.info.page.recommend.playlist", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        LiteralArgumentBuilder<FabricClientCommandSource> Sublist = literal("sublist");

        // cloudmusic my sublist album
        CloudMusic.then(My.then(Sublist.then(literal("album").executes(contextdata -> {
            runCommand(contextdata, context -> {
                page = getMy(false).sublist_album();
                page.setInfoText(Text.translatable("cloudmusic.info.page.sublist.album", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my sublist artist
        CloudMusic.then(My.then(Sublist.then(literal("artist").executes(contextdata -> {
            runCommand(contextdata, context -> {
                page = getMy(false).sublist_artist();
                page.setInfoText(Text.translatable("cloudmusic.info.page.sublist.artist", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic search music
        CloudMusic.then(Search.then(literal("music").then(
            argument("key", StringArgumentType.string()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    String key = StringArgumentType.getString(context, "key");
                    page = music163.searchMusic(key);
                    page.setInfoText(Text.translatable("cloudmusic.info.page.search", key));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic search album
        CloudMusic.then(Search.then(literal("album").then(
            argument("key", StringArgumentType.string()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    String key = StringArgumentType.getString(context, "key");
                    page = music163.searchAlbum(key);
                    page.setInfoText(Text.translatable("cloudmusic.info.page.search", key));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic search artist
        CloudMusic.then(Search.then(literal("artist").then(
            argument("key", StringArgumentType.string()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    String key = StringArgumentType.getString(context, "key");
                    page = music163.searchArtist(key);
                    page.setInfoText(Text.translatable("cloudmusic.info.page.search", key));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic search playlist
        CloudMusic.then(Search.then(literal("playlist").then(
            argument("key", StringArgumentType.string()).executes(contextdata -> {
                runCommand(contextdata, context -> {
                    String key = StringArgumentType.getString(context, "key");
                    page = music163.searchPlayList(key);
                    page.setInfoText(Text.translatable("cloudmusic.info.page.search", key));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

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
        
        // cloudmusic playing
        CloudMusic.then(Playing.executes(context -> {
            player.playing().printToChatHud(context.getSource());
            return Command.SINGLE_SUCCESS;
        }));

        // cloudmusic playing all
        CloudMusic.then(Playing.then(literal("all").executes(context -> {
                page = player.playingAll();
                page.setInfoText(Text.translatable("cloudmusic.info.page.playing.all"));
                page.look(context.getSource());
                return Command.SINGLE_SUCCESS;
            })
        ));

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
                        // cloudmusic to
                        literal("to").then(
                            argument("index",IntegerArgumentType.integer()).executes(context -> {
                                player.to(IntegerArgumentType.getInteger(context, "index"));
                                return Command.SINGLE_SUCCESS;
                            })
                    ))
                    .then(
                        // cloudmusic exit
                        literal("exit").executes(context -> {
                            player.exit();
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

                                source.sendFeedback(Text.translatable("cloudmusic.info.config.complete"));

                                try {
                                    source.sendFeedback(Text.translatable("cloudmusic.info.config.cookie", "§c" + getMy(true).name));
                                } catch (ActionException err) {
                                    source.sendFeedback(Text.literal(err.getMessage()));
                                    source.sendFeedback(Text.translatable("cloudmusic.info.config.cookie", "§cnull"));
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
