package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.ValidUtils;
import Method.Client.utils.system.Connection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AimBot extends Module {
  Setting priority = Main.setmgr.add(new Setting("priority", this, "Closest", new String[] { "Closest", "Health" }));
  
  Setting walls = Main.setmgr.add(new Setting("walls", this, false));
  
  Setting yaw = Main.setmgr.add(new Setting("yaw", this, 15.0D, 0.0D, 50.0D, false));
  
  Setting pitch = Main.setmgr.add(new Setting("pitch", this, 15.0D, 0.0D, 50.0D, false));
  
  Setting range = Main.setmgr.add(new Setting("range", this, 4.7D, 0.1D, 10.0D, false));
  
  Setting FOV = Main.setmgr.add(new Setting("FOV", this, 90.0D, 1.0D, 180.0D, false));
  
  Setting Silent = Main.setmgr.add(new Setting("Silent", this, false));
  
  Setting Mobs = Main.setmgr.add(new Setting("Mobs", this, true));
  
  Setting Animals = Main.setmgr.add(new Setting("Animals", this, false));
  
  public EntityLivingBase target;
  
  public AimBot() {
    super("AimBot", 0, Category.COMBAT, "Aims to enemys");
  }
  
  public void onDisable() {
    this.target = null;
    super.onDisable();
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && 
      this.Silent.getValBoolean()) {
      if (packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation) {
        updateTarget();
        CPacketPlayer packet2 = (CPacketPlayer)packet;
        float[] rot = Utils.getNeededRotations(this.target.getPositionVector().add(0.0D, (this.target.getEyeHeight() / 2.0F), 0.0D), (float)this.yaw.getValDouble(), (float)this.pitch.getValDouble());
        packet2.yaw = rot[0];
        packet2.pitch = rot[1];
      } 
      this.target = null;
    } 
    return true;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    updateTarget();
    if (!this.Silent.getValBoolean()) {
      float[] rot = Utils.getNeededRotations(this.target.getPositionVector().add(0.0D, (this.target.getEyeHeight() / 2.0F), 0.0D), (float)this.yaw.getValDouble(), (float)this.pitch.getValDouble());
      mc.player.rotationYaw = rot[0];
      mc.player.rotationPitch = rot[1];
    } 
    this.target = null;
    super.onClientTick(event);
  }
  
  void updateTarget() {
    for (Object object : mc.world.loadedEntityList) {
      if (object instanceof EntityLivingBase) {
        EntityLivingBase entity = (EntityLivingBase)object;
        if (check(entity))
          this.target = entity; 
      } 
    } 
  }
  
  public boolean check(EntityLivingBase entity) {
    if (Checks(entity, this.FOV))
      return false; 
    if (!ValidUtils.isInAttackRange(entity, (float)this.range.getValDouble()))
      return false; 
    if (!ValidUtils.pingCheck(entity))
      return false; 
    if (!isPriority(entity))
      return false; 
    if (!this.walls.getValBoolean())
      return mc.player.canEntityBeSeen((Entity)entity); 
    return true;
  }
  
  boolean Checks(EntityLivingBase entity, Setting fov) {
    if (entity instanceof net.minecraft.entity.item.EntityArmorStand)
      return true; 
    if (!ValidUtils.isNoScreen())
      return true; 
    if (entity == mc.player)
      return true; 
    if (entity.isDead)
      return true; 
    if (ValidUtils.isBot(entity))
      return true; 
    if (ValidUtils.isFriendEnemy(entity))
      return true; 
    if (!ValidUtils.isInAttackFOV(entity, (int)fov.getValDouble()))
      return true; 
    if (this.Animals.getValBoolean() && 
      entity instanceof net.minecraft.entity.passive.IAnimals)
      return false; 
    if (this.Mobs.getValBoolean() && 
      entity instanceof net.minecraft.entity.monster.IMob)
      return false; 
    if (entity instanceof net.minecraft.entity.player.EntityPlayer)
      return false; 
    return true;
  }
  
  boolean isPriority(EntityLivingBase entity) {
    return ((this.priority.getValString().equalsIgnoreCase("Closest") && ValidUtils.isClosest(entity, this.target)) || (this.priority.getValString().equalsIgnoreCase("Health") && ValidUtils.isLowHealth(entity, this.target)));
  }
}
