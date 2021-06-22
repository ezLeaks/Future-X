package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import net.minecraft.item.ItemStack;

public class StackSize extends Command {
  public StackSize() {
    super("StackSize");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (!mc.player.isCreative()) {
        ChatUtils.error("Creative mode is required to use this command.");
        return;
      } 
      ItemStack itemStack = mc.player.getHeldItemMainhand();
      if (itemStack.isEmpty()) {
        ChatUtils.error("Please hold an item in your main hand to enchant.");
        return;
      } 
      String id = args[0];
      int num = Integer.parseInt(id);
      itemStack.setCount(num);
      assert itemStack.getTagCompound() != null;
      itemStack.getItem().updateItemStackNBT(itemStack.getTagCompound());
      ChatUtils.error("Set your stack size to " + num);
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "StackSize";
  }
  
  public String getSyntax() {
    return "StackSize ";
  }
}
