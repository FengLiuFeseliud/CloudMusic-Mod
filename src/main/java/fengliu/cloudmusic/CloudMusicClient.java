package fengliu.cloudmusic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import fengliu.cloudmusic.client.command.MusicCommand;
import fengliu.cloudmusic.util.SimpleConfig;
import fengliu.cloudmusic.util.SimpleConfig.ConfigRequest;
import net.fabricmc.api.ClientModInitializer;

public class CloudMusicClient implements ClientModInitializer  {

    public static ConfigRequest configRequest = SimpleConfig.of("cloud_music_config").provider(CloudMusicClient::provider);
    public static SimpleConfig CONFIG = configRequest.request();

    private static String provider(String filename) {
      return "#CloudMusic default config\n";
    }

    public static void resetConfig(){
        configRequest = SimpleConfig.of("cloud_music_config").provider(CloudMusicClient::provider);
        CONFIG = configRequest.request();
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
                break;
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
