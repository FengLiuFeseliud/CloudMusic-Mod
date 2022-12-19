package fengliu.cloudmusic.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HttpClient {
    private final String MainPath;
    private Map<String, String> Header;
    private int timeout = 6000;

    public HttpClient(String path){
        this.MainPath = path;
        this.Header = new HashMap<String, String>();
    }

    public HttpClient(String path, Map<String, String> header){
        this.MainPath = path;
        this.Header = header;
    }

    public HttpClient setTimeOut(int timeout){
        this.timeout = timeout;
        return this;
    }

    public void setCookies(String cookies){
        this.Header.put("Cookie", cookies);
    }

    public Map<String, String> getHeader(){
        return this.Header;
    }

    public String getCookies(){
        if(!this.Header.containsKey("Cookie")){
            return "";
        }
        return this.Header.get("Cookie");
    }

    public String getUrl(String paht){
        return MainPath + paht;
    }

    public HttpResult GET(String paht, @Nullable Map<String, Object> data){
        return this.connection(this.getUrl(paht), data, (HttpURLConnection connection) -> {
            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException exception) {
                exception.printStackTrace();
            }
            return connection;
        });
    }

    public HttpResult POST(String paht, @Nullable Map<String, Object> data){
        return this.connection(this.getUrl(paht), data, (HttpURLConnection connection) -> {
            try {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);
            } catch (ProtocolException exception) {
                exception.printStackTrace();
            }
            return connection;
        });
    }

    public class ApiException extends RuntimeException{

        public ApiException(int code, String msg){
            super("请求错误 " + code + ": " + msg);
        }
    }

    public JsonObject POST_API(String paht, @Nullable Map<String, Object> data){
        HttpResult result = this.POST(paht, data);
        JsonObject json = result.getJson();

        if(json.get("code").getAsInt() != 200){
            throw new ApiException(json.get("code").getAsInt(), json.get("message").getAsString());
        }

        return json;
    }

    private HttpURLConnection setRequestHeader(HttpURLConnection httpConnection){
        this.Header.forEach((key, value) -> {
            httpConnection.setRequestProperty(key, value);
        });
        return httpConnection;
    }

    private byte[] setData(Map<String, Object> data){
        String[] toDataString = {""};
        data.forEach((key, value) -> {
            toDataString[0] += key + "=" + value.toString() + "&";
        });
        return toDataString[0].getBytes();
    }

    private HttpResult connection(String httpUrl, @Nullable Map<String, Object> data, Connection connection){
        HttpURLConnection httpConnection = null;
        InputStream inputStream = null;
        try {
            //创建连接
            httpConnection = this.setRequestHeader(connection.set((HttpURLConnection) new URL(httpUrl).openConnection()));
            if(data != null){
                httpConnection.getOutputStream().write(this.setData(data));
            }
            //设置连接超时时间
            httpConnection.setReadTimeout(timeout);
            httpConnection.connect();
            //获取响应数据
            int code = httpConnection.getResponseCode();
            if (code == 200) {
                inputStream = httpConnection.getInputStream();
                return new HttpResult(code, true, inputStream.readAllBytes());
            }else{
                inputStream = httpConnection.getErrorStream();
                return new HttpResult(code, false, inputStream.readAllBytes());
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            //关闭远程连接
            httpConnection.disconnect();
        }
        byte[] edata = {};
        return new HttpResult(500, false, edata);
    }

    private interface Connection{
        HttpURLConnection set(HttpURLConnection connection);
    }

    public class HttpResult{
        public final int code;
        public final boolean status;
        private final byte[] data;

        public HttpResult(int code, boolean status, byte[] data){
            this.code = code;
            this.status = status;

            if(data == null){
                byte[] edata = {};
                this.data = edata;
                return;
            }
            this.data = data;
        }

        public String getString(){
            try {
                return new String(this.data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }

        public JsonObject getJson(){
            return JsonParser.parseString(getString()).getAsJsonObject();
        }
    }
}
