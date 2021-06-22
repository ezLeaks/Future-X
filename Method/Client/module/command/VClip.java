package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.math.BigInteger;

public class VClip extends Command {
  public VClip() {
    super("vclip");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      mc.player.setPosition(mc.player.posX, mc.player.posY + (new BigInteger(args[0]))
          .longValue(), mc.player.posZ);
      ChatUtils.message("Height teleported to " + (new BigInteger(args[0])).longValue());
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Teleports you up/down.";
  }
  
  public String getSyntax() {
    return "vclip <height>";
  }
}
