package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

public class Hat extends Command {
  public Hat() {
    super("Hat");
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
      ItemStack head = mc.player.inventory.armorItemInSlot(3);
      mc.player.inventory.armorInventory.set(3, stack);
      updateSlot(5, stack);
      updateSlot(36 + mc.player.inventory.currentItem, head);
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public static void updateSlot(int slot, ItemStack stack) {
    ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketCreativeInventoryAction(slot, stack));
  }
  
  public String getDescription() {
    return "Hand to head";
  }
  
  public String getSyntax() {
    return "Hat";
  }
}
