package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import Method.Client.utils.Utils;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Strafe extends Module {
  Setting mode;
  
  Setting jump;
  
  Setting extraYBoost;
  
  Setting jumpDetect;
  
  Setting speedDetect;
  
  Setting multiplier;
  
  Setting ground;
  
  int waitCounter;
  
  int forward;
  
  private double motionSpeed;
  
  private int currentState;
  
  private double prevDist;
  
  public Strafe() {
    super("Strafe", 0, Category.COMBAT, "Strafe");
    this.mode = Main.setmgr.add(new Setting("Strafe Mode", this, "Vanilla", new String[] { "Vanilla", "Fast", "Bypass" }));
    this.jump = Main.setmgr.add(new Setting("FastJump", this, true, this.mode, "Bypass", 1));
    this.extraYBoost = Main.setmgr.add(new Setting("extraYBoost", this, 0.0D, 0.0D, 1.0D, false, this.mode, "Fast", 1));
    this.jumpDetect = Main.setmgr.add(new Setting("jumpDetect", this, false, this.mode, "Fast", 1));
    this.speedDetect = Main.setmgr.add(new Setting("speedDetect", this, false, this.mode, "Fast", 1));
    this.multiplier = Main.setmgr.add(new Setting("multiplier", this, 1.0D, 0.0D, 1.0D, false, this.mode, "Fast", 1));
    this.ground = Main.setmgr.add(new Setting("ground", this, true, this.mode, "Vanilla", 1));
    this.waitCounter = 0;
    this.forward = 1;
  }
  
  public void onDisable() {
    mc.timer.tickLength = 50.0F;
    onEnable();
  }
  
  private double getBaseMotionSpeed() {
    double v = 0.272D;
    if (mc.player.isPotionActive(MobEffects.SPEED) && this.speedDetect.getValBoolean()) {
      int amplifier = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier();
      v *= 1.0D + 0.2D * amplifier;
    } 
    return v;
  }
  
  private static int calcl(float var0) {
    float var2;
    return ((var2 = var0 - 0.0F) == 0.0F) ? 0 : ((var2 < 0.0F) ? -1 : 1);
  }
  
  private static int Call(double var0) {
    double var4;
    return ((var4 = var0 - 0.0D) == 0.0D) ? 0 : ((var4 < 0.0D) ? -1 : 1);
  }
  
  public void onPlayerMove(PlayerMoveEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Fast")) {
      double v;
      switch (this.currentState) {
        case 0:
          this.currentState++;
          this.prevDist = 0.0D;
          break;
        case 2:
          v = 0.40123128D + this.extraYBoost.getValDouble();
          if ((calcl(mc.player.moveForward) != 0 || calcl(mc.player.moveStrafing) != 0) && (mc.player.onGround)) {
            if ((mc.player.isPotionActive(MobEffects.JUMP_BOOST)) && (this.jumpDetect.getValBoolean()))
              v += ((((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST))).getAmplifier() + 1) * 0.1F); 
            mc.player.motionY = v;
            this.motionSpeed *= 2.149D;
          } 
          break;
        case 3:
          this.motionSpeed = this.prevDist - 0.76D * (this.prevDist - getBaseMotionSpeed());
          break;
        default:
          if ((mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D)).size() > 0 || (mc.player.collidedVertically)) && this.currentState > 0)
            if (calcl(mc.player.moveForward) == 0 && calcl(mc.player.moveStrafing) == 0) {
              this.currentState = 0;
            } else {
              this.currentState = 1;
            }  
          this.motionSpeed = this.prevDist - this.prevDist / 159.0D;
          break;
      } 
      this.motionSpeed = Math.max(this.motionSpeed, getBaseMotionSpeed());
      double v1 = mc.player.movementInput.moveForward;
      double v2 = mc.player.movementInput.moveStrafe;
      double v3 = mc.player.rotationYaw;
      if (Call(v1) == 0 && Call(v2) == 0) {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      } 
      if (Call(v1) != 0 && Call(v2) != 0) {
        v1 *= Math.sin(0.7853981633974483D);
        v2 *= Math.cos(0.7853981633974483D);
      } 
      mc.player.motionX = (v1 * this.motionSpeed * -Math.sin(Math.toRadians(v3)) + v2 * this.motionSpeed * Math.cos(Math.toRadians(v3))) * this.multiplier.getValDouble() * 0.99D;
      mc.player.motionZ = (v1 * this.motionSpeed * Math.cos(Math.toRadians(v3)) - v2 * this.motionSpeed * -Math.sin(Math.toRadians(v3))) * this.multiplier.getValDouble() * 0.99D;
      this.currentState++;
    } 
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Fast"))
      this.prevDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ)); 
    if (Utils.isMoving((Entity)mc.player) && this.mode.getValString().equalsIgnoreCase("Vanilla")) {
      if (mc.player.isSneaking() || mc.player.isOnLadder() || mc.player.isInWeb || mc.player.isInLava() || mc.player.isInWater() || mc.player.capabilities.isFlying)
        return; 
      if (!this.ground.getValBoolean() && 
        mc.player.onGround)
        return; 
      float playerSpeed = 0.2873F;
      if (mc.player.isPotionActive(MobEffects.SPEED)) {
        int amplifier = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier();
        playerSpeed *= 1.0F + 0.2F * (amplifier + 1);
      } 
      if (mc.player.movementInput.moveForward == 0.0F && mc.player.movementInput.moveStrafe == 0.0F) {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      } else {
        Utils.directionSpeed(playerSpeed);
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Bypass")) {
      mc.timer.tickLength = 45.5F;
      boolean boost = (Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90.0F);
      if ((isPlayerTryingMoveForward() || isPlayerTryingStrafe()) && mc.player.onGround)
        mc.player.motionY = 0.4D; 
      if (mc.player.moveForward != 0.0F) {
        if (!mc.player.isSprinting())
          mc.player.setSprinting(true); 
        float yaw = mc.player.rotationYaw;
        if (mc.player.moveForward > 0.0F) {
          if (mc.player.movementInput.moveStrafe != 0.0F)
            yaw += (mc.player.movementInput.moveStrafe > 0.0F) ? -45.0F : 45.0F; 
          this.forward = 1;
          mc.player.moveForward = 1.5F;
          mc.player.moveStrafing = 1.5F;
        } else if (mc.player.moveForward < 0.0F) {
          if (mc.player.movementInput.moveStrafe != 0.0F)
            yaw += (mc.player.movementInput.moveStrafe > 0.0F) ? 45.0F : -45.0F; 
          this.forward = -1;
          mc.player.moveForward = -1.5F;
          mc.player.moveStrafing = 1.5F;
        } 
        if (mc.player.onGround) {
          float f = (float)Math.toRadians(yaw);
          if (this.jump.getValBoolean() && mc.gameSettings.keyBindJump.isPressed())
            Move(f); 
        } else {
          if (this.waitCounter < 1) {
            this.waitCounter++;
            return;
          } 
          this.waitCounter = 0;
          double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
          double speed = boost ? 1.05D : 1.025D;
          if (mc.player.motionY < 0.0D)
            speed = 1.0D; 
          double direction = Math.toRadians(yaw);
          mc.player.motionX = -Math.sin(direction) * speed * currentSpeed * this.forward;
          mc.player.motionZ = Math.cos(direction) * speed * currentSpeed * this.forward;
        } 
      } 
      if (!isPlayerTryingMoveForward() && !isPlayerTryingStrafe()) {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      } 
    } 
    super.onClientTick(event);
  }
  
  private void Move(float f) {
    if (this.jump.getValBoolean())
      mc.player.motionY = 0.4D; 
    mc.player.motionX -= (MathHelper.sin(f) * 0.2F) * this.forward;
    mc.player.motionZ += (MathHelper.cos(f) * 0.2F) * this.forward;
  }
  
  public boolean isPlayerTryingMoveForward() {
    return (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown());
  }
  
  public boolean isPlayerTryingStrafe() {
    return (mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown());
  }
}
