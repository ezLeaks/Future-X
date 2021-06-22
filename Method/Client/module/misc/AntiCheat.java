package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiCheat extends Module {
  Setting Experimental = Main.setmgr.add(new Setting("Experimental", this, true));
  
  public AntiCheat() {
    super("CheatDetect", 0, Category.MISC, "AntiCheat For others!");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.player.ticksExisted % 5 == 0)
      for (Entity entityplayer : mc.world.loadedEntityList) {
        if (entityplayer instanceof net.minecraft.entity.player.EntityPlayer && !entityplayer.getName().equalsIgnoreCase(mc.player.getName()) && !FriendManager.isFriend(entityplayer.getName())) {
          EntityLivingBase entity = (EntityLivingBase)entityplayer;
          if (entity.ticksExisted > 40) {
            if (entity.rotationPitch > 90.0F || entity.rotationPitch < -90.0F)
              ChatUtils.message(entity.getName() + " flagged Invalid Head Pitch "); 
            if (Math.abs(Math.abs(entity.posY) - Math.abs(entity.prevPosY)) > 1.0D && !entity.onGround && !entity.collidedVertically && entity.posY > entity.prevPosY && !entity.isOnLadder() && !entity.isInWater())
              ChatUtils.message(entity.getName() + " flagged Ascension "); 
            if (Utils.isMoving((Entity)entity) && ((entity.cameraPitch == entity.prevCameraPitch && entity.cameraPitch == (int)entity.cameraPitch) || (entity.rotationYaw == entity.prevRotationYaw && entity.rotationYaw == (int)entity.prevRotationYaw)))
              ChatUtils.message(entity.getName() + " flagged  Yaw/Pitch [AA]"); 
            if (this.Experimental.getValBoolean()) {
              double yawDelta = Math.abs(Math.abs(entity.rotationYaw) - Math.abs(entity.prevRotationYaw));
              double pitchDelta = Math.abs(Math.abs(entity.rotationPitch) - Math.abs(entity.prevRotationPitch));
              if (entity.rotationYaw == Math.round(entity.rotationYaw) && entity.rotationYaw != 0.0F && yawDelta > 10.0D)
                ChatUtils.message(entity.getName() + " flagged KillAura [AA]"); 
              if (entity.rotationPitch == Math.round(entity.rotationPitch) && entity.rotationPitch != 90.0F && entity.rotationPitch != -90.0F && pitchDelta > 10.0D)
                ChatUtils.message(entity.getName() + " flagged KillAura [AB]"); 
              if (entity.isSprinting() && Utils.isMoving((Entity)entity) && entity.moveForward < 0.0F)
                ChatUtils.message(entity.getName() + " flagged Omni-Sprint "); 
              if (entity.isRiding() && 
                entity.getRidingEntity() instanceof net.minecraft.entity.item.EntityBoat && 
                !entity.inWater)
                ChatUtils.message(entity.getName() + " flagged Boat-Fly "); 
              if (entity.isOverWater() && 
                entity.isSprinting())
                ChatUtils.message(entity.getName() + " flagged Jesus "); 
              if (entity.stepHeight > 1.0F)
                ChatUtils.message(entity.getName() + " flagged Step "); 
              if (Math.abs(entity.posX - entity.lastTickPosX) > 0.42D || 
                Math.abs(entity.posZ - entity.lastTickPosZ) > 0.42D)
                ChatUtils.message(entity.getName() + " flagged Speed "); 
              if (entity.isActiveItemStackBlocking() && (
                Math.abs(entity.posX - entity.lastTickPosX) > 0.3D || Math.abs(entity.posZ - entity.lastTickPosZ) > 0.3D))
                ChatUtils.message(entity.getName() + " flagged NoSlow "); 
              if (!entity.isElytraFlying() && !mc.world.checkBlockCollision(entity.getEntityBoundingBox().offset(0.0D, -1.1D, 0.0D)) && entity.prevPosY < entity.posY)
                ChatUtils.message(entity.getName() + " flagged Flying "); 
              if (entity.hurtResistantTime > 6 && entity.hurtResistantTime < 12 && entity.lastTickPosX == entity.posX && entity.posZ == entity.lastTickPosZ && 
                
                !mc.world.checkBlockCollision(entity.getEntityBoundingBox().expand(0.05D, 0.0D, 0.05D)))
                ChatUtils.message(entity.getName() + " flagged KnockBack "); 
              if (entity.hurtResistantTime > 6 && entity.hurtResistantTime < 12 && entity.lastTickPosY == entity.posY)
                ChatUtils.message(entity.getName() + " flagged KnockBack "); 
              if (entity.onGround && entity.isElytraFlying())
                ChatUtils.message(entity.getName() + " flagged Ground Elytra "); 
            } 
          } 
        } 
      }  
  }
}
