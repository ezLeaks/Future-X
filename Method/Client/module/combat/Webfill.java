package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Webfill extends Module {
  Setting always_on = Main.setmgr.add(new Setting("hole toggle", this, true));
  
  Setting rotate = Main.setmgr.add(new Setting("hole rotate", this, true));
  
  Setting range = Main.setmgr.add(new Setting("range", this, 4.0D, 1.0D, 6.0D, true));
  
  int new_slot = -1;
  
  boolean sneak = false;
  
  public Webfill() {
    super("Webfill", 0, Category.COMBAT, "Webfill");
  }
  
  public void onEnable() {
    if (mc.player != null) {
      this.new_slot = find_in_hotbar();
      if (this.new_slot == -1)
        toggle(); 
    } 
  }
  
  public void onDisable() {
    if (mc.player != null && 
      this.sneak) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
      this.sneak = false;
    } 
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.player == null)
      return; 
    if (this.always_on.getValBoolean()) {
      EntityPlayer target = find_closest_target();
      if (target == null)
        return; 
      if (mc.player.getDistance((Entity)target) < this.range.getValDouble() && is_surround()) {
        int last_slot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = this.new_slot;
        mc.playerController.updateController();
        place_blocks(HoleFill.GetLocalPlayerPosFloored());
        mc.player.inventory.currentItem = last_slot;
      } 
    } else {
      int last_slot = mc.player.inventory.currentItem;
      mc.player.inventory.currentItem = this.new_slot;
      mc.playerController.updateController();
      place_blocks(HoleFill.GetLocalPlayerPosFloored());
      mc.player.inventory.currentItem = last_slot;
      toggle();
    } 
  }
  
  public EntityPlayer find_closest_target() {
    if (mc.world.playerEntities.isEmpty())
      return null; 
    EntityPlayer closestTarget = null;
    for (EntityPlayer target : mc.world.playerEntities) {
      if (target == mc.player)
        continue; 
      if (FriendManager.isFriend(target.getName()))
        continue; 
      if (!Utils.isLiving((Entity)target))
        continue; 
      if (target.getHealth() <= 0.0F)
        continue; 
      if (closestTarget != null && 
        mc.player.getDistance((Entity)target) > mc.player.getDistance((Entity)closestTarget))
        continue; 
      closestTarget = target;
    } 
    return closestTarget;
  }
  
  private int find_in_hotbar() {
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack.getItem() == Item.getItemById(30))
        return i; 
    } 
    return -1;
  }
  
  private boolean is_surround() {
    BlockPos player_block = HoleFill.GetLocalPlayerPosFloored();
    return (mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(player_block.west()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(player_block.north()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(player_block.south()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(player_block).getBlock() == Blocks.AIR);
  }
  
  private void place_blocks(BlockPos pos) {
    if (!mc.world.getBlockState(pos).getMaterial().isReplaceable())
      return; 
    if (!Utils.checkForNeighbours(pos))
      return; 
    EnumFacing[] arrayOfEnumFacing;
    int i;
    byte b;
    for (arrayOfEnumFacing = EnumFacing.values(), i = arrayOfEnumFacing.length, b = 0; b < i; ) {
      EnumFacing side = arrayOfEnumFacing[b];
      BlockPos neighbor = pos.offset(side);
      EnumFacing side2 = side.getOpposite();
      if (!Utils.canBeClicked(neighbor)) {
        b++;
        continue;
      } 
      if (Surrond.blackList.contains(mc.world.getBlockState(neighbor).getBlock())) {
        mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
        this.sneak = true;
      } 
      Vec3d hitVec = (new Vec3d((Vec3i)neighbor)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(side2.getDirectionVec())).scale(0.5D));
      if (this.rotate.getValBoolean())
        Utils.faceVectorPacketInstant(hitVec); 
      mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
      mc.player.swingArm(EnumHand.MAIN_HAND);
      return;
    } 
  }
}
