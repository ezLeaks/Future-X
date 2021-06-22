package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class Criticals extends Module {
  TimerUtils timer = new TimerUtils();
  
  boolean cancelSomePackets;
  
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Packet", new String[] { "Packet", "Simple", "Groundspoof", "Jump", "Fpacket", "Bpacket", "Falldist", "MiniJump", "NBypass" }));
  
  Setting ShowCrit = Main.setmgr.add(new Setting("ShowCrit", this, true));
  
  public Criticals() {
    super("Auto Criticals", 0, Category.COMBAT, "Criticals on hit");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (mc.player.onGround)
      if (side == Connection.Side.OUT) {
        if (packet instanceof CPacketUseEntity) {
          CPacketUseEntity attack = (CPacketUseEntity)packet;
          if (attack.getAction() == CPacketUseEntity.Action.ATTACK) {
            if (this.mode.getValString().equalsIgnoreCase("Bpacket"))
              try {
                Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11D, mc.player.posZ, true));
                Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579D, mc.player.posZ, false));
                Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.3579E-6D, mc.player.posZ, false));
              } catch (Exception exception) {} 
            if (this.mode.getValString().equalsIgnoreCase("NBypass")) {
              mc.player.motionY = 0.41999998688697815D;
              if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
                mc.player.motionY += ((((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST))).getAmplifier() + 1) * 0.1F); 
              if (mc.player.isSprinting()) {
                float var1 = mc.player.rotationYaw * 0.017453292F;
                mc.player.motionX -= (MathHelper.sin(var1) * 0.2F);
                mc.player.motionZ += (MathHelper.cos(var1) * 0.2F);
              } 
              mc.player.isAirBorne = true;
            } 
            if (this.mode.getValString().equalsIgnoreCase("Simple"))
              if (canJump()) {
                mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY - 0.5D, mc.player.posZ);
                mc.player.setSprinting(true);
              } else {
                mc.player.motionY = -0.1D;
              }  
            if (this.mode.getValString().equalsIgnoreCase("MiniJump")) {
              mc.player.jump();
              mc.player.motionY -= 0.30000001192092896D;
            } 
            if (this.mode.getValString().equalsIgnoreCase("Fpacket"))
              doCritical(); 
            if (this.mode.getValString().equalsIgnoreCase("Falldist")) {
              mc.player.motionY -= -0.001D;
              mc.player.fallDistance = 9999.0F;
              mc.player.fall(20.0F, 0.0F);
            } 
            if (this.mode.getValString().equalsIgnoreCase("Groundspoof")) {
              mc.player.onGround = false;
              mc.player.isAirBorne = true;
            } 
            if (this.mode.getValString().equalsIgnoreCase("Packet")) {
              if (mc.player.collidedVertically && this.timer.isDelay(500L)) {
                Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0627D, mc.player.posZ, false));
                Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                this.timer.setLastMS();
                this.cancelSomePackets = true;
              } 
            } else if (this.mode.getValString().equalsIgnoreCase("Jump") && 
              canJump()) {
              mc.player.jump();
            } 
            if (this.ShowCrit.getValBoolean()) {
              Entity entity = attack.getEntityFromWorld((World)mc.world);
              if (entity != null)
                mc.player.onCriticalHit(entity); 
            } 
          } 
        } 
      } else if (this.mode.getValString().equalsIgnoreCase("Packet") && packet instanceof CPacketPlayer && 
        this.cancelSomePackets) {
        this.cancelSomePackets = false;
        return false;
      }  
    return true;
  }
  
  private void doCritical() {
    if (mc.player.isInLava() || mc.player.isInWater())
      return; 
    double posX = mc.player.posX;
    double posY = mc.player.posY;
    double posZ = mc.player.posZ;
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(posX, posY + 0.0625D, posZ, false));
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(posX, posY, posZ, false));
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(posX, posY + 1.1E-5D, posZ, false));
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(posX, posY, posZ, false));
  }
  
  boolean canJump() {
    if (mc.player.isOnLadder())
      return false; 
    if (mc.player.isInWater())
      return false; 
    if (mc.player.isInLava())
      return false; 
    if (mc.player.isSneaking())
      return false; 
    if (mc.player.isRiding())
      return false; 
    return !mc.player.isPotionActive(MobEffects.BLINDNESS);
  }
}
