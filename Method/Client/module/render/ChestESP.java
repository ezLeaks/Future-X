package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.ChatUtils;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.block.BlockChest;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ChestESP extends Module {
  public boolean shouldInform = true;
  
  Setting Chest = Main.setmgr.add(new Setting("Chest", this, true));
  
  Setting Shulker = Main.setmgr.add(new Setting("Shulker", this, true));
  
  Setting Trappedchest = Main.setmgr.add(new Setting("Trapped", this, true));
  
  Setting EnderChest = Main.setmgr.add(new Setting("Ender", this, true));
  
  Setting MinecartChest = Main.setmgr.add(new Setting("Minecart", this, true));
  
  Setting ChestColor = Main.setmgr.add(new Setting("ChestC", this, 0.22D, 1.0D, 1.0D, 0.5D, this.Chest, 7));
  
  Setting TrappedchestColor = Main.setmgr.add(new Setting("TrappedC", this, 0.0D, 1.0D, 1.0D, 0.5D, this.Trappedchest, 7));
  
  Setting EnderChestColor = Main.setmgr.add(new Setting("EnderC", this, 0.44D, 1.0D, 1.0D, 0.5D, this.EnderChest, 7));
  
  Setting MinecartChestColor = Main.setmgr.add(new Setting("MinecartC", this, 0.88D, 1.0D, 1.0D, 0.5D, this.MinecartChest, 7));
  
  Setting ShulkerColor = Main.setmgr.add(new Setting("ShulkerColor", this, 0.96D, 1.0D, 1.0D, 0.5D, this.MinecartChest, 7));
  
  Setting Mode = Main.setmgr.add(new Setting("Draw Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  Setting ChangeSeen = Main.setmgr.add(new Setting("Has been opened", this, true));
  
  Setting OpenedColor = Main.setmgr.add(new Setting("OpenedColor", this, 0.0D, 1.0D, 1.0D, 0.5D, this.ChangeSeen, 7));
  
  Setting Notify = Main.setmgr.add(new Setting("Notify", this, 50.0D, 100.0D, 2000.0D, true));
  
  Setting maxChests = Main.setmgr.add(new Setting("maxChests", this, 1000.0D, 100.0D, 2000.0D, true));
  
  ArrayList<BlockPos> Openedpos = new ArrayList<>();
  
  public ChestESP() {
    super("ChestESP", 0, Category.RENDER, "ChestESP");
  }
  
  public void onEnable() {
    this.shouldInform = true;
    this.Openedpos.clear();
    super.onEnable();
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    int chests = 0;
    for (TileEntity entity : mc.world.loadedTileEntityList) {
      if (entity instanceof TileEntityChest) {
        if (chests >= this.maxChests.getValDouble() && this.shouldInform)
          break; 
        chests++;
        TileEntityChest chest = (TileEntityChest)entity;
        if (this.ChangeSeen.getValBoolean()) {
          if (chest.numPlayersUsing > 0)
            this.Openedpos.add(chest.getPos()); 
          if (this.Openedpos.contains(chest.getPos())) {
            RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Standardbb(chest.getPos()), this.OpenedColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
            continue;
          } 
        } 
        if (chest.getChestType() == BlockChest.Type.TRAP && this.Trappedchest.getValBoolean()) {
          RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Standardbb(chest.getPos()), this.TrappedchestColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
          continue;
        } 
        if (this.Chest.getValBoolean()) {
          RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Standardbb(chest.getPos()), this.ChestColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
          continue;
        } 
      } 
      if (entity instanceof net.minecraft.tileentity.TileEntityEnderChest && this.EnderChest.getValBoolean()) {
        chests++;
        RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Standardbb(entity.getPos()), this.EnderChestColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      } 
      if (entity instanceof net.minecraft.tileentity.TileEntityShulkerBox && this.Shulker.getValBoolean()) {
        chests++;
        RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Standardbb(entity.getPos()), this.ShulkerColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      } 
    } 
    for (Entity entity : mc.world.loadedEntityList) {
      if (chests >= this.maxChests.getValDouble())
        break; 
      if (entity instanceof net.minecraft.entity.item.EntityMinecartChest && this.MinecartChest.getValBoolean()) {
        chests++;
        RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Standardbb(entity.getPosition()), this.MinecartChestColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      } 
    } 
    if (this.shouldInform) {
      if (chests >= this.Notify.getValDouble()) {
        ChatUtils.warning("Found " + chests + " chests.");
        mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getRecord(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F));
      } 
      if (chests >= this.maxChests.getValDouble()) {
        ChatUtils.warning("To prevent lag, it will only show the first " + this.maxChests.getValDouble() + " chests.");
        this.shouldInform = false;
      } 
    } else if (chests < this.maxChests.getValDouble()) {
      this.shouldInform = true;
    } 
    super.onRenderWorldLast(event);
  }
  
  @Nullable
  public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
    return getContainer(worldIn, pos, false);
  }
  
  @Nullable
  public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking) {
    InventoryLargeChest inventoryLargeChest;
    TileEntity tileentity = worldIn.getTileEntity(pos);
    if (!(tileentity instanceof TileEntityChest))
      return null; 
    TileEntityChest tileEntityChest = (TileEntityChest)tileentity;
    for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
      BlockPos blockpos = pos.offset(enumfacing);
      TileEntity tileentity1 = worldIn.getTileEntity(blockpos);
      if (tileentity1 instanceof TileEntityChest) {
        if (enumfacing != EnumFacing.WEST && enumfacing != EnumFacing.NORTH) {
          inventoryLargeChest = new InventoryLargeChest("container.chestDouble", (ILockableContainer)tileEntityChest, (ILockableContainer)tileentity1);
          continue;
        } 
        inventoryLargeChest = new InventoryLargeChest("container.chestDouble", (ILockableContainer)tileentity1, (ILockableContainer)inventoryLargeChest);
      } 
    } 
    return (ILockableContainer)inventoryLargeChest;
  }
}
