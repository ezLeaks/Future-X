package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Velocity extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Simple", new String[] { "Simple", "AAC", "Fast", "YPort", "AAC4Flag", "Pull", "Airmove", "HurtPacket" }));
  
  Setting XMult = Main.setmgr.add(new Setting("XMultipl", this, 0.0D, 0.0D, 10.0D, false, this.mode, "Simple", 1));
  
  Setting YMult = Main.setmgr.add(new Setting("YMultipl", this, 0.0D, 0.0D, 10.0D, false, this.mode, "Simple", 2));
  
  Setting ZMult = Main.setmgr.add(new Setting("ZMultipl", this, 0.0D, 0.0D, 10.0D, false, this.mode, "Simple", 3));
  
  Setting onPacket = Main.setmgr.add(new Setting("Only Packet", this, true, this.mode, "Simple", 4));
  
  Setting CancelPacket = Main.setmgr.add(new Setting("CancelPacket", this, true, this.mode, "Simple", 5));
  
  Setting Super = Main.setmgr.add(new Setting("Super", this, true, this.mode, "Pull", 1));
  
  Setting Pushspeed = Main.setmgr.add(new Setting("Pushspeed", this, 0.25D, 1.0E-4D, 0.4D, false, this.mode, "Airmove", 2));
  
  Setting Pushstart = Main.setmgr.add(new Setting("Pushstart", this, 8.0D, 2.0D, 9.0D, false, this.mode, "Airmove", 3));
  
  private double motionX;
  
  private double motionZ;
  
  private final TimerUtils timer = new TimerUtils();
  
  public Velocity() {
    super("Velocity", 0, Category.COMBAT, "Velocity");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("AAC")) {
      if (mc.player.hurtTime > 0 && mc.player.hurtTime <= 7) {
        mc.player.motionX *= 0.5D;
        mc.player.motionZ *= 0.5D;
      } 
      if (mc.player.hurtTime > 0 && mc.player.hurtTime < 6) {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Fast") && 
      mc.player.hurtTime < 9 && !mc.player.onGround) {
      double yaw = mc.player.rotationYawHead;
      yaw = Math.toRadians(yaw);
      double dX = -Math.sin(yaw) * 0.08D;
      double dZ = Math.cos(yaw) * 0.08D;
      if (mc.player.getHealth() >= 6.0F) {
        mc.player.motionX = dX;
        mc.player.motionZ = dZ;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Simple") && !this.onPacket.getValBoolean() && 
      mc.player.hurtTime > 0 && mc.player.fallDistance < 3.0F && 
      this.timer.isDelay(100L)) {
      if (Utils.isMovinginput()) {
        mc.player.motionX *= this.XMult.getValDouble();
        mc.player.motionZ *= this.ZMult.getValDouble();
      } else {
        mc.player.motionX *= this.XMult.getValDouble() + 0.2D;
        mc.player.motionZ *= this.ZMult.getValDouble() + 0.2D;
      } 
      mc.player.motionY -= this.YMult.getValDouble();
      mc.player.motionY += this.YMult.getValDouble();
      this.timer.setLastMS();
    } 
    if (this.mode.getValString().equalsIgnoreCase("AAC4Flag") && (mc.player.hurtTime == 3 || mc.player.hurtTime == 4)) {
      double[] directionSpeedVanilla = Utils.directionSpeed(0.05D);
      mc.player.motionX = directionSpeedVanilla[0];
      mc.player.motionZ = directionSpeedVanilla[1];
    } 
    if (this.mode.getValString().equalsIgnoreCase("Pull")) {
      if (mc.player.hurtTime == 9) {
        this.motionX = mc.player.motionX;
        this.motionZ = mc.player.motionZ;
      } 
      if (this.Super.getValBoolean()) {
        if (mc.player.hurtTime == 8) {
          mc.player.motionX = -this.motionX * 0.45D;
          mc.player.motionZ = -this.motionZ * 0.45D;
        } 
      } else if (mc.player.hurtTime == 4) {
        mc.player.motionX = -this.motionX * 0.6D;
        mc.player.motionZ = -this.motionZ * 0.6D;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Airmove"))
      if (mc.player.hurtTime == 9) {
        this.motionX = mc.player.motionX;
        this.motionZ = mc.player.motionZ;
      } else if (mc.player.hurtTime == this.Pushstart.getValDouble() - 1.0D) {
        mc.player.motionX *= -this.Pushspeed.getValDouble();
        mc.player.motionZ *= -this.Pushspeed.getValDouble();
      }  
    if (this.mode.getValString().equalsIgnoreCase("HurtPacket") && 
      mc.player.hurtResistantTime > 18)
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 12.0D, mc.player.posZ, false)); 
    super.onClientTick(event);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.mode.getValString().equalsIgnoreCase("Simple") && this.onPacket.getValBoolean()) {
      if (this.CancelPacket.getValBoolean()) {
        if (packet instanceof SPacketEntityVelocity) {
          SPacketEntityVelocity packet2 = (SPacketEntityVelocity)packet;
          return (packet2.getEntityID() != mc.player.getEntityId());
        } 
        if (packet instanceof SPacketExplosion && 
          this.YMult.getValDouble() == 0.0D && this.XMult.getValDouble() == 0.0D && this.ZMult.getValDouble() == 0.0D)
          return false; 
        return true;
      } 
      if (this.timer.isDelay(100L)) {
        if (packet instanceof SPacketEntityVelocity) {
          SPacketEntityVelocity packet2 = (SPacketEntityVelocity)packet;
          packet2.motionY = (int)(packet2.motionY * this.YMult.getValDouble());
          packet2.motionX = (int)(packet2.motionX * this.XMult.getValDouble());
          packet2.motionZ = (int)(packet2.motionZ * this.ZMult.getValDouble());
        } 
        if (packet instanceof SPacketExplosion) {
          SPacketExplosion packet2 = (SPacketExplosion)packet;
          packet2.motionY = (float)(packet2.motionY * this.YMult.getValDouble());
          packet2.motionX = (float)(packet2.motionX * this.XMult.getValDouble());
          packet2.motionZ = (float)(packet2.motionZ * this.ZMult.getValDouble());
        } 
        this.timer.setLastMS();
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("YPort") && 
      mc.player.hurtTime >= 8) {
      mc.player.setPosition(mc.player.lastTickPosX, mc.player.lastTickPosY + 2.0D, mc.player.lastTickPosZ);
      mc.player.motionY -= 0.3D;
      mc.player.motionX *= 0.8D;
      mc.player.motionZ *= 0.8D;
    } 
    return true;
  }
}
