package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Step extends Module {
  public int ticks = 0;
  
  Setting mode = Main.setmgr.add(new Setting("STEP", this, "Vanilla", new String[] { "Vanilla", "ACC", "Packet", "FastAAC", "NCP", "Hop", "SPAM", "Step" }));
  
  Setting Height = Main.setmgr.add(new Setting("Height", this, 1.0D, 0.5D, 4.0D, true));
  
  Setting Entity = Main.setmgr.add(new Setting("Entity", this, true));
  
  Setting Timer = Main.setmgr.add(new Setting("Timer", this, true, this.mode, "Packet", 3));
  
  public Step() {
    super("Step", 0, Category.MOVEMENT, "Allows you to step up.");
  }
  
  public void onEnable() {
    this.ticks = 0;
    super.onEnable();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.Entity.getValBoolean() && 
      mc.player.getRidingEntity() != null && (mc.player.getRidingEntity()).stepHeight != (int)this.Height.getValDouble())
      (mc.player.getRidingEntity()).stepHeight = (int)this.Height.getValDouble(); 
    if (this.mode.getValString().equalsIgnoreCase("Step")) {
      if (mc.player.collidedHorizontally && mc.player.onGround)
        mc.player.jump(); 
      if (mc.player.collidedHorizontally && mc.player.onGround && mc.player.posY + 1.065D < mc.player.posY)
        mc.player.setVelocity(mc.player.motionX, -0.1D, mc.player.motionZ); 
    } 
    if (this.mode.getValString().equalsIgnoreCase("FastAAC")) {
      BlockPos pos1 = new BlockPos(mc.player.posX + 1.0D, mc.player.posY + 1.0D, mc.player.posZ);
      BlockPos pos2 = new BlockPos(mc.player.posX - 1.0D, mc.player.posY + 1.0D, mc.player.posZ);
      BlockPos pos3 = new BlockPos(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ + 1.0D);
      BlockPos pos4 = new BlockPos(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ - 1.0D);
      Block block1 = mc.world.getBlockState(pos1).getBlock();
      Block block2 = mc.world.getBlockState(pos2).getBlock();
      Block block3 = mc.world.getBlockState(pos3).getBlock();
      Block block4 = mc.world.getBlockState(pos4).getBlock();
      if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || (mc.gameSettings.keyBindBack.isKeyDown() && mc.player.collidedHorizontally && (block1 == Blocks.AIR || block2 == Blocks.AIR || block3 == Blocks.AIR || block4 == Blocks.AIR)))
        if (mc.player.onGround) {
          mc.player.jump();
          mc.player.motionY = 0.386D;
        } else {
          toFwd(0.26D);
        }  
    } 
    if (this.mode.getValString().equalsIgnoreCase("Spam"))
      Spam(); 
    if (this.mode.getValString().equalsIgnoreCase("Packet")) {
      if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown())
        return; 
      if (this.Timer.getValBoolean())
        if (this.ticks == 0) {
          mc.timer.tickLength = 50.0F;
        } else {
          this.ticks--;
        }  
      if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder())
        for (double y = 0.0D; y < this.Height.getValDouble() + 0.5D; y += 0.01D) {
          if (!mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -y, 0.0D)).isEmpty()) {
            mc.player.motionY = -10.0D;
            break;
          } 
        }  
      double[] dir = Utils.directionSpeed(0.1D);
      boolean twofive = false;
      boolean two = false;
      boolean onefive = false;
      boolean one = false;
      if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.6D, dir[1])).isEmpty() && !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.4D, dir[1])).isEmpty())
        twofive = true; 
      if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.1D, dir[1])).isEmpty() && !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.9D, dir[1])).isEmpty())
        two = true; 
      if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.6D, dir[1])).isEmpty() && !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.4D, dir[1])).isEmpty())
        onefive = true; 
      if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.0D, dir[1])).isEmpty() && !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 0.6D, dir[1])).isEmpty())
        one = true; 
      if (mc.player.collidedHorizontally && (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
        if (one && this.Height.getValDouble() >= 1.0D) {
          double[] oneOffset = { 0.42D, 0.753D };
          for (double v : oneOffset)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround)); 
          if (this.Timer.getValBoolean())
            mc.timer.tickLength = 83.33333F; 
          mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
          this.ticks = 1;
        } 
        if (onefive && this.Height.getValDouble() >= 1.5D) {
          double[] oneFiveOffset = { 0.42D, 0.75D, 1.0D, 1.16D, 1.23D, 1.2D };
          for (double v : oneFiveOffset)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround)); 
          if (this.Timer.getValBoolean())
            mc.timer.tickLength = 142.85715F; 
          mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5D, mc.player.posZ);
          this.ticks = 1;
        } 
        if (two && this.Height.getValDouble() >= 2.0D) {
          double[] twoOffset = { 0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D };
          for (double v : twoOffset)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround)); 
          if (this.Timer.getValBoolean())
            mc.timer.tickLength = 200.0F; 
          mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ);
          this.ticks = 2;
        } 
        if (twofive && this.Height.getValDouble() >= 2.5D) {
          double[] twoFiveOffset = { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D };
          for (double v : twoFiveOffset)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround)); 
          if (this.Timer.getValBoolean())
            mc.timer.tickLength = 333.3333F; 
          mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5D, mc.player.posZ);
          this.ticks = 2;
        } 
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("ACC")) {
      EntityPlayerSP player = mc.player;
      if (player.collidedHorizontally) {
        switch (this.ticks) {
          case 0:
            if (player.onGround)
              player.jump(); 
            break;
          case 7:
            player.motionY = 0.0D;
            break;
          case 8:
            if (!player.onGround)
              player.setPosition(player.posX, player.posY + 1.0D, player.posZ); 
            break;
        } 
        this.ticks++;
      } else {
        this.ticks = 0;
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("Vanilla")) {
      mc.player.stepHeight = (float)this.Height.getValDouble();
    } else if (this.mode.getValString().equalsIgnoreCase("NCP")) {
      if (mc.player.collidedHorizontally && mc.player.onGround && mc.player.collidedVertically && mc.player.collided)
        StepRun(); 
    } else if (this.mode.getValString().equalsIgnoreCase("Hop") && 
      mc.gameSettings.keyBindJump.pressed && mc.player.collidedHorizontally) {
      mc.player.setPosition(mc.player.lastTickPosX, mc.player.serverPosY + mc.player.lastTickPosY + 0.09000000357627869D, mc.player.serverPosZ + mc.player.posZ);
    } 
    super.onClientTick(event);
  }
  
  public double get_n_normal() {
    mc.player.stepHeight = 0.5F;
    double max_y = -1.0D;
    AxisAlignedBB grow = mc.player.getEntityBoundingBox().offset(0.0D, 0.05D, 0.0D).grow(0.05D);
    if (!mc.world.getCollisionBoxes((Entity)mc.player, grow.offset(0.0D, 2.0D, 0.0D)).isEmpty())
      return 100.0D; 
    for (AxisAlignedBB aabb : mc.world.getCollisionBoxes((Entity)mc.player, grow)) {
      if (aabb.maxY > max_y)
        max_y = aabb.maxY; 
    } 
    return max_y - mc.player.posY;
  }
  
  public static void toFwd(double speed) {
    float yaw = mc.player.rotationYaw * 0.017453292F;
    mc.player.motionX -= MathHelper.sin(yaw) * speed;
    mc.player.motionZ += MathHelper.cos(yaw) * speed;
  }
  
  private void Spam() {
    if (mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.movementInput.jump && !mc.player.noClip && (
      mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F)) {
      double n = get_n_normal();
      if (n < 0.0D || n > 2.0D)
        return; 
      if (n == 2.0D) {
        Sendpos(0.42D);
        Sendpos(0.78D);
        Sendpos(0.63D);
        Sendpos(0.51D);
        Sendpos(0.9D);
        Sendpos(1.21D);
        Sendpos(1.45D);
        Sendpos(1.43D);
        mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ);
      } 
      if (n == 1.5D) {
        Sendpos(0.41999998688698D);
        Sendpos(0.7531999805212D);
        Sendpos(1.00133597911214D);
        Sendpos(1.16610926093821D);
        Sendpos(1.24918707874468D);
        Sendpos(1.1707870772188D);
        mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
      } 
      if (n == 1.0D) {
        Sendpos(0.41999998688698D);
        Sendpos(0.7531999805212D);
        mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
      } 
    } 
  }
  
  private void Sendpos(double pos) {
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + pos, mc.player.posZ, mc.player.onGround));
  }
  
  private void StepRun() {
    mc.player.setSprinting(true);
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42D, mc.player.posZ, mc.player.onGround));
    mc.player.setSprinting(true);
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.753D, mc.player.posZ, mc.player.onGround));
    mc.player.setSprinting(true);
    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
    mc.player.setSprinting(true);
  }
  
  public void onDisable() {
    mc.player.stepHeight = 0.5F;
    mc.timer.tickLength = 50.0F;
    super.onDisable();
    try {
      if (mc.player.getRidingEntity() != null)
        (mc.player.getRidingEntity()).stepHeight = 1.0F; 
    } catch (Exception exception) {}
  }
}
