package fengliu.cloudmusic.util.music163.page;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import fengliu.cloudmusic.util.HttpClient;
import fengliu.cloudmusic.util.page.ApiPage;
import fengliu.cloudmusic.util.page.JsonPage;

public class AlbumPage{

    protected static Map<String, String> setPageData(List<?> pageData) {
        Map<String, String> newPageData = new LinkedHashMap<>();
        for(Object data: pageData){
            JsonObject album = ((JsonElement) data).getAsJsonObject(); 
            Long id = album.get("id").getAsLong();
            newPageData.put("[" +(newPageData.size() + 1) + "] §b" + album.get("name").getAsString() + "§r - id: " + id, "/cloudmusic album " + id);
        }
        return newPageData;
    }

    public static class FixedAlbum extends JsonPage {

        public FixedAlbum(JsonArray data) {
            super(data);
        }

        @Override
        public Map<String, String> setPageData(List<?> pageData) {
            return AlbumPage.setPageData(pageData);
        }

    }

    public static abstract class UncertaintyAlbum extends ApiPage {

        public UncertaintyAlbum(JsonArray data, int dataCount, String path, HttpClient api, Map<String, Object> postData) {
            super(data, dataCount, path, api, postData);
        }

        @Override
        public Map<String, String> setPageData(List<?> pageData) {
            return AlbumPage.setPageData(pageData);
        }

    }
}
