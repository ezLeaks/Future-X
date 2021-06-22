package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.BlockUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Nuker extends Module {
  public Setting mode = Main.setmgr.add(new Setting("Mode", this, "All", new String[] { "ID", "All" }));
  
  public Setting distance = Main.setmgr.add(new Setting("Distance", this, 6.0D, 0.1D, 6.0D, false));
  
  public Setting Drawbox = Main.setmgr.add(new Setting("Draw box", this, true));
  
  public Setting StopOnKick = Main.setmgr.add(new Setting("StopOnKick", this, true));
  
  public Setting allcolor = Main.setmgr.add(new Setting("allcolor", this, 0.0D, 1.0D, 1.0D, 1.0D));
  
  public Setting idcolor = Main.setmgr.add(new Setting("idcolor", this, 0.22D, 1.0D, 1.0D, 1.0D));
  
  Setting Drawmode = Main.setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  public final ArrayDeque<Set<BlockPos>> prevBlocks = new ArrayDeque<>();
  
  public BlockPos currentBlock;
  
  public float progress;
  
  public float prevProgress;
  
  public int id;
  
  public Nuker() {
    super("Nuker", 0, Category.PLAYER, "Nuker");
  }
  
  public void onDisable() {
    if (this.currentBlock != null) {
      mc.playerController.isHittingBlock = true;
      Wrapper.INSTANCE.controller().resetBlockRemoving();
      this.currentBlock = null;
    } 
    this.prevBlocks.clear();
    this.id = 0;
    super.onDisable();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.world == null && this.StopOnKick.getValBoolean() && isToggled())
      toggle(); 
    this.currentBlock = null;
    Vec3d eyesPos = Utils.getEyesPos().subtract(0.5D, 0.5D, 0.5D);
    BlockPos eyesBlock = new BlockPos(Utils.getEyesPos());
    double rangeSq = Math.pow(this.distance.getValDouble(), 2.0D);
    int blockRange = (int)Math.ceil(this.distance.getValDouble());
    Stream<BlockPos> stream = StreamSupport.stream(BlockPos.getAllInBox(eyesBlock
          .add(blockRange, blockRange, blockRange), eyesBlock
          .add(-blockRange, -blockRange, -blockRange)).spliterator(), true);
    stream = stream.filter(pos -> (eyesPos.squareDistanceTo(new Vec3d((Vec3i)pos)) <= rangeSq)).filter(BlockUtils::canBeClicked).sorted(Comparator.comparingDouble(pos -> eyesPos.squareDistanceTo(new Vec3d((Vec3i)pos))));
    if (this.mode.getValString().equalsIgnoreCase("id"))
      stream = stream.filter(pos -> (Block.getIdFromBlock(BlockUtils.getBlock(pos)) == this.id)); 
    List<BlockPos> blocks = stream.collect((Collector)Collectors.toList());
    if (mc.player.capabilities.isCreativeMode) {
      Stream<BlockPos> stream2 = blocks.parallelStream();
      for (Set<BlockPos> set : this.prevBlocks)
        stream2 = stream2.filter(pos -> !set.contains(pos)); 
      List<BlockPos> blocks2 = stream2.collect((Collector)Collectors.toList());
      this.prevBlocks.addLast(new HashSet<>(blocks2));
      while (this.prevBlocks.size() > 5)
        this.prevBlocks.removeFirst(); 
      if (!blocks2.isEmpty())
        this.currentBlock = blocks2.get(0); 
      Wrapper.INSTANCE.controller().resetBlockRemoving();
      this.progress = 1.0F;
      this.prevProgress = 1.0F;
      BlockUtils.breakBlocksPacketSpam(blocks2);
      return;
    } 
    for (BlockPos pos : blocks) {
      if (BlockUtils.breakBlockSimple(pos)) {
        this.currentBlock = pos;
        break;
      } 
    } 
    if (this.currentBlock == null)
      Wrapper.INSTANCE.controller().resetBlockRemoving(); 
    if (this.currentBlock != null && BlockUtils.getHardness(this.currentBlock) < 1.0F)
      this.prevProgress = this.progress; 
    this.progress = mc.playerController.curBlockDamageMP;
    if (this.progress < this.prevProgress) {
      this.prevProgress = this.progress;
    } else {
      this.progress = 1.0F;
      this.prevProgress = 1.0F;
    } 
    super.onClientTick(event);
  }
  
  public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
    if (this.mode.getValString().equalsIgnoreCase("id") && mc.world.isRemote) {
      IBlockState blockState = BlockUtils.getState(event.getPos());
      this.id = Block.getIdFromBlock(blockState.getBlock());
    } 
    super.onLeftClickBlock(event);
  }
  
  public void onWorldUnload(WorldEvent.Unload event) {
    if (this.StopOnKick.getValBoolean())
      toggle(); 
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (this.currentBlock == null)
      return; 
    if (this.Drawbox.getValBoolean())
      if (this.mode.getValString().equalsIgnoreCase("all")) {
        RenderUtils.RenderBlock(this.Drawmode.getValString(), RenderUtils.Standardbb(this.currentBlock), this.allcolor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      } else if (this.mode.getValString().equalsIgnoreCase("id")) {
        RenderUtils.RenderBlock(this.Drawmode.getValString(), RenderUtils.Standardbb(this.currentBlock), this.idcolor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      }  
    super.onRenderWorldLast(event);
  }
}
