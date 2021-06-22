package Method.Client.utils.SeedViewer;

import Method.Client.utils.system.Wrapper;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;

public class AwesomeWorld extends World {
  private ChunkProviderClient clientChunkProvider;
  
  public ChunkProviderClient getChunkProvider() {
    return (ChunkProviderClient)super.getChunkProvider();
  }
  
  protected AwesomeWorld(WorldInfo worldInfo) {
    super((ISaveHandler)new SaveHandlerMP(), worldInfo, DimensionManager.createProviderFor(0), Wrapper.mc.profiler, true);
    getWorldInfo().setDifficulty(EnumDifficulty.PEACEFUL);
    this.provider.setWorld(this);
    setSpawnPoint(new BlockPos(8, 64, 8));
    this.chunkProvider = createChunkProvider();
    this.mapStorage = (MapStorage)new SaveDataMemoryStorage();
    calculateInitialSkylight();
    calculateInitialWeather();
    initCapabilities();
  }
  
  protected IChunkProvider createChunkProvider() {
    this.clientChunkProvider = new ChunkProviderClient(this);
    return (IChunkProvider)this.clientChunkProvider;
  }
  
  protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
    return (allowEmpty || !getChunkProvider().provideChunk(x, z).isEmpty());
  }
}
