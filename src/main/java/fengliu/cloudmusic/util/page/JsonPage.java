package fengliu.cloudmusic.util.page;

import com.google.gson.JsonArray;

public abstract class JsonPage extends Page {

    public JsonPage(JsonArray data) {
        super(data.asList());
    }

    public JsonPage(JsonArray data, int dataCount) {
        super(data.asList(), dataCount);
    }
}
