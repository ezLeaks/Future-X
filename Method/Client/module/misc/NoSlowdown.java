package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;

public class NoSlowdown extends Module {
  private boolean sneaking;
  
  Setting web;
  
  Setting Webfall;
  
  Setting Eat;
  
  Setting Slowdownbypass;
  
  Setting Breakdelay;
  
  Setting Slimeblock;
  
  Setting NoIceSlip;
  
  public NoSlowdown() {
    super("NoSlowdown", 0, Category.MISC, "No more slow");
    this.web = Main.setmgr.add(new Setting("webs", this, false));
    this.Webfall = Main.setmgr.add(new Setting("Webfall", this, false));
    this.Eat = Main.setmgr.add(new Setting("Eat", this, false));
    this.Slowdownbypass = Main.setmgr.add(new Setting("Slowdown Bypass", this, false));
    this.Breakdelay = Main.setmgr.add(new Setting("Breakdelay", this, false));
    this.Slimeblock = Main.setmgr.add(new Setting("Slimeblock", this, false));
    this.NoIceSlip = Main.setmgr.add(new Setting("NoIceSlip", this, false));
  }
  
  public void onDisable() {
    Blocks.ICE.slipperiness = 0.98F;
    Blocks.PACKED_ICE.slipperiness = 0.98F;
    Blocks.FROSTED_ICE.slipperiness = 0.98F;
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    if (this.Slowdownbypass.getValBoolean()) {
      if (this.sneaking && !mc.player.isHandActive()) {
        mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        this.sneaking = false;
      } 
      if (!this.sneaking && mc.player.isHandActive()) {
        mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
        this.sneaking = true;
      } 
    } 
    if (this.NoIceSlip.getValBoolean()) {
      Blocks.ICE.slipperiness = 0.0F;
      Blocks.PACKED_ICE.slipperiness = 0.0F;
      Blocks.FROSTED_ICE.slipperiness = 0.0F;
    } 
    if (this.Slimeblock.getValBoolean()) {
      BlockPos pos = new BlockPos(Math.floor(mc.player.posX), Math.ceil(mc.player.posY), Math.floor(mc.player.posZ));
      if (mc.world.getBlockState(pos.add(0, -1, 0)).getBlock() instanceof net.minecraft.block.BlockSlime && mc.player.onGround)
        mc.player.motionY = 1.5D; 
    } 
    if (mc.player.isHandActive() && this.Eat.getValBoolean()) {
      mc.player.moveForward *= 5.0F;
      mc.player.moveStrafing *= 5.0F;
      mc.playerController.syncCurrentPlayItem();
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
    } 
    if (this.web.getValBoolean())
      mc.player.isInWeb = false; 
    if (this.Webfall.getValBoolean() && 
      !mc.player.onGround && mc.player.fallDistance > 3.0F)
      mc.player.motionY = -0.22000000000000003D; 
    if (this.Breakdelay.getValBoolean())
      mc.playerController.blockHitDelay = 0; 
  }
}
