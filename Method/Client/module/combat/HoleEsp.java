package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.visual.Executer;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class HoleEsp extends Module {
  Setting Radius = Main.setmgr.add(new Setting("Radius", this, 8.0D, 0.0D, 32.0D, true));
  
  Setting Void = Main.setmgr.add(new Setting("Void", this, 0.85D, 1.0D, 1.0D, 0.75D));
  
  Setting Bedrock = Main.setmgr.add(new Setting("Bedrock", this, 0.55D, 1.0D, 1.0D, 0.75D));
  
  Setting obby = Main.setmgr.add(new Setting("obby", this, 0.22D, 1.0D, 1.0D, 0.75D));
  
  Setting Burrow = Main.setmgr.add(new Setting("Burrow", this, 0.4D, 1.0D, 1.0D, 0.75D));
  
  Setting OwnHole = Main.setmgr.add(new Setting("Ignore Own", this, true));
  
  Setting Timer = Main.setmgr.add(new Setting("Timer", this, 250.0D, 0.0D, 500.0D, true));
  
  Setting Mode = Main.setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  Setting BurrowDetect = Main.setmgr.add(new Setting("Burrow Detect", this, true));
  
  Vec3i playerPos;
  
  TimerUtils timer = new TimerUtils();
  
  public final List<Hole> holes = new ArrayList<>();
  
  public HoleEsp() {
    super("HoleEsp", 0, Category.COMBAT, "HoleEsp");
  }
  
  public void onEnable() {
    Executer.init();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.timer.isDelay((long)this.Timer.getValDouble()) && 
      !this.Mode.getValString().equalsIgnoreCase("None")) {
      this.playerPos = new Vec3i(mc.player.posX, mc.player.posY, mc.player.posZ);
      this.holes.clear();
      Executer.execute(() -> {
            if (this.BurrowDetect.getValBoolean())
              for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityPlayer) {
                  EntityPlayer entityPlayer = (EntityPlayer)entity;
                  if (isInBurrow(entityPlayer)) {
                    BlockPos b = new BlockPos((Entity)entityPlayer);
                    this.holes.add(new Hole(b.x, b.y, b.z, b, Hole.HoleTypes.Burrow, false));
                  } 
                } 
              }  
            for (int x = (int)(this.playerPos.getX() - this.Radius.getValDouble()); x < this.playerPos.getX() + this.Radius.getValDouble(); x++) {
              for (int z = (int)(this.playerPos.getZ() - this.Radius.getValDouble()); z < this.playerPos.getZ() + this.Radius.getValDouble(); z++) {
                for (int y = this.playerPos.getY() + 6; y > this.playerPos.getY() - 6; y--) {
                  BlockPos blockPos = new BlockPos(x, y, z);
                  if (!this.OwnHole.getValBoolean() || mc.player.getDistanceSq(blockPos) > 1.0D) {
                    Hole.HoleTypes l_Type = isHoleValid(mc.world.getBlockState(blockPos), blockPos);
                    if (l_Type != Hole.HoleTypes.None)
                      if (l_Type == Hole.HoleTypes.Void) {
                        this.holes.add(new Hole(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos, Hole.HoleTypes.Void, true));
                      } else {
                        IBlockState downBlockState = mc.world.getBlockState(blockPos.down());
                        if (downBlockState.getBlock() == Blocks.AIR) {
                          BlockPos downPos = blockPos.down();
                          l_Type = isHoleValid(downBlockState, blockPos);
                          if (l_Type != Hole.HoleTypes.None)
                            this.holes.add(new Hole(downPos.getX(), downPos.getY(), downPos.getZ(), downPos, l_Type, true)); 
                        } else {
                          this.holes.add(new Hole(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos, l_Type, false));
                        } 
                      }  
                  } 
                } 
              } 
            } 
          });
      this.timer.setLastMS();
    } 
  }
  
  private boolean isInBurrow(EntityPlayer entityPlayer) {
    BlockPos playerPos = new BlockPos(Math.floor(entityPlayer.posX + 0.5D), entityPlayer.posY, Math.floor(entityPlayer.posZ + 0.5D));
    return (MC.world.getBlockState(playerPos).getBlock() == Blocks.OBSIDIAN || MC.world
      .getBlockState(playerPos).getBlock() == Blocks.ENDER_CHEST || MC.world
      .getBlockState(playerPos).getBlock() == Blocks.ANVIL);
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (!this.Mode.getValString().equalsIgnoreCase("None"))
      for (Hole hole : this.holes) {
        double renderPosX = hole.getX() - (mc.getRenderManager()).viewerPosX;
        double renderPosY = hole.getY() - (mc.getRenderManager()).viewerPosY;
        double renderPosZ = hole.getZ() - (mc.getRenderManager()).viewerPosZ;
        AxisAlignedBB bb = new AxisAlignedBB(renderPosX, renderPosY, renderPosZ, renderPosX + 1.0D, renderPosY + (hole.isTall() ? 2 : true), renderPosZ + 1.0D);
        RenderUtils.RenderBlock(this.Mode.getValString(), bb, (hole.GetHoleType() == Hole.HoleTypes.Bedrock) ? this.Bedrock.getcolor() : ((hole.GetHoleType() == Hole.HoleTypes.Obsidian) ? this.obby.getcolor() : ((hole.GetHoleType() == Hole.HoleTypes.Burrow) ? this.Burrow.getcolor() : this.Void.getcolor())), Double.valueOf(this.LineWidth.getValDouble()));
      }  
  }
  
  public static Hole.HoleTypes isHoleValid(IBlockState blockState, BlockPos blockPos) {
    if (blockState.getBlock() != Blocks.AIR)
      return Hole.HoleTypes.None; 
    if (blockState.getBlock() == Blocks.AIR && blockPos.y == 0)
      return Hole.HoleTypes.Void; 
    if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR)
      return Hole.HoleTypes.None; 
    if (mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR)
      return Hole.HoleTypes.None; 
    if (mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR)
      return Hole.HoleTypes.None; 
    BlockPos[] touchingBlocks = { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west() };
    boolean AllBedrock = true;
    for (BlockPos touching : touchingBlocks) {
      IBlockState touchingState = mc.world.getBlockState(touching);
      if (touchingState.getBlock() != Blocks.AIR && touchingState.isFullBlock()) {
        if (touchingState.getBlock() == Blocks.OBSIDIAN) {
          AllBedrock = false;
        } else if (touchingState.getBlock() != Blocks.BEDROCK) {
          return Hole.HoleTypes.None;
        } 
      } else {
        return Hole.HoleTypes.None;
      } 
    } 
    return AllBedrock ? Hole.HoleTypes.Bedrock : Hole.HoleTypes.Obsidian;
  }
}
