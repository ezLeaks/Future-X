package Method.Client.module.command;

import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

public class PlayerFinder extends Command {
  public PlayerFinder() {
    super("pfind");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      ArrayList<String> list = new ArrayList<>();
      if (args[0].equalsIgnoreCase("all")) {
        for (NetworkPlayerInfo npi : ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(Wrapper.INSTANCE.mc().getConnection())).getPlayerInfoMap())
          list.add("\n" + npi.getGameProfile().getName()); 
      } else if (args[0].equalsIgnoreCase("creatives")) {
        for (NetworkPlayerInfo npi : ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(Wrapper.INSTANCE.mc().getConnection())).getPlayerInfoMap()) {
          if (npi.getGameType().isCreative())
            list.add("\n" + npi.getGameProfile().getName()); 
        } 
      } 
      if (list.isEmpty()) {
        ChatUtils.error("List is empty.");
      } else {
        Wrapper.INSTANCE.copy(list.toString());
        ChatUtils.message("List copied to clipboard.");
      } 
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Get list of players.";
  }
  
  public String getSyntax() {
    return "pfind <all/creatives>";
  }
}
