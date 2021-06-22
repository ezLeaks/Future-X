package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

public class Repair extends Command {
  public Repair() {
    super("Repair");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (!mc.player.isCreative())
        ChatUtils.warning("You must be in creative mode."); 
      if (args.length > 0)
        ChatUtils.warning("Too many arguments."); 
      ItemStack stack = mc.player.inventory.getCurrentItem();
      if (stack.isEmpty()) {
        ChatUtils.error("You must hold an item in your hand.");
        return;
      } 
      if (!stack.isItemStackDamageable()) {
        ChatUtils.error("This item cannot take any damage.");
        return;
      } 
      if (!stack.isItemDamaged()) {
        ChatUtils.error("This item is not damaged.");
        return;
      } 
      stack.setItemDamage(0);
      updateSlot(36 + mc.player.inventory.currentItem, stack);
      ChatUtils.message("Item §7" + stack.getDisplayName() + " §ehas been repaired.");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public static void updateSlot(int slot, ItemStack stack) {
    ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketCreativeInventoryAction(slot, stack));
  }
  
  public String getDescription() {
    return "Repair Item In hand";
  }
  
  public String getSyntax() {
    return "Repair";
  }
}
