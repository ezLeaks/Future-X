package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import java.util.Random;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;

public class ChatMutator extends Module {
  Setting mode;
  
  public ChatMutator() {
    super("ChatMutator", 0, Category.MISC, "ChatMutator");
    this.mode = Main.setmgr.add(new Setting("Mode", this, "FANCY", new String[] { "LEET", "FANCY", "DUMB", "CONSOLE", "BYPASS" }));
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof CPacketChatMessage) {
      CPacketChatMessage packet2 = (CPacketChatMessage)packet;
      if (packet2.getMessage().startsWith("/") || packet2.getMessage().startsWith("&"))
        return false; 
      if (this.mode.getValString().equalsIgnoreCase("BYPASS")) {
        StringBuilder msg = new StringBuilder();
        char[] charArray = packet2.getMessage().toCharArray();
        for (char c : charArray)
          msg.append(c).append("Řś"); 
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketChatMessage(msg.toString().replaceFirst("%", "")));
        return false;
      } 
      if (this.mode.getValString().equalsIgnoreCase("leet"))
        packet2.message = leetSpeak(packet2.message); 
      if (this.mode.getValString().equalsIgnoreCase("FANCY"))
        packet2.message = fancy(packet2.message); 
      if (this.mode.getValString().equalsIgnoreCase("DUMB"))
        packet2.message = retard(packet2.message); 
      if (this.mode.getValString().equalsIgnoreCase("CONSOLE"))
        packet2.message = console(packet2.message); 
    } 
    return true;
  }
  
  public String leetSpeak(String input) {
    input = input.toLowerCase().replace("a", "4");
    input = input.toLowerCase().replace("e", "3");
    input = input.toLowerCase().replace("g", "9");
    input = input.toLowerCase().replace("h", "1");
    input = input.toLowerCase().replace("o", "0");
    input = input.toLowerCase().replace("s", "5");
    input = input.toLowerCase().replace("t", "7");
    input = input.toLowerCase().replace("i", "1");
    return input;
  }
  
  public String fancy(String input) {
    StringBuilder sb = new StringBuilder();
    for (char c : input.toCharArray()) {
      if (c >= '!' && c <= '') {
        sb.append(Character.toChars(c + 65248));
      } else {
        sb.append(c);
      } 
    } 
    return sb.toString();
  }
  
  public String retard(String input) {
    StringBuilder sb = new StringBuilder(input);
    for (int i = 0; i < sb.length(); i += 2)
      sb.replace(i, i + 1, sb.substring(i, i + 1).toUpperCase()); 
    return sb.toString();
  }
  
  public String console(String input) {
    StringBuilder ret = new StringBuilder();
    char[] unicodeChars = { 
        '⸻', '⛐', '⛨', '⚽', '⚾', '⛷', '⏪', '⏩', '⏫', '⏬', 
        '✅', '❌', '⛄' };
    int length = input.length();
    for (int i = 1, current = 0; i <= length || current < length; current = i, i++) {
      if (current != 0) {
        Random random = new Random();
        for (int j = 0; j <= 2; j++)
          ret.append(unicodeChars[random.nextInt(unicodeChars.length)]); 
      } 
      if (i <= length) {
        ret.append(input, current, i);
      } else {
        ret.append(input.substring(current));
      } 
    } 
    return ret.toString();
  }
}
