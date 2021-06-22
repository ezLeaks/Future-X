package Method.Client.utils;

import Method.Client.utils.system.Wrapper;
import java.util.LinkedList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockUtils {
  private static final Minecraft mc = Wrapper.INSTANCE.mc();
  
  public static Material getMaterial(BlockPos pos) {
    return getState(pos).getMaterial();
  }
  
  public static LinkedList<BlockPos> blocks = new LinkedList<>();
  
  public static IBlockState getState(BlockPos pos) {
    return mc.world.getBlockState(pos);
  }
  
  public static Block getBlock(BlockPos pos) {
    return getState(pos).getBlock();
  }
  
  public static boolean canBeClicked(BlockPos pos) {
    return getBlock(pos).canCollideCheck(getState(pos), false);
  }
  
  public static float getHardness(BlockPos pos) {
    return getState(pos).getPlayerRelativeBlockHardness((EntityPlayer)Wrapper.INSTANCE.player(), (World)Wrapper.INSTANCE.world(), pos);
  }
  
  public static boolean breakBlockSimple(BlockPos pos) {
    EnumFacing side = null;
    EnumFacing[] sides = EnumFacing.values();
    Vec3d eyesPos = Utils.getEyesPos();
    Vec3d relCenter = getState(pos).getBoundingBox((IBlockAccess)Wrapper.INSTANCE.world(), pos).getCenter();
    Vec3d center = (new Vec3d((Vec3i)pos)).add(relCenter);
    Vec3d[] hitVecs = new Vec3d[sides.length];
    int i;
    for (i = 0; i < sides.length; i++) {
      Vec3i dirVec = sides[i].getDirectionVec();
      Vec3d relHitVec = new Vec3d(relCenter.x * dirVec.getX(), relCenter.y * dirVec.getY(), relCenter.z * dirVec.getZ());
      hitVecs[i] = center.add(relHitVec);
    } 
    for (i = 0; i < sides.length; ) {
      if (Wrapper.INSTANCE.world().rayTraceBlocks(eyesPos, hitVecs[i], false, true, false) != null) {
        i++;
        continue;
      } 
      side = sides[i];
    } 
    if (side == null) {
      double distanceSqToCenter = eyesPos.squareDistanceTo(center);
      for (int j = 0; j < sides.length; ) {
        if (eyesPos.squareDistanceTo(hitVecs[j]) >= distanceSqToCenter) {
          j++;
          continue;
        } 
        side = sides[j];
      } 
    } 
    if (side == null)
      side = sides[0]; 
    Utils.faceVectorPacket(hitVecs[side.ordinal()]);
    if (!Wrapper.INSTANCE.controller().onPlayerDamageBlock(pos, side))
      return false; 
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
    return true;
  }
  
  public static void breakBlocksPacketSpam(Iterable<BlockPos> blocks) {
    Vec3d eyesPos = Utils.getEyesPos();
    NetHandlerPlayClient connection = (Wrapper.INSTANCE.player()).connection;
    for (BlockPos pos : blocks) {
      Vec3d posVec = (new Vec3d((Vec3i)pos)).add(0.5D, 0.5D, 0.5D);
      double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
      EnumFacing[] arrayOfEnumFacing;
      int i;
      byte b;
      for (arrayOfEnumFacing = EnumFacing.values(), i = arrayOfEnumFacing.length, b = 0; b < i; ) {
        EnumFacing side = arrayOfEnumFacing[b];
        Vec3d hitVec = posVec.add((new Vec3d(side.getDirectionVec())).scale(0.5D));
        if (eyesPos.squareDistanceTo(hitVec) >= distanceSqPosVec) {
          b++;
          continue;
        } 
        connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
        connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
      } 
    } 
  }
}
