package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FastFall extends Module {
  Setting speed = Main.setmgr.add(new Setting("Speed", this, 0.1D, 0.1D, 4.0D, false));
  
  Setting timer = Main.setmgr.add(new Setting("timer", this, false));
  
  public FastFall() {
    super("FastFall", 0, Category.MOVEMENT, "Fast Fall");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.timer.getValBoolean() && mc.player.onGround)
      setTickLength(50.0F); 
    if (mc.player.isElytraFlying() || mc.player.capabilities.isFlying)
      return; 
    boolean b = (!mc.world.isAirBlock(mc.player.getPosition().add(0, -1, 0)) || !mc.world.isAirBlock(mc.player.getPosition().add(0, -2, 0)));
    if (!mc.player.onGround && !b)
      if (this.timer.getValBoolean() && !mc.player.onGround) {
        setTickLength((float)(50.0D / this.speed.getValDouble()));
      } else {
        mc.player.motionY = -this.speed.getValDouble();
      }  
  }
  
  private void setTickLength(float tickLength) {
    mc.timer.tickLength = 1.0F * tickLength;
  }
  
  public void onDisable() {
    setTickLength(50.0F);
  }
}
