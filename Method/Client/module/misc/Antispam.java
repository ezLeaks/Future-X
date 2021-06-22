package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class Antispam extends Module {
  Setting unicode = Main.setmgr.add(new Setting("Unicode", this, true));
  
  Setting broadcasts = Main.setmgr.add(new Setting("Server Broadcasts", this, false));
  
  Setting spam = Main.setmgr.add(new Setting("Repeated messages", this, true));
  
  Setting deletenew = Main.setmgr.add(new Setting("Delete Repeated", this, true, this.spam, 3));
  
  Setting Similarity = Main.setmgr.add(new Setting("Similarity %", this, 0.85D, 0.0D, 1.0D, false, this.spam, 4));
  
  public static List<Priorchatlist> priorchatlists = Lists.newArrayList();
  
  public static int line;
  
  public static List<ChatLine> lastChatLine;
  
  public Antispam() {
    super("Antispam", 0, Category.MISC, "Antispam");
  }
  
  public void ClientChatReceivedEvent(ClientChatReceivedEvent event) {
    if (this.spam.getValBoolean()) {
      GuiNewChat chatinst = mc.ingameGUI.getChatGUI();
      String Incomingtext = event.getMessage().getUnformattedText();
      for (Priorchatlist Oldchat : priorchatlists) {
        if (levenshteinDistance(Oldchat.fulltext, Incomingtext) >= this.Similarity.getValDouble()) {
          Oldchat.spammedtimes++;
          String change = TextFormatting.GRAY + " (" + TextFormatting.GOLD + "x" + Oldchat.spammedtimes + TextFormatting.GRAY + ")";
          event.getMessage().appendText(change);
          chatinst.deleteChatLine(Oldchat.Removable + 1);
          Oldchat.Removable = line;
          break;
        } 
      } 
      priorchatlists.add(new Priorchatlist(line, Incomingtext, 1));
      line++;
      if (!event.isCanceled())
        chatinst.printChatMessageWithOptionalDeletion(event.getMessage(), line); 
      if (line > 256) {
        line = 0;
        priorchatlists.clear();
      } 
      event.setCanceled(true);
    } 
    super.ClientChatReceivedEvent(event);
  }
  
  public static class Priorchatlist {
    public int Removable;
    
    public String fulltext;
    
    public int spammedtimes;
    
    public Priorchatlist(int Removable, String fulltext, int spammedtimes) {
      this.Removable = Removable;
      this.fulltext = fulltext;
      this.spammedtimes = spammedtimes;
    }
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof SPacketChat) {
      SPacketChat packet2 = (SPacketChat)packet;
      if (this.broadcasts.getValBoolean() && 
        packet2.getChatComponent().getFormattedText().startsWith("§5[SERVER]"))
        return false; 
      if (this.unicode.getValBoolean() && 
        packet2.getChatComponent() instanceof TextComponentString) {
        TextComponentString component = (TextComponentString)packet2.getChatComponent();
        StringBuilder sb = new StringBuilder();
        boolean containsUnicode = false;
        for (String s : component.getFormattedText().split(" ")) {
          StringBuilder line = new StringBuilder();
          for (char c : s.toCharArray()) {
            if (c >= 'ﻠ') {
              c = (char)(c - 65248);
              containsUnicode = true;
            } 
            line.append(c);
          } 
          sb.append(line).append(" ");
        } 
        if (containsUnicode)
          packet2.chatComponent = (ITextComponent)new TextComponentString(sb.toString()); 
      } 
    } 
    return true;
  }
  
  public static double levenshteinDistance(String s1, String s2) {
    String longer = s1, shorter = s2;
    if (s1.length() < s2.length()) {
      longer = s2;
      shorter = s1;
    } 
    int longerLength = longer.length();
    if (longerLength == 0)
      return 1.0D; 
    return (longerLength - editDistance(longer, shorter)) / longerLength;
  }
  
  public static int editDistance(String s1, String s2) {
    s1 = s1.toLowerCase();
    s2 = s2.toLowerCase();
    int[] costs = new int[s2.length() + 1];
    for (int i = 0; i <= s1.length(); i++) {
      int lastValue = i;
      for (int j = 0; j <= s2.length(); j++) {
        if (i == 0) {
          costs[j] = j;
        } else if (j > 0) {
          int newValue = costs[j - 1];
          if (s1.charAt(i - 1) != s2.charAt(j - 1))
            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1; 
          costs[j - 1] = lastValue;
          lastValue = newValue;
        } 
      } 
      if (i > 0)
        costs[s2.length()] = lastValue; 
    } 
    return costs[s2.length()];
  }
}
