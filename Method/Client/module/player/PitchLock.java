package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.proxy.Overrides.PitchYawHelper;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.MouseHelper;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PitchLock extends Module {
  float PitchOLD;
  
  boolean overshot;
  
  Setting auto;
  
  Setting slice;
  
  Setting pitch;
  
  Setting Gradual;
  
  Setting Gradualamnt;
  
  Setting Wiggle;
  
  Setting Wiggleamnt;
  
  Setting Silent;
  
  float NewPitch;
  
  public PitchLock() {
    super("PitchLock", 0, Category.PLAYER, "PitchLock");
    this.PitchOLD = 0.0F;
    this.auto = Main.setmgr.add(new Setting("Auto Snap", this, true));
    this.slice = Main.setmgr.add(new Setting("Auto Slice", this, 45.0D, 0.0D, 360.0D, true, this.auto, 1));
    this.pitch = Main.setmgr.add(new Setting("pitch", this, 0.0D, -180.0D, 180.0D, true));
    this.Gradual = Main.setmgr.add(new Setting("Gradual", this, true));
    this.Gradualamnt = Main.setmgr.add(new Setting("Gradualamnt", this, 0.1D, 0.0D, 1.0D, false));
    this.Wiggle = Main.setmgr.add(new Setting("Wiggle", this, true));
    this.Wiggleamnt = Main.setmgr.add(new Setting("Wiggleamnt", this, 0.1D, 0.0D, 1.0D, false, this.Wiggle, 5));
    this.Silent = Main.setmgr.add(new Setting("Silent", this, true));
  }
  
  public void onEnable() {
    mc.mouseHelper = (MouseHelper)new PitchYawHelper();
    PitchYawHelper.Pitch = true;
    this.PitchOLD = mc.player.rotationPitch;
    this.overshot = false;
    if (this.pitch.getValDouble() > 90.0D || this.pitch.getValDouble() < -90.0D)
      ChatUtils.warning("Out of normal Range! Use Silent?"); 
  }
  
  public void onDisable() {
    PitchYawHelper.Pitch = false;
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    PitchYawHelper.Pitch = !this.Silent.getValBoolean();
    this.NewPitch = this.PitchOLD;
    if (this.Gradual.getValBoolean() && !this.overshot) {
      this.PitchOLD = (float)(this.PitchOLD + ((this.PitchOLD < this.pitch.getValDouble()) ? this.Gradualamnt.getValDouble() : -this.Gradualamnt.getValDouble()));
      if ((this.NewPitch > this.pitch.getValDouble() && this.PitchOLD < this.pitch.getValDouble()) || (this.NewPitch < this.pitch.getValDouble() && this.PitchOLD > this.pitch.getValDouble())) {
        this.PitchOLD = (float)this.pitch.getValDouble();
        this.overshot = true;
      } 
    } 
    if (this.overshot && this.pitch.getValDouble() != this.PitchOLD)
      this.overshot = false; 
    if (!this.Gradual.getValBoolean())
      this.NewPitch = (float)this.pitch.getValDouble(); 
    if (this.Wiggle.getValBoolean())
      this.NewPitch = (float)(this.NewPitch + this.Wiggleamnt.getValDouble() * ((Math.random() > 0.5D) ? Math.random() : -Math.random())); 
    if (!this.Silent.getValBoolean()) {
      if (this.slice.getValDouble() == 0.0D)
        return; 
      if (this.auto.getValBoolean()) {
        int angle = (int)(360.0D / this.slice.getValDouble());
        float yaw = mc.player.rotationPitch;
        yaw = (Math.round(yaw / angle) * angle);
        mc.player.rotationPitch = yaw;
        if (mc.player.isRiding())
          ((Entity)Objects.requireNonNull((T)mc.player.getRidingEntity())).rotationPitch = yaw; 
      } else {
        mc.player.rotationPitch = this.NewPitch;
      } 
    } 
    mc.player.renderYawOffset = this.NewPitch;
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && 
      this.Silent.getValBoolean()) {
      if (packet instanceof CPacketPlayer.Rotation) {
        CPacketPlayer.Rotation p = (CPacketPlayer.Rotation)packet;
        p.pitch = this.NewPitch;
      } 
      if (packet instanceof CPacketPlayer.PositionRotation) {
        CPacketPlayer.PositionRotation p = (CPacketPlayer.PositionRotation)packet;
        p.pitch = this.NewPitch;
      } 
    } 
    return true;
  }
}
