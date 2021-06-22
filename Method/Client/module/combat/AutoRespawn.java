package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoRespawn extends Module {
  Setting DeathCoords = Main.setmgr.add(new Setting("DeathCoords", this, true));
  
  Setting Delay = Main.setmgr.add(new Setting("Delay", this, 2.0D, 0.0D, 50.0D, true));
  
  private TimerUtils timer = new TimerUtils();
  
  boolean canrespawn = false;
  
  public AutoRespawn() {
    super("AutoRespawn", 0, Category.COMBAT, "AutoRespawn");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (Wrapper.mc.currentScreen instanceof net.minecraft.client.gui.GuiGameOver) {
      if (!this.canrespawn) {
        this.timer.reset();
        this.canrespawn = true;
      } 
      if (this.timer.hasReached((float)(this.Delay.getValDouble() * 1000.0D))) {
        this.timer.reset();
        mc.player.respawnPlayer();
        Wrapper.mc.displayGuiScreen(null);
        if (this.DeathCoords.getValBoolean())
          ChatUtils.message(String.format("you have died at x %d y %d z %d", new Object[] { Integer.valueOf((int)mc.player.posX), Integer.valueOf((int)mc.player.posY), Integer.valueOf((int)mc.player.posZ) })); 
        this.canrespawn = false;
      } 
    } 
  }
}
