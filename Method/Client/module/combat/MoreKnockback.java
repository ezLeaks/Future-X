package Method.Client.module.combat;

import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;

public class MoreKnockback extends Module {
  public MoreKnockback() {
    super("MoreKnockback", 0, Category.COMBAT, "More Knockback");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (mc.player.onGround && 
      side == Connection.Side.OUT && 
      packet instanceof CPacketUseEntity) {
      CPacketUseEntity attack = (CPacketUseEntity)packet;
      if (attack.getAction() == CPacketUseEntity.Action.ATTACK) {
        Entity entity = mc.world.getEntityByID(attack.entityId);
        if (entity != mc.player && entity != null && 
          entity.getDistance((Entity)mc.player) < 4.0F) {
          boolean oldSprint = mc.player.isSprinting();
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SPRINTING));
          mc.player.setSprinting(oldSprint);
        } 
      } 
    } 
    return true;
  }
}
