package Method.Client.module.command;

import Method.Client.module.misc.GuiPeek;
import Method.Client.module.misc.Shulkerspy;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class Peek extends Command {
  public Peek() {
    super("Peek");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (args[0] != null) {
        String name = args[0].toLowerCase();
        if (!Shulkerspy.shulkerMap.containsKey(name.toLowerCase())) {
          ChatUtils.error("have not seen this player hold a shulkerbox. Check your spelling.");
          return;
        } 
        IInventory inv = (IInventory)Shulkerspy.shulkerMap.get(name.toLowerCase());
        (new Thread(() -> {
              try {
                Thread.sleep(100L);
              } catch (InterruptedException interruptedException) {}
              mc.player.displayGUIChest(inv);
            })).start();
      } else {
        if (!(mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemShulkerBox))
          ChatUtils.error("You Have to hold a shulker box"); 
        ItemStack itemStack = mc.player.getHeldItemMainhand();
        if (itemStack.getItem() instanceof net.minecraft.item.ItemShulkerBox) {
          ChatUtils.message("Opening your shulker box.");
          GuiPeek.Peekcode(itemStack, mc);
        } 
      } 
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public String getDescription() {
    return "Peek into shukler!";
  }
  
  public String getSyntax() {
    return "Peek <Name>";
  }
}
