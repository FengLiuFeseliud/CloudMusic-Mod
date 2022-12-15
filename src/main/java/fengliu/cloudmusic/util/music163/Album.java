package fengliu.cloudmusic.util.music163;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class Album extends Music163Object implements MusicList {
    public final long id;
    public final String name;
    public final String cover;
    public final JsonArray alias;
    public final JsonArray artists;
    public final int size;
    public final String[] description;
    private JsonArray songs;
    private List<Music> musics;

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
        this.description = album.get("description").getAsString().split("\n");
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        if(this.alias.size() == 0){
            source.sendFeedback(Text.literal(this.name));
        }else{
            String aliasName = "";
            for(JsonElement alia: this.alias.asList()){
                aliasName += alia.getAsString() + " / ";
            }
            aliasName = aliasName.substring(0, aliasName.length() - 3);

            source.sendFeedback(Text.literal(this.name + " §7(" + aliasName + ")"));
        }
        
        String artistName = "";
        for(JsonElement artist: this.artists.asList()){
            artistName += artist.getAsJsonObject().get("name").getAsString() + "/";
        }
        artistName = artistName.substring(0, artistName.length() - 1);
        source.sendFeedback(Text.literal(artistName));

        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.translatable("cloudmusic.info.album.id", this.id));
        source.sendFeedback(Text.translatable("cloudmusic.info.album.size", this.size));
        
        if(this.description == null){
            return;
        }
        
        source.sendFeedback(Text.literal(""));
        for (String row : this.description) {
            source.sendFeedback(Text.literal("§7" + row));
        }
    }

    @Override
    public List<Music> getMusics() {
        if(this.musics != null){
            return this.musics;
        }

        this.musics = new ArrayList<>();
        this.songs.forEach(element -> {
            this.musics.add(new Music(api, element.getAsJsonObject()));
        });
        return this.musics;
    }
    
}
