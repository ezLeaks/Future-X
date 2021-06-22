package Method.Client.module.misc;

import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FastSleep extends Module {
  public FastSleep() {
    super("FastSleep", 0, Category.MISC, "Fast Sleep");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    EntityPlayerSP player = mc.player;
    if (player.isPlayerSleeping() && 
      player.getSleepTimer() > 10)
      player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.STOP_SLEEPING)); 
  }
}
