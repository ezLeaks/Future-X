package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Entity301;
import Method.Client.utils.system.Connection;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class Blink extends Module {
  public Entity301 entity301 = null;
  
  Setting Limit;
  
  Setting Renable;
  
  Setting ShowPos;
  
  Setting Entity;
  
  int limitcount;
  
  public Blink() {
    super("Blink", 0, Category.MOVEMENT, "Cancel packets and move");
    this.Limit = Main.setmgr.add(new Setting("Packet limit", this, 0.0D, 0.0D, 500.0D, true));
    this.Renable = Main.setmgr.add(new Setting("Renable", this, false));
    this.ShowPos = Main.setmgr.add(new Setting("ShowPos", this, true));
    this.Entity = Main.setmgr.add(new Setting("Entity", this, true));
    this.limitcount = 0;
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && packet instanceof net.minecraft.network.play.client.CPacketVehicleMove && this.Entity.getValBoolean()) {
      this.limitcount++;
      if (this.Limit.getValDouble() == 0.0D)
        return false; 
      if (this.Limit.getValDouble() < this.limitcount) {
        this.limitcount = 0;
        onoff();
        return true;
      } 
    } 
    if (side == Connection.Side.OUT && packet instanceof net.minecraft.network.play.client.CPacketPlayer) {
      this.limitcount++;
      if (this.Limit.getValDouble() == 0.0D)
        return false; 
      if (this.Limit.getValDouble() < this.limitcount) {
        this.limitcount = 0;
        onoff();
        return true;
      } 
    } 
    return true;
  }
  
  public void onEnable() {
    Enable();
    super.onEnable();
  }
  
  public void onDisable() {
    this.limitcount = 0;
    if (this.entity301 != null && mc.world != null) {
      mc.world.removeEntity((Entity)this.entity301);
      this.entity301 = null;
    } 
    super.onDisable();
  }
  
  private void onoff() {
    this.limitcount = 0;
    if (this.entity301 != null && mc.world != null) {
      mc.world.removeEntity((Entity)this.entity301);
      this.entity301 = null;
    } 
    if (this.Renable.getValBoolean())
      Enable(); 
  }
  
  private void Enable() {
    this.limitcount = 0;
    if (mc.player != null && mc.world != null && this.ShowPos.getValBoolean()) {
      this.entity301 = new Entity301((World)mc.world, mc.player.getGameProfile());
      this.entity301.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
      this.entity301.inventory = mc.player.inventory;
      this.entity301.rotationPitch = mc.player.rotationPitch;
      this.entity301.rotationYaw = mc.player.rotationYaw;
      this.entity301.rotationYawHead = mc.player.rotationYawHead;
      mc.world.spawnEntity((Entity)this.entity301);
    } 
  }
}
