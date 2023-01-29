package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.music163.ICanSubscribe;
import fengliu.cloudmusic.music163.IPrint;
import fengliu.cloudmusic.music163.Shares;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.MusicPlayer;
import fengliu.cloudmusic.util.TextClick;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

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
        source.sendFeedback(new LiteralText(""));

        source.sendFeedback(new LiteralText(this.name));

        source.sendFeedback(new LiteralText(""));

        source.sendFeedback(TextClick.suggestText("cloudmusic.info.dj.creator", "§b" + this.dj.get("nickname").getAsString(), "/cloudmusic user " + this.dj.get("userId").getAsLong()));
        Map<String, String> artistsTextData = new LinkedHashMap<>();
        artistsTextData.put("§b§n" + this.category, "/cloudmusic dj category " + this.categoryId);
        artistsTextData.put("§b§n" + this.secondCategory, "/cloudmusic dj category " + this.secondCategoryId);
        source.sendFeedback(TextClick.suggestTextMap("cloudmusic.info.dj.category", artistsTextData, "§f§l/"));
        source.sendFeedback(new TranslatableText("cloudmusic.info.dj.size", this.programCount));
        source.sendFeedback(new TranslatableText("cloudmusic.info.dj.count", this.subCount, this.shareCount));
        source.sendFeedback(new TranslatableText("cloudmusic.info.dj.id", this.id));

        if(this.description != null){
            source.sendFeedback(new LiteralText(""));
            for (String row : this.description) {
                source.sendFeedback(new LiteralText("§7" + row));
            }
        }

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + new TranslatableText("cloudmusic.options.play").getString(), "/cloudmusic dj play " + this.id);
        optionsTextData.put("§c§l" + new TranslatableText("cloudmusic.options.subscribe").getString(), "/cloudmusic dj subscribe " + this.id);
        optionsTextData.put("§c§l" + new TranslatableText("cloudmusic.options.unsubscribe").getString(), "/cloudmusic dj unsubscribe " + this.id);
        optionsTextData.put("§c§l" + new TranslatableText("cloudmusic.options.shar").getString(), Shares.DJ_RADIO.getShar(this.id));
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }
}
