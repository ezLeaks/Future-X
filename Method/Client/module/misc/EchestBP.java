package Method.Client.module.misc;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class EchestBP extends Module {
  private GuiScreen echestScreen;
  
  boolean EchestSet;
  
  boolean Tryrightclick;
  
  public EchestBP() {
    super("EchestBP", 0, Category.MISC, "EchestBP");
    this.echestScreen = null;
    this.EchestSet = false;
    this.Tryrightclick = false;
  }
  
  public void onEnable() {
    this.EchestSet = false;
    this.Tryrightclick = false;
    ChatUtils.message(ChatFormatting.AQUA + " Open an Echest to start!");
  }
  
  public void onDisable() {
    if (this.echestScreen != null)
      mc.displayGuiScreen(this.echestScreen); 
    this.echestScreen = null;
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof CPacketEntityAction) {
      CPacketEntityAction pac = (CPacketEntityAction)packet;
      if (pac.getAction().equals(CPacketEntityAction.Action.OPEN_INVENTORY))
        return false; 
    } 
    return !(packet instanceof net.minecraft.network.play.client.CPacketCloseWindow);
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory)
      mc.playerController.updateController(); 
    if (mc.currentScreen instanceof GuiContainer) {
      if (((GuiContainer)mc.currentScreen).inventorySlots instanceof ContainerChest) {
        Container inventorySlots = ((GuiContainer)mc.currentScreen).inventorySlots;
        if (((ContainerChest)inventorySlots).getLowerChestInventory() instanceof net.minecraft.inventory.InventoryBasic && (
          (ContainerChest)inventorySlots).getLowerChestInventory().getName().equalsIgnoreCase("Ender Chest"))
          if (!this.EchestSet) {
            this.EchestSet = true;
            mc.player.closeScreen();
          } else {
            this.echestScreen = mc.currentScreen;
            mc.currentScreen = null;
            ChatUtils.message(ChatFormatting.AQUA + "Done! To open please disable EchestBP");
            Mouse.setGrabbed(true);
          }  
      } 
    } else if (this.EchestSet && !this.Tryrightclick) {
      this.Tryrightclick = true;
      mc.rightClickMouse();
    } 
  }
}
