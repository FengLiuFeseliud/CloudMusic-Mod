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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloudMusicClient implements ClientModInitializer  {

    public static final String MOD_ID = "cloudmusic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Path MC_PATH = FabricLoader.getInstance().getGameDir();
    public static ConfigRequest configRequest = SimpleConfig.of("cloud_music_config").provider(CloudMusicClient::provider);
    public static SimpleConfig CONFIG = configRequest.request();
    public static boolean lyric = CONFIG.getOrDefault("lyric", true);
    public static int lyricColor = CONFIG.getOrDefault("lyric.color", 0xFFFFFF);
    public static float lyricScale = (float) CONFIG.getOrDefault("lyric.scale", 1.5f);
    public static int lyricWidth = CONFIG.getOrDefault("lyric.width", 0);
    public static int lyricHeight = CONFIG.getOrDefault("lyric.height", 0);
    /**
     * 缓存工具对象
     */
    public static CacheHelper cacheHelper = new CacheHelper();

    /**
     * 默认配置文件
     */
    private static String provider(String filename) {
      return """
            # CloudMusic 配置
            # ps: 否为 false, 是为 true ...

            # 音量
            volume=80

            # cookie 用户
            login.cookie=
            # 手机登录时使用手机的国家码 (默认 86 中国)
            login.country.code=86
            # 二维码轮查最大次数 (默认 10 次)
            login.qr.check.num=10
            # 二维码轮查间隔时长 (默认 3 秒)
            login.qr.check.time=3
            
            # 是否绘制歌词
            lyric = true
            # 歌词颜色 (十六进制 默认 0xFFFFFF 白色)
            lyric.color=0xFFFFFF
            # 歌词绘制缩放比例 (默认 1.5 倍)
            lyric.scale=1.5
            # 歌词绘制所在宽度
            lyric.width=0
            # 歌词绘制所在高度
            lyric.height=0

            # 是否直接播放, 不下载缓存音乐文件
            # ps: 直接播放可以节省空间, 但有可能出现音乐无法播放完整
            play.url=false
            # 循环播放
            play.loop=true

            # 缓存配置

            # 缓存路径 (默认游戏目录下的 cloud_music_cache)
            cache.path=
            # 最大缓存 (mb)
            cache.maxmb=512
            # 超出缓存时清理多少空间 (mb)
            cache.deletemb=126
              """;
    }

    /**
     * 重新加载配置
     */
    public static void resetConfig(){
        configRequest = SimpleConfig.of("cloud_music_config").provider(CloudMusicClient::provider);
        CONFIG = configRequest.request();

        lyric = CONFIG.getOrDefault("lyric", true);
        lyricColor = CONFIG.getOrDefault("lyric.color", 0xFFFFFF);
        lyricScale = (float) CONFIG.getOrDefault("lyric.scale", 1.5f);
        lyricWidth = CONFIG.getOrDefault("lyric.width", 0);
        lyricHeight = CONFIG.getOrDefault("lyric.height", 0);
        cacheHelper = new CacheHelper();
    }

    /**
     * 设置配置值
     * @param key 键
     * @param value 值
     */
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
