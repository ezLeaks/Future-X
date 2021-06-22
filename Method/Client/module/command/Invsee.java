package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

public class Invsee extends Command {
  public Invsee() {
    super("Invsee");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (mc.player.capabilities.isCreativeMode) {
        ChatUtils.error("Must Be Creative");
        return;
      } 
      String id = args[0];
      for (EntityPlayer entityPlayer : mc.world.playerEntities) {
        if (entityPlayer.getDisplayNameString().equalsIgnoreCase(id)) {
          mc.displayGuiScreen((GuiScreen)new GuiInventory(entityPlayer));
          return;
        } 
      } 
      ChatUtils.error("Could not find player " + id);
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "See inv of other players";
  }
  
  public String getSyntax() {
    return "Invsee <Player>";
  }
}
