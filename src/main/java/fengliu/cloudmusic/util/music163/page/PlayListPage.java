package fengliu.cloudmusic.util.music163.page;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.page.ApiPage;
import fengliu.cloudmusic.util.page.JsonPage;

public class PlayListPage{

    protected static Map<String, String> setPageData(List<?> pageData) {
        Map<String, String> newPageData = new LinkedHashMap<>();
        for(Object data: pageData){
            JsonObject playList = ((JsonElement) data).getAsJsonObject(); 
            Long id = playList.get("id").getAsLong();
            newPageData.put("[" +(newPageData.size() + 1) + "] §b" + playList.get("name").getAsString() + "§r - id: " + id, "/cloudmusic playlist " + id);
        }
        return newPageData;
    }

    public static class FixedPlayList extends JsonPage {

        public FixedPlayList(JsonArray data) {
            super(data);
        }

        @Override
        public Map<String, String> setPageData(List<?> pageData) {
            return PlayListPage.setPageData(pageData);
        }

    }

    public static abstract class UncertaintyPlayList extends ApiPage {

        public UncertaintyPlayList(JsonArray data, int dataCount, String path, HttpClient api, Map<String, Object> postData) {
            super(data, dataCount, path, api, postData);
        }

        @Override
        public Map<String, String> setPageData(List<?> pageData) {
            return PlayListPage.setPageData(pageData);
        }

    }
}
