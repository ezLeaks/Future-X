package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Anchor extends Module {
  Setting maxheight = Main.setmgr.add(this.maxheight = new Setting("max height", this, 15.0D, 0.0D, 255.0D, false));
  
  Setting JumpOut = Main.setmgr.add(this.JumpOut = new Setting("JumpOut", this, true));
  
  Setting Onerun = Main.setmgr.add(this.Onerun = new Setting("Run Once", this, true));
  
  BlockPos playerPos;
  
  private final TimerUtils timer = new TimerUtils();
  
  private boolean WasJump = false;
  
  public Anchor() {
    super("Anchor", 0, Category.COMBAT, "Anchor to Holes");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.WasJump) {
      if (this.timer.isDelay(1800L)) {
        this.timer.setLastMS();
        this.WasJump = false;
      } 
      return;
    } 
    if (mc.player.posY < 0.0D)
      return; 
    if (mc.player.posY > this.maxheight.getValDouble())
      return; 
    double newX = mc.player.posX;
    double newZ = mc.player.posZ;
    newX = (mc.player.posX > Math.round(mc.player.posX)) ? (Math.round(mc.player.posX) + 0.5D) : newX;
    newX = (mc.player.posX < Math.round(mc.player.posX)) ? (Math.round(mc.player.posX) - 0.5D) : newX;
    newZ = (mc.player.posZ > Math.round(mc.player.posZ)) ? (Math.round(mc.player.posZ) + 0.5D) : newZ;
    newZ = (mc.player.posZ < Math.round(mc.player.posZ)) ? (Math.round(mc.player.posZ) - 0.5D) : newZ;
    this.playerPos = new BlockPos(newX, mc.player.posY, newZ);
    if (mc.world.getBlockState(this.playerPos).getBlock() != Blocks.AIR)
      return; 
    if (this.JumpOut.getValBoolean() && 
      mc.gameSettings.keyBindJump.isPressed() && 
      mc.world.getBlockState(this.playerPos.east()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.west()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.north()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.south()).getBlock() != Blocks.AIR) {
      this.WasJump = true;
      return;
    } 
    if (mc.world.getBlockState(this.playerPos.down()).getBlock() == Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down().east()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down().west()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down().north()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down().south()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down(2)).getBlock() != Blocks.AIR) {
      double lMotionX = Math.floor(mc.player.posX) + 0.5D - mc.player.posX;
      double lMotionZ = Math.floor(mc.player.posZ) + 0.5D - mc.player.posZ;
      mc.player.motionX = lMotionX / 2.0D;
      mc.player.motionZ = lMotionZ / 2.0D;
    } else if (mc.world.getBlockState(this.playerPos.down()).getBlock() == Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down(2)).getBlock() == Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down(2).east()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down(2).west()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down(2).north()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down(2).south()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(this.playerPos.down(3)).getBlock() != Blocks.AIR) {
      double lMotionX = Math.floor(mc.player.posX) + 0.5D - mc.player.posX;
      double lMotionZ = Math.floor(mc.player.posZ) + 0.5D - mc.player.posZ;
      mc.player.motionX = lMotionX / 2.0D;
      mc.player.motionZ = lMotionZ / 2.0D;
    } 
    if (this.Onerun.getValBoolean())
      toggle(); 
    super.onClientTick(event);
  }
}
