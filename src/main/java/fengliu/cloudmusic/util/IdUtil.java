package fengliu.cloudmusic.util;

import fengliu.cloudmusic.CloudMusicClient;

public class IdUtil {

    public static String getConfig(String name) {
        return CloudMusicClient.MOD_ID + ".config." + name;
    }

    public static String getConfigComment(String name) {
        return getConfig(name) + ".comment";
    }

    public static String getConfigPretty(String name) {
        return getConfig(name) + ".pretty";
    }

    public static String getHotkeyConfig(String name) {
        return CloudMusicClient.MOD_ID + ".config.hotkey." + name;
    }

    public static String getHotkeyConfigComment(String name) {
        return getHotkeyConfig(name) + ".comment";
    }

    public static String getOptions(String name) {
        return "cloudmusic.options.%s".formatted(name);
    }

    public static String getOptionsShow(String name) {
        return "cloudmusic.options.%s.show".formatted(name);
    }

    public static String getConfigTag(String name) {
        return "cloudmusic.gui.tab.%s".formatted(name);
    }

    public static String getShowInfo(String name) {
        return "cloudmusic.info.%s.show".formatted(name);
    }
}
