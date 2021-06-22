package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoTrap extends Module {
  public Setting place_mode;
  
  public Setting blocks_per_tick;
  
  public Setting rotate;
  
  public Setting chad_mode;
  
  public Setting range;
  
  public Setting Hand;
  
  private String last_tick_target_name;
  
  private int offset_step;
  
  private int timeout_ticker;
  
  private int y_level;
  
  private boolean first_run;
  
  private final Vec3d[] offsets_default;
  
  private final Vec3d[] offsets_face;
  
  private final Vec3d[] offsets_feet;
  
  private final Vec3d[] offsets_extra;
  
  public AutoTrap() {
    super("AutoTrap", 0, Category.COMBAT, "AutoTrap");
    this.place_mode = Main.setmgr.add(new Setting("cage", this, "Extra", new String[] { "Extra", "Face", "Normal", "Feet" }));
    this.blocks_per_tick = Main.setmgr.add(new Setting("blocks Per Tick", this, 4.0D, 0.0D, 8.0D, true));
    this.rotate = Main.setmgr.add(new Setting("rotate", this, false));
    this.chad_mode = Main.setmgr.add(new Setting("Modify mode", this, false));
    this.range = Main.setmgr.add(new Setting("range", this, 5.5D, 3.5D, 10.0D, false));
    this.Hand = Main.setmgr.add(new Setting("Hand", this, "Mainhand", new String[] { "Mainhand", "Offhand", "Both", "None" }));
    this.last_tick_target_name = "";
    this.offset_step = 0;
    this.timeout_ticker = 0;
    this.first_run = true;
    this.offsets_default = new Vec3d[] { 
        new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(1.0D, 2.0D, 0.0D), 
        new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(0.0D, 3.0D, 1.0D), new Vec3d(1.0D, 3.0D, 0.0D), new Vec3d(-1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 3.0D, 0.0D) };
    this.offsets_face = new Vec3d[] { 
        new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(0.0D, 3.0D, -1.0D), 
        new Vec3d(0.0D, 3.0D, 1.0D), new Vec3d(1.0D, 3.0D, 0.0D), new Vec3d(-1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 3.0D, 0.0D) };
    this.offsets_feet = new Vec3d[] { 
        new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 3.0D, -1.0D), 
        new Vec3d(0.0D, 3.0D, 1.0D), new Vec3d(1.0D, 3.0D, 0.0D), new Vec3d(-1.0D, 3.0D, 0.0D), new Vec3d(0.0D, 3.0D, 0.0D) };
    this.offsets_extra = new Vec3d[] { 
        new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 2.0D, -1.0D), new Vec3d(1.0D, 2.0D, 0.0D), 
        new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 3.0D, -1.0D), new Vec3d(0.0D, 3.0D, 0.0D), new Vec3d(0.0D, 4.0D, 0.0D) };
  }
  
  public void onEnable() {
    this.timeout_ticker = 0;
    this.y_level = (int)Math.round(mc.player.posY);
    this.first_run = true;
    if (find_obi_in_hotbar() == -1)
      toggle(); 
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    int timeout_ticks = 20;
    if (this.timeout_ticker > timeout_ticks && this.chad_mode.getValBoolean()) {
      this.timeout_ticker = 0;
      toggle();
      return;
    } 
    EntityPlayer closest_target = find_closest_target();
    if (closest_target == null) {
      toggle();
      return;
    } 
    if (this.chad_mode.getValBoolean() && (int)Math.round(mc.player.posY) != this.y_level) {
      toggle();
      return;
    } 
    if (this.first_run) {
      this.first_run = false;
      this.last_tick_target_name = closest_target.getName();
    } else if (!this.last_tick_target_name.equals(closest_target.getName())) {
      this.last_tick_target_name = closest_target.getName();
      this.offset_step = 0;
    } 
    List<Vec3d> place_targets = new ArrayList<>();
    if (this.place_mode.getValString().equalsIgnoreCase("Normal")) {
      Collections.addAll(place_targets, this.offsets_default);
    } else if (this.place_mode.getValString().equalsIgnoreCase("Extra")) {
      Collections.addAll(place_targets, this.offsets_extra);
    } else if (this.place_mode.getValString().equalsIgnoreCase("Feet")) {
      Collections.addAll(place_targets, this.offsets_feet);
    } else {
      Collections.addAll(place_targets, this.offsets_face);
    } 
    int blocks_placed = 0;
    while (blocks_placed < this.blocks_per_tick.getValDouble()) {
      if (this.offset_step >= place_targets.size()) {
        this.offset_step = 0;
        break;
      } 
      BlockPos offset_pos = new BlockPos(place_targets.get(this.offset_step));
      BlockPos target_pos = (new BlockPos(closest_target.getPositionVector())).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());
      int old_slot = -1;
      if (find_obi_in_hotbar() != mc.player.inventory.currentItem) {
        old_slot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = find_obi_in_hotbar();
      } 
      if (Utils.trytoplace(target_pos) && Utils.placeBlock(target_pos, this.rotate.getValBoolean(), this.Hand))
        blocks_placed++; 
      if (old_slot != -1)
        mc.player.inventory.currentItem = old_slot; 
      this.offset_step++;
    } 
    this.timeout_ticker++;
  }
  
  private int find_obi_in_hotbar() {
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
      if (closestTarget != null && mc.player.getDistance((Entity)target) > mc.player.getDistance((Entity)closestTarget))
        continue; 
      closestTarget = target;
    } 
    return closestTarget;
  }
}
