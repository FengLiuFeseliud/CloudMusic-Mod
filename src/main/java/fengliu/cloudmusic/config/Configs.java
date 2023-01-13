package fengliu.cloudmusic.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.CloudMusicClient;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.minecraft.text.Text;

import java.io.File;

public class Configs implements IConfigHandler {
    public static Configs INSTANCE = new Configs();
    private static String ts(String key){
        return Text.translatable(key).getString();
    }
    private static final String CONFIG_FILE_NAME = CloudMusicClient.MOD_ID + ".json";

    public static class ALL {
        public static final ConfigInteger VOLUME = new ConfigInteger(ts("cloudmusic.config.volume"), 80, 0, 100, "cloudmusic.config.volume.comment");
        public static final ConfigBoolean PLAY_URL = new ConfigBoolean(ts("cloudmusic.config.play.url"), false, ts("cloudmusic.config.play.url.comment"));
        public static final ConfigBoolean PLAY_LOOP = new ConfigBoolean(ts("cloudmusic.config.play.loop"), true, ts("cloudmusic.config.play.loop.comment"));
        public static final ConfigString CACHE_PATH = new ConfigString(ts("cloudmusic.config.cache.path"), (new File(FileUtils.getMinecraftDirectory(), "cloud_music_cache")).getAbsolutePath(), ts("cloudmusic.config.cache.path.comment"));
        public static final ConfigInteger CACHE_MAX_MB = new ConfigInteger("cloudmusic.config.cache.max.mb", 512, 512, 8000, "cloudmusic.config.cache.max.mb.comment");
        public static final ConfigInteger CACHE_DELETE_MB = new ConfigInteger("cloudmusic.config.cache.delete.mb", 126, 126, 8000, "cloudmusic.config.cache.delete.mb.comment");
        public static final ConfigInteger PAGE_LIMIT = new ConfigInteger("cloudmusic.config.page.limit", 8, 5, 16, "cloudmusic.config.page.limit.comment");
        public static final ConfigBoolean LYRIC = new ConfigBoolean("cloudmusic.config.lyric", true, "cloudmusic.config.lyric.comment");
        public static final ConfigString LYRIC_COLOR = new ConfigString("cloudmusic.config.lyric.color", "FFFFFF", "cloudmusic.config.lyric.color.comment");
        public static final ConfigDouble LYRIC_SCALE = new ConfigDouble("cloudmusic.config.lyric.scale", 1.5, "cloudmusic.config.lyric.scale.comment");
        public static final ConfigInteger LYRIC_WIDTH = new ConfigInteger("cloudmusic.config.lyric.width", 0, 0, 4000, "cloudmusic.config.lyric.width.comment");
        public static final ConfigInteger LYRIC_HEIGHT = new ConfigInteger("cloudmusic.config.lyric.height", 0, 0, 3000, "cloudmusic.config.lyric.height.comment");
        public static final ConfigString COOKIE = new ConfigString("cloudmusic.config.login.cookie", "", "cloudmusic.config.login.cookie.comment");
        public static final ConfigInteger COUNTRY_CODE = new ConfigInteger("cloudmusic.config.login.country.code", 86, "cloudmusic.config.login.country.code.comment");
        public static final ConfigInteger QR_CHECK_NUM = new ConfigInteger("cloudmusic.config.login.qr.check.num", 10, 1, 60, "cloudmusic.config.login.qr.check.num.comment");
        public static final ConfigInteger QR_CHECK_TIME = new ConfigInteger("cloudmusic.config.login.qr.check.time", 3, 1, 60, "cloudmusic.config.login.qr.check.time.comment");
        public static final ConfigInteger MAX_RETRY = new ConfigInteger("cloudmusic.config.http.max.retry", 3, 0, 10, "cloudmusic.config.http.max.retry.comment");
        public static final ConfigInteger TIME_OUT = new ConfigInteger("cloudmusic.config.http.time.out", 30, 0, 180, "cloudmusic.config.http.time.out.comment");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
            VOLUME,
            PLAY_URL,
            PLAY_LOOP,
            CACHE_PATH,
            CACHE_MAX_MB,
            CACHE_DELETE_MB,
            PAGE_LIMIT,
            LYRIC,
            LYRIC_COLOR,
            LYRIC_SCALE,
            LYRIC_WIDTH,
            LYRIC_HEIGHT,
            COOKIE,
            COUNTRY_CODE,
            QR_CHECK_NUM,
            QR_CHECK_TIME,
            MAX_RETRY,
            TIME_OUT
        );
    }

    public static class PLAY {
        public static final ConfigInteger VOLUME = ALL.VOLUME;
        public static final ConfigBoolean PLAY_URL = ALL.PLAY_URL;
        public static final ConfigBoolean PLAY_LOOP = ALL.PLAY_LOOP;
        public static final ConfigString CACHE_PATH = ALL.CACHE_PATH;
        public static final ConfigInteger CACHE_MAX_MB = ALL.CACHE_MAX_MB;
        public static final ConfigInteger CACHE_DELETE_MB = ALL.CACHE_DELETE_MB;

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
            VOLUME,
            PLAY_URL,
            PLAY_LOOP,
            CACHE_PATH,
            CACHE_MAX_MB,
            CACHE_DELETE_MB
        );
    }

    public static class GUI {
        public static final ConfigInteger PAGE_LIMIT = ALL.PAGE_LIMIT;
        public static final ConfigBoolean LYRIC = ALL.LYRIC;
        public static final ConfigString LYRIC_COLOR = ALL.LYRIC_COLOR;
        public static final ConfigDouble LYRIC_SCALE = ALL.LYRIC_SCALE;
        public static final ConfigInteger LYRIC_WIDTH = ALL.LYRIC_WIDTH;
        public static final ConfigInteger LYRIC_HEIGHT = ALL.LYRIC_HEIGHT;

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
            PAGE_LIMIT,
            LYRIC,
            LYRIC_COLOR,
            LYRIC_SCALE,
            LYRIC_WIDTH,
            LYRIC_HEIGHT
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
