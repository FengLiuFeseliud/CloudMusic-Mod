package fengliu.cloudmusic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;

import fengliu.cloudmusic.client.command.MusicCommand;
import fengliu.cloudmusic.util.CacheHelper;
import fengliu.cloudmusic.util.SimpleConfig;
import fengliu.cloudmusic.util.SimpleConfig.ConfigRequest;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class CloudMusicClient implements ClientModInitializer  {

    public static Path MC_PATH = FabricLoader.getInstance().getGameDir();

    public static ConfigRequest configRequest = SimpleConfig.of("cloud_music_config").provider(CloudMusicClient::provider);
    public static SimpleConfig CONFIG = configRequest.request();

    public static CacheHelper cacheHelper = new CacheHelper();

    private static String provider(String filename) {
      return """
            # CloudMusic 配置
            # ps: 否为 false, 是为 true ...

            # 音量
            volume=80

            # cookie 用户
            cookie=

            # 是否直接播放, 不下载缓存音乐文件
            # ps: 直接播放可以节省空间, 但有可能出现音乐无法播放完整
            play.url=false

            # 缓存配置

            # 缓存路径 (默认游戏目录下的 cloud_music_cache)
            cache.path=
            # 最大缓存 (mb)
            cache.maxmb=512
            # 超出缓存时清理多少空间 (mb)
            cache.deletemb=126
              """;
    }

    public static void resetConfig(){
        configRequest = SimpleConfig.of("cloud_music_config").provider(CloudMusicClient::provider);
        CONFIG = configRequest.request();
        cacheHelper = new CacheHelper();
    }

    public static void setConfigValue(String key, Object value){
        StringBuilder sb = new StringBuilder();
        PrintWriter pw = null;
        boolean setIn = false;

        try (BufferedReader br = new BufferedReader(new FileReader(configRequest.file))) {
            for(String line = br.readLine(); line != null; line = br.readLine()){
                if(line.equals("")){
                    sb.append(line + "\n");
                    continue;
                }

                if(String.valueOf(line.charAt(0)).equals("#")){
                    sb.append(line + "\n");
                    continue;
                }

                String[] keyValue = line.split("=", 2);
                if(!keyValue[0].equals(key)){
                    sb.append(line + "\n");
                    continue;
                }
                
                sb.append(key + "=" + String.valueOf(value) + "\n");
                setIn = true;
            }

            if(!setIn){
                sb.append(key + "=" + String.valueOf(value) + "\n");
            }

            pw = new PrintWriter(new FileWriter(configRequest.file));
            pw.print(sb);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
  
    @Override
	public void onInitializeClient() {
        MusicCommand.registerAll();
    }

}
