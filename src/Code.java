import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/*
名称：Code
作用：自动发送HTTP请求到UbuntuPaste返回相应的链接。
 */
public class Code{
    private String code;
    private String syntax;
    private String poster;
    private static final String paste_url = "https://paste.ubuntu.com/";
    private static String last_url = "null";
    public Code(String code, String syntax, String poster) {
        this.code = code;
        this.syntax = syntax;
        this.poster = poster;
    }

    // 返回最近一次的代码链接
    public static String getLastUrl(){
        return last_url;
    }

    // 向UbuntuPaste发送请求并且返回代码链接
    public String pasteIt(){
        try {
            URL url = new URL(paste_url);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setInstanceFollowRedirects(false);
            OutputStream out = con.getOutputStream();
            out.write(String.format("poster=%s&syntax=%s&content=%s",
                    URLEncoder.encode(this.poster,"UTF-8"),
                    URLEncoder.encode(this.syntax,"UTF-8"),
                    URLEncoder.encode(this.code,"UTF-8")).getBytes());
            Map<String, List<String>> headers = con.getHeaderFields();
            String pasted_url = null;
            try{
                pasted_url = headers.get("Location").get(0);
                last_url = pasted_url;
            }
            catch (Exception ex){
                ex.printStackTrace();
                for (Map.Entry<String, List<String>> header : con.getHeaderFields().entrySet()) {
                    System.out.println(header.getKey() + "=" + header.getValue());
                }
                return null;
            }
            return pasted_url;
        }
        catch (Exception ex){
            return null;
        }
    }
}