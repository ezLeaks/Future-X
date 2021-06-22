package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.PlayerIdentity;
import java.util.LinkedHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class MobOwner extends Module {
  Setting Speedh;
  
  Setting Jumph;
  
  Setting Hpm;
  
  public MobOwner() {
    super("MobOwner", 0, Category.RENDER, "MobOwner");
    this.Speedh = Main.setmgr.add(new Setting("Speed horse", this, false));
    this.Jumph = Main.setmgr.add(new Setting("Jump Horse", this, false));
    this.Hpm = Main.setmgr.add(new Setting("Hp", this, false));
  }
  
  public static LinkedHashMap<String, PlayerIdentity> identityCacheMap = new LinkedHashMap<>();
  
  public static PlayerIdentity getPlayerIdentity(String UUID) {
    if (identityCacheMap.containsKey(UUID))
      return identityCacheMap.get(UUID); 
    return new PlayerIdentity(UUID);
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    for (Entity entity : mc.world.getLoadedEntityList()) {
      if (entity instanceof EntityTameable) {
        EntityTameable tameableEntity = (EntityTameable)entity;
        if (tameableEntity.isTamed() && tameableEntity.getOwnerId() != null) {
          tameableEntity.setAlwaysRenderNameTag(true);
          String Hp = this.Hpm.getValBoolean() ? ("\n" + ((EntityTameable)entity).getHealth()) : "";
          PlayerIdentity identity = getPlayerIdentity(tameableEntity.getOwnerId().toString());
          tameableEntity.setCustomNameTag("Owned by " + identity.getDisplayName() + Hp);
        } 
      } 
      if (entity instanceof AbstractHorse) {
        AbstractHorse tameableEntity = (AbstractHorse)entity;
        if (tameableEntity.isTame() && tameableEntity.getOwnerUniqueId() != null) {
          String Speed = this.Speedh.getValBoolean() ? (" Speed: " + (((AbstractHorse)entity).getAIMoveSpeed() * 43.17D)) : "";
          String Hp = this.Hpm.getValBoolean() ? (" HP: " + ((AbstractHorse)entity).getHealth()) : "";
          String Jump = this.Jumph.getValBoolean() ? (" Jump: " + (-0.1817584952D * Math.pow(((AbstractHorse)entity).getHorseJumpStrength(), 3.0D) + 3.689713992D * Math.pow(((AbstractHorse)entity).getHorseJumpStrength(), 2.0D) + 2.128599134D * ((AbstractHorse)entity).getHorseJumpStrength() - 0.343930367D)) : "";
          tameableEntity.setAlwaysRenderNameTag(true);
          PlayerIdentity identity = getPlayerIdentity(tameableEntity.getOwnerUniqueId().toString());
          tameableEntity.setCustomNameTag("Owned by " + identity.getDisplayName() + Speed + Jump + Hp);
        } 
      } 
    } 
    super.onRenderWorldLast(event);
  }
}
