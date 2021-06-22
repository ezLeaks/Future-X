package Method.Client.module.misc;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketResourcePackStatus;

public class AntiHandShake extends Module {
  public AntiHandShake() {
    super("AntiHandShake", 0, Category.MISC, "No Mod List sent on login");
  }
  
  public boolean onDisablePacket(Object packet, Connection.Side side) {
    if (packet instanceof CPacketResourcePackStatus)
      ((CPacketResourcePackStatus)packet).action = CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED; 
    if (side == Connection.Side.OUT) {
      if (packet instanceof net.minecraftforge.fml.common.network.internal.FMLProxyPacket && !mc.isSingleplayer())
        return false; 
      if (packet instanceof CPacketCustomPayload && !mc.isSingleplayer()) {
        CPacketCustomPayload packet2 = (CPacketCustomPayload)packet;
        if (packet2.getChannelName().equals("MC|Brand"))
          packet2.data = (new PacketBuffer(Unpooled.buffer())).writeString("vanilla"); 
      } 
    } 
    return true;
  }
}
