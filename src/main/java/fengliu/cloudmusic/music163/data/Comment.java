package fengliu.cloudmusic.music163.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.music163.ActionException;
import fengliu.cloudmusic.music163.IPrint;
import fengliu.cloudmusic.music163.Music163Obj;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.ApiPage;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;

public class Comment extends Music163Obj implements IPrint {
    public final long id;
    public final String threadId;
    public final String content;
    public final long time;
    public final String timeStr;
    public final int likedCount;
    public final JsonObject user;
    public final JsonArray beReplied;
    public final JsonObject ipLocation;

    /**
     * 初始化对象
     *
     * @param api  HttpClient api
     * @param data 对象数据
     */
    public Comment(HttpClient api, JsonObject data, String threadId) {
        super(api, data);
        this.id = data.has("commentId") ? data.get("commentId").getAsLong() : data.get("beRepliedCommentId").getAsLong();
        this.threadId = threadId;
        this.content = data.get("content").getAsString();
        this.time = data.get("time").getAsLong();
        this.timeStr = data.get("timeStr").getAsString();
        this.likedCount = data.get("likedCount").getAsInt();
        this.user = data.get("user").getAsJsonObject();
        this.beReplied = data.get("beReplied").getAsJsonArray();
        this.ipLocation = data.get("ipLocation").getAsJsonObject();
    }

    public ApiPage floors() {
        String threadId = this.threadId;
        Map<String, Object> data = new HashMap<>();
        data.put("parentCommentId", this.id);
        data.put("threadId", threadId);
        data.put("limit", 24);

        JsonObject json = api.POST_API("/api/resource/comment/floor/get", data);

        int total = json.getAsJsonObject("data").get("totalCount").getAsInt();
        if (total == 0) {
            throw new ActionException(Text.translatable("cloudmusic.exception.not.comment.floors"));
        }

        return new ApiPage(json.getAsJsonObject("data").getAsJsonArray("comments"), total, "/api/resource/comment/floor/get", api, data) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonObject("data").getAsJsonArray("comments");
            }

            @Override
            protected TextClickItem putPageItem(Object data) {
                Comment comment = new Comment(this.api, (JsonObject) data, threadId);
                return new TextClickItem(
                        Text.literal(comment.getPageItem()),
                        Text.translatable(IdUtil.getShowInfo("page.comment")),
                        "/cloudmusic comment %s %s".formatted(comment.id, threadId)
                );
            }
        };
    }

    public Text getBeContent() {
        JsonObject beReplied = this.beReplied.get(0).getAsJsonObject();
        String beContent;
        if (!beReplied.get("content").isJsonNull()) {
            beContent = beReplied.get("content").getAsString();
        } else {
            beContent = beReplied.get("status").getAsInt() == -50 ? Text.translatable("cloudmusic.info.page.comment.null").getString() : Text.translatable("cloudmusic.info.page.comment.err.null").getString();
        }
        return Text.translatable("cloudmusic.info.comment.be.replied", beReplied.get("user").getAsJsonObject().get("nickname").getAsString(),
                beReplied.get("ipLocation").getAsJsonObject().get("location").getAsString(), beContent);
    }

    public String getPageItem() {
        if (!this.beReplied.isEmpty()) {
            return "%s§r§7 - §b%s - %s: §r§f%s §7- %s - id: %s".formatted(this.getBeContent().getString(), this.user.get("nickname").getAsString(), this.ipLocation.get("location").getAsString(),
                    this.content, Text.translatable("cloudmusic.page.item.comments.like", this.likedCount).getString(), this.id);
        }
        return "§b%s - %s: §r§f%s §7- %s - id: %s".formatted(this.user.get("nickname").getAsString(), this.ipLocation.get("location").getAsString(),
                this.content, Text.translatable("cloudmusic.page.item.comments.like", this.likedCount).getString(), this.id);
    }

    public void reply(String content) {
        Map<String, Object> data = new HashMap<>();
        data.put("threadId", this.threadId);
        data.put("commentId", this.id);
        data.put("content", content);

        this.api.POST_API("/api/resource/comments/reply", data);
    }

    protected void like(boolean _in) {
        Map<String, Object> data = new HashMap<>();
        data.put("threadId", this.threadId);
        data.put("commentId", this.id);

        this.api.POST_API("/api/v1/comment/" + (_in ? "like" : "unlike"), data);
    }

    public void like() {
        this.like(true);
    }

    public void unlike() {
        this.like(false);
    }

    public void delete() {
        Map<String, Object> data = new HashMap<>();
        data.put("threadId", this.threadId);
        data.put("commentId", this.id);

        this.api.POST_API("/api/resource/comments/delete", data);
    }

    @Override
    public void printToChatHud(FabricClientCommandSource source) {
        if (!this.beReplied.isEmpty()) {
//            source.sendFeedback(new TextClickItem(
//                    (MutableText) this.getBeContent(),
//                    Text.translatable(IdUtil.getShowInfo("page.comment")),
//                    "/cloudmusic comment %s %s".formatted(this.beReplied.get(0).getAsJsonObject().get("beRepliedCommentId").getAsLong(), this.threadId)
//            ).build());
            source.sendFeedback(this.getBeContent());
            source.sendFeedback(Text.literal("========================").formatted(Formatting.GRAY));
        }

        source.sendFeedback(new TextClickItem(
                Text.literal("%s - %s: %s".formatted(this.user.get("nickname").getAsString(), this.ipLocation.get("location").getAsString(), this.content)),
                Text.translatable(IdUtil.getShowInfo("comment.user")),
                "/cloudmusic user " + this.user.get("userId").getAsLong()
        ).build());
        source.sendFeedback(Text.translatable("cloudmusic.info.comment.time", this.timeStr));
        source.sendFeedback(Text.translatable("cloudmusic.page.item.comments.like", this.likedCount));

        source.sendFeedback(TextClickItem.combine(
                new TextClickItem("comment.floors", "/cloudmusic comment floors %s %s".formatted(id, this.threadId)),
                new TextClickItem("comment.reply", "/cloudmusic comment reply %s %s".formatted(id, this.threadId)),
                new TextClickItem("comment.like", "/cloudmusic comment like %s %s".formatted(id, this.threadId)),
                new TextClickItem("comment.unlike", "/cloudmusic comment unlike %s %s".formatted(id, this.threadId)),
                new TextClickItem("comment.delete", "/cloudmusic comment delete %s %s".formatted(id, this.threadId))
        ));
    }
}
