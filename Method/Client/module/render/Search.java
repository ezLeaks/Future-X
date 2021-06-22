package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Screens.Custom.Search.SearchGuiSettings;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.Executer;
import Method.Client.utils.visual.RenderUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class Search extends Module {
  Setting OverlayColor = Main.setmgr.add(new Setting("OverlayColor", this, 0.0D, 1.0D, 1.0D, 0.42D));
  
  Setting Mode = Main.setmgr.add(new Setting("block Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.8D, 0.0D, 3.0D, false));
  
  Setting Gui = Main.setmgr.add(new Setting("Gui", this, (GuiScreen)Main.Search));
  
  private final SearchGuiSettings blocks = new SearchGuiSettings(new Block[] { 
        Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, 
        Blocks.ENCHANTING_TABLE, Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, 
        Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX });
  
  public static ArrayList<String> blockNames;
  
  private final Long2ObjectArrayMap<MyChunk> chunks;
  
  private final Pool<MyBlock> blockPool;
  
  private final LongList toRemove;
  
  private final BlockPos.MutableBlockPos blockPos;
  
  public Search() {
    super("Search", 0, Category.RENDER, "Search");
    this.chunks = new Long2ObjectArrayMap();
    this.blockPool = new Pool<>(() -> new MyBlock());
    this.toRemove = (LongList)new LongArrayList();
    this.blockPos = new BlockPos.MutableBlockPos();
  }
  
  public void setup() {
    Executer.init();
  }
  
  public void onEnable() {
    blockNames = new ArrayList<>(SearchGuiSettings.getBlockNames());
    Executer.init();
    searchViewDistance();
  }
  
  public void onDisable() {
    for (ObjectIterator<MyChunk> objectIterator = this.chunks.values().iterator(); objectIterator.hasNext(); ) {
      MyChunk chunk = objectIterator.next();
      chunk.dispose();
    } 
    this.chunks.clear();
  }
  
  private void searchViewDistance() {
    int viewDist = mc.gameSettings.renderDistanceChunks;
    for (int x = mc.player.chunkCoordX - viewDist; x <= mc.player.chunkCoordX + viewDist; x++) {
      for (int z = mc.player.chunkCoordZ - viewDist; z <= mc.player.chunkCoordZ + viewDist; z++) {
        if (mc.world.isChunkGeneratedAt(x, z))
          searchChunk(mc.world.getChunk(x, z)); 
      } 
    } 
  }
  
  public void ChunkeventLOAD(ChunkEvent.Load event) {
    searchChunk(event.getChunk());
  }
  
  private void searchChunk(Chunk chunk) {
    Executer.execute(() -> {
          MyChunk myChunk = new MyChunk((chunk.getPos()).x, (chunk.getPos()).z);
          for (int x = chunk.getPos().getXStart(); x <= chunk.getPos().getXEnd(); x++) {
            for (int z = chunk.getPos().getZStart(); z <= chunk.getPos().getZEnd(); z++) {
              for (int y = 0; y < 256; y++) {
                this.blockPos.setPos(x, y, z);
                if (isVisible(chunk.getBlockState((BlockPos)this.blockPos).getBlock()))
                  myChunk.add((BlockPos)this.blockPos, false); 
              } 
            } 
          } 
          synchronized (this.chunks) {
            if (myChunk.blocks.size() > 0)
              this.chunks.put(ChunkPos.asLong((chunk.getPos()).x, (chunk.getPos()).z), myChunk); 
          } 
        });
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof SPacketBlockChange) {
      SPacketBlockChange packet2 = (SPacketBlockChange)packet;
      onBlockUpdate(packet2.getBlockPosition(), packet2.blockState);
    } 
    if (packet instanceof SPacketBlockAction) {
      SPacketBlockAction packet2 = (SPacketBlockAction)packet;
      onBlockUpdate(packet2.getBlockPosition(), packet2.getBlockType().getDefaultState());
    } 
    if (packet instanceof SPacketMultiBlockChange) {
      SPacketMultiBlockChange packet2 = (SPacketMultiBlockChange)packet;
      for (SPacketMultiBlockChange.BlockUpdateData changedBlock : packet2.getChangedBlocks())
        onBlockUpdate(changedBlock.getPos(), changedBlock.getBlockState()); 
    } 
    return true;
  }
  
  public void onBlockUpdate(BlockPos blockPos, IBlockState blockState) {
    Executer.execute(() -> {
          int chunkX = blockPos.getX() >> 4;
          int chunkZ = blockPos.getZ() >> 4;
          long key = ChunkPos.asLong(chunkX, chunkZ);
          synchronized (this.chunks) {
            if (isVisible(blockState.getBlock())) {
              ((MyChunk)this.chunks.computeIfAbsent(Long.valueOf(key), ())).add(blockPos, true);
            } else {
              MyChunk chunk = (MyChunk)this.chunks.get(key);
              if (chunk != null)
                chunk.remove(blockPos); 
            } 
          } 
        });
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    synchronized (this.chunks) {
      this.toRemove.clear();
      for (LongIterator<Long> longIterator = this.chunks.keySet().iterator(); longIterator.hasNext(); ) {
        long key = ((Long)longIterator.next()).longValue();
        MyChunk chunk = (MyChunk)this.chunks.get(key);
        if (chunk.shouldBeDeleted()) {
          this.toRemove.add(key);
          continue;
        } 
        chunk.render();
      } 
      for (LongListIterator<Long> longListIterator = this.toRemove.iterator(); longListIterator.hasNext(); ) {
        long key = ((Long)longListIterator.next()).longValue();
        this.chunks.remove(key);
      } 
    } 
  }
  
  private boolean isVisible(Block block) {
    String name = getName(block);
    int index = Collections.binarySearch((List)blockNames, name);
    return (index >= 0);
  }
  
  public static String getName(Block block) {
    return "" + Block.REGISTRY.getNameForObject(block);
  }
  
  private class MyChunk {
    private final int x;
    
    private final int z;
    
    private final List<Search.MyBlock> blocks = new ArrayList<>();
    
    public MyChunk(int x, int z) {
      this.x = x;
      this.z = z;
    }
    
    public void add(BlockPos blockPos, boolean checkForDuplicates) {
      if (checkForDuplicates)
        for (Search.MyBlock myBlock : this.blocks) {
          if (myBlock.equals(blockPos))
            return; 
        }  
      Search.MyBlock block = Search.this.blockPool.get();
      block.set(blockPos);
      this.blocks.add(block);
    }
    
    public void remove(BlockPos blockPos) {
      for (int i = 0; i < this.blocks.size(); i++) {
        Search.MyBlock block = this.blocks.get(i);
        if (block.equals(blockPos)) {
          this.blocks.remove(i);
          return;
        } 
      } 
    }
    
    public boolean shouldBeDeleted() {
      int viewDist = Search.mc.gameSettings.renderDistanceChunks + 1;
      return (this.x > Search.mc.player.chunkCoordX + viewDist || this.x < Search.mc.player.chunkCoordX - viewDist || this.z > Search.mc.player.chunkCoordZ + viewDist || this.z < Search.mc.player.chunkCoordZ - viewDist);
    }
    
    public void render() {
      for (Search.MyBlock block : this.blocks)
        block.render(); 
    }
    
    public void dispose() {
      for (Search.MyBlock block : this.blocks)
        Search.this.blockPool.free(block); 
      this.blocks.clear();
    }
  }
  
  private class MyBlock {
    private int x;
    
    private int y;
    
    private int z;
    
    private MyBlock() {}
    
    public void set(BlockPos blockPos) {
      this.x = blockPos.getX();
      this.y = blockPos.getY();
      this.z = blockPos.getZ();
    }
    
    public void render() {
      RenderUtils.RenderBlock(Search.this.Mode.getValString(), RenderUtils.Standardbb(new BlockPos(this.x, this.y, this.z)), Search.this.OverlayColor.getcolor(), Double.valueOf(Search.this.LineWidth.getValDouble()));
    }
    
    public boolean equals(BlockPos blockPos) {
      return (this.x == blockPos.getX() && this.y == blockPos.getY() && this.z == blockPos.getZ());
    }
  }
  
  public static class Pool<T> {
    private final List<T> items = new ArrayList<>();
    
    private final Search.Producer<T> producer;
    
    public Pool(Search.Producer<T> producer) {
      this.producer = producer;
    }
    
    public T get() {
      if (this.items.size() > 0)
        return this.items.remove(this.items.size() - 1); 
      return this.producer.create();
    }
    
    public void free(T obj) {
      this.items.add(obj);
    }
  }
  
  public static interface Producer<T> {
    T create();
  }
}
