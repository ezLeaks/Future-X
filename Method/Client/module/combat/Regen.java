package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Regen extends Module {
  Setting mode;
  
  Setting packets;
  
  public Regen() {
    super("Regen", 0, Category.COMBAT, "Regen");
    this.mode = Main.setmgr.add(new Setting("Regen Mode", this, "Vanilla", new String[] { "Vanilla", "Packet" }));
    this.packets = Main.setmgr.add(new Setting("packets", this, 20.0D, 20.0D, 200.0D, false, this.mode, "Packet", 2));
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Packet") && 
      mc.player.getHealth() < mc.player.getMaxHealth() && mc.player
      .getFoodStats().getFoodLevel() > 1)
      for (int i = 0; i < this.packets.getValDouble(); i++)
        mc.player.connection.sendPacket((Packet)new CPacketPlayer());  
    if (this.mode.getValString().equalsIgnoreCase("Vanilla") && 
      mc.player.getHealth() < 20.0F) {
      mc.timer.tickLength = 0.8F;
      mc.player.setHealth(20.0F);
    } 
    super.onClientTick(event);
  }
  
  public void onDisable() {
    if (this.mode.getValString().equalsIgnoreCase("Vanilla"))
      mc.timer.tickLength = 1.0F; 
  }
}
