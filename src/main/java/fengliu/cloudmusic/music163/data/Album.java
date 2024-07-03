package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.music163.*;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.ApiPage;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专辑对象
 */
public class Album extends Music163Obj implements IMusicList, ICanSubscribe, ICanComment {
    public final long id;
    public final String name;
    public final String cover;
    public final JsonArray alias;
    public final JsonArray artists;
    public final int size;
    public final String[] description;
    private JsonArray songs;
    private List<IMusic> musics;
    public final String threadId;

    public Album(HttpClient api, JsonObject album) {
        super(api, album);
        this.songs = album.get("songs").getAsJsonArray();
        album = album.get("album").getAsJsonObject();

        this.id = album.get("id").getAsLong();
        this.name = album.get("name").getAsString();
        this.cover = album.get("picUrl").getAsString();
        this.size = album.get("size").getAsInt();
        this.artists = album.get("artists").getAsJsonArray();
        this.alias = album.get("alias").getAsJsonArray();
        if (!album.get("description").isJsonNull()) {
            this.description = album.get("description").getAsString().split("\n");
        } else {
            this.description = null;
        }
        this.threadId = "R_AL_3_%s".formatted(this.id);
    }

    @Override
    public ApiPage getComments(boolean hot) {
        return this.comments(this.api, this.id, this.threadId, hot);
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        if (this.alias.size() == 0) {
            source.sendFeedback(Text.literal(this.name));
        } else {
            StringBuilder aliasName = new StringBuilder();
            for (JsonElement alia : this.alias.asList()) {
                aliasName.append(alia.getAsString()).append(" / ");
            }
            aliasName = new StringBuilder(aliasName.substring(0, aliasName.length() - 3));

            source.sendFeedback(Text.literal(this.name + " §7(" + aliasName + ")"));
        }

        source.sendFeedback(Text.literal(""));

        List<TextClickItem> artistsTexts = new ArrayList<>();
        for (JsonElement artistData : this.artists.asList()) {
            JsonObject artist = artistData.getAsJsonObject();
            artistsTexts.add(new TextClickItem(
                    Text.literal(artist.get("name").getAsString()),
                    Text.translatable(IdUtil.getShowInfo("music.artist")),
                    "/cloudmusic artist " + artist.get("id").getAsLong()
            ));
        }

        source.sendFeedback(TextClickItem.combine(
                "§f§l/",
                text -> text.setStyle(text.getStyle().withColor(Formatting.AQUA).withUnderline(true)),
                artistsTexts.toArray(new TextClickItem[]{})
        ));

        source.sendFeedback(Text.translatable("cloudmusic.info.album.size", this.size));
        source.sendFeedback(Text.translatable("cloudmusic.info.album.id", this.id));

        if (this.description != null) {
            source.sendFeedback(Text.literal(""));
            for (String row : this.description) {
                source.sendFeedback(Text.literal("§7" + row));
            }
        }

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("play", "/cloudmusic album play " + this.id),
                new TextClickItem("hot.comment", "/cloudmusic album hotComment " + this.id),
                new TextClickItem("comment", "/cloudmusic album comment " + this.id),
                new TextClickItem("subscribe", "/cloudmusic album subscribe " + this.id),
                new TextClickItem("unsubscribe", "/cloudmusic album unsubscribe " + this.id),
                new TextClickItem("shar", Shares.ALBUM.getShar(this.id))
        ));
    }

    @Override
    public List<IMusic> getMusics() {
        if (this.musics != null) {
            return this.musics;
        }

        this.musics = new ArrayList<>();
        this.songs.forEach(element -> {
            this.musics.add(new Music(api, element.getAsJsonObject(), this.cover));
        });
        return this.musics;
    }

    @Override
    public void subscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        JsonObject json = this.api.POST_API("/api/album/sub", data);
        if (json.has("message")) {
            throw new ActionException(json.get("message").getAsString());
        }
    }

    @Override
    public void unsubscribe() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", this.id);

        JsonObject json = this.api.POST_API("/api/album/unsub", data);
        if (json.has("message")) {
            throw new ActionException(json.get("message").getAsString());
        }
    }
}
