package Method.Client.utils;

import Method.Client.managers.FriendManager;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.combat.AntiBot;
import Method.Client.module.player.NoEffect;
import Method.Client.utils.system.Wrapper;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class ValidUtils {
  public static boolean pingCheck(EntityLivingBase entity) {
    if (ModuleManager.getModuleByName("AntiBot").isToggled() && entity instanceof EntityPlayer) {
      ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(Wrapper.INSTANCE.mc().getConnection())).getPlayerInfo(entity.getUniqueID());
      return (((NetHandlerPlayClient)Objects.requireNonNull((T)Wrapper.INSTANCE.mc().getConnection())).getPlayerInfo(entity.getUniqueID()).getResponseTime() <= 5);
    } 
    return true;
  }
  
  public static boolean isInAttackFOV(EntityLivingBase entity, int fov) {
    return (Utils.getDistanceFromMouse(entity) <= fov);
  }
  
  public static boolean isClosest(EntityLivingBase entity, EntityLivingBase entityPriority) {
    return (entityPriority == null || Wrapper.INSTANCE.player().getDistance((Entity)entity) < Wrapper.INSTANCE.player().getDistance((Entity)entityPriority));
  }
  
  public static boolean isLowHealth(EntityLivingBase entity, EntityLivingBase entityPriority) {
    return (entityPriority == null || entity.getHealth() < entityPriority.getHealth());
  }
  
  public static boolean isInAttackRange(EntityLivingBase entity, float range) {
    return (entity.getDistance((Entity)Wrapper.INSTANCE.player()) <= range);
  }
  
  public static boolean isBot(EntityLivingBase entity) {
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer)entity;
      Module module = ModuleManager.getModuleByName("AntiBot");
      return (module.isToggled() && AntiBot.isBot(player));
    } 
    return false;
  }
  
  public static boolean isFriendEnemy(EntityLivingBase entity) {
    if (entity instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer)entity;
      String ID = Utils.getPlayerName(player);
      return FriendManager.friendsList.contains(ID);
    } 
    return true;
  }
  
  public static boolean isNoScreen() {
    if (ModuleManager.getModuleByName("NoEffect").isToggled()) {
      if (NoEffect.NoScreenEvents.getValBoolean())
        return !Utils.checkScreen(); 
      return false;
    } 
    return true;
  }
}
