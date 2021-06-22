package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.render.Trail;
import Method.Client.utils.system.Connection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HitEffects extends Module {
  Setting Lightning;
  
  Setting Sounds;
  
  Setting Mode;
  
  Setting Yoffset;
  
  public HitEffects() {
    super("HitEffects", 0, Category.MISC, "Effects on Hit");
    this.Lightning = Main.setmgr.add(new Setting("Lightning", this, false));
    this.Sounds = Main.setmgr.add(new Setting("Sounds", this, false));
    this.Mode = Main.setmgr.add(new Setting("Mode", this, "SMOKE", new String[] { 
            "HEART", "FIREWORK", "FLAME", "CLOUD", "WATER", "LAVA", "SLIME", "EXPLOSION", "MAGIC", "REDSTONE", 
            "SWORD" }));
    this.Yoffset = Main.setmgr.add(new Setting("YPos offset", this, 0.0D, 0.0D, 2.0D, false));
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof CPacketUseEntity) {
      CPacketUseEntity packet2 = (CPacketUseEntity)packet;
      if (packet2.getAction() == CPacketUseEntity.Action.ATTACK) {
        Entity entity = ((CPacketUseEntity)packet).getEntityFromWorld((World)mc.world);
        if (entity != null && !entity.isDead) {
          if (this.Lightning.getValBoolean())
            mc.world.spawnEntity((Entity)new EntityLightningBolt((World)mc.world, entity.posX, entity.posY, entity.posZ, true)); 
          if (this.Sounds.getValBoolean()) {
            SoundEvent thunderSound = new SoundEvent(new ResourceLocation("minecraft", "entity.lightning.thunder"));
            SoundEvent lightningImpactSound = new SoundEvent(new ResourceLocation("minecraft", "entity.lightning.impact"));
            mc.world.playSound((EntityPlayer)mc.player, new BlockPos(entity.posX, entity.posY, entity.posZ), thunderSound, SoundCategory.WEATHER, 1.0F, 1.0F);
            mc.world.playSound((EntityPlayer)mc.player, new BlockPos(entity.posX, entity.posY, entity.posZ), lightningImpactSound, SoundCategory.WEATHER, 1.0F, 1.0F);
          } 
          for (int i = 0; i < 5; i++)
            Trail.Renderparticle((EntityLivingBase)entity, this.Mode.getValString(), this.Yoffset.getValDouble()); 
        } 
      } 
    } 
    return true;
  }
}
