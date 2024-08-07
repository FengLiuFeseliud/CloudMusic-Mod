package fengliu.cloudmusic.config;

import fengliu.cloudmusic.CloudMusicClient;
import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.util.IdUtil;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.gui.screen.Screen;

import java.util.Collections;
import java.util.List;

public class ConfigGui extends GuiConfigsBase {

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = TabManager.getConfigGuiTab();

        if (tab == ConfigGuiTab.ALL) {
            configs = Configs.ALL.OPTIONS;
        } else if (tab == ConfigGuiTab.PLAY) {
            configs = Configs.PLAY.OPTIONS;
        } else if (tab == ConfigGuiTab.GUI) {
            configs = Configs.GUI.OPTIONS;
        } else if (tab == ConfigGuiTab.COMMAND) {
            configs = Configs.COMMAND.OPTIONS;
        } else if (tab == ConfigGuiTab.LOGIN) {
            configs = Configs.LOGIN.OPTIONS;
        } else if (tab == ConfigGuiTab.HTTP) {
            configs = Configs.HTTP.OPTIONS;
        } else if (tab == ConfigGuiTab.ENABLE) {
            configs = Configs.ENABLE.HOTKEY_LIST;
        } else if (tab == ConfigGuiTab.HOTKEY) {
            configs = Configs.HOTKEY.HOTKEY_LIST;
        } else {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

    public static class TabManager {
        private static ConfigGuiTab configGuiTab = ConfigGuiTab.ALL;

        public static void setConfigGuiTab(ConfigGuiTab tab) {
            configGuiTab = tab;
        }

        public static ConfigGuiTab getConfigGuiTab() {
            return configGuiTab;
        }
    }

    private record ButtonListener(ConfigGuiTab tab, ConfigGui parent) implements IButtonActionListener {
        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton){
            TabManager.setConfigGuiTab(this.tab);

            this.parent.reCreateListWidget();
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    private int createButton(int x, int y, ConfigGuiTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, tab.getDisplayName());
        button.setEnabled(TabManager.getConfigGuiTab() != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    public ConfigGui(Screen screen){
        super(10, 50, CloudMusicClient.MOD_ID, null, "cloudmusic.gui.configs.title");
        this.setParent(screen);
    }

    public ConfigGui(){
        super(10, 50, CloudMusicClient.MOD_ID, null, "cloudmusic.gui.configs.title");
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.values()) {
            x += this.createButton(x, y, tab);
        }
    }

    public enum ConfigGuiTab {
        ALL(IdUtil.getConfigTag("all")),
        PLAY(IdUtil.getConfigTag("play")),
        GUI(IdUtil.getConfigTag("gui")),
        COMMAND(IdUtil.getConfigTag("command")),
        LOGIN(IdUtil.getConfigTag("login")),
        HTTP(IdUtil.getConfigTag("http")),
        ENABLE(IdUtil.getConfigTag("enable")),
        HOTKEY(IdUtil.getConfigTag("hotkey"));

        private final String translationKey;

        ConfigGuiTab(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getDisplayName() {
            return StringUtils.translate(this.translationKey);
        }
    }

    @Override
    public void removed() {
        super.removed();
        Configs.INSTANCE.save();

        MusicCommand.getPlayer().volumeSet(Configs.PLAY.VOLUME.getIntegerValue());
    }
}
