package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.music163.*;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClickItem;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 歌单对象
 */
public class PlayList extends Music163Obj implements IMusicList, ICanSubscribe, ICanComment {
    public final long id;
    public final String name;
    public final String cover;
    public final int count;
    public final long playCount;
    public final JsonObject creator;
    public final String[] description;
    public final JsonArray tags;
    private final JsonArray tracks;
    private List<IMusic> musics;
    public final String threadId;

    public PlayList(HttpClient api, JsonObject data) {
        super(api, data);

        JsonObject playlist;
        if(data.has("playlist")){
            playlist = data.get("playlist").getAsJsonObject();
        }else{
            playlist = data;
        }

        this.id = playlist.get("id").getAsLong();
        this.name = playlist.get("name").getAsString();
        this.cover = playlist.get("coverImgUrl").getAsString();
        this.count = playlist.get("trackCount").getAsInt();
        this.playCount = playlist.get("playCount").getAsLong();
        this.creator = playlist.get("creator").getAsJsonObject();
        if(!playlist.get("description").isJsonNull()){
            this.description = playlist.get("description").getAsString().split("\n");
        } else {
            this.description = null;
        }
        this.tags = playlist.get("tags").getAsJsonArray();

        if (!playlist.get("tracks").isJsonNull()) {
            this.tracks = playlist.get("tracks").getAsJsonArray();
        } else {
            this.tracks = new JsonArray();
        }

        this.threadId = "A_PL_0_%s".formatted(this.id);
    }

    public void add(long musicId){
        Map<String, Object> data = new HashMap<>();
        data.put("op", "add");
        data.put("pid", this.id);
        data.put("trackIds", "[" + musicId + "]");
        data.put("imme", "true");

        this.api.POST_API("/api/playlist/manipulate/tracks", data);
    }

    public void del(long musicId){
        Map<String, Object> data = new HashMap<>();
        data.put("op", "del");
        data.put("pid", this.id);
        data.put("trackIds", "[" + musicId + "]");
        data.put("imme", "true");

        this.api.POST_API("/api/playlist/manipulate/tracks", data);
    }

    @Override
    public List<IMusic> getMusics(){
        if(this.musics != null){
            return this.musics;
        }

        this.musics = new ArrayList<>();
        this.tracks.forEach(element -> {
            this.musics.add(new Music(api, element.getAsJsonObject(), null));
        });
        return this.musics;
    }

    @Override
    public String getThreadId() {
        return this.threadId;
    }

    @Override
    public HttpClient getApi() {
        return this.api;
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.literal(this.name));

        source.sendFeedback(Text.literal(""));

        source.sendFeedback(new TextClickItem(
                "info.playlist.creator",
                "/cloudmusic user " + this.creator.get("userId").getAsLong()
        ).append("§b" + this.creator.get("nickname").getAsString()).build());

        if (!this.tags.isEmpty()) {
            List<TextClickItem> tagsTexts = new ArrayList<>();
            for (JsonElement tag : this.tags) {
                String tagName = tag.getAsString();
                tagsTexts.add(new TextClickItem(
                        Text.literal("§b§n" + tagName),
                        Text.translatable(IdUtil.getShowInfo("playlist.tag"), tagName),
                        "/cloudmusic top playlist \"%s\"".formatted(tagName)
                ));
            }

            source.sendFeedback(Text.translatable("cloudmusic.info.playlist.tags", TextClickItem.combine(
                    "§f§l/",
                    text -> text.setStyle(text.getStyle().withColor(Formatting.AQUA).withUnderline(true)),
                    tagsTexts.toArray(new TextClickItem[]{})
            )));
        }
        source.sendFeedback(Text.translatable("cloudmusic.info.playlist.count", this.count, this.playCount));
        source.sendFeedback(Text.translatable("cloudmusic.info.playlist.id", this.id));

        if (this.description != null) {
            source.sendFeedback(Text.literal(""));
            for (String row : this.description) {
                source.sendFeedback(Text.literal("§7" + row));
            }
        }

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("play", "/cloudmusic playlist play " + this.id),
                new TextClickItem("send.comment", "/cloudmusic playlist send comment " + this.id),
                new TextClickItem("hot.comment", "/cloudmusic playlist hotComment " + this.id),
                new TextClickItem("comment", "/cloudmusic playlist comment " + this.id),
                new TextClickItem("subscribe", "/cloudmusic playlist subscribe " + this.id),
                new TextClickItem("unsubscribe", "/cloudmusic playlist unsubscribe " + this.id),
                new TextClickItem("shar", Shares.PLAY_LIST.getShar(this.id))
        ));
    }

    @Override
    public void subscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        this.api.POST_API("/api/playlist/subscribe", data);
    }

    @Override
    public void unsubscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        this.api.POST_API("/api/playlist/unsubscribe", data);
    }
}
