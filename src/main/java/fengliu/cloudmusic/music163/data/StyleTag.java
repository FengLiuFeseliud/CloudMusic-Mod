package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.music163.IPrint;
import fengliu.cloudmusic.music163.Music163;
import fengliu.cloudmusic.music163.Music163Obj;
import fengliu.cloudmusic.music163.Shares;
import fengliu.cloudmusic.music163.page.StylePage;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClick;
import fengliu.cloudmusic.util.click.TextClickItem;
import fengliu.cloudmusic.util.page.Page;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;

public class StyleTag extends Music163Obj implements IPrint {
    public final int id;
    public final String name;
    public final String enName;
    public final String desc;
    public final JsonObject professionalReviews;
    public final JsonObject tagPortrait;
    public final String songNum;
    public final String artistNum;
    public final Music favouriteSong;

    /**
     * 初始化对象
     *
     * @param api  HttpClient api
     * @param data 对象数据
     */
    public StyleTag(HttpClient api, JsonObject data) {
        super(api, data);
        this.id = data.get("tagId").getAsInt();
        this.name = data.get("name").getAsString();
        this.enName = data.get("enName").getAsString();
        this.desc = data.get("desc").getAsString();

        if (data.get("professionalReviews").isJsonNull()){
            this.professionalReviews = null;
        } else {
            this.professionalReviews = data.getAsJsonObject("professionalReviews");
        }

        if (data.get("tagPortrait").isJsonNull()){
            this.tagPortrait = null;
        } else {
            this.tagPortrait = data.getAsJsonObject("tagPortrait");
        }

        this.songNum = data.get("songNum").getAsString();
        this.artistNum = data.get("artistNum").getAsString();

        if (data.get("favouriteSong").isJsonNull()){
            this.favouriteSong = null;
        } else {
            this.favouriteSong = new Music(api, data.getAsJsonObject("favouriteSong").getAsJsonObject("favouriteSong"), null);;
        }
    }

