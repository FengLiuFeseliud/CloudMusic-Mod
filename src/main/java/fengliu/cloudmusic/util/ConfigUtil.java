package fengliu.cloudmusic.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.config.options.*;

public class ConfigUtil {

    public static ConfigString addConfigString(String name, String defaultValue) {
        return new ConfigString(IdUtil.getConfig(name), defaultValue, IdUtil.getConfigComment(name));
    }

    public static ConfigString addConfigString(String name) {
        return addConfigString(name, "");
    }

    public static ConfigInteger addConfigInteger(String name, int defaultValue) {
        return new ConfigInteger(IdUtil.getConfig(name), defaultValue, IdUtil.getConfigComment(name));
    }

    public static ConfigInteger addConfigInteger(String name, int defaultValue, int minValue, int maxValue) {
        return new ConfigInteger(IdUtil.getConfig(name), defaultValue, minValue, maxValue, IdUtil.getConfigComment(name));
    }

    public static ConfigDouble addConfigDouble(String name, double defaultValue) {
        return new ConfigDouble(IdUtil.getConfig(name), defaultValue, IdUtil.getConfigComment(name));
    }

    public static ConfigHotkey addConfigHotkey(String name, String defaultKey) {
        return new ConfigHotkey(IdUtil.getHotkeyConfig(name), defaultKey, IdUtil.getHotkeyConfigComment(name));
    }

    public static ConfigHotkey addConfigHotkey(String name) {
        return addConfigHotkey(name, "");
    }

    public static ConfigBoolean addConfigBoolean(String name, boolean defaultKey) {
        return new ConfigBoolean(IdUtil.getConfig(name), defaultKey, IdUtil.getConfigComment(name));
    }

    public static ConfigBoolean addConfigBoolean(String name) {
        return addConfigBoolean(name, false);
    }

    public static ConfigBooleanHotkeyed addConfigBooleanHotkeyed(String name, boolean defaultValue, String defaultKey) {
        return new ConfigBooleanHotkeyed(IdUtil.getConfig(name), defaultValue, defaultKey, IdUtil.getConfigComment(name), IdUtil.getConfigPretty(name));
    }

    public static ConfigBooleanHotkeyed addConfigBooleanHotkeyed(String name) {
        return addConfigBooleanHotkeyed(name, true, "");
    }

    public static ConfigColor addConfigColor(String name, String defaultValue) {
        return new ConfigColor(IdUtil.getConfig(name), defaultValue, IdUtil.getConfigComment(name));
    }

    public static ConfigColor addConfigColor(String name) {
        return addConfigColor(name, "#00FFFFFF");
    }

    public static <O extends IConfigOptionListEntry> ConfigOptionList addConfigOptionList(String name, O defaultValue) {
        return new ConfigOptionList(IdUtil.getConfig(name), defaultValue, IdUtil.getConfigPretty(name));
    }
}
