package Method.Client.secutils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class HWIDUtils {
  public static boolean checkUserHWID() {
    try {
      return checkHwid(HWID.bytesToHex(HWID.generateHWID()));
    } catch (Exception e) {
      return false;
    } 
  }
  
  private static String readUrl(String urlString) throws Exception {
    BufferedReader reader = null;
    try {
      URL url = new URL(urlString);
      reader = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuffer buffer = new StringBuffer();
      char[] chars = new char[1024];
      int read;
      while ((read = reader.read(chars)) != -1)
        buffer.append(chars, 0, read); 
      return buffer.toString();
    } finally {
      if (reader != null)
        reader.close(); 
    } 
  }
  
  private static boolean checkHwid(String hwid) throws Exception {
    JsonElement jelement = (new JsonParser()).parse(readUrl("https://www.futurexclient.tk/hwid.json"));
    JsonObject jobject = jelement.getAsJsonObject();
    JsonArray jarray = jobject.getAsJsonArray("hwid");
    for (JsonElement j : jarray) {
      if (j.getAsString().equals(hwid))
        return true; 
    } 
    return false;
  }
}
