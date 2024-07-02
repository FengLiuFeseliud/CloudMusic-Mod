package fengliu.cloudmusic.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.music163.Quality;
import fengliu.cloudmusic.util.ConfigUtil;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;

import java.io.File;
import java.util.List;

public class Configs implements IConfigHandler {
    public static Configs INSTANCE = new Configs();
    private static final String CONFIG_FILE_NAME = CloudMusicClient.MOD_ID + ".json";

    public static class ALL {
        public static final ConfigInteger VOLUME = ConfigUtil.addConfigInteger("volume", 80, 0, 100);
        public static final ConfigBoolean PLAY_URL = ConfigUtil.addConfigBoolean("play.url");
        public static final ConfigBooleanHotkeyed PLAY_LOOP = ConfigUtil.addConfigBooleanHotkeyed("play.loop");
        public static final ConfigBooleanHotkeyed PLAY_AUTO_RANDOM = ConfigUtil.addConfigBooleanHotkeyed("play.auto.random", false, "");
        public static final ConfigOptionList PLAY_QUALITY = ConfigUtil.addConfigOptionList("play.quality", Quality.EXHIGH);
        public static final ConfigBoolean DJRADIO_PLAY_ASC = ConfigUtil.addConfigBoolean("dj.radio.play.asc");
        public static final ConfigString CACHE_PATH = ConfigUtil.addConfigString("cache.path", (new File(FileUtils.getMinecraftDirectory(), "cloud_music_cache")).getAbsolutePath());
        public static final ConfigInteger CACHE_MAX_MB = ConfigUtil.addConfigInteger("cache.max.mb", 512, 512, 8000);
        public static final ConfigInteger CACHE_DELETE_MB = ConfigUtil.addConfigInteger("cache.delete.mb", 126, 126, 8000);
        public static final ConfigBooleanHotkeyed MUSIC_INFO = ConfigUtil.addConfigBooleanHotkeyed("music.info");
        public static final ConfigBooleanHotkeyed LYRIC = ConfigUtil.addConfigBooleanHotkeyed("lyric");
        public static final ConfigInteger PAGE_LIMIT = ConfigUtil.addConfigInteger("page.limit", 8, 5, 16);
        public static final ConfigInteger MUSIC_INFO_X = ConfigUtil.addConfigInteger("music.info.x", 0, 0, 4000);
        public static final ConfigInteger MUSIC_INFO_Y = ConfigUtil.addConfigInteger("music.info.y", 30, 0, 3000);
        public static final ConfigBooleanHotkeyed MUSIC_INFO_EFFECT_OFFSET = ConfigUtil.addConfigBooleanHotkeyed("music.info.effect.offset");
        public static final ConfigInteger MUSIC_INFO_EFFECT_OFFSET_X = ConfigUtil.addConfigInteger("music.info.effect.offset.x", 0, 0, 4000);
        public static final ConfigInteger MUSIC_INFO_EFFECT_OFFSET_Y = ConfigUtil.addConfigInteger("music.info.effect.offset.y", 21, 0, 3000);
        public static final ConfigColor MUSIC_INFO_COLOR = ConfigUtil.addConfigColor("music.info.color", "#4DE41318");
        public static final ConfigColor MUSIC_PROGRESS_BAR_COLOR = ConfigUtil.addConfigColor("music.progress.bar.color", "#FF858585");
        public static final ConfigColor MUSIC_PLAYED_PROGRESS_BAR_COLOR = ConfigUtil.addConfigColor("music.player.progress.bar.color", "#FFFF9600");
        public static final ConfigColor MUSIC_PROGRESS_FONT_COLOR = ConfigUtil.addConfigColor("music.progress.font.color", "#00858585");
        public static final ConfigColor MUSIC_INFO_TITLE_FONT_COLOR = ConfigUtil.addConfigColor("music.info.title.font.color");
        public static final ConfigColor MUSIC_INFO_FONT_COLOR = ConfigUtil.addConfigColor("music.info.font.color", "#00858585");
        public static final ConfigColor LYRIC_COLOR = ConfigUtil.addConfigColor("lyric.color");
        public static final ConfigDouble LYRIC_SCALE = ConfigUtil.addConfigDouble("lyric.scale", 1.5);
        public static final ConfigInteger LYRIC_X = ConfigUtil.addConfigInteger("lyric.x", 0, 0, 4000);
        public static final ConfigInteger LYRIC_Y = ConfigUtil.addConfigInteger("lyric.y", 0, 0, 3000);
        public static final ConfigString COOKIE = ConfigUtil.addConfigString("login.cookie");
        public static final ConfigInteger COUNTRY_CODE = ConfigUtil.addConfigInteger("login.country.code", 86);
        public static final ConfigInteger QR_CHECK_NUM = ConfigUtil.addConfigInteger("login.qr.check.num", 10, 1, 60);
        public static final ConfigInteger QR_CHECK_TIME = ConfigUtil.addConfigInteger("login.qr.check.time", 3, 1, 60);
        public static final ConfigInteger MAX_RETRY = ConfigUtil.addConfigInteger("http.max.retry", 3, 0, 10);
        public static final ConfigInteger TIME_OUT = ConfigUtil.addConfigInteger("http.time.out", 30, 0, 180);
        public static final ConfigBooleanHotkeyed HTTP_PROXY = ConfigUtil.addConfigBooleanHotkeyed("http.proxy", false, "");
        public static final ConfigString HTTP_PROXY_IP = ConfigUtil.addConfigString("http.proxy.ip");
        public static final ConfigInteger HTTP_PROXY_PORT = ConfigUtil.addConfigInteger("http.proxy.port", 8080);
        public static final ConfigHotkey OPEN_CONFIG_GUI = ConfigUtil.addConfigHotkey("open.config.gui", "LEFT_CONTROL,C,M");
        public static final ConfigHotkey SWITCH_PLAY_MUSIC = ConfigUtil.addConfigHotkey("switch.play.music");
        public static final ConfigHotkey PLAY_MUSIC = ConfigUtil.addConfigHotkey("play.music");
        public static final ConfigHotkey NEXT_MUSIC = ConfigUtil.addConfigHotkey("next.music");
        public static final ConfigHotkey PREV_MUSIC = ConfigUtil.addConfigHotkey("prev.music");
        public static final ConfigHotkey STOP_MUSIC = ConfigUtil.addConfigHotkey("stop.music");
        public static final ConfigHotkey EXIT_PLAY = ConfigUtil.addConfigHotkey("exit.play");
        public static final ConfigHotkey PLAY_VOLUME_ADD = ConfigUtil.addConfigHotkey("play.volume.add");
        public static final ConfigHotkey PLAY_VOLUME_DOWN = ConfigUtil.addConfigHotkey("play.volume.down");
        public static final ConfigHotkey DELETE_PLAY_MUSIC = ConfigUtil.addConfigHotkey("delete.play.music");
        public static final ConfigHotkey TRASH_ADD_PLAY_MUSIC = ConfigUtil.addConfigHotkey("trash.add.play.music");
        public static final ConfigHotkey LIKE_MUSIC = ConfigUtil.addConfigHotkey("like.music");
        public static final ConfigHotkey PLAYLIST_ADD_MUSIC = ConfigUtil.addConfigHotkey("playlist.add.music");
        public static final ConfigHotkey PLAYLIST_DEL_MUSIC = ConfigUtil.addConfigHotkey("playlist.del.music");
        public static final ConfigHotkey PLAYLIST_RANDOM = ConfigUtil.addConfigHotkey("playlist.random");
        public static final ConfigBooleanHotkeyed CLICK_RUN_COMMAND = ConfigUtil.addConfigBooleanHotkeyed("click.run.command");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                VOLUME,
                PLAY_URL,
                PLAY_LOOP,
                PLAY_AUTO_RANDOM,
                PLAY_QUALITY,
                DJRADIO_PLAY_ASC,
                CACHE_PATH,
                CACHE_MAX_MB,
                CACHE_DELETE_MB,
                PAGE_LIMIT,
                MUSIC_INFO,
                LYRIC,
                MUSIC_INFO_X,
                MUSIC_INFO_Y,
                MUSIC_INFO_EFFECT_OFFSET,
                MUSIC_INFO_EFFECT_OFFSET_X,
                MUSIC_INFO_EFFECT_OFFSET_Y,
                MUSIC_INFO_COLOR,
                MUSIC_PROGRESS_BAR_COLOR,
                MUSIC_PLAYED_PROGRESS_BAR_COLOR,
                MUSIC_PROGRESS_FONT_COLOR,
                MUSIC_INFO_TITLE_FONT_COLOR,
                MUSIC_INFO_FONT_COLOR,
                LYRIC_COLOR,
                LYRIC_SCALE,
                LYRIC_X,
                LYRIC_Y,
                COOKIE,
                COUNTRY_CODE,
                QR_CHECK_NUM,
                QR_CHECK_TIME,
                MAX_RETRY,
                TIME_OUT,
                HTTP_PROXY,
                HTTP_PROXY_IP,
                HTTP_PROXY_PORT,
                OPEN_CONFIG_GUI,
                SWITCH_PLAY_MUSIC,
                PLAY_MUSIC,
                NEXT_MUSIC,
                PREV_MUSIC,
                STOP_MUSIC,
                EXIT_PLAY,
                PLAY_VOLUME_ADD,
                PLAY_VOLUME_DOWN,
                DELETE_PLAY_MUSIC,
                TRASH_ADD_PLAY_MUSIC,
                LIKE_MUSIC,
                PLAYLIST_ADD_MUSIC,
                PLAYLIST_DEL_MUSIC,
                PLAYLIST_RANDOM
        );
    }

    public static class PLAY {
        public static final ConfigInteger VOLUME = ALL.VOLUME;
        public static final ConfigBoolean PLAY_URL = ALL.PLAY_URL;
        public static final ConfigBooleanHotkeyed PLAY_LOOP = ALL.PLAY_LOOP;
        public static final ConfigBooleanHotkeyed PLAY_AUTO_RANDOM = ALL.PLAY_AUTO_RANDOM;
        public static final ConfigOptionList PLAY_QUALITY = ALL.PLAY_QUALITY;
        public static final ConfigBoolean DJRADIO_PLAY_ASC = ALL.DJRADIO_PLAY_ASC;
        public static final ConfigString CACHE_PATH = ALL.CACHE_PATH;
        public static final ConfigInteger CACHE_MAX_MB = ALL.CACHE_MAX_MB;
        public static final ConfigInteger CACHE_DELETE_MB = ALL.CACHE_DELETE_MB;

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                VOLUME,
                PLAY_URL,
                PLAY_LOOP,
                PLAY_AUTO_RANDOM,
                PLAY_QUALITY,
                DJRADIO_PLAY_ASC,
                CACHE_PATH,
                CACHE_MAX_MB,
                CACHE_DELETE_MB
        );
    }

    public static class GUI {
        public static final ConfigBooleanHotkeyed MUSIC_INFO = ALL.MUSIC_INFO;
        public static final ConfigBooleanHotkeyed LYRIC = ALL.LYRIC;
        public static final ConfigInteger PAGE_LIMIT = ALL.PAGE_LIMIT;
        public static final ConfigInteger MUSIC_INFO_X = ALL.MUSIC_INFO_X;
        public static final ConfigInteger MUSIC_INFO_Y = ALL.MUSIC_INFO_Y;
        public static final ConfigBooleanHotkeyed MUSIC_INFO_EFFECT_OFFSET = ALL.MUSIC_INFO_EFFECT_OFFSET;
        public static final ConfigInteger MUSIC_INFO_EFFECT_OFFSET_X = ALL.MUSIC_INFO_EFFECT_OFFSET_X;
        public static final ConfigInteger MUSIC_INFO_EFFECT_OFFSET_Y = ALL.MUSIC_INFO_EFFECT_OFFSET_Y;
        public static final ConfigColor MUSIC_INFO_COLOR = ALL.MUSIC_INFO_COLOR;
        public static final ConfigColor MUSIC_PROGRESS_BAR_COLOR = ALL.MUSIC_PROGRESS_BAR_COLOR;
        public static final ConfigColor MUSIC_PLAYED_PROGRESS_BAR_COLOR = ALL.MUSIC_PLAYED_PROGRESS_BAR_COLOR;
        public static final ConfigColor MUSIC_PROGRESS_FONT_COLOR = ALL.MUSIC_PROGRESS_FONT_COLOR;
        public static final ConfigColor MUSIC_INFO_TITLE_FONT_COLOR = ALL.MUSIC_INFO_TITLE_FONT_COLOR;
        public static final ConfigColor MUSIC_INFO_FONT_COLOR = ALL.MUSIC_INFO_FONT_COLOR;
        public static final ConfigColor LYRIC_COLOR = ALL.LYRIC_COLOR;
        public static final ConfigDouble LYRIC_SCALE = ALL.LYRIC_SCALE;
        public static final ConfigInteger LYRIC_X = ALL.LYRIC_X;
        public static final ConfigInteger LYRIC_Y = ALL.LYRIC_Y;

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                MUSIC_INFO,
                LYRIC,
                PAGE_LIMIT,
                MUSIC_INFO_X,
                MUSIC_INFO_Y,
                MUSIC_INFO_EFFECT_OFFSET,
                MUSIC_INFO_EFFECT_OFFSET_X,
                MUSIC_INFO_EFFECT_OFFSET_Y,
                MUSIC_INFO_COLOR,
                MUSIC_PROGRESS_BAR_COLOR,
                MUSIC_PLAYED_PROGRESS_BAR_COLOR,
                MUSIC_PROGRESS_FONT_COLOR,
                MUSIC_INFO_TITLE_FONT_COLOR,
                MUSIC_INFO_FONT_COLOR,
                LYRIC_COLOR,
                LYRIC_SCALE,
                LYRIC_X,
                LYRIC_Y
        );
    }

    public static class COMMAND {
        public static final ConfigBooleanHotkeyed CLICK_RUN_COMMAND = ALL.CLICK_RUN_COMMAND;

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                CLICK_RUN_COMMAND
        );
    }

    public static class LOGIN {
        public static final ConfigString COOKIE = ALL.COOKIE;
        public static final ConfigInteger COUNTRY_CODE = ALL.COUNTRY_CODE;
        public static final ConfigInteger QR_CHECK_NUM = ALL.QR_CHECK_NUM;
        public static final ConfigInteger QR_CHECK_TIME = ALL.QR_CHECK_TIME;

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                COOKIE,
                COUNTRY_CODE,
                QR_CHECK_NUM,
                QR_CHECK_TIME
        );
    }

    public static class HTTP {
        public static final ConfigInteger MAX_RETRY = ALL.MAX_RETRY;
        public static final ConfigInteger TIME_OUT = ALL.TIME_OUT;
        public static final ConfigBooleanHotkeyed HTTP_PROXY = ALL.HTTP_PROXY;
        public static final ConfigString HTTP_PROXY_IP = ALL.HTTP_PROXY_IP;
        public static final ConfigInteger HTTP_PROXY_PORT = ALL.HTTP_PROXY_PORT;

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                MAX_RETRY,
                TIME_OUT,
                HTTP_PROXY,
                HTTP_PROXY_IP,
                HTTP_PROXY_PORT
        );
    }

    public static class ENABLE {
        public static final ConfigBooleanHotkeyed MUSIC_INFO = ALL.MUSIC_INFO;
        public static final ConfigBooleanHotkeyed LYRIC = ALL.LYRIC;
        public static final ConfigBooleanHotkeyed PLAY_LOOP = ALL.PLAY_LOOP;
        public static final ConfigBooleanHotkeyed PLAY_AUTO_RANDOM = ALL.PLAY_AUTO_RANDOM;
        public static final ConfigBooleanHotkeyed MUSIC_INFO_EFFECT_OFFSET = ALL.MUSIC_INFO_EFFECT_OFFSET;
        public static final ConfigBooleanHotkeyed HTTP_PROXY = ALL.HTTP_PROXY;

        public static final ImmutableList<ConfigBooleanHotkeyed> HOTKEY_LIST = ImmutableList.of(
                MUSIC_INFO,
                LYRIC,
                PLAY_LOOP,
                PLAY_AUTO_RANDOM,
                MUSIC_INFO_EFFECT_OFFSET,
                HTTP_PROXY
        );
    }

    public static class HOTKEY {
        public static final ConfigHotkey OPEN_CONFIG_GUI = ALL.OPEN_CONFIG_GUI;
        public static final ConfigHotkey SWITCH_PLAY_MUSIC = ALL.SWITCH_PLAY_MUSIC;
        public static final ConfigHotkey PLAY_MUSIC = ALL.PLAY_MUSIC;
        public static final ConfigHotkey NEXT_MUSIC = ALL.NEXT_MUSIC;
        public static final ConfigHotkey PREV_MUSIC = ALL.PREV_MUSIC;
        public static final ConfigHotkey STOP_MUSIC = ALL.STOP_MUSIC;
        public static final ConfigHotkey EXIT_PLAY = ALL.EXIT_PLAY;
        public static final ConfigHotkey PLAY_VOLUME_ADD = ALL.PLAY_VOLUME_ADD;
        public static final ConfigHotkey PLAY_VOLUME_DOWN = ALL.PLAY_VOLUME_DOWN;
        public static final ConfigHotkey DELETE_PLAY_MUSIC = ALL.DELETE_PLAY_MUSIC;
        public static final ConfigHotkey TRASH_ADD_PLAY_MUSIC = ALL.TRASH_ADD_PLAY_MUSIC;
        public static final ConfigHotkey LIKE_MUSIC = ALL.LIKE_MUSIC;
        public static final ConfigHotkey PLAYLIST_ADD_MUSIC = ALL.PLAYLIST_ADD_MUSIC;
        public static final ConfigHotkey PLAYLIST_DEL_MUSIC = ALL.PLAYLIST_DEL_MUSIC;
        public static final ConfigHotkey PLAYLIST_RANDOM = ALL.PLAYLIST_RANDOM;

        public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
                OPEN_CONFIG_GUI,
                SWITCH_PLAY_MUSIC,
                PLAY_MUSIC,
                NEXT_MUSIC,
                PREV_MUSIC,
                STOP_MUSIC,
                EXIT_PLAY,
                PLAY_VOLUME_ADD,
                PLAY_VOLUME_DOWN,
                DELETE_PLAY_MUSIC,
                TRASH_ADD_PLAY_MUSIC,
                LIKE_MUSIC,
                PLAYLIST_ADD_MUSIC,
                PLAYLIST_DEL_MUSIC,
                PLAYLIST_RANDOM
        );
    }

    @Override
    public void load() {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);
        if (configFile.isFile() && configFile.exists()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);
            if(element == null || !element.isJsonObject()){
                return;
            }

            JsonObject root = element.getAsJsonObject();
            ConfigUtils.readConfigBase(root, "ALLConfigs", ALL.OPTIONS);
        }
    }

    @Override
    public void save() {
        File dir = FileUtils.getConfigDirectory();
        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();
            ConfigUtils.writeConfigBase(root, "ALLConfigs", ALL.OPTIONS);
            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }
}
