package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.math.BigInteger;

public class Yaw extends Command {
  public Yaw() {
    super("Yaw");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      long l = (new BigInteger(args[0])).longValue();
      mc.player.rotationYaw = (float)l;
      ChatUtils.message("Yaw =" + l);
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Set Yaw";
  }
  
  public String getSyntax() {
    return "Yaw <Num>";
  }
}
