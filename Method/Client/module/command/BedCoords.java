package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;

public class BedCoords extends Command {
  public BedCoords() {
    super("BedCoords");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      ChatUtils.message(mc.player.getBedLocation().toString());
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "BedCoords";
  }
  
  public String getSyntax() {
    return "BedCoords ";
  }
}
