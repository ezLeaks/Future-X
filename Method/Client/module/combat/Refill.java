package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Refill extends Module {
  Setting delay;
  
  Setting percentage;
  
  Setting offHand;
  
  private final TimerUtils timer;
  
  public Refill() {
    super("Refill", 0, Category.COMBAT, "Refill");
    this.delay = Main.setmgr.add(new Setting("delay", this, 5.0D, 0.0D, 10.0D, true));
    this.percentage = Main.setmgr.add(new Setting("percentage", this, 50.0D, 0.0D, 100.0D, false));
    this.offHand = Main.setmgr.add(new Setting("offHand", this, true));
    this.timer = new TimerUtils();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.timer.isDelay((long)(this.delay.getValDouble() * 1000.0D)) && 
      mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory)
      return; 
    int toRefill = getRefillable(mc.player);
    if (toRefill != -1)
      refillHotbarSlot(toRefill); 
  }
  
  private int getRefillable(EntityPlayerSP player) {
    if (this.offHand.getValBoolean() && 
      player.getHeldItemOffhand().getItem() != Items.AIR && player
      .getHeldItemOffhand().getCount() < player.getHeldItemOffhand().getMaxStackSize() && player
      .getHeldItemOffhand().getCount() / player.getHeldItemOffhand().getMaxStackSize() <= this.percentage.getValDouble() / 100.0D)
      return 45; 
    for (int i = 0; i < 9; i++) {
      ItemStack stack = (ItemStack)player.inventory.mainInventory.get(i);
      if (stack.getItem() != Items.AIR && stack.getCount() < stack.getMaxStackSize() && stack
        .getCount() / stack.getMaxStackSize() <= this.percentage.getValDouble() / 100.0D)
        return i; 
    } 
    return -1;
  }
  
  private int getSmallestStack(EntityPlayerSP player, ItemStack itemStack) {
    if (itemStack == null)
      return -1; 
    int minCount = itemStack.getMaxStackSize() + 1;
    int minIndex = -1;
    for (int i = 9; i < player.inventory.mainInventory.size(); i++) {
      ItemStack stack = (ItemStack)player.inventory.mainInventory.get(i);
      if (stack.getItem() != Items.AIR && stack
        .getItem() == itemStack.getItem() && stack
        .getCount() < minCount) {
        minCount = stack.getCount();
        minIndex = i;
      } 
    } 
    return minIndex;
  }
  
  public void refillHotbarSlot(int slot) {
    ItemStack stack;
    if (slot == 45) {
      stack = mc.player.getHeldItemOffhand();
    } else {
      stack = (ItemStack)mc.player.inventory.mainInventory.get(slot);
    } 
    if (stack.getItem() == Items.AIR)
      return; 
    int biggestStack = getSmallestStack(mc.player, stack);
    if (biggestStack == -1)
      return; 
    if (slot == 45) {
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      return;
    } 
    int overflow = -1;
    for (int i = 0; i < 9 && overflow == -1; i++) {
      if (((ItemStack)mc.player.inventory.mainInventory.get(i)).getItem() == Items.AIR)
        overflow = i; 
    } 
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
    if (overflow != -1 && ((ItemStack)mc.player.inventory.mainInventory.get(overflow)).getItem() != Items.AIR)
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, biggestStack, overflow, ClickType.SWAP, (EntityPlayer)mc.player); 
  }
}
