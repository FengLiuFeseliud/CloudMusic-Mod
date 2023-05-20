package fengliu.cloudmusic.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.music163.Quality;
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
        public static final ConfigInteger VOLUME = new ConfigInteger("cloudmusic.config.volume", 80, 0, 100, "cloudmusic.config.volume.comment");
        public static final ConfigBoolean PLAY_URL = new ConfigBoolean("cloudmusic.config.play.url", false, "cloudmusic.config.play.url.comment");
        public static final ConfigBooleanHotkeyed PLAY_LOOP = new ConfigBooleanHotkeyed("cloudmusic.config.play.loop", true, "", "cloudmusic.config.play.loop.comment", "cloudmusic.config.play.loop.pretty");
        public static final ConfigBooleanHotkeyed PLAY_AUTO_RANDOM = new ConfigBooleanHotkeyed("cloudmusic.config.play.auto.random", false, "", "cloudmusic.config.play.auto.random.comment", "cloudmusic.config.play.auto.random.pretty");
        public static final ConfigOptionList PLAY_QUALITY = new ConfigOptionList("cloudmusic.config.play.quality", Quality.EXHIGH, "cloudmusic.config.play.quality.comment");
        public static final ConfigBoolean DJRADIO_PLAY_ASC = new ConfigBoolean("cloudmusic.config.dj.radio.play.asc", false, "cloudmusic.config.dj.radio.play.asc.comment");
        public static final ConfigString CACHE_PATH = new ConfigString("cloudmusic.config.cache.path", (new File(FileUtils.getMinecraftDirectory(), "cloud_music_cache")).getAbsolutePath(), "cloudmusic.config.cache.path.comment");
        public static final ConfigInteger CACHE_MAX_MB = new ConfigInteger("cloudmusic.config.cache.max.mb", 512, 512, 8000, "cloudmusic.config.cache.max.mb.comment");
        public static final ConfigInteger CACHE_DELETE_MB = new ConfigInteger("cloudmusic.config.cache.delete.mb", 126, 126, 8000, "cloudmusic.config.cache.delete.mb.comment");
        public static final ConfigBooleanHotkeyed MUSIC_INFO = new ConfigBooleanHotkeyed("cloudmusic.config.music.info", true, "", "cloudmusic.config.music.info.comment", "cloudmusic.config.music.info.pretty");
        public static final ConfigBooleanHotkeyed LYRIC = new ConfigBooleanHotkeyed("cloudmusic.config.lyric", true, "", "cloudmusic.config.lyric.comment", "cloudmusic.config.lyric.pretty");
        public static final ConfigInteger PAGE_LIMIT = new ConfigInteger("cloudmusic.config.page.limit", 8, 5, 16, "cloudmusic.config.page.limit.comment");
        public static final ConfigInteger MUSIC_INFO_X = new ConfigInteger("cloudmusic.config.music.info.x", 0, 0, 4000, "cloudmusic.config.music.info.x.comment");
        public static final ConfigInteger MUSIC_INFO_Y = new ConfigInteger("cloudmusic.config.music.info.y", 30, 0, 3000, "cloudmusic.config.music.info.y.comment");
        public static final ConfigColor MUSIC_INFO_COLOR = new ConfigColor("cloudmusic.config.music.info.color", "#4DE41318", "cloudmusic.config.music.info.color.comment");
        public static final ConfigColor MUSIC_PROGRESS_BAR_COLOR = new ConfigColor("cloudmusic.config.music.progress.bar.color", "#FF858585", "cloudmusic.config.music.progress.bar.color.comment");
        public static final ConfigColor MUSIC_PLAYED_PROGRESS_BAR_COLOR = new ConfigColor("cloudmusic.config.music.player.progress.bar.color", "#FFFF9600", "cloudmusic.config.music.player.progress.bar.color.comment");
        public static final ConfigColor MUSIC_PROGRESS_FONT_COLOR = new ConfigColor("cloudmusic.config.music.progress.font.color", "#00858585", "cloudmusic.config.music.progress.font.color.comment");
        public static final ConfigColor MUSIC_INFO_TITLE_FONT_COLOR = new ConfigColor("cloudmusic.config.music.info.title.font.color", "#00FFFFFF", "cloudmusic.config.music.info.title.font.color.comment");
        public static final ConfigColor MUSIC_INFO_FONT_COLOR = new ConfigColor("cloudmusic.config.music.info.font.color", "#00858585", "cloudmusic.config.music.info.font.color.comment");
        public static final ConfigColor LYRIC_COLOR = new ConfigColor("cloudmusic.config.lyric.color", "#00FFFFFF", "cloudmusic.config.lyric.color.comment");
        public static final ConfigDouble LYRIC_SCALE = new ConfigDouble("cloudmusic.config.lyric.scale", 1.5, "cloudmusic.config.lyric.scale.comment");
        public static final ConfigInteger LYRIC_X = new ConfigInteger("cloudmusic.config.lyric.x", 0, 0, 4000, "cloudmusic.config.lyric.x.comment");
        public static final ConfigInteger LYRIC_Y = new ConfigInteger("cloudmusic.config.lyric.y", 0, 0, 3000, "cloudmusic.config.lyric.y.comment");
        public static final ConfigString COOKIE = new ConfigString("cloudmusic.config.login.cookie", "", "cloudmusic.config.login.cookie.comment");
        public static final ConfigInteger COUNTRY_CODE = new ConfigInteger("cloudmusic.config.login.country.code", 86, "cloudmusic.config.login.country.code.comment");
        public static final ConfigInteger QR_CHECK_NUM = new ConfigInteger("cloudmusic.config.login.qr.check.num", 10, 1, 60, "cloudmusic.config.login.qr.check.num.comment");
        public static final ConfigInteger QR_CHECK_TIME = new ConfigInteger("cloudmusic.config.login.qr.check.time", 3, 1, 60, "cloudmusic.config.login.qr.check.time.comment");
        public static final ConfigInteger MAX_RETRY = new ConfigInteger("cloudmusic.config.http.max.retry", 3, 0, 10, "cloudmusic.config.http.max.retry.comment");
        public static final ConfigInteger TIME_OUT = new ConfigInteger("cloudmusic.config.http.time.out", 30, 0, 180, "cloudmusic.config.http.time.out.comment");
        public static final ConfigHotkey OPEN_CONFIG_GUI = new ConfigHotkey("cloudmusic.config.hotkey.open.config.gui", "LEFT_CONTROL,C,M", "cloudmusic.config.hotkey.open.config.gui.comment");
        public static final ConfigHotkey SWITCH_PLAY_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.switch.play.music", "", "cloudmusic.config.hotkey.switch.play.music.comment");
        public static final ConfigHotkey PLAY_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.play.music", "", "cloudmusic.config.hotkey.play.music.comment");
        public static final ConfigHotkey NEXT_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.next.music", "", "cloudmusic.config.hotkey.next.music.comment");
        public static final ConfigHotkey PREV_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.prev.music", "", "cloudmusic.config.hotkey.prev.music.comment");
        public static final ConfigHotkey STOP_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.stop.music", "", "cloudmusic.config.hotkey.stop.music.comment");
        public static final ConfigHotkey EXIT_PLAY = new ConfigHotkey("cloudmusic.config.hotkey.exit.play", "", "cloudmusic.config.hotkey.exit.play.comment");
        public static final ConfigHotkey PLAY_VOLUME_ADD = new ConfigHotkey("cloudmusic.config.hotkey.play.volume.add", "", "cloudmusic.config.hotkey.play.volume.add.comment");
        public static final ConfigHotkey PLAY_VOLUME_DOWN = new ConfigHotkey("cloudmusic.config.hotkey.play.volume.down", "", "cloudmusic.config.hotkey.play.volume.down.comment");
        public static final ConfigHotkey DELETE_PLAY_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.delete.play.music", "", "cloudmusic.config.hotkey.delete.play.music.comment");
        public static final ConfigHotkey TRASH_ADD_PLAY_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.trash.add.play.music", "", "cloudmusic.config.hotkey.trash.add.play.music.comment");
        public static final ConfigHotkey LIKE_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.like.music", "", "cloudmusic.config.hotkey.like.music.comment");
        public static final ConfigHotkey PLAYLIST_ADD_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.playlist.add.music", "", "cloudmusic.config.hotkey.playlist.add.music.comment");
        public static final ConfigHotkey PLAYLIST_DEL_MUSIC = new ConfigHotkey("cloudmusic.config.hotkey.playlist.del.music", "", "cloudmusic.config.hotkey.playlist.del.music.comment");
        public static final ConfigHotkey PLAYLIST_RANDOM = new ConfigHotkey("cloudmusic.config.hotkey.playlist.random", "", "cloudmusic.config.hotkey.playlist.random.comment");

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

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
            MAX_RETRY,
            TIME_OUT
        );
    }

    public static class ENABLE {
        public static final ConfigBooleanHotkeyed MUSIC_INFO = ALL.MUSIC_INFO;
        public static final ConfigBooleanHotkeyed LYRIC = ALL.LYRIC;
        public static final ConfigBooleanHotkeyed PLAY_LOOP = ALL.PLAY_LOOP;
        public static final ConfigBooleanHotkeyed PLAY_AUTO_RANDOM = ALL.PLAY_AUTO_RANDOM;

        public static final ImmutableList<ConfigBooleanHotkeyed> HOTKEY_LIST = ImmutableList.of(
            MUSIC_INFO,
            LYRIC,
            PLAY_LOOP,
            PLAY_AUTO_RANDOM
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
