package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class Trail extends Module {
  Setting Self = Main.setmgr.add(new Setting("Self", this, true));
  
  Setting Player = Main.setmgr.add(new Setting("Player", this, true));
  
  Setting Mob = Main.setmgr.add(new Setting("Mob", this, false));
  
  Setting Hostile = Main.setmgr.add(new Setting("Hostile", this, false));
  
  Setting Tickrate = Main.setmgr.add(new Setting("Per Sec", this, 10.0D, 2.0D, 20.0D, true));
  
  Setting Yoffset = Main.setmgr.add(new Setting("Y Offset", this, 0.0D, 0.0D, 2.0D, false));
  
  Setting Trail = Main.setmgr.add(new Setting("Mode", this, "SMOKE", new String[] { 
          "HEART", "FIREWORK", "FLAME", "CLOUD", "WATER", "LAVA", "SLIME", "EXPLOSION", "MAGIC", "REDSTONE", 
          "SWORD" }));
  
  public Trail() {
    super("Trail", 0, Category.RENDER, "Trail");
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (mc.player.ticksExisted % this.Tickrate.getValDouble() == 0.0D)
      for (Entity object : mc.world.loadedEntityList) {
        if (object instanceof EntityLivingBase) {
          EntityLivingBase entity = (EntityLivingBase)object;
          if (entity instanceof net.minecraft.entity.passive.IAnimals && this.Mob.getValBoolean())
            Renderparticle(entity, this.Trail.getValString(), this.Yoffset.getValDouble()); 
          if (entity instanceof net.minecraft.entity.monster.IMob && this.Hostile.getValBoolean())
            Renderparticle(entity, this.Trail.getValString(), this.Yoffset.getValDouble()); 
          if (entity instanceof net.minecraft.entity.player.EntityPlayer && this.Player.getValBoolean() && entity != mc.player)
            Renderparticle(entity, this.Trail.getValString(), this.Yoffset.getValDouble()); 
          if (entity == mc.player && this.Self.getValBoolean())
            Renderparticle(entity, this.Trail.getValString(), this.Yoffset.getValDouble()); 
        } 
      }  
  }
  
  public static void Renderparticle(EntityLivingBase entity, String s, double yoffset) {
    try {
      switch (s) {
        case "HEART":
          mc.world.spawnParticle(EnumParticleTypes.HEART, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "SWORD":
          mc.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          mc.world.spawnParticle(EnumParticleTypes.CRIT, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          mc.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          mc.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          mc.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "REDSTONE":
          mc.world.spawnParticle(EnumParticleTypes.REDSTONE, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "MAGIC":
          mc.world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "LAVA":
          mc.world.spawnParticle(EnumParticleTypes.LAVA, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          mc.world.spawnParticle(EnumParticleTypes.DRIP_LAVA, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "SMOKE":
          mc.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "CLOUD":
          mc.world.spawnParticle(EnumParticleTypes.CLOUD, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "FLAME":
          mc.world.spawnParticle(EnumParticleTypes.FLAME, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "SLIME":
          mc.world.spawnParticle(EnumParticleTypes.SLIME, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "EXPLOSION":
          mc.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "WATER":
          mc.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          mc.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          return;
        case "FIREWORK":
          mc.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, entity.posX, entity.posY + 0.01D + yoffset, entity.posZ, entity.motionX * 0.4D, entity.motionY * 0.4D, entity.motionZ * 0.4D, new int[0]);
          break;
      } 
    } catch (Exception exception) {}
  }
}
