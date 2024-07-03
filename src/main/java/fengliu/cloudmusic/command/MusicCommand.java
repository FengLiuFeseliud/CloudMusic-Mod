package fengliu.cloudmusic.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.*;
import fengliu.cloudmusic.music163.data.*;
import fengliu.cloudmusic.util.MusicPlayer;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.Page;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class MusicCommand {
    private static final LoginMusic163 loginMusic163 = new LoginMusic163();
    private static Music163 music163 = new Music163(Configs.LOGIN.COOKIE.getStringValue());
    private static MusicPlayer player = new MusicPlayer(new ArrayList<>());
    private static Page page = null;
    private static Object data = null;
    private static My my = null;
    public static boolean loadQRCode = false;
    private static final Text[] helps = {
            Text.translatable("cloudmusic.help.music"),
            Text.translatable("cloudmusic.help.music.play"),
            Text.translatable("cloudmusic.help.music.like"),
            Text.translatable("cloudmusic.help.music.unlike"),
            Text.translatable("cloudmusic.help.music.similar.music"),
            Text.translatable("cloudmusic.help.music.similar.playlist"),
            Text.translatable("cloudmusic.help.music.comment"),
            Text.translatable("cloudmusic.help.music.hot.comment"),
            Text.translatable("cloudmusic.help.music.send.comment"),

            Text.translatable("cloudmusic.help.playlist"),
            Text.translatable("cloudmusic.help.playlist.play"),
            Text.translatable("cloudmusic.help.playlist.subscribe"),
            Text.translatable("cloudmusic.help.playlist.unsubscribe"),
            Text.translatable("cloudmusic.help.playlist.add"),
            Text.translatable("cloudmusic.help.playlist.del"),
            Text.translatable("cloudmusic.help.playlist.comment"),
            Text.translatable("cloudmusic.help.playlist.hot.comment"),
            Text.translatable("cloudmusic.help.playlist.send.comment"),

            Text.translatable("cloudmusic.help.artist"),
            Text.translatable("cloudmusic.help.artist.top"),
            Text.translatable("cloudmusic.help.artist.album"),
            Text.translatable("cloudmusic.help.artist.similar"),
            Text.translatable("cloudmusic.help.artist.subscribe"),
            Text.translatable("cloudmusic.help.artist.unsubscribe"),

            Text.translatable("cloudmusic.help.album"),
            Text.translatable("cloudmusic.help.album.play"),
            Text.translatable("cloudmusic.help.album.subscribe"),
            Text.translatable("cloudmusic.help.album.unsubscribe"),
            Text.translatable("cloudmusic.help.album.comment"),
            Text.translatable("cloudmusic.help.album.hot.comment"),
            Text.translatable("cloudmusic.help.album.send.comment"),

            Text.translatable("cloudmusic.help.dj"),
            Text.translatable("cloudmusic.help.dj.play"),
            Text.translatable("cloudmusic.help.dj.music"),
            Text.translatable("cloudmusic.help.dj.music.play"),
            Text.translatable("cloudmusic.help.dj.music.send.comment"),
            Text.translatable("cloudmusic.help.dj.music.comment"),
            Text.translatable("cloudmusic.help.dj.music.hot.comment"),
            Text.translatable("cloudmusic.help.dj.subscribe"),
            Text.translatable("cloudmusic.help.dj.unsubscribe"),
            Text.translatable("cloudmusic.help.dj.send.comment"),
            Text.translatable("cloudmusic.help.dj.comment"),
            Text.translatable("cloudmusic.help.dj.hot.comment"),

            Text.translatable("cloudmusic.help.user"),
            Text.translatable("cloudmusic.help.user.playlist"),
            Text.translatable("cloudmusic.help.user.dj"),
            Text.translatable("cloudmusic.help.user.like"),
            Text.translatable("cloudmusic.help.user.record.all"),
            Text.translatable("cloudmusic.help.user.record.week"),

            Text.translatable("cloudmusic.help.my"),
            Text.translatable("cloudmusic.help.my.fm"),
            Text.translatable("cloudmusic.help.my.intelligence"),
            Text.translatable("cloudmusic.help.my.like"),
            Text.translatable("cloudmusic.help.my.playlist"),
            Text.translatable("cloudmusic.help.my.dj"),
            Text.translatable("cloudmusic.help.my.style"),
            Text.translatable("cloudmusic.help.my.playlist.add"),
            Text.translatable("cloudmusic.help.my.playlist.del"),
            Text.translatable("cloudmusic.help.my.recommend.music"),
            Text.translatable("cloudmusic.help.my.recommend.playlist"),
            Text.translatable("cloudmusic.help.my.sublist.album"),
            Text.translatable("cloudmusic.help.my.sublist.artist"),
            Text.translatable("cloudmusic.help.my.sublist.dj"),
            Text.translatable("cloudmusic.help.my.record.music"),
            Text.translatable("cloudmusic.help.my.record.djmusic"),
            Text.translatable("cloudmusic.help.my.record.playlist"),
            Text.translatable("cloudmusic.help.my.record.album"),
            Text.translatable("cloudmusic.help.my.record.dj"),

            Text.translatable("cloudmusic.help.style"),
            Text.translatable("cloudmusic.help.style.all"),
            Text.translatable("cloudmusic.help.style.children"),
            Text.translatable("cloudmusic.help.style.music"),
            Text.translatable("cloudmusic.help.style.playlist"),
            Text.translatable("cloudmusic.help.style.artist"),
            Text.translatable("cloudmusic.help.style.album"),

            Text.translatable("cloudmusic.help.top.list"),
            Text.translatable("cloudmusic.help.top.artist"),
            Text.translatable("cloudmusic.help.top.playlist.highquality.tags"),
            Text.translatable("cloudmusic.help.top.playlist.highquality"),
            Text.translatable("cloudmusic.help.top.playlist.tags"),
            Text.translatable("cloudmusic.help.top.playlist.tags.hot"),
            Text.translatable("cloudmusic.help.top.playlist"),

            Text.translatable("cloudmusic.help.search.music"),
            Text.translatable("cloudmusic.help.search.album"),
            Text.translatable("cloudmusic.help.search.artist"),
            Text.translatable("cloudmusic.help.search.playlist"),
            Text.translatable("cloudmusic.help.search.dj"),

            Text.translatable("cloudmusic.help.login.email"),
            Text.translatable("cloudmusic.help.login.captcha"),
            Text.translatable("cloudmusic.help.login.captcha.login"),
            Text.translatable("cloudmusic.help.login.captcha.phone"),
            Text.translatable("cloudmusic.help.login.qr"),

            Text.translatable("cloudmusic.help.volume"),
            Text.translatable("cloudmusic.help.volume.volume"),

            Text.translatable("cloudmusic.help.page.prev"),
            Text.translatable("cloudmusic.help.page.next"),
            Text.translatable("cloudmusic.help.page.to"),

            Text.translatable("cloudmusic.help.playing"),
            Text.translatable("cloudmusic.help.playing.all"),

            Text.translatable("cloudmusic.help.stop"),
            Text.translatable("cloudmusic.help.continue"),
            Text.translatable("cloudmusic.help.prev"),
            Text.translatable("cloudmusic.help.next"),
            Text.translatable("cloudmusic.help.to"),
            Text.translatable("cloudmusic.help.del"),
            Text.translatable("cloudmusic.help.trash"),
            Text.translatable("cloudmusic.help.random"),
            Text.translatable("cloudmusic.help.exit"),
            Text.translatable("cloudmusic.help.cloudmusic"),
    };
    private static final List<Text> helpsList = new ArrayList<>();

    public static MusicPlayer getPlayer(){
        return player;
    }

    public static void setPage(Page Page){
        page = Page;
    }

    public static My getMy(boolean reset){
        if(my == null || reset){
            my = music163.my();
        }

        return my;
    }
    
    /**
     * 重置歌曲播放器
     * @param musics 歌曲列表
     */
    private static void resetPlayer(List<IMusic> musics){
        try {
            player.exit();
        } catch (Exception e) {
            
        }
        player = new MusicPlayer(musics);
    }

    /**
     * 重置歌曲播放器
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
     * 重置歌曲播放器
     * @param music 歌曲
     */
    private static void resetPlayer(IMusic music){
        List<IMusic> musics = new ArrayList<>();
        musics.add(music);

        resetPlayer(musics);
    }

    private static void resetCookie(String cookie){
        if(cookie == null || music163.getHttpClient().getCookies().equals(cookie)){
            return;
        }

        Configs.LOGIN.COOKIE.setValueFromString(cookie);
        music163 = new Music163(cookie);
        getMy(true);

        Configs.INSTANCE.save();
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
        LiteralArgumentBuilder<FabricClientCommandSource> Dj = literal("dj");
        LiteralArgumentBuilder<FabricClientCommandSource> User = literal("user");
        LiteralArgumentBuilder<FabricClientCommandSource> My = literal("my");
        LiteralArgumentBuilder<FabricClientCommandSource> Style = literal("style");
        LiteralArgumentBuilder<FabricClientCommandSource> Top = literal("top");
        LiteralArgumentBuilder<FabricClientCommandSource> Playing = literal("playing");
        LiteralArgumentBuilder<FabricClientCommandSource> Search = literal("search");
        LiteralArgumentBuilder<FabricClientCommandSource> Volume = literal("volume");
        LiteralArgumentBuilder<FabricClientCommandSource> Page = literal("page");
        LiteralArgumentBuilder<FabricClientCommandSource> Login = literal("login");

        Collections.addAll(helpsList, helps);
        CloudMusic.executes(context -> {
            page = new Page(helpsList) {
                @Override
                protected TextClickItem putPageItem(Object data) {
                    return new TextClickItem((MutableText) data, "");
                }
            };
            page.setInfoText(Text.translatable("cloudmusic.info.page.help"));
            page.look(context.getSource());
            return Command.SINGLE_SUCCESS;
        });

        // cloudmusic music id
        CloudMusic.then(Music.then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    data = music163.music(LongArgumentType.getLong(context, "id"));
                    ((Music) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));
        
        // cloudmusic music play id
        CloudMusic.then(Music.then(literal("play").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
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
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    Music music = music163.music(LongArgumentType.getLong(context, "id"));
                    music.like();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.music.like", music.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic music unlike id
        CloudMusic.then(Music.then(literal("unlike").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    Music music = music163.music(LongArgumentType.getLong(context, "id"));
                    music.unlike();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.music.unlike", music.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        LiteralArgumentBuilder<FabricClientCommandSource> Similar = literal("similar");

        // cloudmusic music similar music
        CloudMusic.then(Music.then(Similar.then(literal("music").then(
             argument("id", LongArgumentType.longArg()).executes(contextData -> {
                 runCommand(contextData, context -> {
                     Music music = music163.music(LongArgumentType.getLong(context, "id"));
                     page = music.similar();
                     page.setInfoText(Text.translatable("cloudmusic.info.page.music.similar", music.name));
                     page.look(context.getSource());
                 });
                 return Command.SINGLE_SUCCESS;
             })
        ))));

        // cloudmusic music similar playlist
        CloudMusic.then(Music.then(Similar.then(literal("playlist").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        Music music = music163.music(LongArgumentType.getLong(context, "id"));
                        page = music.similarPlaylist();
                        page.setInfoText(Text.translatable("cloudmusic.info.page.music.similar.playlist", music.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        ))));


        // cloudmusic music comment id
        CloudMusic.then(Music.then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        Music music = music163.music(LongArgumentType.getLong(contextData, "id"));
                        page = music.comments(false);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.music.comments", music.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic music hotComment id
        CloudMusic.then(Music.then(literal("hotComment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        Music music = music163.music(LongArgumentType.getLong(contextData, "id"));
                        page = music.comments(true);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.music.hot.comments", music.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic music send comment id content
        CloudMusic.then(Music.then(literal("send").then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).then(
                        argument("content", StringArgumentType.string()).executes(contextData -> {
                            runCommand(contextData, context -> {
                                Music music = music163.music(LongArgumentType.getLong(context, "id"));
                                music.send(StringArgumentType.getString(context, "content"));
                                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.send.comment", music.name));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                )))));

        // cloudmusic playlist id
        CloudMusic.then(PlayList.then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        data = music163.playlist(LongArgumentType.getLong(context, "id"));
                        ((PlayList) data).printToChatHud(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        ));
        
        // cloudmusic playlist play id
        CloudMusic.then(PlayList.then(literal("play").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        PlayList playList = music163.playlist(LongArgumentType.getLong(context, "id"));
                        resetPlayer(playList.getMusics());
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.playlist.play", playList.name));
                        player.start();
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic playlist send comment id content
        CloudMusic.then(PlayList.then(literal("send").then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).then(
                        argument("content", StringArgumentType.string()).executes(contextData -> {
                            runCommand(contextData, context -> {
                                PlayList playlist = music163.playlist(LongArgumentType.getLong(context, "id"));
                                playlist.send(StringArgumentType.getString(context, "content"));
                                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.send.comment", playlist.name));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                )))));


        // cloudmusic playlist comment id
        CloudMusic.then(PlayList.then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        PlayList playList = music163.playlist(LongArgumentType.getLong(contextData, "id"));
                        page = playList.comments(false);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.playlist.comments", playList.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic playlist hotComment id
        CloudMusic.then(PlayList.then(literal("hotComment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        PlayList playList = music163.playlist(LongArgumentType.getLong(contextData, "id"));
                        page = playList.comments(true);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.playlist.hot.comments", playList.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic playlist subscribe id
        CloudMusic.then(PlayList.then(literal("subscribe").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        PlayList playList = music163.playlist(LongArgumentType.getLong(context, "id"));
                        playList.subscribe();
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.playlist.subscribe", playList.name));
                    });
                    return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic playlist unsubscribe id
        CloudMusic.then(PlayList.then(literal("unsubscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
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
                argument("musicId", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        long musicId = LongArgumentType.getLong(context, "musicId");
                        PlayList playList = music163.playlist(LongArgumentType.getLong(context, "id"));
                        playList.add(musicId);
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.playlist.add", playList.name, musicId));
                    });
                    return Command.SINGLE_SUCCESS;
                })
            ))
        ));

        // cloudmusic playlist del id musicId
        CloudMusic.then(PlayList.then(literal("del").then(
            argument("id", LongArgumentType.longArg()).then(
                argument("musicId", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        long musicId = LongArgumentType.getLong(context, "musicId");
                        PlayList playList = music163.playlist(LongArgumentType.getLong(context, "id"));
                        playList.del(musicId);
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.playlist.del", playList.name, musicId));
                    });
                    return Command.SINGLE_SUCCESS;
                })
            ))
        ));

        // cloudmusic artist id
        CloudMusic.then(Artist.then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    data = music163.artist(LongArgumentType.getLong(context, "id"));
                    ((Artist) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));

        // cloudmusic artist top id
        CloudMusic.then(Artist.then(literal("top").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
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
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    Artist artist = music163.artist(LongArgumentType.getLong(context, "id"));
                    page = artist.albumPage();
                    page.setInfoText(Text.translatable("cloudmusic.info.page.artist.album", artist.name));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic artist similar id
        CloudMusic.then(Artist.then(literal("similar").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        Artist artist = music163.artist(LongArgumentType.getLong(context, "id"));
                        page = artist.similar();
                        page.setInfoText(Text.translatable("cloudmusic.info.page.artist.similar", artist.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic artist subscribe id
        CloudMusic.then(Artist.then(literal("subscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    Artist artist = music163.artist(LongArgumentType.getLong(context, "id"));
                    artist.subscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.artist.subscribe", artist.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic artist unsubscribe id
        CloudMusic.then(Artist.then(literal("unsubscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    Artist artist = music163.artist(LongArgumentType.getLong(context, "id"));
                    artist.unsubscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.artist.unsubscribe", artist.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic artist music id
        // CloudMusic.then(Artist.then(literal("music").then(
        //     argument("id", LongArgumentType.longArg()).executes(contextData -> {
        //         runCommand(contextData, context -> {
        //             resetPlayer((new MusicPlayList(music163.artist(LongArgumentType.getLong(context, "id")).music())).createMusicPlayer(false));
        //             player.start();
        //         });
        //         return Command.SINGLE_SUCCESS;
        //     })
        // )));

        // cloudmusic album id
        CloudMusic.then(Album.then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    data = music163.album(LongArgumentType.getLong(context, "id"));
                    ((Album) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));
        
        // cloudmusic album play id
        CloudMusic.then(Album.then(literal("play").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        Album album = music163.album(LongArgumentType.getLong(context, "id"));
                        resetPlayer(album.getMusics());
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.album.play", album.name));
                        player.start();
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic album send comment id content
        CloudMusic.then(Album.then(literal("send").then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).then(
                        argument("content", StringArgumentType.string()).executes(contextData -> {
                            runCommand(contextData, context -> {
                                Album album = music163.album(LongArgumentType.getLong(context, "id"));
                                album.send(StringArgumentType.getString(context, "content"));
                                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.send.comment", album.name));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                )))));


        // cloudmusic album comment id
        CloudMusic.then(Album.then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        Album album = music163.album(LongArgumentType.getLong(context, "id"));
                        page = album.comments(false);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.album.comments", album.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic album hotComment id
        CloudMusic.then(Album.then(literal("hotComment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        Album album = music163.album(LongArgumentType.getLong(context, "id"));
                        page = album.comments(true);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.album.hot.comments", album.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic album subscribe id
        CloudMusic.then(Album.then(literal("subscribe").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        Album album = music163.album(LongArgumentType.getLong(context, "id"));
                        album.subscribe();
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.album.subscribe", album.name));
                    });
                    return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic album unsubscribe id
        CloudMusic.then(Album.then(literal("unsubscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    Album album = music163.album(LongArgumentType.getLong(context, "id"));
                    album.unsubscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.album.unsubscribe", album.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic dj id
        CloudMusic.then(Dj.then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    data = music163.djRadio(LongArgumentType.getLong(context, "id"));
                    ((DjRadio) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));

        // cloudmusic dj play id
        CloudMusic.then(Dj.then(literal("play").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        DjRadio djRadio = music163.djRadio(LongArgumentType.getLong(context, "id"));
                        resetPlayer(djRadio);
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.dj.play", djRadio.name));
                        player.start();
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic dj send comment id content
        CloudMusic.then(Dj.then(literal("send").then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).then(
                        argument("content", StringArgumentType.string()).executes(contextData -> {
                            runCommand(contextData, context -> {
                                DjRadio djRadio = music163.djRadio(LongArgumentType.getLong(context, "id"));
                                djRadio.send(StringArgumentType.getString(context, "content"));
                                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.send.comment", djRadio.name));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                )))));

        // cloudmusic dj comment id
        CloudMusic.then(Dj.then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        DjRadio djRadio = music163.djRadio(LongArgumentType.getLong(context, "id"));
                        page = djRadio.comments(false);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.dj.radio.comments", djRadio.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));

        // cloudmusic dj hotComment id
        CloudMusic.then(Dj.then(literal("hotComment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        DjRadio djRadio = music163.djRadio(LongArgumentType.getLong(context, "id"));
                        page = djRadio.comments(true);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.dj.radio.hot.comments", djRadio.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        )));


        LiteralArgumentBuilder<FabricClientCommandSource> DjMusic = literal("music");

        // cloudmusic dj music id
        CloudMusic.then(Dj.then(DjMusic.then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        data = music163.djMusic(LongArgumentType.getLong(context, "id"));
                        ((DjMusic) data).printToChatHud(context.getSource());
                    });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic dj music play id
        CloudMusic.then(Dj.then(DjMusic.then(literal("play").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        DjMusic music = music163.djMusic(LongArgumentType.getLong(context, "id"));
                        resetPlayer(music);
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.dj.music.play", music.name));
                        player.start();
                    });
                    return Command.SINGLE_SUCCESS;
                })
        ))));

        // cloudmusic dj music send comment id content
        CloudMusic.then(Dj.then(DjMusic.then(literal("send").then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).then(
                        argument("content", StringArgumentType.string()).executes(contextData -> {
                            runCommand(contextData, context -> {
                                DjMusic music = music163.djMusic(LongArgumentType.getLong(context, "id"));
                                music.send(StringArgumentType.getString(context, "content"));

                                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.send.comment", music.name));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                ))))));

        // cloudmusic dj music comment id
        CloudMusic.then(Dj.then(DjMusic.then(literal("comment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        DjMusic music = music163.djMusic(LongArgumentType.getLong(context, "id"));
                        page = music.comments(false);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.dj.music.comments", music.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        ))));

        // cloudmusic dj music hotComment id
        CloudMusic.then(Dj.then(DjMusic.then(literal("hotComment").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        DjMusic music = music163.djMusic(LongArgumentType.getLong(context, "id"));
                        page = music.comments(true);
                        page.setInfoText(Text.translatable("cloudmusic.info.page.dj.music.hot.comments", music.name));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        ))));

        // cloudmusic dj subscribe id
        CloudMusic.then(Dj.then(literal("subscribe").then(
                argument("id", LongArgumentType.longArg()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        DjRadio djRadio = music163.djRadio(LongArgumentType.getLong(context, "id"));
                        djRadio.subscribe();
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.dj.subscribe", djRadio.name));
                    });
                    return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic dj unsubscribe id
        CloudMusic.then(Dj.then(literal("unsubscribe").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    DjRadio djRadio = music163.djRadio(LongArgumentType.getLong(context, "id"));
                    djRadio.unsubscribe();
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.dj.unsubscribe", djRadio.name));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic user id
        CloudMusic.then(User.then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    data = music163.user(LongArgumentType.getLong(context, "id"));
                    ((User) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));

        // cloudmusic user playlist id
        CloudMusic.then(User.then(literal("playlist").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    User user = music163.user(LongArgumentType.getLong(context, "id"));
                    page = user.playListsPage();
                    page.setInfoText(Text.translatable("cloudmusic.info.page.user.playlist", user.name));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            }))
        ));

        // cloudmusic user dj id
        CloudMusic.then(User.then(literal("dj").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    User user = music163.user(LongArgumentType.getLong(context, "id"));
                    page = user.djRadio();
                    page.setInfoText(Text.translatable("cloudmusic.info.page.user.dj", user.name));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            }))
        ));

        // cloudmusic user like id
        CloudMusic.then(User.then(literal("like").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    User user = music163.user(LongArgumentType.getLong(context, "id"));
                    resetPlayer(user.likeMusicPlayList().getMusics());
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.like", user.name));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            }))
        ));

        LiteralArgumentBuilder<FabricClientCommandSource> Record = literal("record");

        // cloudmusic user record all id
        CloudMusic.then(User.then(Record.then(literal("all").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    User user = music163.user(LongArgumentType.getLong(context, "id"));
                    resetPlayer(user.recordAll());
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.user.record.all", user.name));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })))
        ));

        // cloudmusic user record week id
        CloudMusic.then(User.then(Record.then(literal("week").then(
            argument("id", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    User user = music163.user(LongArgumentType.getLong(context, "id"));
                    resetPlayer(user.recordWeek());
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.user.record.week", user.name));
                    player.start();
                });
                return Command.SINGLE_SUCCESS;
            })))
        ));
        
        // cloudmusic my
        CloudMusic.then(My.executes(contextData -> {
            runCommand(contextData, context -> {
                getMy(true).printToChatHud(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }));

        // cloudmusic my like
        CloudMusic.then(My.then(literal("like").executes(contextData -> {
            runCommand(contextData, context -> {
                resetPlayer(getMy(false).likeMusicPlayList().getMusics());
                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.like", getMy(false).name));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic my fm
        CloudMusic.then(My.then(literal("fm").executes(contextData -> {
            runCommand(contextData, context -> {
                resetPlayer(new Fm(getMy(false)));
                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.fm"));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic my intelligence
        CloudMusic.then(My.then(literal("intelligence").executes(contextData -> {
            runCommand(contextData, context -> {
                resetPlayer(getMy(false).intelligencePlayMode());
                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.intelligence"));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        })));

        LiteralArgumentBuilder<FabricClientCommandSource> MyPlayList = literal("playlist");

        // cloudmusic my playlist
        CloudMusic.then(My.then(MyPlayList.executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).playListsPage();
                page.setInfoText(Text.translatable("cloudmusic.info.page.user.playlist", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic my dj
        CloudMusic.then(My.then(literal("dj").executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).djRadio();
                page.setInfoText(Text.translatable("cloudmusic.info.page.user.dj", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic my style
        CloudMusic.then(My.then(literal("style").executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).preferenceStyles();
                page.setInfoText(Text.translatable("cloudmusic.info.page.preference.style", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        LiteralArgumentBuilder<FabricClientCommandSource> PlayRecord = literal("record");

        // cloudmusic my record music
        CloudMusic.then(My.then(PlayRecord.then(literal("music").executes(contextData -> {
            runCommand(contextData, context -> {
                resetPlayer(getMy(false).recordPlayMusic());
                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.record.music", getMy(false).name));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my record djmusic
        CloudMusic.then(My.then(PlayRecord.then(literal("djmusic").executes(contextData -> {
            runCommand(contextData, context -> {
                resetPlayer(getMy(false).recordPlayDjMusic());
                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.record.djmusic", getMy(false).name));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my record playlist
        CloudMusic.then(My.then(PlayRecord.then(literal("playlist").executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).recordPlayPlayList();
                page.setInfoText(Text.translatable("cloudmusic.info.page.record.playlist", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my record album
        CloudMusic.then(My.then(PlayRecord.then(literal("album").executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).recordPlayAlbum();
                page.setInfoText(Text.translatable("cloudmusic.info.page.record.album", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my record dj
        CloudMusic.then(My.then(PlayRecord.then(literal("dj").executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).recordPlayDj();
                page.setInfoText(Text.translatable("cloudmusic.info.page.record.dj", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my playlist add musicId
        CloudMusic.then(My.then(MyPlayList.then(literal("add").then(
            argument("musicId", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    page = getMy(false).playListSetMusic(LongArgumentType.getLong(context, "musicId"), "add");
                    page.setInfoText(Text.translatable("cloudmusic.info.page.user.playlist.add", getMy(false).name));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ))));

        // cloudmusic my playlist del musicId
        CloudMusic.then(My.then(MyPlayList.then(literal("del").then(
            argument("musicId", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    page = getMy(false).playListSetMusic(LongArgumentType.getLong(context, "musicId"), "del");
                    page.setInfoText(Text.translatable("cloudmusic.info.page.user.playlist.del", getMy(false).name));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ))));

        LiteralArgumentBuilder<FabricClientCommandSource> Recommend = literal("recommend");

        // cloudmusic my recommend music
        CloudMusic.then(My.then(Recommend.then(literal("music").executes(contextData -> {
            runCommand(contextData, context -> {
                resetPlayer(getMy(false).recommendSongs());
                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.recommend.music"));
                player.start();
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my recommend playlist
        CloudMusic.then(My.then(Recommend.then(literal("playlist").executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).recommendResource();
                page.setInfoText(Text.translatable("cloudmusic.info.page.recommend.playlist", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        LiteralArgumentBuilder<FabricClientCommandSource> Sublist = literal("sublist");

        // cloudmusic my sublist album
        CloudMusic.then(My.then(Sublist.then(literal("album").executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).sublistAlbum();
                page.setInfoText(Text.translatable("cloudmusic.info.page.sublist.album", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my sublist artist
        CloudMusic.then(My.then(Sublist.then(literal("artist").executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).sublistArtist();
                page.setInfoText(Text.translatable("cloudmusic.info.page.sublist.artist", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic my sublist dj
        CloudMusic.then(My.then(Sublist.then(literal("dj").executes(contextData -> {
            runCommand(contextData, context -> {
                page = getMy(false).sublistDjRadio();
                page.setInfoText(Text.translatable("cloudmusic.info.page.sublist.dj", getMy(false).name));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic style id
        CloudMusic.then(Style.then(
            argument("id", IntegerArgumentType.integer()).executes(contextData -> {
                runCommand(contextData, context -> {
                    data = music163.style(IntegerArgumentType.getInteger(context, "id"));
                    ((StyleTag) data).printToChatHud(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ));

        // cloudmusic style all
        CloudMusic.then(Style.then(literal("all").executes(contextData -> {
            runCommand(contextData, context -> {
                page = music163.styleList();
                page.setInfoText(Text.translatable("cloudmusic.info.page.style"));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic style children id
        CloudMusic.then(Style.then(literal("children").then(
            argument("id", IntegerArgumentType.integer()).executes(contextData -> {
                runCommand(contextData, context -> {
                    StyleTag style = music163.style(IntegerArgumentType.getInteger(context, "id"));
                    page = style.childrenStyles();
                    if (page == null) {
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.style.not.children", style.name, style.enName));
                        return;
                    }
                    page.setInfoText(Text.translatable("cloudmusic.info.page.style.children", style.name, style.enName));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic style music id
        CloudMusic.then(Style.then(literal("music").then(
            argument("id", IntegerArgumentType.integer()).executes(contextData -> {
                runCommand(contextData, context -> {
                    StyleTag style = music163.style(IntegerArgumentType.getInteger(context, "id"));
                    page = style.music();
                    page.setInfoText(Text.translatable("cloudmusic.info.page.style.music", style.name, style.enName));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic style playlist id
        CloudMusic.then(Style.then(literal("playlist").then(
            argument("id", IntegerArgumentType.integer()).executes(contextData -> {
                runCommand(contextData, context -> {
                    StyleTag style = music163.style(IntegerArgumentType.getInteger(context, "id"));
                    page = style.playlist();
                    page.setInfoText(Text.translatable("cloudmusic.info.page.style.playlist", style.name, style.enName));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic style artist id
        CloudMusic.then(Style.then(literal("artist").then(
            argument("id", IntegerArgumentType.integer()).executes(contextData -> {
                runCommand(contextData, context -> {
                    StyleTag style = music163.style(IntegerArgumentType.getInteger(context, "id"));
                    page = style.artist();
                    page.setInfoText(Text.translatable("cloudmusic.info.page.style.artist", style.name, style.enName));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic style album id
        CloudMusic.then(Style.then(literal("album").then(
            argument("id", IntegerArgumentType.integer()).executes(contextData -> {
                runCommand(contextData, context -> {
                    StyleTag style = music163.style(IntegerArgumentType.getInteger(context, "id"));
                    page = style.album();
                    page.setInfoText(Text.translatable("cloudmusic.info.page.style.album", style.name, style.enName));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic top list
        CloudMusic.then(Top.then(literal("list").executes(contextData -> {
            runCommand(contextData, context -> {
                page = music163.topList();
                page.setInfoText(Text.translatable("cloudmusic.info.page.top.list"));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic top artist
        CloudMusic.then(Top.then(literal("artist").executes(contextData -> {
            runCommand(contextData, context -> {
                page = music163.topArtistList();
                page.setInfoText(Text.translatable("cloudmusic.info.page.top.artist"));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        LiteralArgumentBuilder<FabricClientCommandSource> TopPlayList = literal("playlist");
        LiteralArgumentBuilder<FabricClientCommandSource> HighQuality = literal("highquality");

        // cloudmusic top playlist highquality tags
        CloudMusic.then(Top.then(TopPlayList.then(HighQuality.then(literal("tags").executes(contextData -> {
            runCommand(contextData, context -> {
                page = music163.playListHighQualityTags();
                page.setInfoText(Text.translatable("cloudmusic.info.page.top.playlist.highquality.tags"));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })))));

        // cloudmusic top playlist highquality
        CloudMusic.then(Top.then(TopPlayList.then(HighQuality.executes(contextData -> {
            runCommand(contextData, context -> {
                page = music163.topPlayListHighQuality("全部");
                page.setInfoText(Text.translatable("cloudmusic.info.page.top.playlist.highquality", "全部"));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic top playlist highquality tag
        CloudMusic.then(Top.then(TopPlayList.then(HighQuality.then(
            argument("tag", StringArgumentType.string()).executes(contextData -> {
                runCommand(contextData, context -> {
                    String tag = StringArgumentType.getString(context, "tag");
                    page = music163.topPlayListHighQuality(tag);
                    if (page == null) {
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.tag.not.top.playlist.highquality", tag));
                        return;
                    }
                    page.setInfoText(Text.translatable("cloudmusic.info.page.top.playlist.highquality", tag));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        ))));

        LiteralArgumentBuilder<FabricClientCommandSource> Tags = literal("tags");

        // cloudmusic top playlist tags
        CloudMusic.then(Top.then(TopPlayList.then(Tags.executes(contextData -> {
            runCommand(contextData, context -> {
                page = music163.playListTags();
                page.setInfoText(Text.translatable("cloudmusic.info.page.top.playlist.tags"));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        }))));

        // cloudmusic top playlist tags hot
        CloudMusic.then(Top.then(TopPlayList.then(Tags.then(literal("hot").executes(contextData -> {
            runCommand(contextData, context -> {
                page = music163.playListTagsHot();
                page.setInfoText(Text.translatable("cloudmusic.info.page.top.playlist.hot.tags"));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })))));

        // cloudmusic top playlist
        CloudMusic.then(Top.then(TopPlayList.executes(contextData -> {
            runCommand(contextData, context -> {
                page = music163.topPlayList("全部");
                page.setInfoText(Text.translatable("cloudmusic.info.page.top.playlist", "全部"));
                page.look(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic top playlist tag
        CloudMusic.then(Top.then(TopPlayList.then(
            argument("tag", StringArgumentType.string()).executes(contextData -> {
                runCommand(contextData, context -> {
                    String tag = StringArgumentType.getString(context, "tag");
                    page = music163.topPlayList(tag);
                    page.setInfoText(Text.translatable("cloudmusic.info.page.top.playlist", tag));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic search music
        CloudMusic.then(Search.then(literal("music").then(
            argument("key", StringArgumentType.string()).executes(contextData -> {
                runCommand(contextData, context -> {
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
            argument("key", StringArgumentType.string()).executes(contextData -> {
                runCommand(contextData, context -> {
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
            argument("key", StringArgumentType.string()).executes(contextData -> {
                runCommand(contextData, context -> {
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
            argument("key", StringArgumentType.string()).executes(contextData -> {
                runCommand(contextData, context -> {
                    String key = StringArgumentType.getString(context, "key");
                    page = music163.searchPlayList(key);
                    page.setInfoText(Text.translatable("cloudmusic.info.page.search", key));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic search dj
        CloudMusic.then(Search.then(literal("dj").then(
            argument("key", StringArgumentType.string()).executes(contextData -> {
                runCommand(contextData, context -> {
                    String key = StringArgumentType.getString(context, "key");
                    page = music163.searchDjRadio(key);
                    page.setInfoText(Text.translatable("cloudmusic.info.page.search", key));
                    page.look(context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));


        LiteralArgumentBuilder<FabricClientCommandSource> Comment = literal("comment");

        // cloudmusic comment id threadId
        CloudMusic.then(Comment.then(
                argument("id", LongArgumentType.longArg()).then(
                        argument("threadId", StringArgumentType.string()).executes(context -> {
                            long id = LongArgumentType.getLong(context, "id");
                            JsonObject json = page.getJsonItem(jsonObject -> jsonObject.get("commentId").getAsLong() == id);
                            if (json == null) {
                                return Command.SINGLE_SUCCESS;
                            }
                            Comment comment = new Comment(music163.getHttpClient(), json, StringArgumentType.getString(context, "threadId"));
                            comment.printToChatHud(context.getSource());
                            return Command.SINGLE_SUCCESS;
                        })
                ))
        );

        // cloudmusic comment floors id threadId
        CloudMusic.then(Comment.then(literal("floors").then(argument("id", LongArgumentType.longArg()).then(
                argument("threadId", StringArgumentType.string()).executes(contextData -> {
                    long id = LongArgumentType.getLong(contextData, "id");
                    JsonObject json = page.getJsonItem(jsonObject -> jsonObject.get("commentId").getAsLong() == id);
                    if (json == null) {
                        return Command.SINGLE_SUCCESS;
                    }
                    Comment comment = new Comment(music163.getHttpClient(), json, StringArgumentType.getString(contextData, "threadId"));
                    runCommand(contextData, context -> {
                        page = comment.floors();
                        page.setInfoText(Text.translatable("cloudmusic.info.page.comment.floors", comment.id));
                        page.look(context.getSource());
                    });
                    return Command.SINGLE_SUCCESS;
                })
        ))));

        // cloudmusic comment like id threadId
        CloudMusic.then(Comment.then(literal("like").then(argument("id", LongArgumentType.longArg()).then(
                argument("threadId", StringArgumentType.string()).executes(contextData -> {
                    long id = LongArgumentType.getLong(contextData, "id");
                    JsonObject json = page.getJsonItem(jsonObject -> jsonObject.get("commentId").getAsLong() == id);
                    if (json == null) {
                        return Command.SINGLE_SUCCESS;
                    }

                    runCommand(contextData, context -> {
                        Comment comment = new Comment(music163.getHttpClient(), json, StringArgumentType.getString(context, "threadId"));
                        comment.like();

                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.comment.like", comment.content));
                    });
                    return Command.SINGLE_SUCCESS;
                })
        ))));

        // cloudmusic comment unlike id threadId
        CloudMusic.then(Comment.then(literal("unlike").then(argument("id", LongArgumentType.longArg()).then(
                argument("threadId", StringArgumentType.string()).executes(contextData -> {
                    long id = LongArgumentType.getLong(contextData, "id");
                    JsonObject json = page.getJsonItem(jsonObject -> jsonObject.get("commentId").getAsLong() == id);
                    if (json == null) {
                        return Command.SINGLE_SUCCESS;
                    }

                    runCommand(contextData, context -> {
                        Comment comment = new Comment(music163.getHttpClient(), json, StringArgumentType.getString(context, "threadId"));
                        comment.unlike();

                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.comment.unlike", comment.content));
                    });
                    return Command.SINGLE_SUCCESS;
                })
        ))));

        // cloudmusic comment delete id threadId
        CloudMusic.then(Comment.then(literal("delete").then(argument("id", LongArgumentType.longArg()).then(
                argument("threadId", StringArgumentType.string()).executes(contextData -> {
                    long id = LongArgumentType.getLong(contextData, "id");
                    JsonObject json = page.getJsonItem(jsonObject -> jsonObject.get("commentId").getAsLong() == id);
                    if (json == null) {
                        return Command.SINGLE_SUCCESS;
                    }

                    runCommand(contextData, context -> {
                        Comment comment = new Comment(music163.getHttpClient(), json, StringArgumentType.getString(context, "threadId"));
                        comment.delete();

                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.comment.delete", comment.content));
                    });
                    return Command.SINGLE_SUCCESS;
                })
        ))));

        // cloudmusic comment reply id threadId content
        CloudMusic.then(Comment.then(literal("reply").then(argument("id", LongArgumentType.longArg()).then(
                argument("threadId", StringArgumentType.string()).then(
                        argument("content", StringArgumentType.string()).executes(contextData -> {
                            long id = LongArgumentType.getLong(contextData, "id");
                            JsonObject json = page.getJsonItem(jsonObject -> jsonObject.get("commentId").getAsLong() == id);
                            if (json == null) {
                                return Command.SINGLE_SUCCESS;
                            }

                            runCommand(contextData, context -> {
                                Comment comment = new Comment(music163.getHttpClient(), json, StringArgumentType.getString(context, "threadId"));
                                comment.reply(StringArgumentType.getString(context, "content"));

                                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.comment.reply", comment.content));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                )))));

        // cloudmusic volume
        CloudMusic.then(Volume.executes(context -> {
            context.getSource().sendFeedback(Text.translatable("cloudmusic.info.volume", Configs.PLAY.VOLUME.getIntegerValue()));
            return Command.SINGLE_SUCCESS;
        }));

        // cloudmusic volume volume
        CloudMusic.then(Volume.then(
                argument("volume", IntegerArgumentType.integer(0, 100)).executes(contextData -> {
                    runCommand(contextData, context -> {
                        player.volumeSet(IntegerArgumentType.getInteger(context, "volume"));
                    });
                    return Command.SINGLE_SUCCESS;
                }))
        );
        
        // cloudmusic page prev
        CloudMusic.then(Page.then(literal("prev").executes(context -> {
            if(page == null){
                return Command.SINGLE_SUCCESS;
            }
            page.prev(context.getSource());
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic page next
        CloudMusic.then(Page.then(literal("next").executes(contextData -> {
            if(page == null){
                return Command.SINGLE_SUCCESS;
            }

            runCommand(contextData, context -> {
                page.next(context.getSource());
            });
            return Command.SINGLE_SUCCESS;
        })));

        // cloudmusic page to page
        CloudMusic.then(Page.then(literal("to").then(
            argument("page", IntegerArgumentType.integer()).executes(contextData -> {
                if(page == null){
                    return Command.SINGLE_SUCCESS;
                }

                runCommand(contextData, context -> {
                    page.to(IntegerArgumentType.getInteger(context, "page") - 1, context.getSource());
                });
                return Command.SINGLE_SUCCESS;
            })
        )));
        
        // cloudmusic playing
        CloudMusic.then(Playing.executes(context -> {
            player.getPlayingMusic().printToChatHud(context.getSource());
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
        
        // cloudmusic login email email password
        CloudMusic.then(Login.then(literal("email").then(
            argument("email", StringArgumentType.string()).then(
                argument("password", StringArgumentType.string()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        resetCookie(loginMusic163.email(StringArgumentType.getString(context, "email"), StringArgumentType.getString(context, "password")));
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.login", my.name));
                    });
                    return Command.SINGLE_SUCCESS;
                })
            ))
        ));

        // cloudmusic login captcha phone
        CloudMusic.then(Login.then(literal("captcha").then(
            argument("phone", LongArgumentType.longArg()).executes(contextData -> {
                runCommand(contextData, context -> {
                    loginMusic163.sendCaptcha(LongArgumentType.getLong(context, "phone"), Configs.LOGIN.COUNTRY_CODE.getIntegerValue());
                    context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.login.send.captcha"));
                });
                return Command.SINGLE_SUCCESS;
            })
        )));

        // cloudmusic login captcha phone captcha
        CloudMusic.then(Login.then(literal("captcha").then(
            argument("phone", LongArgumentType.longArg()).then(
                argument("captcha", IntegerArgumentType.integer()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        resetCookie(loginMusic163.cellphone(LongArgumentType.getLong(context, "phone"), IntegerArgumentType.getInteger(context, "captcha"), Configs.LOGIN.COUNTRY_CODE.getIntegerValue()));
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.login", my.name));
                    });
                    return Command.SINGLE_SUCCESS;
                }))
        )));

        // cloudmusic login phone phone password
        CloudMusic.then(Login.then(literal("phone").then(
            argument("phone", LongArgumentType.longArg()).then(
                argument("password", StringArgumentType.string()).executes(contextData -> {
                    runCommand(contextData, context -> {
                        resetCookie(loginMusic163.cellphone(LongArgumentType.getLong(context, "phone"), StringArgumentType.getString(context, "password"), Configs.LOGIN.COUNTRY_CODE.getIntegerValue()));
                        context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.login", my.name));
                    });
                    return Command.SINGLE_SUCCESS;
                }))
        )));

        // cloudmusic login qr
        CloudMusic.then(Login.then(literal("qr").executes(contextData -> {
            runCommand(contextData, context -> {
                String qrKey = loginMusic163.qrKey();
                loginMusic163.getQRCodeTexture(qrKey);
                try {
                    loadQRCode = true;
                    resetCookie(loginMusic163.qrLogin(qrKey));
                }catch (ActionException err){
                    context.getSource().sendFeedback(Text.literal(err.getMessage()));
                    return;
                }finally {
                    loadQRCode = false;
                }

                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.login", my.name));
            });
            return Command.SINGLE_SUCCESS;
        })));

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
                        // cloudmusic prev
                        literal("prev").executes(context -> {
                            player.prev();
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic next
                        literal("next").executes(context -> {
                            player.next();
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
                        // cloudmusic del
                        literal("del").executes(context -> {
                            player.deletePlayingMusic();
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic trash
                        literal("trash").executes(contextData -> {
                            IMusic music = player.getPlayingMusic();
                            if (!(music instanceof Music)){
                                return Command.SINGLE_SUCCESS;
                            }

                            player.deletePlayingMusic();
                            runCommand(contextData, context -> {
                                ((Music) music).addTrashCan();
                                context.getSource().sendFeedback(Text.translatable("cloudmusic.info.command.trash", music.getName()));
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic random
                        literal("random").executes(context -> {
                            player.randomPlay();
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                    .then(
                        // cloudmusic exit
                        literal("exit").executes(context -> {
                            resetPlayer(new ArrayList<>());
                            return Command.SINGLE_SUCCESS;
                        })
                    )

            );
        });
    }
}
