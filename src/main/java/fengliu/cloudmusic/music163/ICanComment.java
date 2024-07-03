package fengliu.cloudmusic.music163;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fengliu.cloudmusic.music163.data.Comment;
import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.IdUtil;
import fengliu.cloudmusic.util.TextClickItem;
import fengliu.cloudmusic.util.page.ApiPage;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

public interface ICanComment {
    String getThreadId();

    HttpClient getApi();

    default void send(String content) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("threadId", this.getThreadId());
        data.put("content", content);

        this.getApi().POST_API("/api/resource/comments/add", data);
    }

    default ApiPage comments(boolean hot) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("rid", this.getThreadId().split("_")[3]);
        data.put("limit", 24);

        String threadId = this.getThreadId();
        String path = "/api/v1/resource/%s/%s".formatted(hot ? "hotcomments" : "comments", threadId);
        JsonObject json = this.getApi().POST_API(path, data);

        int total = json.get("total").getAsInt();
        if (total == 0) {
            throw new ActionException(Text.translatable("cloudmusic.exception.not%scomments".formatted(hot ? ".hot." : ".")));
        }

        String arrayKey = hot ? "hotComments" : "comments";
        return new ApiPage(json.getAsJsonArray(arrayKey), json.get("total").getAsInt(), path, this.getApi(), data) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray(arrayKey);
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
}
