package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

public class Give extends Command {
  public Give() {
    super("Give");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (!mc.player.isCreative())
        ChatUtils.error("You must be in creative mode."); 
      Item item = null;
      int amount = 1;
      int metadata = 0;
      StringBuilder nbt = null;
      item = Item.getByNameOrId(args[0]);
      if (item == null) {
        ChatUtils.error("There's no such item with name §7" + args[0] + "§c.");
        return;
      } 
      if (args.length > 1) {
        try {
          amount = Integer.parseInt(args[1]);
        } catch (NullPointerException|NumberFormatException e) {
          ChatUtils.error("§7" + args[1] + "§c is not a valid number.");
          return;
        } 
        if (args.length > 2) {
          try {
            metadata = Integer.parseInt(args[2]);
          } catch (NullPointerException|NumberFormatException e) {
            ChatUtils.error("§7" + args[2] + "§c is not a valid number.");
            return;
          } 
          if (args.length > 3) {
            nbt = new StringBuilder(args[3]);
            for (int i = 4; i < args.length; i++)
              nbt.append(" ").append(args[i]); 
            nbt = new StringBuilder(nbt.toString().replace('&', '§').replace("§§", "&"));
          } 
        } 
      } 
      ItemStack stack = new ItemStack(item, amount, metadata);
      if (nbt != null)
        try {
          stack.setTagCompound(JsonToNBT.getTagFromJson(nbt.toString()));
        } catch (NBTException e) {
          ChatUtils.error("Data tag parsing failed: " + e.getMessage());
          return;
        }  
      updateFirstEmptySlot(stack);
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Gives items";
  }
  
  public static void updateFirstEmptySlot(ItemStack stack) {
    int slot = 0;
    boolean slotFound = false;
    for (int i = 0; i < 36; i++) {
      if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
        slot = i;
        slotFound = true;
        break;
      } 
    } 
    if (!slotFound) {
      ChatUtils.warning("Could not find empty slot. Operation has been aborted.");
      return;
    } 
    int convertedSlot = slot;
    if (slot < 9)
      convertedSlot += 36; 
    if (stack.getCount() > 64) {
      ItemStack passStack = stack.copy();
      stack.setCount(64);
      passStack.setCount(passStack.getCount() - 64);
      mc.player.inventory.setInventorySlotContents(slot, stack);
      ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection()))
        .sendPacket((Packet)new CPacketCreativeInventoryAction(convertedSlot, stack));
      updateFirstEmptySlot(passStack);
      return;
    } 
    ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketCreativeInventoryAction(convertedSlot, stack));
  }
  
  public String getSyntax() {
    return "Give <Id> <MetaData>";
  }
}
