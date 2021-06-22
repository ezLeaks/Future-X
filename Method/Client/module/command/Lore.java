package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

public class Lore extends Command {
  public Lore() {
    super("Lore");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (args.length < 1) {
        ChatUtils.error("Invalid syntax.");
        return;
      } 
      ItemStack stack = mc.player.inventory.getCurrentItem();
      if (stack.isEmpty()) {
        ChatUtils.error("You must hold an item in your hand.");
        return;
      } 
      StringBuilder lore = new StringBuilder(args[0]);
      for (int i = 1; i < args.length; i++)
        lore.append(" ").append(args[i]); 
      lore = new StringBuilder(lore.toString().replace('&', '§').replace("§§", "&"));
      if (!mc.player.isCreative())
        ChatUtils.warning("You must be in creative mode."); 
      if (stack.hasTagCompound()) {
        assert stack.getTagCompound() != null;
        stack.getTagCompound().getCompoundTag("display").getTag("Lore");
        NBTTagList lores = (NBTTagList)stack.getTagCompound().getCompoundTag("display").getTag("Lore");
        lores.appendTag((NBTBase)new NBTTagString(lore.toString()));
        NBTTagCompound display = new NBTTagCompound();
        display.setTag("Lore", (NBTBase)lores);
        stack.getTagCompound().getCompoundTag("display").merge(display);
      } else {
        NBTTagList lores = new NBTTagList();
        lores.appendTag((NBTBase)new NBTTagString(lore.toString()));
        NBTTagCompound display = new NBTTagCompound();
        display.setTag("Lore", (NBTBase)lores);
        stack.setTagInfo("display", (NBTBase)display);
      } 
      updateSlot(36 + mc.player.inventory.currentItem, stack);
      ChatUtils.message("Added lore §7" + lore + "§e to the item.");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public static void updateSlot(int slot, ItemStack stack) {
    ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketCreativeInventoryAction(slot, stack));
  }
  
  public String getDescription() {
    return "Adds Lore to and object";
  }
  
  public String getSyntax() {
    return "Lore <Lore>  ";
  }
}
