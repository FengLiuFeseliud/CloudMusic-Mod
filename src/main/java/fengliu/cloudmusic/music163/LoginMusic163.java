package fengliu.cloudmusic.music163;


import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import fengliu.cloudmusic.command.MusicCommand;
import fengliu.cloudmusic.config.Configs;
import fengliu.cloudmusic.render.MusicIconTexture;
import fengliu.cloudmusic.util.HttpClient;
import net.minecraft.text.Text;

public class LoginMusic163 {
    private Map<String, String> Header = new HashMap<String, String>();
    private final HttpClient api;
    
    public LoginMusic163(){
        this.Header.put("Accept", "*/*");
        this.Header.put("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
        this.Header.put("Connection", "keep-alive");
        this.Header.put("Content-Type", "application/x-www-form-urlencoded");
        this.Header.put("Referer", "http://music.163.com");
        this.Header.put("Host", "music.163.com");
        this.Header.put("Cookie", "appver=2.7.1.198277; os=pc; ");
        this.Header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
        this.api = new HttpClient("https://music.163.com", this.Header);
    }

    public HttpClient getHttpClient(){
        return this.api; 
    }

    /**
     * md5 加密
     * @param plainText 待加密文本
     * @return md5 加密文本
     */
    public static String encryption(String plainText) {
        String md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
 
            int i;
 
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
 
            md5 = buf.toString();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    /**
     * 发送验证码
     * @param phone 手机号
     * @param countryCode 国家码
     */
    public void sendCaptcha(long phone, int countryCode) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("ctcode", countryCode);
        data.put("cellphone", phone);

        this.api.POST_API("/api/sms/captcha/sent", data);
    }

    /**
     * 手机密码 / 验证码 登录
     * @param phone 手机号
     * @param password 密码 / 验证码
     * @param countryCode 国家码
     * @param captcha 是否为 验证码 登录
     * @return 登录成功返回 cookie
     */
    public String cellphone(long phone, String password, int countryCode, boolean captcha) {
        Map<String, Object> data = new HashMap<String, Object>();
        if(captcha){
            data.put("captcha", password);
        }else{
            data.put("password", LoginMusic163.encryption(password));
        }
        data.put("rememberLogin", true);
        data.put("countrycode", countryCode);
        data.put("phone", phone);

        return this.api.POST_LOGIN("/api/login/cellphone", data);
    }

    /**
     * 邮箱登录
     * @param email 邮箱
     * @param password 密码
     * @return 登录成功返回 cookie
     */
    public String email(String email, String password) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("username", email);
        data.put("password", LoginMusic163.encryption(password));
        data.put("rememberLogin", true);

        return this.api.POST_LOGIN("/api/login", data);
    }

    /**
     * 获取二维码 key
     * @return key
     */
    public String qrKey(){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", 1);

        JsonObject json = this.api.POST_API("/api/login/qrcode/unikey", data);
        return json.get("unikey").getAsString();
    }

    /**
     * 查询二维码状态
     * 状态码:801 等待扫码 802 授权中 800 二维码不存在或已过期 803 登录成功
     * @param qrKey 二维码 key
     * @return HttpResult
     */
    private HttpClient.HttpResult qrCheck(String qrKey){
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("key", qrKey);
        data.put("type", 1);

        return this.api.POST("/api/login/qrcode/client/login", data);
    }

    /**
     * 生成二维码, 并注册材质
     * @param qrKey 二维码 key
     */
    public void getQRCodeTexture(String qrKey){
        MusicIconTexture.getQRCode("https://music.163.com/login?codekey=" + qrKey);
    }

    /**
     *  二维码登录
     * @param qrKey 二维码 key
     * @return 登录成功返回 cookie
     */
    public String qrLogin(String qrKey) throws InterruptedException {
        int qrCheckTime = Configs.LOGIN.QR_CHECK_TIME.getIntegerValue();
        int qrCheckNum = Configs.LOGIN.QR_CHECK_NUM.getIntegerValue();

        while(qrCheckNum > 0){
            HttpClient.HttpResult result = qrCheck(qrKey);
            JsonObject json = result.getJson();

            int code = json.get("code").getAsInt();
            if(code == 801 || code == 802){
                Thread.sleep(qrCheckTime* 1000L);
                qrCheckNum -= 1;
                continue;
            }

            if(code == 800){
                throw new ActionException(Text.translatable("cloudmusic.exception.login.qr.code"));
            }

            if(code == 803){
                return result.getSetCookie();
            }
        }

        throw new ActionException(Text.translatable("cloudmusic.exception.login.qr.code.time.out"));
    }
}
