package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

public class Nbt extends Command {
  public Nbt() {
    super("Nbt");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      StringBuilder nbt;
      String tag;
      int i;
      StringSelection selection;
      Clipboard clipboard;
      if (args.length < 1) {
        ChatUtils.error("Invalid syntax.");
        return;
      } 
      ItemStack stack = mc.player.inventory.getCurrentItem();
      if (stack.isEmpty()) {
        ChatUtils.error("You must hold an item in your hand.");
        return;
      } 
      switch (args[0]) {
        case "add":
          if (!mc.player.isCreative())
            ChatUtils.warning("You must be in creative mode."); 
          if (args.length < 2) {
            ChatUtils.error("No NBT data provided.");
            return;
          } 
          nbt = new StringBuilder(args[1]);
          for (i = 2; i < args.length; i++)
            nbt.append(" ").append(args[i]); 
          nbt = new StringBuilder(nbt.toString().replace('&', '§').replace("§§", "&"));
          try {
            if (!stack.hasTagCompound()) {
              stack.setTagCompound(JsonToNBT.getTagFromJson(nbt.toString()));
            } else {
              assert stack.getTagCompound() != null;
              stack.getTagCompound().merge(JsonToNBT.getTagFromJson(nbt.toString()));
            } 
            updateSlot(36 + mc.player.inventory.currentItem, stack);
            ChatUtils.message("Item modified.");
          } catch (NBTException e) {
            ChatUtils.error("Data tag parsing failed: " + e.getMessage());
          } 
          break;
        case "set":
          if (!mc.player.isCreative())
            ChatUtils.warning("You must be in creative mode."); 
          if (args.length < 2) {
            ChatUtils.error("No NBT data provided.");
            return;
          } 
          nbt = new StringBuilder(args[1]);
          for (i = 2; i < args.length; i++)
            nbt.append(" ").append(args[i]); 
          nbt = new StringBuilder(nbt.toString().replace('&', '§').replace("§§", "&"));
          try {
            stack.setTagCompound(JsonToNBT.getTagFromJson(nbt.toString()));
          } catch (NBTException e) {
            ChatUtils.error("Data tag parsing failed: " + e.getMessage());
            return;
          } 
          updateSlot(36 + mc.player.inventory.currentItem, stack);
          ChatUtils.message("Item modified.");
          break;
        case "remove":
          if (!mc.player.isCreative())
            ChatUtils.warning("You must be in creative mode."); 
          if (args.length < 2) {
            ChatUtils.error("No NBT tag specified.");
            return;
          } 
          if (args.length > 2)
            ChatUtils.warning("Too many arguments."); 
          tag = args[1];
          if (!stack.hasTagCompound() || !((NBTTagCompound)Objects.<NBTTagCompound>requireNonNull(stack.getTagCompound())).hasKey(tag)) {
            ChatUtils.error("Item has no NBT tag with name §7" + args[1] + "§c.");
            return;
          } 
          stack.getTagCompound().removeTag(tag);
          if (stack.getTagCompound().isEmpty())
            stack.setTagCompound(null); 
          updateSlot(36 + mc.player.inventory.currentItem, stack);
          ChatUtils.message("Item modified.");
          break;
        case "clear":
          if (!mc.player.isCreative())
            ChatUtils.warning("You must be in creative mode."); 
          if (args.length > 1)
            ChatUtils.warning("Too many arguments."); 
          if (!stack.hasTagCompound()) {
            ChatUtils.error("Item has no NBT data.");
            return;
          } 
          stack.setTagCompound(null);
          updateSlot(36 + mc.player.inventory.currentItem, stack);
          ChatUtils.message("Cleared item's NBT data.");
          break;
        case "copy":
          if (args.length > 1)
            ChatUtils.warning("Too many arguments."); 
          if (!stack.hasTagCompound()) {
            ChatUtils.error("Item has no NBT data.");
            return;
          } 
          assert stack.getTagCompound() != null;
          selection = new StringSelection(stack.getTagCompound().toString());
          clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
          clipboard.setContents(selection, selection);
          ChatUtils.message("Copied item's NBT data to clipboard.");
          break;
      } 
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public static void updateSlot(int slot, ItemStack stack) {
    ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketCreativeInventoryAction(slot, stack));
  }
  
  public String getDescription() {
    return "Modifies held item's NBT data.";
  }
  
  public String getSyntax() {
    return "nbt <add <dataTag>|set <dataTag>|remove <tagName>|clear|copy>";
  }
}
