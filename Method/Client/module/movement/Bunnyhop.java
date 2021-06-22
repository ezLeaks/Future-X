package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Bunnyhop extends Module {
  private int airMoves;
  
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "AAC", new String[] { "AAC", "NCP", "Timer", "Spartan" }));
  
  public Bunnyhop() {
    super("Bunnyhop", 0, Category.MOVEMENT, "Bunny hop");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Timer"))
      if (mc.player.onGround) {
        if (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F)
          mc.player.jump(); 
        mc.player.motionZ /= 2.0D;
        mc.player.motionX /= 2.0D;
        mc.player.motionY += 0.05000000074505806D;
      } else {
        mc.player.motionY -= 0.029999999329447746D;
        mc.player.moveStrafing *= 0.07F;
        mc.player.jumpMovementFactor = 0.05F;
      }  
    if (this.mode.getValString().equalsIgnoreCase("Spartan") && 
      mc.gameSettings.keyBindForward.isPressed() && !mc.gameSettings.keyBindJump.isPressed())
      if (mc.player.onGround) {
        mc.player.jump();
        this.airMoves = 0;
      } else {
        if (this.airMoves >= 3)
          mc.player.jumpMovementFactor = 0.0275F; 
        if (this.airMoves >= 4 && (this.airMoves % 2) == 0.0D) {
          mc.player.motionY = -0.3199999928474426D - 0.009D * Math.random();
          mc.player.jumpMovementFactor = 0.0238F;
        } 
        this.airMoves++;
      }  
    if (this.mode.getValString().equalsIgnoreCase("AAC")) {
      if (mc.player.isInWater())
        return; 
      if (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) {
        if (mc.player.onGround) {
          mc.player.jump();
          mc.player.motionX *= 1.012D;
          mc.player.motionZ *= 1.012D;
        } else if (mc.player.motionY > -0.2D) {
          mc.player.jumpMovementFactor = 0.08F;
          mc.player.motionY += 3.1E-4D;
          mc.player.jumpMovementFactor = 0.07F;
        } 
      } else {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("NCP") && 
      mc.player != null && mc.world != null && 
      mc.gameSettings.keyBindForward.pressed && !mc.player.collidedHorizontally) {
      mc.gameSettings.keyBindJump.pressed = false;
      if (mc.player.onGround) {
        mc.player.jump();
        mc.player.motionX *= 1.0707999467849731D;
        mc.player.motionZ *= 1.0707999467849731D;
        mc.player.moveStrafing *= 2.0F;
      } else {
        mc.player.jumpMovementFactor = 0.0265F;
      } 
    } 
  }
  
  public void onDisable() {
    mc.player.jumpMovementFactor = 0.03F;
    super.onDisable();
  }
}
