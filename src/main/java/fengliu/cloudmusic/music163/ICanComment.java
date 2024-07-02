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
    String getCommentId();

    ApiPage getComments(boolean hot);

    default ApiPage comments(HttpClient api, long id, boolean hot) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("rid", id);
        data.put("limit", 24);

        String commentId = this.getCommentId();
        String path = "/api/v1/resource/%s/%s".formatted(hot ? "hotcomments" : "comments", commentId);
        JsonObject json = api.POST_API(path, data);

        int total = json.get("total").getAsInt();
        if (total == 0) {
            throw new ActionException(Text.translatable("cloudmusic.exception.not%scomments".formatted(hot ? ".hot." : ".")));
        }

        String arrayKey = hot ? "hotComments" : "comments";
        return new ApiPage(json.getAsJsonArray(arrayKey), json.get("total").getAsInt(), path, api, data) {
            @Override
            protected JsonArray getNewPageDataJsonArray(JsonObject result) {
                return result.getAsJsonArray(arrayKey);
            }

            @Override
            protected TextClickItem putPageItem(Object data) {
                Comment comment = new Comment(this.api, (JsonObject) data, commentId);
                return new TextClickItem(
                        Text.literal(comment.getPageItem()),
                        Text.translatable(IdUtil.getShowInfo("page.comment")),
                        "/cloudmusic comment %s %s".formatted(comment.id, commentId)
                );
            }
        };
    }
}
