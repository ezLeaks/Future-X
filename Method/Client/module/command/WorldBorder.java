package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;

public class WorldBorder extends Command {
  public WorldBorder() {
    super("WorldBorder");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      this;
      net.minecraft.world.border.WorldBorder worldBorder = mc.world.getWorldBorder();
      ChatUtils.message("World border is at:\nMinX: " + worldBorder.minX() + "\nMinZ: " + worldBorder.minZ() + "\nMaxX: " + worldBorder.maxX() + "\nMaxZ: " + worldBorder.maxZ() + "\n");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "WorldBorder distance";
  }
  
  public String getSyntax() {
    return "WorldBorder ";
  }
}
