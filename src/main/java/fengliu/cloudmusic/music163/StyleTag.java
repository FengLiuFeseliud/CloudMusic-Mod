package fengliu.cloudmusic.music163;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.TextClick;
import fengliu.cloudmusic.util.page.Page;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;

public class StyleTag extends Music163Obj implements IPrint{
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
            protected Map<String, String> putPageItem(Map<String, String> newPageData, Object data) {
                JsonObject style = (JsonObject) data;
                newPageData.put("[" +(newPageData.size() + 1) + "] §b" + style.get("tagName").getAsString() + "§r§7 - " + style.get("enName").getAsString() + " - id: " + style.get("tagId").getAsInt(), "/cloudmusic style " + style.get("tagId").getAsInt());
                return newPageData;
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

            source.sendFeedback(Text.literal(
                templateContents[2]
                    .replace("${minorityTag0}", pattern.getAsJsonObject("minorityTag0").get("text").getAsString())
                    .replace("${minorityTag1}",pattern.getAsJsonObject("minorityTag1").get("text").getAsString())
                    .replace("${minorityTag2}", pattern.getAsJsonObject("minorityTag2").get("text").getAsString())
                    .replace("${minorityTag3}", pattern.getAsJsonObject("minorityTag3").get("text").getAsString())
                    .replace("${minorityTag4}", pattern.getAsJsonObject("minorityTag4").get("text").getAsString())
                    .replace("${minorityTag5}", pattern.getAsJsonObject("minorityTag5").get("text").getAsString())
            ));
        }

        Map<String, String> optionsTextData = new LinkedHashMap<>();
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.children.styles").getString(), "/cloudmusic style children " + this.id);
        optionsTextData.put("§c§l" + Text.translatable("cloudmusic.options.shar").getString(), Shares.STYLE.getShar(this.id));
        source.sendFeedback(TextClick.suggestTextMap(optionsTextData, " "));
    }
}
