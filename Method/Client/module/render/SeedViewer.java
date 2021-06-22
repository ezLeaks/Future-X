package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.SeedViewer.WorldLoader;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.visual.ChatUtils;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SeedViewer extends Module {
  Setting OverlayColor = Main.setmgr.add(new Setting("OverlayColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
  
  Setting Mode = Main.setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  Setting BlockLimit = Main.setmgr.add(new Setting("BlockLimit", this, 200.0D, 0.0D, 5000.0D, false));
  
  Setting Fallingblock = Main.setmgr.add(new Setting("Falling block", this, false));
  
  Setting Liquid = Main.setmgr.add(new Setting("Liquid", this, false));
  
  Setting Tree = Main.setmgr.add(new Setting("Tree", this, false));
  
  Setting Bush = Main.setmgr.add(new Setting("Bush", this, false));
  
  Setting Distance = Main.setmgr.add(new Setting("Distance", this, 6.0D, 0.0D, 15.0D, true));
  
  Setting LavaMix = Main.setmgr.add(new Setting("LavaMix", this, false));
  
  Setting FalsePositive = Main.setmgr.add(new Setting("FalsePositive", this, false));
  
  Setting GrassSpread = Main.setmgr.add(new Setting("GrassSpread", this, false));
  
  private static ExecutorService executor;
  
  private static ExecutorService executor2;
  
  public void setup() {
    executor = Executors.newSingleThreadExecutor();
    executor2 = Executors.newSingleThreadExecutor();
  }
  
  public int currentdis = 0;
  
  private ArrayList<ChunkData> chunks;
  
  private ArrayList<int[]> tobesearch;
  
  private final TimerUtils timer;
  
  public SeedViewer() {
    super("SeedViewer", 0, Category.RENDER, "SeedViewer");
    this.chunks = new ArrayList<>();
    this.tobesearch = (ArrayList)new ArrayList<>();
    this.timer = new TimerUtils();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.timer.isDelay(500L)) {
      if (mc.player.dimension != this.currentdis) {
        ChatUtils.warning("You must reenable on dimension change!");
        toggle();
      } 
      searchViewDistance();
      runviewdistance();
      this.timer.setLastMS();
    } 
    int[] remove = null;
    for (int[] vec2d : this.tobesearch) {
      remove = vec2d;
      executor.execute(() -> WorldLoader.CreateChunk(vec2d[0], vec2d[1], mc.player.dimension));
    } 
    this.tobesearch.remove(remove);
  }
  
  public void onEnable() {
    if (mc.isSingleplayer()) {
      ChatUtils.warning("Only for multiplayer");
      toggle();
    } 
    if (WorldLoader.seed == 44776655L) {
      ChatUtils.message("Set Seed using the" + TextFormatting.GOLD + " @WorldSeed" + TextFormatting.RESET + " Command");
      toggle();
      return;
    } 
    this.currentdis = mc.player.dimension;
    executor = Executors.newSingleThreadExecutor();
    executor2 = Executors.newSingleThreadExecutor();
    WorldLoader.setup();
    ChatUtils.warning("Still Working on this.");
    this.chunks = new ArrayList<>();
    searchViewDistance();
  }
  
  private void searchViewDistance() {
    executor.execute(() -> {
          for (int x = mc.player.chunkCoordX - (int)this.Distance.getValDouble(); x <= mc.player.chunkCoordX + (int)this.Distance.getValDouble(); x++) {
            for (int z = mc.player.chunkCoordZ - (int)this.Distance.getValDouble(); z <= mc.player.chunkCoordZ + (int)this.Distance.getValDouble(); z++) {
              if (havenotsearched(x, z) && mc.world.isChunkGeneratedAt(x, z)) {
                boolean found = false;
                for (int[] vec2d : this.tobesearch) {
                  if (vec2d[0] == x && vec2d[1] == z) {
                    found = true;
                    break;
                  } 
                } 
                if (!found)
                  this.tobesearch.add(new int[] { x, z }); 
              } 
            } 
          } 
        });
  }
  
  private void runviewdistance() {
    for (int x = mc.player.chunkCoordX - (int)this.Distance.getValDouble(); x <= mc.player.chunkCoordX + (int)this.Distance.getValDouble(); x++) {
      for (int z = mc.player.chunkCoordZ - (int)this.Distance.getValDouble(); z <= mc.player.chunkCoordZ + (int)this.Distance.getValDouble(); z++) {
        if (mc.world.isChunkGeneratedAt(x, z) && 
          WorldLoader.fakeworld.isChunkGeneratedAt(x, z) && WorldLoader.fakeworld.isChunkGeneratedAt(x + 1, z) && WorldLoader.fakeworld.isChunkGeneratedAt(x, z + 1) && WorldLoader.fakeworld
          .isChunkGeneratedAt(x + 1, z + 1) && 
          havenotsearched(x, z)) {
          ChunkData data = new ChunkData(new ChunkPos(x, z), false);
          searchChunk(mc.world.getChunk(x, z), data);
          this.chunks.add(data);
        } 
      } 
    } 
  }
  
  private boolean havenotsearched(int x, int z) {
    for (ChunkData chunk : this.chunks) {
      if (chunk.chunkPos.x == x && chunk.chunkPos.z == z)
        return false; 
    } 
    return true;
  }
  
  private void searchChunk(Chunk chunk, ChunkData data) {
    executor2.execute(() -> {
          try {
            for (int x = chunk.getPos().getXStart(); x <= chunk.getPos().getXEnd(); x++) {
              for (int z = chunk.getPos().getZStart(); z <= chunk.getPos().getZEnd(); z++) {
                for (int y = 0; y < 255; y++) {
                  if (BlockFast(new BlockPos(x, y, z), WorldLoader.fakeworld.getBlockState(new BlockPos(x, y, z)).getBlock(), chunk.getBlockState(x, y, z).getBlock()))
                    data.blocks.add(new BlockPos(x, y, z)); 
                } 
              } 
            } 
            data.Searched = true;
          } catch (Exception exception) {}
        });
  }
  
  private boolean BlockFast(BlockPos blockPos, Block FakeChunk, Block RealChunk) {
    if (RealChunk instanceof net.minecraft.block.BlockSnow)
      return false; 
    if (FakeChunk instanceof net.minecraft.block.BlockSnow)
      return false; 
    if (RealChunk instanceof net.minecraft.block.BlockVine)
      return false; 
    if (FakeChunk instanceof net.minecraft.block.BlockVine)
      return false; 
    if (!this.Fallingblock.getValBoolean()) {
      if (RealChunk instanceof net.minecraft.block.BlockFalling)
        return false; 
      if (FakeChunk instanceof net.minecraft.block.BlockFalling)
        return false; 
    } 
    if (!this.Liquid.getValBoolean()) {
      if (RealChunk instanceof net.minecraft.block.BlockLiquid)
        return false; 
      if (FakeChunk instanceof net.minecraft.block.BlockLiquid)
        return false; 
      if (mc.world.getBlockState(blockPos.down()).getBlock() instanceof net.minecraft.block.BlockLiquid)
        return false; 
      if (mc.world.getBlockState(blockPos.down(2)).getBlock() instanceof net.minecraft.block.BlockLiquid)
        return false; 
    } 
    if (!this.Tree.getValBoolean()) {
      if (FakeChunk instanceof net.minecraft.block.BlockGrass && 
        Treeroots(blockPos))
        return false; 
      if (RealChunk instanceof net.minecraft.block.BlockLog || RealChunk instanceof net.minecraft.block.BlockLeaves)
        return false; 
      if (FakeChunk instanceof net.minecraft.block.BlockLog || FakeChunk instanceof net.minecraft.block.BlockLeaves)
        return false; 
    } 
    if (!this.GrassSpread.getValBoolean()) {
      if (RealChunk instanceof net.minecraft.block.BlockGrass && FakeChunk instanceof net.minecraft.block.BlockDirt)
        return false; 
      if (RealChunk instanceof net.minecraft.block.BlockDirt && FakeChunk instanceof net.minecraft.block.BlockGrass)
        return false; 
    } 
    if (!this.Bush.getValBoolean()) {
      if (RealChunk instanceof net.minecraft.block.BlockBush)
        return false; 
      if (RealChunk instanceof net.minecraft.block.BlockReed)
        return false; 
      if (FakeChunk instanceof net.minecraft.block.BlockBush)
        return false; 
    } 
    if (!this.LavaMix.getValBoolean() && (
      RealChunk instanceof net.minecraft.block.BlockObsidian || RealChunk.equals(Blocks.COBBLESTONE)) && 
      Lavamix(blockPos))
      return false; 
    if (!this.FalsePositive.getValBoolean()) {
      if (FakeChunk instanceof net.minecraft.block.BlockOre && (RealChunk instanceof net.minecraft.block.BlockStone || RealChunk instanceof net.minecraft.block.BlockMagma || RealChunk instanceof net.minecraft.block.BlockNetherrack || RealChunk instanceof net.minecraft.block.BlockDirt))
        return false; 
      if (RealChunk instanceof net.minecraft.block.BlockOre && (FakeChunk instanceof net.minecraft.block.BlockStone || FakeChunk instanceof net.minecraft.block.BlockMagma || FakeChunk instanceof net.minecraft.block.BlockNetherrack || FakeChunk instanceof net.minecraft.block.BlockDirt))
        return false; 
      if (FakeChunk instanceof net.minecraft.block.BlockRedstoneOre && (RealChunk instanceof net.minecraft.block.BlockStone || RealChunk instanceof net.minecraft.block.BlockDirt))
        return false; 
      if (RealChunk instanceof net.minecraft.block.BlockRedstoneOre && (FakeChunk instanceof net.minecraft.block.BlockStone || FakeChunk instanceof net.minecraft.block.BlockDirt))
        return false; 
      if (FakeChunk instanceof net.minecraft.block.BlockGlowstone && RealChunk instanceof net.minecraft.block.BlockAir)
        return false; 
      if (RealChunk instanceof net.minecraft.block.BlockGlowstone && FakeChunk instanceof net.minecraft.block.BlockAir)
        return false; 
      if (FakeChunk instanceof net.minecraft.block.BlockMagma && RealChunk instanceof net.minecraft.block.BlockNetherrack)
        return false; 
      if (RealChunk instanceof net.minecraft.block.BlockMagma && FakeChunk instanceof net.minecraft.block.BlockNetherrack)
        return false; 
      if (RealChunk instanceof net.minecraft.block.BlockFire || FakeChunk instanceof net.minecraft.block.BlockFire)
        return false; 
      if (RealChunk instanceof net.minecraft.block.BlockOre && FakeChunk instanceof net.minecraft.block.BlockOre)
        return false; 
      if (RealChunk.getLocalizedName().equals(Blocks.MONSTER_EGG.getLocalizedName()) && FakeChunk instanceof net.minecraft.block.BlockStone)
        return false; 
      if ((FakeChunk instanceof net.minecraft.block.BlockStone && RealChunk instanceof net.minecraft.block.BlockDirt) || (FakeChunk instanceof net.minecraft.block.BlockDirt && RealChunk instanceof net.minecraft.block.BlockStone))
        return false; 
      if (!(FakeChunk instanceof net.minecraft.block.BlockAir) && RealChunk instanceof net.minecraft.block.BlockAir && 
        !mc.world.getBlockState(blockPos).getBlock().getLocalizedName().equals(RealChunk.getLocalizedName()))
        return false; 
    } 
    if (!FakeChunk.getLocalizedName().equals(RealChunk.getLocalizedName()))
      return true; 
    return false;
  }
  
  public boolean Treeroots(BlockPos b) {
    if (mc.world.getBlockState(b.up()).getBlock() instanceof net.minecraft.block.BlockLog)
      return true; 
    return false;
  }
  
  public boolean Lavamix(BlockPos b) {
    if (mc.world.getBlockState(b.up()).getBlock() instanceof net.minecraft.block.BlockLiquid)
      return true; 
    if (mc.world.getBlockState(b.down()).getBlock() instanceof net.minecraft.block.BlockLiquid)
      return true; 
    if (mc.world.getBlockState(b.add(1, 0, 0)).getBlock() instanceof net.minecraft.block.BlockLiquid)
      return true; 
    if (mc.world.getBlockState(b.add(0, 0, 1)).getBlock() instanceof net.minecraft.block.BlockLiquid)
      return true; 
    if (mc.world.getBlockState(b.add(-1, 0, 0)).getBlock() instanceof net.minecraft.block.BlockLiquid)
      return true; 
    if (mc.world.getBlockState(b.add(0, 0, -1)).getBlock() instanceof net.minecraft.block.BlockLiquid)
      return true; 
    return false;
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    try {
      int blocklimit = 0;
      ArrayList<ChunkData> Remove = new ArrayList<>();
      for (ChunkData chunk : this.chunks) {
        if (chunk.Searched) {
          if (mc.player.getDistance(chunk.chunkPos.getXEnd(), 100.0D, chunk.chunkPos.getZEnd()) > 2000.0D)
            Remove.add(chunk); 
          for (BlockPos block : chunk.blocks) {
            if (blocklimit > this.BlockLimit.getValDouble())
              break; 
            RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Standardbb(new BlockPos(block.x, block.y, block.z)), this.OverlayColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
            blocklimit++;
          } 
        } 
      } 
      this.chunks.removeAll(Remove);
    } catch (Exception exception) {}
    super.onRenderWorldLast(event);
  }
  
  public static class ChunkData {
    private boolean Searched;
    
    public List<BlockPos> getBlocks() {
      return this.blocks;
    }
    
    public final List<BlockPos> blocks = new ArrayList<>();
    
    private ChunkPos chunkPos;
    
    public ChunkData(ChunkPos chunkPos, boolean Searched) {
      this.chunkPos = chunkPos;
      this.Searched = Searched;
    }
  }
}
