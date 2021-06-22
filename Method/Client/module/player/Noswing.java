package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerDamageBlockEvent;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Noswing extends Module {
  Setting mode = Main.setmgr.add(new Setting("No swing", this, "Vanilla", new String[] { "Vanilla", "Packet", "BlockClick", "PacketSwing", "Clientonly" }));
  
  Setting Nobreakani = Main.setmgr.add(new Setting("Nobreakani", this, false));
  
  EnumFacing lastFacing;
  
  BlockPos lastPos;
  
  boolean isMining;
  
  public Noswing() {
    super("Noswing", 0, Category.PLAYER, "Noswing");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Clientonly")) {
      ItemRenderer itemRenderer = mc.entityRenderer.itemRenderer;
      itemRenderer.equippedProgressMainHand = 1.0F;
      itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
    } 
    if (this.mode.getValString().equalsIgnoreCase("Vanilla") && 
      mc.player.swingProgress <= 0.0F)
      mc.player.swingProgressInt = 5; 
    if (this.Nobreakani.getValBoolean())
      if (mc.gameSettings.keyBindAttack.isKeyDown()) {
        resetMining();
      } else if (this.isMining && this.lastPos != null && this.lastFacing != null) {
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing));
      }  
  }
  
  public void resetMining() {
    this.isMining = false;
    this.lastPos = null;
    this.lastFacing = null;
  }
  
  public void DamageBlock(PlayerDamageBlockEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("PacketSwing"))
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, event.getPos(), event.getFacing())); 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof CPacketPlayerDigging && side == Connection.Side.OUT && this.Nobreakani.getValBoolean()) {
      CPacketPlayerDigging packet2 = (CPacketPlayerDigging)packet;
      for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(packet2.getPosition()))) {
        if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal) {
          resetMining();
          continue;
        } 
        if (entity instanceof net.minecraft.entity.EntityLivingBase)
          resetMining(); 
      } 
      if (packet2.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
        this.isMining = true;
        setMiningInfo(packet2.getPosition(), packet2.getFacing());
      } 
      if (packet2.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK))
        resetMining(); 
    } 
    return (!(packet instanceof net.minecraft.network.play.client.CPacketAnimation) || !this.mode.getValString().equalsIgnoreCase("packet"));
  }
  
  public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
    if (this.mode.getValString().equalsIgnoreCase("BlockClick"))
      Blockclick((Event)event); 
  }
  
  public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    if (this.mode.getValString().equalsIgnoreCase("BlockClick"))
      Blockclick((Event)event); 
  }
  
  void Blockclick(Event event) {
    if (mc.objectMouseOver.entityHit == null) {
      BlockPos blockPos = mc.objectMouseOver.getBlockPos();
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.UP));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.UP));
      event.setCanceled(true);
    } 
    if (mc.objectMouseOver.entityHit != null) {
      mc.playerController.attackEntity((EntityPlayer)mc.player, mc.objectMouseOver.entityHit);
      event.setCanceled(true);
    } 
  }
  
  private void setMiningInfo(BlockPos blockPos, EnumFacing enumFacing) {
    this.lastPos = blockPos;
    this.lastFacing = enumFacing;
  }
}
