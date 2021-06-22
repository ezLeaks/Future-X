package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.item.ItemStack;

public class Rename extends Command {
  public Rename() {
    super("Rename");
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
      StringBuilder name = new StringBuilder(args[0]);
      for (int i = 1; i < args.length; i++)
        name.append(" ").append(args[i]); 
      name = new StringBuilder(name.toString().replace('&', '§').replace("§§", "&"));
      if (!mc.player.isCreative())
        ChatUtils.warning("You must be in creative mode!"); 
      stack.setStackDisplayName(name.toString());
      Nbt.updateSlot(36 + mc.player.inventory.currentItem, stack);
      ChatUtils.message("Item's name changed to §7" + name + "§e.");
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Rename item!";
  }
  
  public String getSyntax() {
    return "Rename <Name>";
  }
}
