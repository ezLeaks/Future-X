package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import java.util.Objects;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetSlot;

public class NoServerChange extends Module {
  Setting Inventory = Main.setmgr.add(new Setting("Held Item Change", this, false));
  
  Setting Rotate = Main.setmgr.add(new Setting("Rotate", this, true));
  
  public NoServerChange() {
    super("NoServerChange", 0, Category.PLAYER, "NoServerChange");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && packet instanceof SPacketSetSlot && 
      this.Inventory.getValBoolean()) {
      int currentSlot = mc.player.inventory.currentItem;
      SPacketSetSlot packet2 = (SPacketSetSlot)packet;
      if (packet2.getSlot() != currentSlot) {
        ((NetworkManager)Objects.<NetworkManager>requireNonNull(mc.networkManager)).sendPacket((Packet)new CPacketHeldItemChange(currentSlot));
        MC.gameSettings.keyBindUseItem.pressed = true;
        return false;
      } 
    } 
    if (packet instanceof SPacketPlayerPosLook && 
      this.Rotate.getValBoolean()) {
      SPacketPlayerPosLook packet3 = (SPacketPlayerPosLook)packet;
      if (mc.player != null) {
        packet3.yaw = mc.player.rotationYaw;
        packet3.pitch = mc.player.rotationPitch;
      } 
    } 
    return true;
  }
}
