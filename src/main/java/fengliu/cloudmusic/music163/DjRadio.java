package fengliu.cloudmusic.music163;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.MusicPlayer;
import fengliu.cloudmusic.util.TextClick;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.*;

public class DjRadio extends MusicPlayer implements ICanSubscribe, IPrint {
    private final HttpClient api;
    public final Long id;
    public final String name;
    public final JsonObject dj;
    public final String category;
    public final int categoryId;
    public final String secondCategory;
    public final int secondCategoryId;
    public final String picUrl;
    public final int programCount;
    public final long subCount;
    public final long shareCount;
    public final String[] description;

    public DjRadio(HttpClient api, JsonObject data) {
        super(new ArrayList<>());
        this.api = api;
        this.id = data.get("id").getAsLong();
        this.name = data.get("name").getAsString();
        this.dj = data.getAsJsonObject("dj");
        this.category = data.get("category").getAsString();
        this.categoryId = data.get("categoryId").getAsInt();
        this.secondCategory = data.get("secondCategory").getAsString();
        this.secondCategoryId = data.get("secondCategoryId").getAsInt();
        this.picUrl = data.get("picUrl").getAsString();
        this.programCount = data.get("programCount").getAsInt();
        this.subCount = data.get("subCount").getAsLong();
        this.shareCount = data.get("shareCount").getAsLong();
        this.description = data.get("desc").getAsString().split("\n");
    }

    /**
     * 获取电台节目
     */
    public void addDjMusic(){
        Map<String, Object> data = new HashMap<>();
        data.put("radioId", this.id);
        data.put("limit", 32);
        data.put("offset", this.playList.size());
        data.put("asc", Configs.PLAY.DJRADIO_PLAY_ASC.getBooleanValue());

        JsonArray musicsJson = this.api.POST_API("/api/dj/program/byradio", data).getAsJsonArray("programs");
        musicsJson.forEach(djMusic -> {
            this.playList.add(new DjMusic(this.api, (JsonObject) djMusic));
        });
    }

    @Override
    public void run() {
        this.addDjMusic();

        while(this.loopPlayIn){
            playMusic();

            if(this.playIn == this.playList.size() - 1){
                this.addDjMusic();
                if (this.playIn == this.playList.size() - 1 && !Configs.PLAY.PLAY_LOOP.getBooleanValue()){
                    this.loopPlayIn = false;
                }
            }

            this.playIn += 1;
        }
    }

    @Override
    public void subscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        this.api.POST_API("/api/djradio/sub", data);
    }

    @Override
    public void unsubscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        this.api.POST_API("/api/djradio/unsub", data);
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.literal(this.name));

        source.sendFeedback(Text.literal(""));

        source.sendFeedback(TextClick.suggestText("cloudmusic.info.dj.creator", "§b" + this.dj.get("nickname").getAsString(), "/cloudmusic user " + this.dj.get("userId").getAsLong()));
        source.sendFeedback(TextClick.suggestText("cloudmusic.info.dj.category", "§b" + this.category + "§r§l/§r§b" + this.secondCategory, "/cloudmusic dj category " + this.categoryId + " " + this.secondCategoryId));
        source.sendFeedback(Text.translatable("cloudmusic.info.dj.size", this.programCount));
        source.sendFeedback(Text.translatable("cloudmusic.info.dj.count", this.subCount, this.shareCount));
        source.sendFeedback(Text.translatable("cloudmusic.info.dj.id", this.id));

        if(this.description != null){
            source.sendFeedback(Text.literal(""));
            for (String row : this.description) {
                source.sendFeedback(Text.literal("§7" + row));
            }
        }

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.play").getString(), "/cloudmusic dj play " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.subscribe").getString(), "/cloudmusic dj subscribe " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.unsubscribe").getString(), "/cloudmusic dj unsubscribe " + this.id);
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }
}
