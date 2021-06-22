package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class SchematicaNCP extends Module {
  private final TimerUtils timer;
  
  Setting KeepRotation;
  
  public float[] Rots;
  
  public SchematicaNCP() {
    super("PrinterBypass", 0, Category.PLAYER, "PrinterBypass");
    this.timer = new TimerUtils();
    this.KeepRotation = Main.setmgr.add(new Setting("Keep Rotation", this, true));
  }
  
  public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    this.timer.setLastMS();
    float[] array = Utils.getNeededRotations(event.getHitVec(), 0.0F, 0.0F);
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(array[0], array[1], mc.player.onGround));
    this.Rots = array;
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && 
      this.KeepRotation.getValBoolean() && this.Rots != null && (
      packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation) && 
      !this.timer.isDelay(4000L)) {
      CPacketPlayer packet2 = (CPacketPlayer)packet;
      packet2.yaw = this.Rots[0];
      packet2.pitch = this.Rots[1];
    } 
    return true;
  }
}
