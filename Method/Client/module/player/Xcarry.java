package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Xcarry extends Module {
  Setting Packetclose;
  
  public Xcarry() {
    super("Xcarry", 0, Category.PLAYER, "Xcarry or SecretClose!");
    this.Packetclose = Main.setmgr.add(new Setting("Fake close", this, false));
  }
  
  public void onDisable() {
    super.onDisable();
    if (mc.world != null)
      mc.player.connection.sendPacket((Packet)new CPacketCloseWindow(mc.player.inventoryContainer.windowId)); 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.Packetclose.getValBoolean() && packet instanceof CPacketEntityAction) {
      CPacketEntityAction pac = (CPacketEntityAction)packet;
      if (pac.getAction().equals(CPacketEntityAction.Action.OPEN_INVENTORY))
        return false; 
    } 
    return !(packet instanceof CPacketCloseWindow);
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory)
      mc.playerController.updateController(); 
  }
}
