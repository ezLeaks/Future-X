package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PortalMod extends Module {
  Setting gui = Main.setmgr.add(new Setting("gui", this, true));
  
  Setting god = Main.setmgr.add(new Setting("god", this, true));
  
  public PortalMod() {
    super("PortalMod", 0, Category.PLAYER, "PortalMod");
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    if (this.gui.getValBoolean())
      mc.player.inPortal = false; 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.god.getValBoolean())
      return !(packet instanceof net.minecraft.network.play.client.CPacketConfirmTeleport); 
    return true;
  }
}
