package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;

public class Author extends Command {
  public Author() {
    super("Author");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (!mc.player.capabilities.isCreativeMode)
        ChatUtils.error("Creative mode only."); 
      ItemStack heldItem = mc.player.inventory.getCurrentItem();
      int heldItemID = Item.getIdFromItem(heldItem.getItem());
      int writtenBookID = Item.getIdFromItem(Items.WRITTEN_BOOK);
      if (heldItemID != writtenBookID) {
        ChatUtils.error("You must hold a written book in your main hand.");
      } else {
        heldItem.setTagInfo("author", (NBTBase)new NBTTagString(args[0]));
        ChatUtils.message("Author Changed! Open Inventory.");
      } 
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Change BookSign Author Creative Only";
  }
  
  public String getSyntax() {
    return "Author <author> ";
  }
}
