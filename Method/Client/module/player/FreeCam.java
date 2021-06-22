package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Entity301;
import Method.Client.utils.Patcher.Events.SetOpaqueCubeEvent;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FreeCam extends Module {
  public Entity301 entity301 = null;
  
  Setting ShowPlayer;
  
  Setting Speed;
  
  Setting Tp;
  
  public FreeCam() {
    super("FreeCam", 0, Category.PLAYER, "FreeCam");
    this.ShowPlayer = Main.setmgr.add(new Setting("ShowPlayer", this, true));
    this.Speed = Main.setmgr.add(new Setting("Speed", this, 1.0D, 0.0D, 3.0D, false));
    this.Tp = Main.setmgr.add(new Setting("Tp", this, false));
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.IN && 
      packet instanceof net.minecraft.network.play.server.SPacketPlayerPosLook)
      return false; 
    return (side != Connection.Side.OUT || !(packet instanceof net.minecraft.network.play.client.CPacketPlayer));
  }
  
  public void onEnable() {
    if (mc.player != null && mc.world != null && !this.Tp.getValBoolean()) {
      this.entity301 = new Entity301((World)mc.world, mc.player.getGameProfile());
      this.entity301.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
      this.entity301.inventory = mc.player.inventory;
      this.entity301.rotationPitch = mc.player.rotationPitch;
      this.entity301.rotationYaw = mc.player.rotationYaw;
      this.entity301.rotationYawHead = mc.player.rotationYawHead;
      if (this.ShowPlayer.getValBoolean())
        mc.world.spawnEntity((Entity)this.entity301); 
    } 
    super.onEnable();
  }
  
  public void onDisable() {
    if (this.entity301 != null && mc.world != null) {
      mc.player.setPosition(this.entity301.posX, this.entity301.posY, this.entity301.posZ);
      mc.player.rotationPitch = this.entity301.rotationPitch;
      mc.player.rotationYaw = this.entity301.rotationYaw;
      mc.player.rotationYawHead = this.entity301.rotationYawHead;
      mc.world.removeEntity((Entity)this.entity301);
      this.entity301 = null;
    } 
    mc.player.noClip = false;
    super.onDisable();
  }
  
  public void SetOpaqueCubeEvent(SetOpaqueCubeEvent event) {
    event.setCanceled(true);
  }
  
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (mc.player.deathTime > 0 || mc.player.getHealth() <= 0.0F) {
      toggle();
      return;
    } 
    EntityPlayerSP player = mc.player;
    player.capabilities.isFlying = false;
    player.motionY = 0.0D;
    player.motionZ = 0.0D;
    player.motionX = 0.0D;
    double[] directionSpeedVanilla = Utils.directionSpeed(this.Speed.getValDouble());
    player.jumpMovementFactor = (float)this.Speed.getValDouble();
    if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
      mc.player.motionX += directionSpeedVanilla[0];
      mc.player.motionZ += directionSpeedVanilla[1];
    } 
    if (mc.gameSettings.keyBindJump.isKeyDown())
      player.motionY += this.Speed.getValDouble(); 
    if (mc.gameSettings.keyBindSneak.isKeyDown())
      player.motionY -= this.Speed.getValDouble(); 
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    mc.player.motionY = 0.0D;
    if (mc.gameSettings.keyBindJump.isKeyDown())
      mc.player.motionY += this.Speed.getValDouble(); 
    if (mc.gameSettings.keyBindSneak.isKeyDown())
      mc.player.motionY -= this.Speed.getValDouble(); 
    mc.player.noClip = true;
    super.onLivingUpdate(event);
  }
}
