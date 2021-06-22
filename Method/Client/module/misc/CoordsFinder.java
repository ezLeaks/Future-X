package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.BlockPos;

public class CoordsFinder extends Module {
  Setting BossDetector;
  
  Setting logLightning;
  
  Setting minLightningDist;
  
  Setting logWolf;
  
  Setting minWolfDist;
  
  Setting logPlayer;
  
  Setting minPlayerDist;
  
  public CoordsFinder() {
    super("CoordsFinder", 0, Category.MISC, "Coords Finder exploit");
    this.BossDetector = Main.setmgr.add(new Setting("Boss detector", this, true));
    this.logLightning = Main.setmgr.add(new Setting("logLightning", this, true));
    this.minLightningDist = Main.setmgr.add(new Setting("minLightningDist", this, 32.0D, 0.0D, 100.0D, true, this.logLightning, 3));
    this.logWolf = Main.setmgr.add(new Setting("logWolf", this, true));
    this.minWolfDist = Main.setmgr.add(new Setting("minWolfDist", this, 256.0D, 0.0D, 1024.0D, true, this.logWolf, 3));
    this.logPlayer = Main.setmgr.add(new Setting("logPlayer", this, true));
    this.minPlayerDist = Main.setmgr.add(new Setting("minPlayerDist", this, 256.0D, 0.0D, 1024.0D, true, this.logPlayer, 3));
  }
  
  private boolean pastDistance(EntityPlayer player, BlockPos pos, double dist) {
    return (player.getDistanceSqToCenter(pos) >= Math.pow(dist, 2.0D));
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.BossDetector.getValBoolean() && 
      packet instanceof SPacketEffect) {
      SPacketEffect sPacketEffect = (SPacketEffect)packet;
      BlockPos pos = new BlockPos((sPacketEffect.getSoundPos()).x, (sPacketEffect.getSoundPos()).y, (sPacketEffect.getSoundPos()).z);
      switch (sPacketEffect.getSoundType()) {
        case 1023:
          ChatUtils.message("Wither Spawned " + pos.x + " Y:" + pos.y + " Z:" + pos.z);
          break;
        case 1028:
          ChatUtils.message("Ender Dragon Defeated " + pos.x + " Y:" + pos.y + " Z:" + pos.z);
          break;
        case 1038:
          ChatUtils.message("End Portal Activated " + pos.x + " Y:" + pos.y + " Z:" + pos.z);
          break;
      } 
    } 
    if (this.logLightning.getValBoolean() && packet instanceof SPacketSoundEffect) {
      SPacketSoundEffect packet2 = (SPacketSoundEffect)packet;
      if (packet2.getSound() != SoundEvents.ENTITY_LIGHTNING_THUNDER)
        return true; 
      BlockPos pos = new BlockPos(packet2.getX(), packet2.getY(), packet2.getZ());
      if (pastDistance((EntityPlayer)mc.player, pos, this.minLightningDist.getValDouble()))
        ChatUtils.warning("Lightning strike At X:" + pos.x + " Y:" + pos.y + " Z:" + pos.z); 
    } else if (packet instanceof SPacketEntityTeleport) {
      SPacketEntityTeleport sPacketEntityTeleport = (SPacketEntityTeleport)packet;
      Entity teleporting = mc.world.getEntityByID(sPacketEntityTeleport.getEntityId());
      BlockPos pos = new BlockPos(sPacketEntityTeleport.getX(), sPacketEntityTeleport.getY(), sPacketEntityTeleport.getZ());
      if (this.logWolf.getValBoolean() && teleporting instanceof net.minecraft.entity.passive.EntityWolf) {
        if (pastDistance((EntityPlayer)mc.player, pos, this.minWolfDist.getValDouble()))
          ChatUtils.warning("Wolf Teleport At X:" + pos.x + " Y:" + pos.y + " Z:" + pos.z); 
      } else if (this.logPlayer.getValBoolean() && teleporting instanceof EntityPlayer && 
        pastDistance((EntityPlayer)mc.player, pos, this.minPlayerDist.getValDouble())) {
        ChatUtils.warning(teleporting.getName() + " Teleported to X:" + pos.x + " Y:" + pos.y + " Z:" + pos.z);
      } 
    } 
    return true;
  }
}
