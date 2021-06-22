package Method.Client.utils.Screens.Override;

import Method.Client.module.ModuleManager;
import Method.Client.module.misc.Ghost;
import Method.Client.utils.Screens.Screen;
import net.minecraftforge.client.event.GuiOpenEvent;

public class DeathOverride extends Screen {
  public static boolean isDead;
  
  public static boolean Override = false;
  
  public void GuiOpen(GuiOpenEvent event) {
    boolean host = ModuleManager.getModuleByName("Ghost").isToggled();
    if (host && 
      event.getGui() instanceof net.minecraft.client.gui.GuiGameOver) {
      event.setGui(null);
      isDead = true;
      mc.player.setHealth((float)Ghost.health.getValDouble() / 2.0F);
    } 
  }
}
