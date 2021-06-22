package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiCrystal extends Module {
  public Setting Hand = Main.setmgr.add(new Setting("Hand", this, "Mainhand", new String[] { "Mainhand", "Offhand", "Both", "None" }));
  
  public Setting rotate = Main.setmgr.add(new Setting("rotate", this, true));
  
  public AntiCrystal() {
    super("AntiCrystal", 0, Category.COMBAT, "String");
  }
  
  private int find_in_hotbar(Item item) {
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack != ItemStack.EMPTY && stack.getItem().equals(item))
        return i; 
    } 
    return -1;
  }
  
  public void onEnable() {
    if (find_in_hotbar(Items.STRING) == -1) {
      ChatUtils.warning("Must have string in hotbar!");
      toggle();
    } 
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.player.ticksExisted % 2 == 0 && 
      find_in_hotbar(Items.STRING) != -1 && 
      !(mc.world.getBlockState(mc.player.getPosition()).getBlock() instanceof net.minecraft.block.BlockTripWire)) {
      int old_slot = -1;
      old_slot = mc.player.inventory.currentItem;
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(find_in_hotbar(Items.STRING)));
      mc.player.inventory.currentItem = find_in_hotbar(Items.STRING);
      Blockplace(EnumHand.MAIN_HAND, new BlockPos(mc.player.posX, Math.ceil(mc.player.posY), mc.player.posZ));
      mc.player.inventory.currentItem = old_slot;
    } 
    super.onClientTick(event);
  }
  
  public void Blockplace(EnumHand enumHand, BlockPos blockPos) {
    Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    BlockPos offset = blockPos.offset(EnumFacing.UP);
    EnumFacing opposite = EnumFacing.UP.getOpposite();
    Vec3d Vec3d = (new Vec3d((Vec3i)offset)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
    if (vec3d.squareDistanceTo(Vec3d) <= 18.0625D) {
      float[] array = Utils.getNeededRotations(Vec3d, 0.0F, 0.0F);
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(array[0], array[1], mc.player.onGround));
      mc.playerController.processRightClickBlock(mc.player, mc.world, offset, opposite, Vec3d, enumHand);
      mc.player.swingArm(enumHand);
    } 
  }
}
