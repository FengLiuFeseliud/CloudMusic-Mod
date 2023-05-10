package fengliu.cloudmusic.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import fengliu.cloudmusic.config.ConfigGui;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (screen) -> {
            ConfigGui gui = new ConfigGui();
            gui.setParent(screen);
            return gui;
        };
    }
}
