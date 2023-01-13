package fengliu.cloudmusic;

import java.nio.file.Path;

import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.util.CacheHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudMusicClient implements ClientModInitializer  {

    public static final String MOD_ID = "cloudmusic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Path MC_PATH = FabricLoader.getInstance().getGameDir();
    /**
     * 缓存工具对象
     */
    public static CacheHelper cacheHelper = new CacheHelper();
  
    @Override
	public void onInitializeClient() {
        Configs.INSTANCE.load();
        MusicCommand.registerAll();
    }

}
