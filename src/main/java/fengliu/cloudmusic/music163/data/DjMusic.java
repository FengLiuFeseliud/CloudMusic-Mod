package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonObject;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.ActionException;
import fengliu.cloudmusic.music163.IMusic;
import fengliu.cloudmusic.music163.Music163Obj;
import fengliu.cloudmusic.music163.Shares;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.TextClickItem;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public class DjMusic extends Music163Obj implements IMusic {
    private final HttpClient api;
    public final long id;
    public final long mainTrackId;
    public final String name;
    public final JsonObject dj;
    public final JsonObject radio;
    public final String coverUrl;
    public final long listenerCount;
    public final long likedCount;
    public final String[] description;
    public final long duration;

    /**
     * 初始化对象
     *
     * @param api  HttpClient api
     * @param data 对象数据
     */
    public DjMusic(HttpClient api, JsonObject data) {
        super(api, data);
        this.api = api;
        this.id = data.get("id").getAsLong();
        this.mainTrackId = data.get("mainTrackId").getAsLong();
        this.name = data.get("name").getAsString();
        this.dj = data.getAsJsonObject("dj");
        this.radio = data.getAsJsonObject("radio");
        this.coverUrl = data.get("coverUrl").getAsString();
        this.listenerCount = data.get("listenerCount").getAsLong();
        if (data.has("likedCount")){
            this.likedCount = data.get("likedCount").getAsLong();
        } else {
            this.likedCount = 0;
        }

        this.description = data.get("description").getAsString().split("\n");
        this.duration = data.get("duration").getAsLong();
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPicUrl() {
        return this.coverUrl;
    }

    @Override
    public String getPlayUrl(){
        HttpClient playApi = new HttpClient("https://interface3.music.163.com", this.api.getHeader());
        Map<String, Object> data = new HashMap<>();
        data.put("ids", "[" + this.mainTrackId +"]");
        data.put("level", Configs.PLAY.PLAY_QUALITY.getStringValue());
        data.put("encodeType", "flac");

        JsonObject result = playApi.POST_API("/api/song/enhance/player/url/v1", data);
        JsonObject music = result.get("data").getAsJsonArray().get(0).getAsJsonObject();
        if(music.get("code").getAsInt() != 200){
            throw new ActionException(Text.translatable("cloudmusic.exception.music.get.url", this.name));
        }
        return music.get("url").getAsString();
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.literal(this.name));

        source.sendFeedback(Text.literal(""));

        source.sendFeedback(new TextClickItem(
                "info.dj.music.radio",
                "/cloudmusic dj " + this.radio.get("id").getAsLong()
        ).append("§b" + this.radio.get("name").getAsString()).build());

        source.sendFeedback(new TextClickItem(
                "info.dj.creator",
                "/cloudmusic user " + this.dj.get("userId").getAsLong()
        ).append("§b" + this.dj.get("nickname").getAsString()).build());

        source.sendFeedback(Text.translatable("cloudmusic.info.dj.music.count", this.listenerCount, this.likedCount));
        source.sendFeedback(Text.translatable("cloudmusic.info.dj.music.duration", this.getDurationToString()));
        source.sendFeedback(Text.translatable("cloudmusic.info.dj.music.id", this.mainTrackId));
        source.sendFeedback(Text.translatable("cloudmusic.info.dj.id", this.id));

        if (this.description != null) {
            source.sendFeedback(Text.literal(""));
            for (String row : this.description) {
                source.sendFeedback(Text.literal("§7" + row));
            }
        }

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("play", "/cloudmusic dj music play " + this.id),
                new TextClickItem("shar", Shares.DJ_MUSIC.getShar(this.id))
        ));
    }
}
