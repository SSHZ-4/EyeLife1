package baidu_voice_to_characters;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONObject;

public class getWords {
	
	
    private static final String serverURL = "http://vop.baidu.com/server_api";
    private static String token = "";
    //private static final String testFileName = "zhangxu.mp3";
    private static final String apiKey = "barUxCHSmuTEApViYzP9rNcQ";
    private static final String secretKey = "5f359539bd27ff57c55a3b560038336e";
    private static final String cuid = "8681692";

    
    public getWords() throws Exception
    {
    	String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" + 
	            "&client_id=" + apiKey + "&client_secret=" + secretKey;
	HttpURLConnection conn1 = (HttpURLConnection) new URL(getTokenURL).openConnection();
	token = new JSONObject(printResponse(conn1)).getString("access_token");
    }

    private static void getToken() throws Exception {
       
    }

    public static String listen(String fileName) throws Exception {
    	
    File pcmFile = new File(fileName);
    HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();

        JSONObject params = new JSONObject();
        params.put("format", "wav");
        params.put("rate", 8000);
        params.put("channel", "1");
        params.put("token", token);
        params.put("cuid", cuid);
        params.put("len", pcmFile.length());
        params.put("speech", DatatypeConverter.printBase64Binary(loadFile(pcmFile)));

        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        conn.setDoInput(true);
        conn.setDoOutput(true);

        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(params.toString());
        wr.flush();
        wr.close();

        String result=printResponse(conn);
        JSONObject jsonObject = new JSONObject(result);
        Object a= jsonObject.get("result");
        result=a.toString();
        return result;
       // return a.toString().replaceAll("]", "").replaceAll("\"", "");
    }


    private static String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            // request error
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        //System.out.println(new JSONObject(response.toString()).toString(4));
        return response.toString();
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }
}
