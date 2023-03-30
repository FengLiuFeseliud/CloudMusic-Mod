package fengliu.cloudmusic.event;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.config.Configs;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.hotkeys.*;

public class InputHandler implements IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler {
    private static final InputHandler INSTANCE = new InputHandler();

    public static InputHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (IHotkey hotkey : Configs.HOTKEY.HOTKEY_LIST) {
            manager.addKeybindToMap(hotkey.getKeybind());
        }

        for (ConfigBooleanHotkeyed configHotkey : Configs.ENABLE.HOTKEY_LIST) {
            manager.addKeybindToMap(configHotkey.getKeybind());
        }
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
        manager.addHotkeysForCategory(CloudMusicClient.MOD_ID, "cloudmusic.hotkeys", Configs.HOTKEY.HOTKEY_LIST);
        manager.addHotkeysForCategory(CloudMusicClient.MOD_ID, "cloudmusic.enable", Configs.ENABLE.HOTKEY_LIST);
    }
}
