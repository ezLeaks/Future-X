package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

public class Head extends Command {
  public Head() {
    super("Head");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (!mc.player.isCreative())
        ChatUtils.warning("You must be in creative mode."); 
      if (args.length < 1) {
        ChatUtils.error("Invalid syntax.");
        return;
      } 
      if (args.length > 1)
        ChatUtils.warning("Too many arguments."); 
      ItemStack stack = mc.player.inventory.getCurrentItem();
      if (!stack.isEmpty() && Item.getIdFromItem(stack.getItem()) == 397 && stack.getMetadata() == 3) {
        stack.setTagInfo("SkullOwner", (NBTBase)new NBTTagString(args[0]));
        updateSlot(36 + mc.player.inventory.currentItem, stack);
        ChatUtils.message("Head's owner changed to §7" + args[0] + "§e.");
        return;
      } 
      ItemStack newStack = new ItemStack(Item.getItemById(397), 1, 3);
      newStack.setTagInfo("SkullOwner", (NBTBase)new NBTTagString(args[0]));
      Give.updateFirstEmptySlot(newStack);
      ChatUtils.message("Given head of player §7" + args[0] + "§e to §7" + mc.player
          .getName() + "§e.");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public static void updateSlot(int slot, ItemStack stack) {
    ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketCreativeInventoryAction(slot, stack));
  }
  
  public String getDescription() {
    return "Head to Hand";
  }
  
  public String getSyntax() {
    return "Head <Player>";
  }
}
