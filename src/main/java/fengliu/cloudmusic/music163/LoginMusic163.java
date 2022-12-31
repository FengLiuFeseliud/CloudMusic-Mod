package fengliu.cloudmusic.music163;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import fengliu.cloudmusic.util.HttpClient;

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

}
