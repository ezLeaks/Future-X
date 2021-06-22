package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Fly extends Module {
  int clock = 0;
  
  private boolean aac;
  
  private double aad;
  
  Setting mode = Main.setmgr.add(new Setting("Fly Mode", this, "Vanilla", new String[] { 
          "Vanilla", "Motion", "Tp", "Servers", "NPacket", "BPacket", "CubeCraft", "Old AAC", "Rewinside", "Clicktp", 
          "AAC" }));
  
  Setting speed = Main.setmgr.add(new Setting("Speed", this, 1.0D, 0.5D, 8.0D, false, this.mode, "Vanilla", 1));
  
  Setting speed2 = Main.setmgr.add(new Setting("NSpeed", this, 1.5D, 0.5D, 5.0D, false, this.mode, "Tp", 1));
  
  Setting speed3 = Main.setmgr.add(new Setting("Speed3", this, 3.0D, 0.5D, 5.0D, false, this.mode, "Motion", 1));
  
  public Fly() {
    super("Fly", 0, Category.MOVEMENT, "Fly me to the skys");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("AAC")) {
      mc.player.setSprinting(false);
      if (mc.player.fallDistance >= 4.0F && !this.aac) {
        this.aac = true;
        this.aad = mc.player.posY + 3.0D;
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
      } 
      mc.player.capabilities.isFlying = false;
      if (this.aac) {
        if (mc.player.onGround)
          this.aac = false; 
        if (mc.player.posY < this.aad) {
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
          if (mc.gameSettings.keyBindSneak.pressed) {
            this.aad -= 2.0D;
          } else if (mc.gameSettings.keyBindSneak.pressed && mc.player.posY < this.aad + 0.8D) {
            this.aad += 2.0D;
          } else {
            mc.player.motionY = 0.7D;
            utils(0.8F);
          } 
        } 
      } else {
        mc.player.capabilities.isFlying = false;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Clicktp") && 
      mc.gameSettings.keyBindAttack.pressed) {
      mc.player.capabilities.isFlying = true;
      double yaw = mc.player.rotationYaw;
      float increment = 8.5F;
      mc.player.setPosition(mc.player.posX + Math.sin(Math.toRadians(-yaw)) * increment, mc.player.posY, mc.player.posZ + Math.cos(Math.toRadians(-yaw)) * increment);
    } 
    if (this.mode.getValString().equalsIgnoreCase("CubeCraft") && 
      mc.player != null && mc.world != null) {
      double teleportV = 1.0D;
      double x = getPosForSetPosX(teleportV);
      double z = getPosForSetPosZ(teleportV);
      mc.player.motionY = -0.25D;
      if (mc.player.fallDistance >= 0.8F) {
        mc.player.setPosition(mc.player.posX + x, mc.player.posY + mc.player.fallDistance - 0.15D, mc.player.posZ + z);
        mc.player.fallDistance = 0.0F;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Old AAC") && 
      mc.world != null && mc.player != null && 
      mc.player.fallDistance > 0.0F)
      mc.player.motionY = (mc.player.ticksExisted % 2 == 0) ? 0.1D : 0.0D; 
    if (this.mode.getValString().equalsIgnoreCase("Rewinside"))
      if (mc.player != null && mc.world != null) {
        mc.gameSettings.keyBindLeft.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;
        mc.gameSettings.keyBindBack.pressed = false;
        mc.gameSettings.keyBindJump.pressed = false;
        mc.gameSettings.keyBindSprint.pressed = false;
        mc.player.setSprinting(false);
        mc.player.motionY = 0.0D;
        mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0E-10D, mc.player.posZ);
        mc.player.onGround = true;
        if (mc.player.ticksExisted % 3 == 0)
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY - 1.0E-10D, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, true)); 
      }  
    if (this.mode.getValString().equalsIgnoreCase("Tp")) {
      EntityPlayerSP player = mc.player;
      player.capabilities.isFlying = false;
      float speedScaled = (float)(this.speed2.getValDouble() * 0.5D);
      double[] directionSpeedVanilla = Utils.directionSpeed(speedScaled);
      if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F)
        player.setPosition(player.posX + directionSpeedVanilla[0], player.posY, player.posZ + directionSpeedVanilla[1]); 
      player.motionX = 0.0D;
      player.motionZ = 0.0D;
      player.motionY = 0.0D;
      player.setVelocity(0.0D, 0.0D, 0.0D);
      if (mc.gameSettings.keyBindJump.isKeyDown()) {
        player.setPosition(player.posX, player.posY + this.speed2
            .getValDouble(), player.posZ);
        player.motionY = 0.0D;
      } 
      if (mc.gameSettings.keyBindSneak.isKeyDown() && !player.onGround)
        player.setPosition(player.posX, player.posY - this.speed2
            .getValDouble(), player.posZ); 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Motion")) {
      float speedScaled = (float)(this.speed3.getValDouble() * 0.6000000238418579D);
      double[] directionSpeedVanilla = Utils.directionSpeed(speedScaled);
      if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
        mc.player.motionX = directionSpeedVanilla[0];
        mc.player.motionZ = directionSpeedVanilla[1];
      } 
      mc.player.motionY = 0.0D;
      if (mc.gameSettings.keyBindJump.isKeyDown())
        mc.player.motionY = this.speed3.getValDouble(); 
      if (mc.gameSettings.keyBindSneak.isKeyDown() && !mc.player.onGround)
        mc.player.motionY = -this.speed3.getValDouble(); 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Vanilla")) {
      EntityPlayerSP player = mc.player;
      player.capabilities.isFlying = false;
      player.motionX = 0.0D;
      player.motionY = 0.0D;
      player.motionZ = 0.0D;
      player.jumpMovementFactor = (float)this.speed.getValDouble();
      if (mc.gameSettings.keyBindJump.isKeyDown())
        player.motionY += this.speed.getValDouble(); 
      if (mc.gameSettings.keyBindSneak.isKeyDown())
        player.motionY -= this.speed.getValDouble(); 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Servers")) {
      mc.player.motionY = 0.0D;
      if (mc.player.ticksExisted % 3 == 0)
        double d = mc.player.posY - 1.0E-10D; 
      double y1 = mc.player.posY + 1.0E-10D;
      mc.player.setPosition(mc.player.posX, y1, mc.player.posZ);
    } 
    if (this.mode.getValString().equalsIgnoreCase("NPacket")) {
      float forward = 0.0F;
      float strafe = 0.0F;
      EntityPlayerSP player = mc.player;
      float var5 = MathHelper.sin(mc.player.rotationYaw * 3.1415927F / 180.0F);
      float var6 = MathHelper.cos(mc.player.rotationYaw * 3.1415927F / 180.0F);
      if (mc.gameSettings.keyBindForward.pressed)
        forward += 0.1F; 
      if (mc.gameSettings.keyBindBack.pressed)
        forward -= 0.1F; 
      if (mc.gameSettings.keyBindLeft.pressed)
        strafe += 0.01F; 
      if (mc.gameSettings.keyBindRight.pressed)
        strafe -= 0.01F; 
      if (!mc.player.isDead) {
        Movement(forward, strafe, player, var5, var6);
        SendPacket();
      } 
      mc.player.motionY = 0.0D;
      this.clock++;
      if (this.clock >= 12)
        this.clock = 0; 
    } 
    if (this.mode.getValString().equalsIgnoreCase("BPacket")) {
      float forward = 0.0F;
      float strafe = 0.0F;
      double speed = 0.2D;
      EntityPlayerSP player = mc.player;
      float var5 = MathHelper.sin(mc.player.rotationYaw * 3.1415927F / 180.0F);
      float var6 = MathHelper.cos(mc.player.rotationYaw * 3.1415927F / 180.0F);
      if (mc.gameSettings.keyBindForward.pressed)
        forward += 0.1F; 
      if (mc.gameSettings.keyBindBack.pressed)
        forward -= 0.1F; 
      if (mc.gameSettings.keyBindLeft.pressed)
        strafe += 0.01F; 
      if (mc.gameSettings.keyBindRight.pressed)
        strafe -= 0.01F; 
      if (!mc.player.isDead)
        Movement(forward, strafe, player, var5, var6); 
      mc.player.motionY = 0.0D;
      this.clock++;
      if (this.clock >= 2) {
        SendPacket();
        this.clock = 0;
      } 
    } 
    super.onClientTick(event);
  }
  
  public void utils(float speed) {
    mc.player.motionX = -(Math.sin(aan()) * speed);
    mc.player.motionZ = Math.cos(aan()) * speed;
  }
  
  public float aan() {
    float var1 = mc.player.rotationYaw;
    if (mc.player.moveForward < 0.0F)
      var1 += 180.0F; 
    float forward = 1.0F;
    if (mc.player.moveForward < 0.0F) {
      forward = -0.5F;
    } else if (mc.player.moveForward > 0.0F) {
      forward = 0.5F;
    } 
    if (mc.player.moveStrafing > 0.0F)
      var1 -= 90.0F * forward; 
    if (mc.player.moveStrafing < 0.0F)
      var1 += 90.0F * forward; 
    var1 *= 0.017453292F;
    return var1;
  }
  
  private void Movement(float forward, float strafe, EntityPlayerSP player, float var5, float var6) {
    mc.player.motionX = player.posX * 5.01E-8D;
    double speed = 2.7999100260353087D;
    mc.player.motionZ = player.posZ * 5.01E-8D;
    mc.player.motionZ = player.posZ * 5.01E-8D;
    Runmove(player);
    Runmove(player);
    mc.player.motionX = player.posX * 5.01E-8D;
    double motionX = (strafe * var6 - forward * var5) * speed;
    double motionZ = (forward * var6 + strafe * var5) * speed;
    mc.player.motionX = motionX;
    mc.player.motionZ = motionZ;
    if (!mc.gameSettings.keyBindJump.pressed)
      mc.player.setPosition(mc.player.posX, mc.player.posY - 0.03948584D, mc.player.posZ); 
  }
  
  private void Runmove(EntityPlayerSP player) {
    mc.player.motionZ = player.posZ * 5.01E-8D;
    mc.player.motionX = player.posX * 5.01E-8D;
    mc.player.motionZ = player.posZ * 5.01E-8D;
    mc.player.motionX = player.posX * 5.01E-8D;
    mc.player.motionZ = player.posZ * 5.01E-8D;
    mc.player.motionX = player.posX * 5.01E-8D;
    mc.player.motionZ = player.posZ * 5.01E-8D;
    mc.player.motionX = player.posX * 5.01E-8D;
    mc.player.motionZ = player.posZ * 5.01E-8D;
    mc.player.motionX = player.posX * 5.01E-8D;
  }
  
  private void SendPacket() {
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? 0.0621D : 0.0D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? 0.0621D : 0.0D), mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY - 999.0D, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
  }
  
  public double getPosForSetPosX(double value) {
    double yaw = Math.toRadians(mc.player.rotationYaw);
    double x = -Math.sin(yaw) * value;
    return x;
  }
  
  public double getPosForSetPosZ(double value) {
    double yaw = Math.toRadians(mc.player.rotationYaw);
    double z = Math.cos(yaw) * value;
    return z;
  }
  
  public void onDisable() {
    mc.player.capabilities.isFlying = false;
    super.onDisable();
  }
}
