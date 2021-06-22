package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Sprint extends Module {
  Setting Backwards;
  
  Setting Foodcheck;
  
  Setting ObstacleCheck;
  
  Setting InstarunTimer;
  
  Setting Fastspeedup;
  
  double quicktimerrun;
  
  boolean startedquickrun;
  
  public Sprint() {
    super("Sprint", 0, Category.MOVEMENT, "Always be running");
    this.Backwards = Main.setmgr.add(new Setting("Backwards", this, false));
    this.Foodcheck = Main.setmgr.add(new Setting("Food check", this, true));
    this.ObstacleCheck = Main.setmgr.add(new Setting("Obstical Check", this, true));
    this.InstarunTimer = Main.setmgr.add(new Setting("InstarunTimer", this, false, this.ObstacleCheck, 3));
    this.Fastspeedup = Main.setmgr.add(new Setting("Fastspeedup", this, 10.0D, 2.0D, 40.0D, true, this.ObstacleCheck, 4));
    this.quicktimerrun = 10.0D;
    this.startedquickrun = false;
  }
  
  private void setTickLength(float tickLength) {
    mc.timer.tickLength = 1.0F * tickLength;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.player.getFoodStats().getFoodLevel() > 6 || !this.Foodcheck.getValBoolean()) {
      if (this.ObstacleCheck.getValBoolean()) {
        if (canSprint())
          mc.player.setSprinting(true); 
      } else {
        mc.player.setSprinting(true);
      } 
      if (mc.player.isSprinting()) {
        if (this.InstarunTimer.getValBoolean() && this.quicktimerrun > 0.0D) {
          this.startedquickrun = true;
          setTickLength(33.333F);
          this.quicktimerrun--;
          if (this.quicktimerrun < 1.0D)
            setTickLength(50.0F); 
        } 
      } else {
        if (this.startedquickrun) {
          this.startedquickrun = false;
          setTickLength(50.0F);
        } 
        this.quicktimerrun = this.Fastspeedup.getValDouble();
      } 
      if (this.Backwards.getValBoolean() && 
        !mc.player.isElytraFlying() && Wrapper.mc.gameSettings.keyBindBack.isKeyDown()) {
        if (mc.player.moveForward > 0.0F && !mc.player.collidedHorizontally)
          mc.player.setSprinting(true); 
        if (mc.player.onGround) {
          mc.player.motionX *= 1.092D;
          mc.player.motionZ *= 1.092D;
        } 
        double sqrt = Math.sqrt(Math.pow(mc.player.motionX, 2.0D) + Math.pow(mc.player.motionZ, 2.0D));
        double n = 0.6500000262260437D;
        if (sqrt > 0.6500000262260437D) {
          mc.player.motionX = mc.player.motionX / sqrt * 0.6500000262260437D;
          mc.player.motionZ = mc.player.motionZ / sqrt * 0.6500000262260437D;
        } 
      } 
    } 
    super.onClientTick(event);
  }
  
  boolean canSprint() {
    if (!mc.player.onGround)
      return false; 
    if (mc.player.isSprinting())
      return false; 
    if (mc.player.isOnLadder())
      return false; 
    if (mc.player.isInWater())
      return false; 
    if (mc.player.isInLava())
      return false; 
    if (mc.player.collidedHorizontally)
      return false; 
    if (mc.player.moveForward < 0.1F)
      return false; 
    if (mc.player.isSneaking())
      return false; 
    if (mc.player.getFoodStats().getFoodLevel() < 6)
      return false; 
    if (mc.player.isRiding())
      return false; 
    return !mc.player.isPotionActive(MobEffects.BLINDNESS);
  }
}
