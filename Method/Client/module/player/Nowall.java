package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Nowall extends Module {
  Setting Storage;
  
  Setting Mine;
  
  private boolean clicked;
  
  private boolean focus;
  
  public Nowall() {
    super("Nowall", 0, Category.PLAYER, "Click through walls");
    this.Storage = Main.setmgr.add(new Setting("Storage", this, true));
    this.Mine = Main.setmgr.add(new Setting("Mine", this, false));
    this.focus = false;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.Mine.getValBoolean()) {
      mc.world.loadedEntityList.stream()
        .filter(entity -> entity instanceof EntityLivingBase)
        .filter(entity -> (mc.player == entity))
        .map(entity -> (EntityLivingBase)entity)
        .filter(entity -> !entity.isDead)
        .forEach(this::process);
      RayTraceResult normal_result = mc.objectMouseOver;
      if (normal_result != null)
        this.focus = (normal_result.typeOfHit == RayTraceResult.Type.ENTITY); 
    } 
  }
  
  private void process(EntityLivingBase event) {
    RayTraceResult bypass_entity_result = event.rayTrace(6.0D, mc.getRenderPartialTicks());
    if (bypass_entity_result != null && this.focus && 
      bypass_entity_result.typeOfHit == RayTraceResult.Type.BLOCK) {
      BlockPos block_pos = bypass_entity_result.getBlockPos();
      if (mc.gameSettings.keyBindAttack.isKeyDown())
        mc.playerController.onPlayerDamageBlock(block_pos, EnumFacing.UP); 
    } 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && this.Storage.getValBoolean() && 
      packet instanceof CPacketPlayerTryUseItemOnBlock) {
      if (this.clicked) {
        this.clicked = false;
        return true;
      } 
      CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)packet;
      if (mc.currentScreen == null) {
        Block block = mc.world.getBlockState(packet2.getPos()).getBlock();
        BlockPos usable = findUsableBlock(packet2.getHand(), packet2.getDirection(), packet2.getFacingX(), packet2.getFacingY(), packet2.getFacingZ());
        if (block.onBlockActivated((World)mc.world, packet2.getPos(), mc.world.getBlockState(packet2.getPos()), (EntityPlayer)mc.player, packet2.getHand(), packet2.getDirection(), packet2.getFacingX(), packet2.getFacingY(), packet2.getFacingZ()))
          return true; 
        if (usable != null) {
          mc.player.swingArm(packet2.getHand());
          mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(usable, packet2.getDirection(), packet2.getHand(), packet2.getFacingX(), packet2.getFacingY(), packet2.getFacingZ()));
          this.clicked = true;
        } else {
          Entity usableEntity = findUsableEntity();
          if (usableEntity != null) {
            mc.player.connection.sendPacket((Packet)new CPacketUseEntity(usableEntity, packet2.getHand()));
            this.clicked = true;
          } 
        } 
      } 
    } 
    return true;
  }
  
  private Entity findUsableEntity() {
    Entity entity = null;
    for (int i = 0; i <= mc.playerController.getBlockReachDistance(); i++) {
      AxisAlignedBB bb = traceToBlock(i, mc.getRenderPartialTicks());
      float maxDist = mc.playerController.getBlockReachDistance();
      for (Entity e : mc.world.getEntitiesWithinAABBExcludingEntity((Entity)mc.player, bb)) {
        float currentDist = mc.player.getDistance(e);
        if (currentDist <= maxDist) {
          entity = e;
          maxDist = currentDist;
        } 
      } 
    } 
    return entity;
  }
  
  private BlockPos findUsableBlock(EnumHand hand, EnumFacing dir, float x, float y, float z) {
    for (int i = 0; i <= mc.playerController.getBlockReachDistance(); i++) {
      AxisAlignedBB bb = traceToBlock(i, mc.getRenderPartialTicks());
      BlockPos pos = new BlockPos(bb.minX, bb.minY, bb.minZ);
      Block block = mc.world.getBlockState(pos).getBlock();
      if (block.onBlockActivated((World)mc.world, pos, mc.world.getBlockState(pos), (EntityPlayer)mc.player, hand, dir, x, y, z))
        return new BlockPos((Vec3i)pos); 
    } 
    return null;
  }
  
  private AxisAlignedBB traceToBlock(double dist, float partialTicks) {
    Vec3d pos = mc.player.getPositionEyes(partialTicks);
    Vec3d angles = mc.player.getLook(partialTicks);
    Vec3d end = pos.add(angles.x * dist, angles.y * dist, angles.z * dist);
    return new AxisAlignedBB(end.x, end.y, end.z, end.x + 1.0D, end.y + 1.0D, end.z + 1.0D);
  }
}
