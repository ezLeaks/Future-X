package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;

public class Parkour extends Module {
  public TimerUtils timer = new TimerUtils();
  
  public TimerUtils Delayer = new TimerUtils();
  
  Setting Nearedge;
  
  Setting autodrop;
  
  Setting runsinglegap;
  
  Setting FullBlock;
  
  public static List<Block> pospoint5;
  
  public static List<Block> Replace;
  
  BlockPos playerPos;
  
  public Parkour() {
    super("Parkour", 0, Category.MOVEMENT, "Auto Parkour+");
    this.Nearedge = Main.setmgr.add(new Setting("Nearedge", this, 0.001D, 0.0D, 0.01D, false));
    this.autodrop = Main.setmgr.add(new Setting("autodrop", this, false));
    this.runsinglegap = Main.setmgr.add(new Setting("Run Single Gap", this, false));
    this.FullBlock = Main.setmgr.add(new Setting("All Full Blocks", this, false));
  }
  
  public void setup() {
    Replace = Arrays.asList(new Block[] { 
          Blocks.ENDER_CHEST, (Block)Blocks.CACTUS, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.IRON_BARS, 
          Blocks.DISPENSER, Blocks.END_PORTAL_FRAME, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE, Blocks.PURPUR_STAIRS, Blocks.RED_SANDSTONE_STAIRS, Blocks.DARK_OAK_STAIRS, Blocks.ACACIA_STAIRS, Blocks.QUARTZ_STAIRS, Blocks.SANDSTONE_STAIRS, 
          Blocks.SPRUCE_STAIRS, Blocks.BIRCH_STAIRS, Blocks.JUNGLE_STAIRS, Blocks.NETHER_BRICK_STAIRS, Blocks.OAK_STAIRS, Blocks.STONE_STAIRS, Blocks.BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS });
    pospoint5 = Arrays.asList(new Block[] { 
          Blocks.ACACIA_FENCE, Blocks.OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE, Blocks.JUNGLE_FENCE, Blocks.DARK_OAK_FENCE, Blocks.ACACIA_FENCE, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE_GATE, Blocks.BIRCH_FENCE_GATE, 
          Blocks.JUNGLE_FENCE_GATE, Blocks.DARK_OAK_FENCE_GATE, Blocks.ACACIA_FENCE_GATE, Blocks.NETHER_BRICK_FENCE, Blocks.COBBLESTONE_WALL });
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    if (this.FullBlock.getValBoolean() && this.Delayer.isDelay(200L)) {
      for (BlockPos b : BlockPos.getAllInBox((int)mc.player.posX - 5, (int)mc.player.posY - 5, (int)mc.player.posZ - 5, (int)mc.player.posX + 5, (int)mc.player.posY + 5, (int)mc.player.posZ + 5)) {
        if (pospoint5.contains(mc.world.getBlockState(b).getBlock())) {
          BlockPos pos = new BlockPos(b.x, b.y + 1, b.z);
          while (pospoint5.contains(mc.world.getBlockState(pos).getBlock()))
            pos.y++; 
          mc.world.setBlockState(pos, Blocks.PURPUR_SLAB.getDefaultState());
        } 
        if (Replace.contains(mc.world.getBlockState(b).getBlock()))
          mc.world.setBlockState(b, Blocks.MELON_BLOCK.getDefaultState()); 
      } 
      this.Delayer.setLastMS();
    } 
    double newX = mc.player.posX;
    double newZ = mc.player.posZ;
    newX = (mc.player.posX > Math.round(mc.player.posX)) ? (Math.round(mc.player.posX) + 0.5D) : newX;
    newX = (mc.player.posX < Math.round(mc.player.posX)) ? (Math.round(mc.player.posX) - 0.5D) : newX;
    newZ = (mc.player.posZ > Math.round(mc.player.posZ)) ? (Math.round(mc.player.posZ) + 0.5D) : newZ;
    newZ = (mc.player.posZ < Math.round(mc.player.posZ)) ? (Math.round(mc.player.posZ) - 0.5D) : newZ;
    this.playerPos = new BlockPos(newX, mc.player.posY, newZ);
    if (this.autodrop.getValBoolean() && mc.player.motionY < -0.01D && !mc.player.onGround)
      for (int i = 0; i < 4; i++) {
        if (mc.world.getBlockState(this.playerPos.down()).getBlock() != Blocks.AIR) {
          mc.player.motionX -= mc.player.motionX / 555.0D;
          mc.player.motionZ -= mc.player.motionZ / 555.0D;
          break;
        } 
        this.playerPos = new BlockPos(newX, mc.player.posY - i, newZ);
      }  
    if (mc.player.onGround && !mc.player.isSneaking() && !mc.gameSettings.keyBindSneak.pressed)
      if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -0.5D, 0.0D).expand(-this.Nearedge.getValDouble(), 0.0D, -this.Nearedge.getValDouble())).isEmpty() && this.timer
        .isDelay(100L))
        if (this.runsinglegap.getValBoolean()) {
          switch (MathHelper.floor((mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 0x7) {
            case 0:
              if (mc.world.getBlockState(this.playerPos.down().south()).getBlock() != Blocks.AIR)
                break; 
              jumpme();
              break;
            case 1:
              if (mc.world.getBlockState(this.playerPos.down().south()).getBlock() != Blocks.AIR || mc.world.getBlockState(this.playerPos.down().west()).getBlock() != Blocks.AIR)
                break; 
              jumpme();
              break;
            case 2:
              if (mc.world.getBlockState(this.playerPos.down().west()).getBlock() != Blocks.AIR)
                break; 
              jumpme();
              break;
            case 3:
              if (mc.world.getBlockState(this.playerPos.down().west()).getBlock() != Blocks.AIR || mc.world.getBlockState(this.playerPos.down().north()).getBlock() != Blocks.AIR)
                break; 
              jumpme();
              break;
            case 4:
              if (mc.world.getBlockState(this.playerPos.down().north()).getBlock() != Blocks.AIR)
                break; 
              jumpme();
              break;
            case 5:
              if (mc.world.getBlockState(this.playerPos.down().east()).getBlock() != Blocks.AIR || mc.world.getBlockState(this.playerPos.down().north()).getBlock() != Blocks.AIR)
                break; 
              jumpme();
              break;
            case 6:
              if (mc.world.getBlockState(this.playerPos.down().east()).getBlock() != Blocks.AIR)
                break; 
              jumpme();
              break;
            case 7:
              if (mc.world.getBlockState(this.playerPos.down().south()).getBlock() != Blocks.AIR || mc.world.getBlockState(this.playerPos.down().east()).getBlock() != Blocks.AIR)
                break; 
              jumpme();
              break;
          } 
        } else {
          jumpme();
        }   
  }
  
  private void jumpme() {
    mc.player.jump();
    this.timer.setLastMS();
  }
}
