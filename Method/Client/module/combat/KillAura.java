package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.ValidUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class KillAura extends Module {
  public Setting priority;
  
  public Setting walls;
  
  public Setting autoDelay;
  
  public Setting packetReach;
  
  public Setting minCPS;
  
  public Setting maxCPS;
  
  public Setting packetRange;
  
  public Setting range;
  
  public Setting FOV;
  
  public Setting Mobs;
  
  public Setting Animals;
  
  public Setting SpoofAngle;
  
  public TimerUtils timer;
  
  public EntityLivingBase target;
  
  public KillAura() {
    super("KillAura", 0, Category.COMBAT, "KillAura");
    this.priority = Main.setmgr.add(new Setting("priority Mode", this, "Closest", new String[] { "Closest", "Health" }));
    this.walls = Main.setmgr.add(new Setting("walls", this, true));
    this.autoDelay = Main.setmgr.add(new Setting("autoDelay", this, false));
    this.packetReach = Main.setmgr.add(new Setting("packetReach", this, false));
    this.minCPS = Main.setmgr.add(new Setting("minCPS", this, 5.0D, 0.0D, 30.0D, false));
    this.maxCPS = Main.setmgr.add(new Setting("maxCPS", this, 8.0D, 0.0D, 30.0D, false));
    this.packetRange = Main.setmgr.add(new Setting("packetRange", this, 5.0D, 0.0D, 100.0D, false));
    this.range = Main.setmgr.add(new Setting("range", this, 5.0D, 0.0D, 10.0D, false));
    this.FOV = Main.setmgr.add(new Setting("FOV", this, 180.0D, 0.0D, 180.0D, false));
    this.Mobs = Main.setmgr.add(new Setting("Mobs", this, true));
    this.Animals = Main.setmgr.add(new Setting("Animals", this, false));
    this.SpoofAngle = Main.setmgr.add(new Setting("SpoofAngle", this, true));
    this.timer = new TimerUtils();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    killAuraUpdate();
    killAuraAttack(this.target);
    super.onClientTick(event);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    killAuraUpdate();
    if (side == Connection.Side.OUT && 
      this.SpoofAngle.getValBoolean()) {
      if (packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation) {
        CPacketPlayer packet2 = (CPacketPlayer)packet;
        float[] rot = Utils.getNeededRotations(this.target.getPositionVector().add(0.0D, (this.target.getEyeHeight() / 2.0F), 0.0D), 0.0F, 0.0F);
        packet2.yaw = rot[0];
        packet2.pitch = rot[1];
      } 
      this.target = null;
    } 
    return true;
  }
  
  void killAuraUpdate() {
    for (Object object : mc.world.getLoadedEntityList()) {
      if (object instanceof EntityLivingBase) {
        EntityLivingBase entity = (EntityLivingBase)object;
        if (check(entity))
          this.target = entity; 
      } 
    } 
  }
  
  public void killAuraAttack(EntityLivingBase entity) {
    if (entity == null)
      return; 
    if (this.autoDelay.getValBoolean()) {
      if (mc.player.getCooledAttackStrength(0.0F) == 1.0F) {
        processAttack(entity);
        this.target = null;
      } 
    } else {
      int CPS = Utils.random((int)this.minCPS.getValDouble(), (int)this.maxCPS.getValDouble());
      int r1 = Utils.random(1, 50), r2 = Utils.random(1, 60), r3 = Utils.random(1, 70);
      if (this.timer.isDelay(((1000 + r1 - r2 + r3) / CPS))) {
        processAttack(entity);
        this.timer.setLastMS();
        this.target = null;
      } 
    } 
  }
  
  public void processAttack(EntityLivingBase entity) {
    if (isInAttackRange(entity) || !ValidUtils.isInAttackFOV(entity, (int)this.FOV.getValDouble()))
      return; 
    float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.player.getHeldItemMainhand(), entity.getCreatureAttribute());
    if (this.packetReach.getValBoolean()) {
      double posX = entity.posX - 3.5D * Math.cos(Math.toRadians((Utils.getYaw((Entity)entity) + 90.0F)));
      double posZ = entity.posZ - 3.5D * Math.sin(Math.toRadians((Utils.getYaw((Entity)entity) + 90.0F)));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(posX, entity.posY, posZ, Utils.getYaw((Entity)entity), Utils.getPitch((Entity)entity), mc.player.onGround));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketUseEntity((Entity)entity));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
    } else if (this.autoDelay.getValBoolean()) {
      Wrapper.INSTANCE.attack((Entity)entity);
    } else {
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketUseEntity((Entity)entity));
    } 
    Wrapper.INSTANCE.swingArm();
    if (sharpLevel > 0.0F)
      mc.player.onEnchantmentCritical((Entity)entity); 
  }
  
  boolean isPriority(EntityLivingBase entity) {
    return ((this.priority.getValString().equalsIgnoreCase("Closest") && ValidUtils.isClosest(entity, this.target)) || (this.priority.getValString().equalsIgnoreCase("Health") && ValidUtils.isLowHealth(entity, this.target)));
  }
  
  boolean isInAttackRange(EntityLivingBase entity) {
    return this.packetReach.getValBoolean() ? ((entity.getDistance((Entity)mc.player) > this.packetRange.getValDouble())) : ((entity.getDistance((Entity)mc.player) > this.range.getValDouble()));
  }
  
  public boolean check(EntityLivingBase entity) {
    if (entity instanceof net.minecraft.entity.item.EntityArmorStand)
      return false; 
    if (!ValidUtils.isNoScreen())
      return false; 
    if (entity == mc.player)
      return false; 
    if (entity.isDead)
      return false; 
    if (entity.deathTime > 0)
      return false; 
    if (ValidUtils.isBot(entity))
      return false; 
    if (ValidUtils.isFriendEnemy(entity))
      return false; 
    if (!ValidUtils.isInAttackFOV(entity, (int)this.FOV.getValDouble()))
      return false; 
    if (isInAttackRange(entity))
      return false; 
    if (!ValidUtils.pingCheck(entity))
      return false; 
    if (!this.walls.getValBoolean() && 
      !mc.player.canEntityBeSeen((Entity)entity))
      return false; 
    if (this.Animals.getValBoolean() && 
      entity instanceof net.minecraft.entity.passive.IAnimals)
      return isPriority(entity); 
    if (this.Mobs.getValBoolean() && 
      entity instanceof net.minecraft.entity.monster.IMob)
      return isPriority(entity); 
    if (entity instanceof net.minecraft.entity.player.EntityPlayer)
      return isPriority(entity); 
    return false;
  }
  
  public static boolean isInvisible(EntityLivingBase entity) {
    return !entity.isInvisible();
  }
}
