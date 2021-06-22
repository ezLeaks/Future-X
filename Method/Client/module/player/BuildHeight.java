package Method.Client.module.player;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;

public class BuildHeight extends Module {
  public BuildHeight() {
    super("BuildHeight", 0, Category.PLAYER, "Interact at Build Height");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && 
      packet instanceof CPacketPlayerTryUseItemOnBlock) {
      CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)packet;
      if (packet2.getPos().getY() >= 255 && packet2.getDirection() == EnumFacing.UP)
        packet2.placedBlockDirection = EnumFacing.DOWN; 
    } 
    return true;
  }
}
