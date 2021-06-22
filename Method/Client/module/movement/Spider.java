package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Spider extends Module {
  Setting mode;
  
  Setting Speed;
  
  public boolean shouldjump;
  
  public float forcedYaw;
  
  public float forcedPitch;
  
  public Spider() {
    super("Spider", 0, Category.MOVEMENT, "Climb Walls");
    this.mode = Main.setmgr.add(new Setting("Spider mode", this, "Vanilla", new String[] { "NCP", "DEV", "Root", "Vanilla" }));
    this.Speed = Main.setmgr.add(new Setting("Speed", this, 0.2D, 0.0D, 1.0D, false, this.mode, "Vanilla", 1));
    this.shouldjump = true;
  }
  
  public void onEnable() {
    this.shouldjump = true;
  }
  
  public void onDisable() {
    this.shouldjump = true;
    mc.player.stepHeight = 0.5F;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Vanilla") && 
      !mc.player.isOnLadder() && mc.player.collidedHorizontally && mc.player.motionY < 0.2D)
      mc.player.motionY = this.Speed.getValDouble(); 
    if (this.mode.getValString().equalsIgnoreCase("Root") && 
      mc.player.collidedHorizontally && mc.player.ticksExisted % 4 == 0)
      mc.player.motionY = 0.25D; 
    if (this.mode.getValString().equalsIgnoreCase("DEV") && 
      mc.player.collidedHorizontally)
      if (mc.player.ticksExisted % 4 == 0) {
        mc.player.motionY = 0.5D;
      } else {
        mc.player.motionY = -0.01D;
      }  
    super.onClientTick(event);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.mode.getValString().equalsIgnoreCase("NCP")) {
      if (packet instanceof CPacketKeepAlive) {
        CPacketKeepAlive packet2 = (CPacketKeepAlive)packet;
        if (mc.player.collidedHorizontally) {
          mc.player.motionX *= 0.0D;
          mc.player.motionZ *= 0.0D;
          if (this.shouldjump) {
            mc.player.jump();
            this.shouldjump = false;
          } 
          if (mc.player.fallDistance > 0.0F) {
            mc.player.setPosition(mc.player.posX, mc.player.posY + 0.08D, mc.player.posZ);
            mc.gameSettings.keyBindJump.pressed = false;
            mc.player.onGround = true;
            mc.player.stepHeight = 2.0F;
          } else {
            mc.player.stepHeight = 0.5F;
          } 
        } else {
          this.forcedYaw = mc.player.rotationYaw;
          this.forcedPitch = mc.player.rotationPitch;
          this.shouldjump = true;
          mc.player.stepHeight = 0.5F;
        } 
      } 
      if (packet instanceof CPacketPlayer) {
        CPacketPlayer packet2 = (CPacketPlayer)packet;
        packet2.onGround = true;
        packet2.yaw = this.forcedYaw;
        packet2.pitch = this.forcedPitch;
      } 
    } 
    return true;
  }
}
