package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FastLadder extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Simple", new String[] { "Simple", "DOWN", "Skip" }));
  
  public FastLadder() {
    super("FastLadder", 0, Category.PLAYER, "Climb Faster");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (!mc.player.isOnLadder() || (mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F))
      return; 
    if (this.mode.getValString().equalsIgnoreCase("Skip") && 
      mc.player.isOnLadder()) {
      mc.player.setSprinting(true);
      mc.player.onGround = true;
    } 
    if (this.mode.getValString().equalsIgnoreCase("DOWN"))
      while (mc.player.isOnLadder() && mc.player.motionY != 0.0D) {
        mc.player.setPosition(mc.player.posX, mc.player.posY + ((mc.player.motionY > 0.0D) ? true : -1), mc.player.posZ);
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
      }  
    if (this.mode.getValString().equalsIgnoreCase("simple"))
      mc.player.motionY = 0.16999999433755875D; 
    super.onClientTick(event);
  }
}
