package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoSwim extends Module {
  Setting mode = Main.setmgr.add(this.mode = new Setting("Mode", this, "Dolphin", new String[] { "Dolphin", "Jump", "Fish" }));
  
  public AutoSwim() {
    super("Auto Swim", 0, Category.MOVEMENT, "Swims for you");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (!mc.player.isInWater() && !mc.player.isInLava())
      return; 
    if (mc.player.isSneaking() || (Wrapper.INSTANCE.mcSettings()).keyBindJump.isKeyDown())
      return; 
    if (this.mode.getValString().equalsIgnoreCase("Jump")) {
      mc.player.jump();
    } else if (this.mode.getValString().equalsIgnoreCase("Dolphin")) {
      mc.player.motionY += 0.03999999910593033D;
    } else if (this.mode.getValString().equalsIgnoreCase("Fish")) {
      mc.player.motionY += 0.019999999552965164D;
    } 
    super.onClientTick(event);
  }
}
