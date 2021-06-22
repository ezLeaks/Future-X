package Method.Client.module.command;

import Method.Client.utils.SeedViewer.WorldLoader;
import Method.Client.utils.visual.ChatUtils;
import java.math.BigInteger;

public class WorldSeed extends Command {
  public WorldSeed() {
    super("WorldSeed");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      long Seed = (new BigInteger(args[0])).longValue();
      WorldLoader.seed = Seed;
      ChatUtils.message("Seed = " + Seed);
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "WorldSeed";
  }
  
  public String getSyntax() {
    return "WorldSeed <seed>";
  }
}
