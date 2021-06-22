package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;

public class AntiHurt extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Packet", new String[] { "Death", "Packet" }));
  
  public AntiHurt() {
    super("AntiHurt", 0, Category.MISC, "Anti Hurt on some instance");
  }
  
  public void onEnable() {
    if (this.mode.getValString().equalsIgnoreCase("Death") && 
      mc.player != null) {
      mc.player.isDead = true;
      mc.gameSettings.keyBindForward.isPressed();
    } 
  }
  
  public void onDisable() {
    mc.player.isDead = false;
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.mode.getValString().equalsIgnoreCase("Packet"))
      return !(packet instanceof net.minecraft.network.play.server.SPacketUpdateHealth); 
    return true;
  }
}
