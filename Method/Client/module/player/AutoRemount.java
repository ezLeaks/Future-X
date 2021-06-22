package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import java.util.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoRemount extends Module {
  Setting Bypass = Main.setmgr.add(new Setting("Bypass", this, true));
  
  Setting boat = Main.setmgr.add(new Setting("boat", this, true));
  
  Setting Minecart = Main.setmgr.add(new Setting("Minecart", this, true));
  
  Setting horse = Main.setmgr.add(new Setting("horse", this, true));
  
  Setting skeletonHorse = Main.setmgr.add(new Setting("skeletonHorse", this, true));
  
  Setting donkey = Main.setmgr.add(new Setting("donkey", this, true));
  
  Setting mule = Main.setmgr.add(new Setting("mule", this, true));
  
  Setting pig = Main.setmgr.add(new Setting("pig ", this, true));
  
  Setting llama = Main.setmgr.add(new Setting("llama", this, true));
  
  Setting range = Main.setmgr.add(new Setting("range", this, 2.0D, 0.0D, 10.0D, true));
  
  public AutoRemount() {
    super("AutoRemount", 0, Category.PLAYER, "AutoRemount");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (!mc.player.isRiding())
      mc.world.loadedEntityList.stream()
        .filter(this::isValidEntity)
        .min(Comparator.comparing(en -> Float.valueOf(mc.player.getDistance(en))))
        .ifPresent(entity -> mc.playerController.interactWithEntity((EntityPlayer)mc.player, entity, EnumHand.MAIN_HAND)); 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof CPacketUseEntity && this.Bypass.getValBoolean()) {
      CPacketUseEntity packet2 = (CPacketUseEntity)packet;
      if (isValidEntity(packet2.getEntityFromWorld((World)mc.world)))
        return !packet2.action.equals(CPacketUseEntity.Action.INTERACT_AT); 
    } 
    return true;
  }
  
  private boolean isValidEntity(Entity entity) {
    if (mc.player.isRiding())
      return false; 
    if (entity.getDistance((Entity)mc.player) > this.range.getValDouble())
      return false; 
    if (entity instanceof AbstractHorse) {
      AbstractHorse horse = (AbstractHorse)entity;
      if (horse.isChild())
        return false; 
    } 
    if (entity instanceof EntityDonkey) {
      EntityDonkey entityDonkey = (EntityDonkey)entity;
      if (entityDonkey.isChild())
        return false; 
    } 
    if (entity instanceof EntityMule) {
      EntityMule entityDonkey = (EntityMule)entity;
      if (entityDonkey.isChild())
        return false; 
    } 
    if (entity instanceof net.minecraft.entity.item.EntityBoat && this.boat.getValBoolean())
      return true; 
    if (entity instanceof net.minecraft.entity.item.EntityMinecart && this.Minecart.getValBoolean())
      return true; 
    if (entity instanceof net.minecraft.entity.passive.EntityHorse && this.horse.getValBoolean())
      return true; 
    if (entity instanceof net.minecraft.entity.passive.EntitySkeletonHorse && this.skeletonHorse.getValBoolean())
      return true; 
    if (entity instanceof EntityDonkey && this.donkey.getValBoolean())
      return true; 
    if (entity instanceof EntityMule && this.mule.getValBoolean())
      return true; 
    if (entity instanceof EntityPig && this.pig.getValBoolean()) {
      EntityPig pig = (EntityPig)entity;
      if (pig.isChild())
        return false; 
      return pig.getSaddled();
    } 
    return (entity instanceof net.minecraft.entity.passive.EntityLlama && this.llama.getValBoolean());
  }
}
