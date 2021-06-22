package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.utils.TimerUtils;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ChestStealer extends Module {
  private final TimerUtils Timer = new TimerUtils();
  
  Setting delay;
  
  Setting Entity;
  
  Setting Shulker;
  
  public static Setting Mode;
  
  public ChestStealer() {
    super("ChestStealer", 0, Category.PLAYER, "ChestStealer");
  }
  
  public void setup() {
    Main.setmgr.add(Mode = new Setting("Mode", this, "Steal", new String[] { "Steal", "Store", "Drop" }));
    Main.setmgr.add(this.delay = new Setting("Delay", this, 3.0D, 0.0D, 20.0D, true));
    Main.setmgr.add(this.Shulker = new Setting("Take Shulker", this, false));
    Main.setmgr.add(this.Entity = new Setting("Entitys Chest", this, true));
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (!this.Timer.isDelay((long)(this.delay.getValDouble() * 100.0D)))
      return; 
    this.Timer.setLastMS();
    if (mc.currentScreen instanceof GuiChest) {
      GuiChest guiChest = (GuiChest)mc.currentScreen;
      Quickhandle((GuiContainer)guiChest, guiChest.lowerChestInventory.getSizeInventory());
    } else if (mc.currentScreen instanceof GuiScreenHorseInventory && this.Entity.getValBoolean()) {
      GuiScreenHorseInventory horseInventory = (GuiScreenHorseInventory)mc.currentScreen;
      Quickhandle((GuiContainer)horseInventory, horseInventory.horseInventory.getSizeInventory());
    } else if (mc.currentScreen instanceof GuiShulkerBox && this.Shulker.getValBoolean()) {
      GuiShulkerBox shulkerBox = (GuiShulkerBox)mc.currentScreen;
      Quickhandle((GuiContainer)mc.currentScreen, shulkerBox.inventory.getSizeInventory());
    } else {
      ModuleManager.getModuleByName("ChestStealer").toggle();
    } 
  }
  
  private void Quickhandle(GuiContainer guiContainer, int size) {
    for (int i = 0; i < size; i++) {
      ItemStack stack = (ItemStack)guiContainer.inventorySlots.getInventory().get(i);
      if ((stack.isEmpty() || stack.getItem() == Items.AIR) && Mode.getValString().equalsIgnoreCase("Store")) {
        HandleStoring(guiContainer.inventorySlots.windowId, size - 9);
        return;
      } 
      if (StealorDrop(guiContainer.inventorySlots.windowId, i, stack))
        break; 
    } 
  }
  
  private void HandleStoring(int pWindowId, int stack) {
    for (int i = 9; i < mc.player.inventoryContainer.inventorySlots.size() - 1; i++) {
      ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
      if (!itemStack.isEmpty() && itemStack.getItem() != Items.AIR)
        if (!this.Shulker.getValBoolean() || itemStack.getItem() instanceof net.minecraft.item.ItemShulkerBox) {
          mc.playerController.windowClick(pWindowId, i + stack, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
          return;
        }  
    } 
  }
  
  private boolean StealorDrop(int windowId, int i, ItemStack stack) {
    if (stack.isEmpty() || (this.Shulker.getValBoolean() && !(stack.getItem() instanceof net.minecraft.item.ItemShulkerBox)))
      return false; 
    if (Mode.getValString().equalsIgnoreCase("Steal")) {
      mc.playerController.windowClick(windowId, i, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
      return true;
    } 
    if (Mode.getValString().equalsIgnoreCase("Drop")) {
      mc.playerController.windowClick(windowId, i, 1, ClickType.THROW, (EntityPlayer)mc.player);
      return true;
    } 
    return false;
  }
}
