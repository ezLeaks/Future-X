package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class HoleFill extends Module {
  Setting hole_toggle = Main.setmgr.add(new Setting("hole toggle", this, true));
  
  Setting hole_rotate = Main.setmgr.add(new Setting("hole rotate", this, true));
  
  Setting hole_range = Main.setmgr.add(new Setting("hole range", this, 4.0D, 1.0D, 6.0D, true));
  
  Setting swing = Main.setmgr.add(new Setting("swing", this, "Mainhand", new String[] { "Mainhand", "Offhand", "Both", "None" }));
  
  private final ArrayList<BlockPos> holes = new ArrayList<>();
  
  public HoleFill() {
    super("HoleFill", 0, Category.COMBAT, "HoleFill");
  }
  
  public void onEnable() {
    if (find_in_hotbar() == -1)
      toggle(); 
    find_new_holes();
  }
  
  public void onDisable() {
    this.holes.clear();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (find_in_hotbar() == -1) {
      toggle();
      return;
    } 
    if (!this.hole_toggle.getValBoolean()) {
      toggle();
      return;
    } 
    find_new_holes();
    BlockPos pos_to_fill = null;
    for (BlockPos pos : new ArrayList(this.holes)) {
      if (pos == null)
        continue; 
      Utils.ValidResult result = Utils.valid(pos);
      if (result != Utils.ValidResult.Ok) {
        this.holes.remove(pos);
        continue;
      } 
      pos_to_fill = pos;
    } 
    int old_slot = -1;
    if (find_in_hotbar() != mc.player.inventory.currentItem) {
      old_slot = mc.player.inventory.currentItem;
      mc.player.inventory.currentItem = find_in_hotbar();
    } 
    if (pos_to_fill != null && 
      Utils.placeBlock(pos_to_fill, this.hole_rotate.getValBoolean(), this.swing))
      this.holes.remove(pos_to_fill); 
    if (old_slot != -1)
      mc.player.inventory.currentItem = old_slot; 
  }
  
  public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
    ArrayList<BlockPos> circleblocks = new ArrayList<>();
    int cx = loc.getX();
    int cy = loc.getY();
    int cz = loc.getZ();
    int x = cx - (int)r;
    while (x <= cx + r) {
      int z = cz - (int)r;
      while (z <= cz + r) {
        int y = sphere ? (cy - (int)r) : cy;
        while (true) {
          float f = sphere ? (cy + r) : (cy + h);
          if (y >= f)
            break; 
          double dist = ((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0));
          if (dist < (r * r) && (!hollow || dist >= ((r - 1.0F) * (r - 1.0F)))) {
            BlockPos l = new BlockPos(x, y + plus_y, z);
            circleblocks.add(l);
          } 
          y++;
        } 
        z++;
      } 
      x++;
    } 
    return circleblocks;
  }
  
  public static BlockPos GetLocalPlayerPosFloored() {
    return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
  }
  
  public void find_new_holes() {
    this.holes.clear();
    for (BlockPos pos : getSphere(GetLocalPlayerPosFloored(), (float)this.hole_range.getValDouble(), (int)this.hole_range.getValDouble(), false, true, 0)) {
      if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
        continue; 
      if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR))
        continue; 
      if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
        continue; 
      boolean possible = true;
      for (BlockPos seems_blocks : new BlockPos[] { new BlockPos(0, -1, 0), new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0) }) {
        Block block = mc.world.getBlockState(pos.add((Vec3i)seems_blocks)).getBlock();
        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
          possible = false;
          break;
        } 
      } 
      if (possible)
        this.holes.add(pos); 
    } 
  }
  
  private int find_in_hotbar() {
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
        Block block = ((ItemBlock)stack.getItem()).getBlock();
        if (block instanceof net.minecraft.block.BlockEnderChest)
          return i; 
        if (block instanceof net.minecraft.block.BlockObsidian)
          return i; 
      } 
    } 
    return -1;
  }
}