    /**
     * 子曲风列表
     * @return 页对象
     */
    public Page childrenStyles(){
        JsonArray styles = (new Music163("")).styles();
        JsonArray children = new JsonArray();
        for(JsonElement style: styles){
            JsonObject styleData = (JsonObject) style;
            if (styleData.get("tagId").getAsInt() == this.id && !styleData.get("childrenTags").isJsonNull()){
                children = styleData.getAsJsonArray("childrenTags");
                break;
            }
            return null;
        }

        return new Page(children) {

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject style = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        style.get("tagName").getAsString(),
                                        style.get("enName").getAsString(),
                                        style.get("tagId").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page.style"), style.get("tagName").getAsString()),
                        "/cloudmusic style " + style.get("tagId").getAsInt()
                );
            }
        };
    }

    /**
     * 曲风歌曲列表
     * @return 页对象
     */
    public Page music(){
        JsonObject json = this.api.POST_API("/api/style-tag/home/song", StylePage.getApiData(this.id));
        return new StylePage(json.getAsJsonObject("data").getAsJsonArray("songs"), json.getAsJsonObject("data").getAsJsonObject("page").get("total").getAsInt(), "/api/style-tag/home/song", this.api, this.id) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("data").getAsJsonArray("songs");
            }

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject music = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        music.get("name").getAsString(),
                                        Music.getArtistsName(music.getAsJsonArray("ar")),
                                        music.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), music.get("name").getAsString()),
                        "/cloudmusic music " + music.get("id").getAsLong()
                );
            }
        };
    }

    /**
     * 曲风歌单列表
     * @return 页对象
     */
    public Page playlist(){
        JsonObject json = this.api.POST_API("/api/style-tag/home/playlist", StylePage.getApiData(this.id));
        return new StylePage(json.getAsJsonObject("data").getAsJsonArray("playlist"), json.getAsJsonObject("data").getAsJsonObject("page").get("total").getAsInt(), "/api/style-tag/home/playlist", this.api, this.id) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("data").getAsJsonArray("playlist");
            }

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject playList = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        playList.get("name").getAsString(),
                                        playList.get("userName").getAsString(),
                                        playList.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), playList.get("name").getAsString()),
                        "/cloudmusic playlist " + playList.get("id").getAsLong()
                );
            }
        };
    }

    /**
     * 曲风歌手列表
     * @return 页对象
     */
    public Page artist(){
        JsonObject json = this.api.POST_API("/api/style-tag/home/artist", StylePage.getApiData(this.id));
        return new StylePage(json.getAsJsonObject("data").getAsJsonArray("artists"), json.getAsJsonObject("data").getAsJsonObject("page").get("total").getAsInt(), "/api/style-tag/home/artist", this.api, this.id) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("data").getAsJsonArray("artists");
            }

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject artist = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- id: %s"
                                .formatted(
                                        artist.get("name").getAsString(),
                                        artist.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), artist.get("name").getAsString()),
                        "/cloudmusic artist " + artist.get("id").getAsLong()
                );
            }
        };
    }

    /**
     * 曲风专辑列表
     * @return 页对象
     */
    public Page album(){
        JsonObject json = this.api.POST_API("/api/style-tag/home/album", StylePage.getApiData(this.id));
        return new StylePage(json.getAsJsonObject("data").getAsJsonArray("albums"), json.getAsJsonObject("data").getAsJsonObject("page").get("total").getAsInt(), "/api/style-tag/home/album", this.api, this.id) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("data").getAsJsonArray("albums");
            }

            @Override
            protected TextClickItem putPageItem(Object data) {
                JsonObject album = (JsonObject) data;
                return new TextClickItem(
                        Text.literal("§b%s §r§7- %s - id: %s"
                                .formatted(
                                        album.get("name").getAsString(),
                                        album.getAsJsonArray("artists").get(0).getAsJsonObject().get("name").getAsString(),
                                        album.get("id").getAsLong())
                        ),
                        Text.translatable(IdUtil.getShowInfo("page"), album.get("name").getAsString()),
                        "/cloudmusic album " + album.get("id").getAsLong()
                );
            }
        };
    }



    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.literal(this.name + " - " + this.enName));

        source.sendFeedback(Text.literal(""));

        source.sendFeedback(Text.translatable("cloudmusic.info.style.music.count", this.songNum));
        source.sendFeedback(Text.translatable("cloudmusic.info.style.artist.count", this.artistNum));
        source.sendFeedback(Text.literal("§7" + this.desc));

        JsonObject pattern;
        if (this.professionalReviews != null){
            source.sendFeedback(Text.literal(""));

            pattern = this.professionalReviews.getAsJsonObject("pattern");
            source.sendFeedback(Text.literal(
                this.professionalReviews.get("templateContent").getAsString()
                    .replace("${tagName}", this.name)
                    .replace("${tagPercent}",pattern.getAsJsonObject("tagPercent").get("text").getAsString())
                    .replace("${userPercent}", pattern.getAsJsonObject("userPercent").get("text").getAsString())
            ));
        }

        if (this.tagPortrait != null){
            source.sendFeedback(Text.literal(""));

            pattern = tagPortrait.getAsJsonObject("pattern");
            String[] templateContents = this.tagPortrait.get("templateContent").getAsString().split("\n");
            source.sendFeedback(Text.literal(
                templateContents[0]
                    .replace("${tagNum}", pattern.getAsJsonObject("tagNum").get("text").getAsString())
            ));

            source.sendFeedback(Text.literal(
                templateContents[1]
                    .replace("${tagName}",pattern.getAsJsonObject("tagName").get("text").getAsString())
                    .replace("${tagPercent}", pattern.getAsJsonObject("tagPercent").get("text").getAsString())
            ));

            for(int index = 0; index < pattern.size() - 3; index++){
                templateContents[2] = templateContents[2].replace("${minorityTag" + index +"}", pattern.getAsJsonObject("minorityTag" + index).get("text").getAsString());
            }

            source.sendFeedback(Text.literal(templateContents[2]));
        }

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.style.music").getString(), "/cloudmusic style music " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.style.playlist").getString(), "/cloudmusic style playlist " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.style.artist").getString(), "/cloudmusic style artist " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.style.album").getString(), "/cloudmusic style album " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.children.style").getString(), "/cloudmusic style children " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.shar").getString(), Shares.STYLE.getShar(this.id));
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }
}
