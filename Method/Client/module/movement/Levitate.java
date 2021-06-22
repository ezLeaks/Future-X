package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Levitate extends Module {
  Setting mode = Main.setmgr.add(new Setting("Fly Mode", this, "Normal", new String[] { "Normal", "Weird", "Old", "MoonWalk" }));
  
  private double startY;
  
  int counter;
  
  public Levitate() {
    super("Levitate", 0, Category.MOVEMENT, "Levitate");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Moonwalk"))
      if (mc.player.onGround && Wrapper.mc.gameSettings.keyBindJump.isPressed()) {
        mc.player.motionY = 0.25D;
      } else if (mc.player.isAirBorne && !mc.player.isInWater() && !mc.player.isOnLadder() && 
        !mc.player.isInsideOfMaterial(Material.LAVA)) {
        mc.player.motionY = 1.0E-6D;
        EntityPlayerSP player = mc.player;
        mc.player.jumpMovementFactor *= 1.21337F;
      }  
    if (this.mode.getValString().equalsIgnoreCase("Normal")) {
      mc.player.motionY = 0.0D;
      if (mc.gameSettings.keyBindSneak.pressed)
        mc.player.motionY = -0.1D; 
      if (mc.gameSettings.keyBindJump.pressed)
        mc.player.motionY = 0.1D; 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Old") && 
      !mc.player.onGround && 
      mc.gameSettings.keyBindJump.pressed)
      mc.player.motionY = (mc.player.posY < this.startY - 1.0D) ? 0.2D : -0.05D; 
    if (this.mode.getValString().equalsIgnoreCase("Weird")) {
      this.counter++;
      if (this.counter > 3.2D) {
        mc.gameSettings.keyBindSneak.pressed = true;
        mc.player.motionX *= 1.2D;
        mc.player.attackedAtYaw = 1.0F;
        mc.player.motionZ *= 1.2D;
        this.counter = 0;
      } else {
        this.counter++;
      } 
      if (this.counter > 3.7D) {
        mc.gameSettings.keyBindSneak.pressed = false;
        this.counter = 0;
      } 
      mc.player.onGround = true;
      mc.player.motionY = 0.0D;
      mc.player.motionX *= 0.2D;
      mc.player.attackedAtYaw = 1.0F;
      mc.player.motionZ *= 0.2D;
      mc.player.resetPositionToBB();
      mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0E-9D, mc.player.posZ);
      if (mc.player.ticksExisted % 3 == 0 && mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 0.2D, mc.player.posZ)).getBlock() instanceof net.minecraft.block.BlockAir)
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY + -0.0D, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true)); 
    } 
    super.onClientTick(event);
  }
  
  public void onEnable() {
    super.onEnable();
    this.startY = mc.player.posY;
    if (this.mode.getValString().equalsIgnoreCase("Weird")) {
      mc.player.motionY = 0.42D;
      int i2 = 1;
      while (i2 < 4) {
        mc.player.maxHurtTime = 9;
        mc.player.performHurtAnimation();
        mc.player.fallDistance = 0.0F;
        i2++;
      } 
    } 
  }
}
