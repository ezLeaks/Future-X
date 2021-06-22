package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ElytraFly extends Module {
  Setting mode = Main.setmgr.add(new Setting("mode", this, "Control", new String[] { "BYPASS", "Control", "Boost", "Try1", "Legit+", "Mouse", "Rocket" }));
  
  Setting speed = Main.setmgr.add(new Setting("speed", this, 1.0D, 0.0D, 5.0D, false));
  
  Setting autoStart = Main.setmgr.add(new Setting("autoStart", this, false));
  
  Setting disableInLiquid = Main.setmgr.add(new Setting("disableInLiquid", this, false));
  
  Setting infiniteDurability = Main.setmgr.add(new Setting("Packet Durability", this, false));
  
  Setting Flatfly = Main.setmgr.add(new Setting("Flatfly", this, false, this.mode, "Control", 6));
  
  Setting BuildupTicks = Main.setmgr.add(new Setting("BuildupTicks", this, 0.0D, 0.0D, 200.0D, false, this.mode, "Control", 7));
  
  Setting noKick = Main.setmgr.add(new Setting("noKick", this, false, this.mode, "Control", 8));
  
  Setting RandomFlap = Main.setmgr.add(new Setting("RandomFlap", this, false));
  
  Setting Yboost = Main.setmgr.add(new Setting("Yboost", this, false, this.mode, "Boost", 8));
  
  Setting Flatboost = Main.setmgr.add(new Setting("Flatboost", this, false, this.mode, "Boost", 8));
  
  Setting Fallspeedboost = Main.setmgr.add(new Setting("FlatFall% ", this, 99.95D, 10.0D, 99.95D, false, this.mode, "Boost", 7));
  
  Setting Fallspeed = Main.setmgr.add(new Setting("Fall multiplier", this, 99.95D, 10.0D, 99.95D, false, this.mode, "Control", 7));
  
  Setting StopPitch = Main.setmgr.add(new Setting("StopPitch", this, 0.0D, 0.0D, 90.0D, false, this.mode, "Control", 7));
  
  Setting Speedme = Main.setmgr.add(new Setting("Speed Weird", this, 1.0D, 0.01D, 3.0D, false, this.mode, "Try1", 6));
  
  Setting upspeed = Main.setmgr.add(new Setting("upspeed", this, 1.0D, 0.0D, 3.0D, false, this.mode, "Mouse", 6));
  
  Setting SpeedMulti = Main.setmgr.add(new Setting("Burner Speed", this, 2.0D, 0.0D, 10.0D, false, this.mode, "Rocket", 7));
  
  Setting AutoRocket = Main.setmgr.add(new Setting("AutoRocket", this, false, this.mode, "Rocket", 6));
  
  Setting RocketTicks = Main.setmgr.add(new Setting("RocketTicks ", this, 22.0D, 0.0D, 100.0D, false, this.mode, "Rocket", 7));
  
  Setting Lockmove = Main.setmgr.add(new Setting("Lockmove", this, false, this.mode, "Rocket", 6));
  
  Setting AfterBurner = Main.setmgr.add(new Setting("After Burner", this, false, this.mode, "Rocket", 6));
  
  Setting TicksAfter = Main.setmgr.add(new Setting("Burner Ticks", this, 22.0D, 0.0D, 60.0D, false, this.mode, "Rocket", 7));
  
  double yposperson;
  
  double Lazyticks = 0.0D;
  
  EntityFireworkRocket wasBoosted = null;
  
  private final TimerUtils timer = new TimerUtils();
  
  private final TimerUtils Fireworktimer1 = new TimerUtils();
  
  private final TimerUtils Fireworktimer2 = new TimerUtils();
  
  public ElytraFly() {
    super("ElytraFly", 0, Category.MOVEMENT, "Elytra Fly");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA)
      return; 
    if (this.disableInLiquid.getValBoolean() && (mc.player.isInWater() || mc.player.isInLava())) {
      if (mc.player.isElytraFlying())
        ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING)); 
      return;
    } 
    if (this.autoStart.getValBoolean() && 
      !mc.player.isElytraFlying())
      if (mc.player.posY + 0.02D < mc.player.lastTickPosY && !mc.player.onGround) {
        mc.player.posY = this.yposperson;
        ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
      } else {
        this.yposperson = mc.player.posY;
      }  
    if (mc.player.isElytraFlying()) {
      if (this.RandomFlap.getValBoolean()) {
        mc.player.rotateElytraX = (float)Math.random();
        mc.player.rotateElytraY = (float)Math.random();
        mc.player.rotateElytraZ = (float)Math.random();
      } 
      if (this.mode.getValString().equalsIgnoreCase("Legit+")) {
        mc.player.jumpMovementFactor *= 1.01222F;
        mc.player.fallDistance = 0.0F;
        mc.player.moveStrafing += 0.1F;
        mc.player.velocityChanged = true;
        if (mc.player.cameraPitch > 88.0F)
          mc.player.addVelocity(0.1D, 0.0D, 0.1D); 
        double[] directionSpeedVanilla = Utils.directionSpeed(0.02D);
        if (mc.player.cameraPitch < 0.0F)
          mc.player.addVelocity(directionSpeedVanilla[0], -0.001D, directionSpeedVanilla[1]); 
      } 
      if (this.mode.getValString().equalsIgnoreCase("Rocket")) {
        mc.player.attackEntityFrom(DamageSource.FIREWORKS, 10.0F);
        if (this.wasBoosted != null) {
          float speedScaled = (float)(this.speed.getValDouble() * 0.05000000074505806D) * 10.0F;
          if (this.Fireworktimer1.isDelay((long)(this.TicksAfter.getValDouble() * 100.0D)) && this.AfterBurner.getValBoolean()) {
            this.wasBoosted = null;
            speedScaled = (float)(speedScaled * this.SpeedMulti.getValDouble());
            this.Fireworktimer1.setLastMS();
          } else if (this.Lockmove.getValBoolean()) {
            freezePlayer((EntityPlayer)mc.player);
          } 
          double[] directionSpeedVanilla = Utils.directionSpeed(speedScaled);
          if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
            mc.player.motionX += directionSpeedVanilla[0];
            mc.player.motionZ += directionSpeedVanilla[1];
          } 
        } else {
          if (this.AutoRocket.getValBoolean() && this.Fireworktimer2.isDelay((long)this.RocketTicks.getValDouble() * 100L)) {
            if ((mc.player.getHeldItemMainhand()).item == Items.FIREWORKS || (mc.player.getHeldItemOffhand()).item == Items.FIREWORKS)
              mc.rightClickMouse(); 
            this.Fireworktimer2.setLastMS();
          } 
          if (this.AfterBurner.getValBoolean())
            for (Entity entity : mc.world.loadedEntityList) {
              if (entity instanceof EntityFireworkRocket) {
                EntityFireworkRocket e = (EntityFireworkRocket)entity;
                if (e.boostedEntity == mc.player && !e.isGlowing()) {
                  e.setGlowing(true);
                  this.wasBoosted = e;
                  this.Fireworktimer1.setLastMS();
                  break;
                } 
              } 
            }  
        } 
      } 
      if (this.mode.getValString().equalsIgnoreCase("Boost")) {
        float pitch = mc.player.rotationPitch;
        float speedScaled = (float)(this.speed.getValDouble() * 0.05000000074505806D);
        double[] directionSpeedVanilla = Utils.directionSpeed(speedScaled);
        if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
          mc.player.motionX += directionSpeedVanilla[0];
          mc.player.motionZ += directionSpeedVanilla[1];
        } 
        mc.player.motionY += this.Yboost.getValBoolean() ? (Math.sin(Math.toRadians(pitch)) * 0.05D) : 0.0D;
        if (this.Flatboost.getValBoolean())
          mc.player.motionY = 0.03D * Math.cos(Math.PI * Math.abs(mc.player.rotationPitch) / 90.0D + Math.PI) + 0.05D * this.Fallspeedboost.getValDouble() / 100.0D; 
        if (mc.gameSettings.keyBindJump.isKeyDown())
          mc.player.motionY += 0.05D; 
        if (mc.gameSettings.keyBindSneak.isKeyDown())
          mc.player.motionY -= 0.05D; 
      } 
      if (this.mode.getValString().equalsIgnoreCase("Try1") && ((
        mc.player.motionY < 0.0D && mc.player.isAirBorne) || mc.player.onGround)) {
        mc.player.motionY = -0.125D;
        mc.player.jumpMovementFactor = (float)(mc.player.jumpMovementFactor * (1.0122699737548828D + this.Speedme.getValDouble()));
        mc.player.noClip = true;
        mc.player.fallDistance = 0.0F;
        mc.player.onGround = true;
        mc.player.moveStrafing = (float)(mc.player.moveStrafing + 0.800000011920929D + this.Speedme.getValDouble());
        mc.player.jumpMovementFactor = (float)(mc.player.jumpMovementFactor + 0.20000000298023224D + this.Speedme.getValDouble());
        mc.player.velocityChanged = true;
        if (mc.player.ticksExisted % 8 == 0 && mc.player.posY <= 240.0D)
          mc.player.motionY = 0.019999999552965164D; 
      } 
      if (this.mode.getValString().equalsIgnoreCase("Control")) {
        runNoKick();
        double[] directionSpeedPacket = Utils.directionSpeed(this.speed.getValDouble() / Math.max(this.Lazyticks, 1.0D));
        if (mc.player.movementInput.moveStrafe == 0.0F && mc.player.movementInput.moveForward == 0.0F) {
          this.Lazyticks = 0.0D;
          freezePlayer((EntityPlayer)mc.player);
          mc.player.motionY = 0.03D * Math.cos(Math.PI * Math.abs(mc.player.rotationPitch) / 90.0D + Math.PI) + 0.05D * this.Fallspeed.getValDouble() / 100.0D;
          if (mc.player.movementInput.sneak)
            mc.player.motionY = -this.speed.getValDouble(); 
        } else if (mc.player.rotationPitch > this.StopPitch.getValDouble() - 0.01D) {
          if (this.BuildupTicks.getValDouble() > 0.0D) {
            if (this.Lazyticks == 0.0D)
              this.Lazyticks = this.BuildupTicks.getValDouble(); 
            if (this.Lazyticks > 1.0D)
              this.Lazyticks /= 1.03D; 
          } 
          freezePlayer((EntityPlayer)mc.player);
          mc.player.motionY = 0.03D * Math.cos(Math.PI * Math.abs(mc.player.rotationPitch) / 90.0D + Math.PI) + 0.05D * this.Fallspeed.getValDouble() / 100.0D;
          if (mc.player.movementInput.sneak)
            mc.player.motionY = -this.speed.getValDouble(); 
          if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
            mc.player.motionX = directionSpeedPacket[0];
            mc.player.motionZ = directionSpeedPacket[1];
          } 
        } else if (mc.player.posY < mc.player.lastTickPosY && this.timer.isDelay(100L)) {
          mc.player.rotationPitch = 0.0F;
          this.timer.setLastMS();
        } 
      } 
      if (this.mode.getValString().equalsIgnoreCase("Mouse")) {
        double[] dir = Utils.directionSpeed(this.speed.getValDouble());
        if (Module.mc.player.movementInput.moveStrafe == 0.0F && Module.mc.player.movementInput.moveForward == 0.0F) {
          Module.mc.player.motionX = 0.0D;
          Module.mc.player.motionZ = 0.0D;
        } else {
          Module.mc.player.motionX = dir[0];
          Module.mc.player.motionZ = dir[1];
          mc.player.motionX -= Module.mc.player.motionX * (Math.abs(Module.mc.player.rotationPitch) + 90.0F) / 90.0D - Module.mc.player.motionX;
          mc.player.motionZ -= Module.mc.player.motionZ * (Math.abs(Module.mc.player.rotationPitch) + 90.0F) / 90.0D - Module.mc.player.motionZ;
        } 
        float y = 0.0F;
        if (mc.gameSettings.keyBindJump.isKeyDown())
          y = (float)this.upspeed.getValDouble(); 
        double Ymove = y + -degToRad(Module.mc.player.rotationPitch) * Module.mc.player.movementInput.moveForward;
        if (this.upspeed.getValDouble() == 0.0D && Ymove > 0.0D)
          Ymove = 0.0D; 
        Module.mc.player.motionY = Ymove;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("BYPASS")) {
      if (mc.gameSettings.keyBindJump.isKeyDown())
        mc.player.motionY = 0.019999999552965164D; 
      if (mc.gameSettings.keyBindSneak.isKeyDown())
        mc.player.motionY = -0.20000000298023224D; 
      if (mc.player.ticksExisted % 8 == 0 && mc.player.posY <= 240.0D)
        mc.player.motionY = 0.019999999552965164D; 
      mc.player.capabilities.isFlying = true;
      mc.player.capabilities.setFlySpeed(0.025F);
      double[] directionSpeedBypass = Utils.directionSpeed(0.5199999809265137D);
      if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
        mc.player.motionX = directionSpeedBypass[0];
        mc.player.motionZ = directionSpeedBypass[1];
      } else {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      } 
    } 
    if (this.infiniteDurability.getValBoolean()) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
    } 
  }
  
  public double degToRad(double deg) {
    return deg * 0.01745329238474369D;
  }
  
  private void runNoKick() {
    if (this.noKick.getValBoolean() && !mc.player.isElytraFlying() && 
      mc.player.ticksExisted % 4 == 0)
      mc.player.motionY = -0.03999999910593033D; 
  }
  
  private void freezePlayer(EntityPlayer player) {
    player.motionX = 0.0D;
    player.motionY = 0.0D;
    player.motionZ = 0.0D;
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof CPacketPlayer.PositionRotation && 
      mc.player.rotationPitch > -this.StopPitch.getValDouble() && this.mode.getValString().equalsIgnoreCase("Control") && 
      mc.player.isElytraFlying() && this.Flatfly.getValBoolean()) {
      CPacketPlayer.PositionRotation rotation = (CPacketPlayer.PositionRotation)packet;
      ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketPlayer.Position(rotation.x, rotation.y, rotation.z, rotation.onGround));
      return false;
    } 
    return true;
  }
  
  public void onDisable() {
    if (mc.player != null)
      mc.player.capabilities.isFlying = false; 
  }
}
