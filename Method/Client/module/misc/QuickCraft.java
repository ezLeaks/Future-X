package Method.Client.module.misc;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.server.SPacketSetSlot;

public class QuickCraft extends Module {
  public QuickCraft() {
    super("QuickCraft", 0, Category.MISC, "Quick Craft");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.IN && 
      packet instanceof SPacketSetSlot && (
      (SPacketSetSlot)packet).getSlot() == 0 && ((SPacketSetSlot)packet).getStack().getItem() != Items.AIR && (
      mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiCrafting)) {
      mc.playerController.windowClick(mc.player.openContainer.windowId, 0, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
      mc.playerController.updateController();
    } 
    return true;
  }
}
