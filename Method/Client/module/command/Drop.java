package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

public class Drop extends Command {
  public Drop() {
    super("Drop");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (args[0].equalsIgnoreCase("all")) {
        ClickType t = ClickType.THROW;
        for (int var2 = 9; var2 < 45; var2++)
          mc.playerController.windowClick(0, var2, 1, t, (EntityPlayer)mc.player); 
      } else if (args[0].equalsIgnoreCase("Mob") && 
        mc.player.getRidingEntity() instanceof net.minecraft.entity.passive.AbstractHorse && mc.player.openContainer instanceof net.minecraft.inventory.ContainerHorseInventory) {
        for (int i = 2; i < 17; i++) {
          ItemStack itemStack = (ItemStack)mc.player.openContainer.getInventory().get(i);
          if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR) {
            mc.playerController.windowClick(mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
            mc.playerController.windowClick(mc.player.openContainer.windowId, -999, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
          } 
        } 
      } 
      if (args[0].equalsIgnoreCase("hand"))
        mc.player.dropItem(true); 
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Drops items";
  }
  
  public String getSyntax() {
    return "Drop <all/hand/Mob> ";
  }
}
