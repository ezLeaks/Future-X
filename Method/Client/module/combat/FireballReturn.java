package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FireballReturn extends Module {
  Setting yaw = Main.setmgr.add(new Setting("yaw", this, 25.0D, 0.0D, 50.0D, false));
  
  Setting pitch = Main.setmgr.add(new Setting("pitch", this, 25.0D, 0.0D, 50.0D, false));
  
  Setting range = Main.setmgr.add(new Setting("range", this, 10.0D, 0.1D, 10.0D, false));
  
  public EntityFireball target;
  
  public TimerUtils timer = new TimerUtils();
  
  public FireballReturn() {
    super("FireballReturn", 0, Category.COMBAT, "Returns Fireballs to sender");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    updateTarget();
    attackTarget();
    super.onClientTick(event);
  }
  
  void updateTarget() {
    for (Object object : mc.world.loadedEntityList) {
      if (object instanceof EntityFireball) {
        EntityFireball entity = (EntityFireball)object;
        if (isInAttackRange(entity) && !entity.isDead && !entity.onGround && entity.canBeAttackedWithItem())
          this.target = entity; 
      } 
    } 
  }
  
  void attackTarget() {
    if (this.target == null)
      return; 
    Utils.getNeededRotations(this.target.getPositionVector(), (float)this.yaw.getValDouble(), (float)this.pitch.getValDouble());
    int currentCPS = Utils.random(4, 7);
    if (this.timer.isDelay((1000 / currentCPS))) {
      mc.clickMouse();
      this.timer.setLastMS();
      this.target = null;
    } 
  }
  
  public boolean isInAttackRange(EntityFireball entity) {
    return (entity.getDistance((Entity)mc.player) <= this.range.getValDouble());
  }
}
