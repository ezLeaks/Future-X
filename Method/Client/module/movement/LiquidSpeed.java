package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LiquidSpeed extends Module {
  Setting waterSpeed = Main.setmgr.add(new Setting("waterSpeed", this, 1.0D, 0.9D, 1.1D, false));
  
  Setting lavaSpeed = Main.setmgr.add(new Setting("lavaSpeed", this, 1.0D, 0.9D, 1.1D, false));
  
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Vanilla", new String[] { "Vanilla", "Bypass" }));
  
  private final TimerUtils timer = new TimerUtils();
  
  public LiquidSpeed() {
    super("LiquidSpeed", 0, Category.MOVEMENT, "Liquid Speed");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Bypass") && 
      mc.player.isInWater() && this.timer.isDelay(940L)) {
      mc.player.motionX *= 1.005D;
      mc.player.motionZ *= 1.005D;
      mc.player.motionY = 0.4D;
      this.timer.setLastMS();
    } 
    if (this.mode.getValString().equalsIgnoreCase("Vanilla")) {
      BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY + 0.4D, mc.player.posZ);
      if (mc.world.getBlockState(blockPos).getBlock() == Blocks.LAVA) {
        Speed(this.lavaSpeed);
        if (mc.gameSettings.keyBindJump.isKeyDown())
          mc.player.motionY = 0.06D; 
        if (mc.gameSettings.keyBindSneak.isKeyDown())
          mc.player.motionY = -0.14D; 
      } 
      if (mc.player.isInWater()) {
        Speed(this.waterSpeed);
        if (mc.gameSettings.keyBindJump.isKeyDown())
          mc.player.motionY *= this.waterSpeed.getValDouble() / 1.2D; 
      } 
    } 
  }
  
  private void Speed(Setting waterSpeed) {
    if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
      mc.player.motionX *= waterSpeed.getValDouble();
      mc.player.motionZ *= waterSpeed.getValDouble();
    } 
  }
}
