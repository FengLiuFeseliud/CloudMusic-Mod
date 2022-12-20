package fengliu.cloudmusic.util.music163;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.TextClick;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class Artist extends Music163Object implements PrintObject {
    public final long id;
    public final String name;
    public final String briefDesc;
    public final int albumSize;
    public final int musicSize;

    public Artist(HttpClient api, JsonObject data) {
        super(api, data);
        JsonObject data_ = data.getAsJsonObject("data").getAsJsonObject("artist");
        this.id = data_.get("id").getAsLong();
        this.name = data_.get("name").getAsString();
        this.briefDesc = data_.get("briefDesc").getAsString();
        this.albumSize = data_.get("albumSize").getAsInt();
        this.musicSize = data_.get("musicSize").getAsInt();
    }

    public List<Music> topSong(){
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("id", this.id);

        JsonObject json = this.api.POST_API("/api/artist/top/song", data);
        List<Music> musics = new ArrayList<>();
        json.get("songs").getAsJsonArray().forEach(music -> {
            musics.add(new Music(this.api, music.getAsJsonObject()));
        });

        return musics;
    }
    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.literal(this.name));

        source.sendFeedback(Text.literal(""));
        
        source.sendFeedback(Text.translatable("cloudmusic.info.artist.music", this.musicSize));
        source.sendFeedback(Text.translatable("cloudmusic.info.artist.album", this.albumSize));
        source.sendFeedback(Text.translatable("cloudmusic.info.artist.id", this.id));
        
        source.sendFeedback(Text.literal(""));
        source.sendFeedback(Text.literal("§7" + this.briefDesc));

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.play.top50").getString(), "/cloudmusic artist top " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.music").getString(), "/cloudmusic artist music " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.album").getString(), "/cloudmusic artist album " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.subscribe").getString(), "/cloudmusic artist subscribe " + this.id);
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }
    
}
