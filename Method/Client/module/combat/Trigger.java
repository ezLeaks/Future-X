package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.ValidUtils;
import Method.Client.utils.system.Wrapper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Trigger extends Module {
  public Setting autoDelay = Main.setmgr.add(new Setting("AutoDelay", this, true));
  
  public Setting advanced = Main.setmgr.add(new Setting("Advanced", this, false));
  
  public Setting minCPS = Main.setmgr.add(new Setting("MinCPS", this, 4.0D, 1.0D, 20.0D, true));
  
  public Setting maxCPS = Main.setmgr.add(new Setting("MaxCPS", this, 8.0D, 1.0D, 20.0D, false));
  
  Setting Mode = Main.setmgr.add(new Setting("Mode", this, "Click", new String[] { "Click", "Attack" }));
  
  public EntityLivingBase target;
  
  public TimerUtils timer = new TimerUtils();
  
  public Trigger() {
    super("Trigger", 0, Category.COMBAT, "Triggers");
  }
  
  public void onDisable() {
    this.target = null;
    super.onDisable();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    updateTarget();
    attackTarget(this.target);
    super.onClientTick(event);
  }
  
  void attackTarget(EntityLivingBase target) {
    if (check(target))
      if (this.autoDelay.getValBoolean()) {
        if (mc.player.getCooledAttackStrength(0.0F) == 1.0F)
          processAttack(target, false); 
      } else {
        int currentCPS = Utils.random((int)this.minCPS.getValDouble(), (int)this.maxCPS.getValDouble());
        if (this.timer.isDelay((1000 / currentCPS))) {
          processAttack(target, true);
          this.timer.setLastMS();
        } 
      }  
  }
  
  public void processAttack(EntityLivingBase entity, boolean packet) {
    float sharpLevel = EnchantmentHelper.getModifierForCreature(mc.player.getHeldItemMainhand(), this.target.getCreatureAttribute());
    if (this.Mode.getValString().equalsIgnoreCase("Click")) {
      mc.player.swingArm(EnumHand.MAIN_HAND);
    } else {
      if (packet) {
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketUseEntity((Entity)this.target));
      } else {
        Wrapper.INSTANCE.attack((Entity)this.target);
      } 
      Wrapper.INSTANCE.swingArm();
      if (sharpLevel > 0.0F)
        mc.player.onEnchantmentCritical((Entity)this.target); 
    } 
  }
  
  void updateTarget() {
    RayTraceResult object = (Wrapper.INSTANCE.mc()).objectMouseOver;
    if (object == null)
      return; 
    EntityLivingBase entity = null;
    if (this.target != entity)
      this.target = null; 
    if (object.typeOfHit == RayTraceResult.Type.ENTITY) {
      if (object.entityHit instanceof EntityLivingBase) {
        entity = (EntityLivingBase)object.entityHit;
        this.target = entity;
      } 
    } else if (object.typeOfHit != RayTraceResult.Type.ENTITY && this.advanced.getValBoolean()) {
      entity = getClosestEntity();
    } 
    if (entity != null)
      this.target = entity; 
  }
  
  EntityLivingBase getClosestEntity() {
    EntityLivingBase closestEntity = null;
    for (Object o : mc.world.loadedEntityList) {
      if (o instanceof EntityLivingBase && !(o instanceof net.minecraft.entity.item.EntityArmorStand)) {
        EntityLivingBase entity = (EntityLivingBase)o;
        if (check(entity) && (
          closestEntity == null || mc.player.getDistance((Entity)entity) < mc.player.getDistance((Entity)closestEntity)))
          closestEntity = entity; 
      } 
    } 
    return closestEntity;
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
    if (ValidUtils.isBot(entity))
      return false; 
    if (ValidUtils.isFriendEnemy(entity))
      return false; 
    if (this.advanced.getValBoolean()) {
      if (!ValidUtils.isInAttackFOV(entity, 50))
        return false; 
      if (!ValidUtils.isInAttackRange(entity, 4.7F))
        return false; 
    } 
    if (!ValidUtils.pingCheck(entity))
      return false; 
    return mc.player.canEntityBeSeen((Entity)entity);
  }
}
