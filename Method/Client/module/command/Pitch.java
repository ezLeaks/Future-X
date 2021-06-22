package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.math.BigInteger;

public class Pitch extends Command {
  public Pitch() {
    super("Pitch");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      long l = (new BigInteger(args[0])).longValue();
      mc.player.rotationPitch = (float)l;
      ChatUtils.message("Pitch =" + l);
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Set Pitch";
  }
  
  public String getSyntax() {
    return "Pitch <Num>";
  }
}
