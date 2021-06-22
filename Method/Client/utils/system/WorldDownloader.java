package Method.Client.utils.system;

import Method.Client.utils.visual.ChatUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.world.ChunkEvent;

public class WorldDownloader {
  public static AnvilChunkLoader newdownload;
  
  public static boolean Saving = false;
  
  public static File SaveDir;
  
  public static String Savename;
  
  public static ArrayList<Chunk> chunks = new ArrayList<>();
  
  public static void ChunkeventLOAD(ChunkEvent.Load event) {
    if (Saving)
      chunks.add(event.getChunk()); 
  }
  
  public static ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata, File file) {
    return (ISaveHandler)new AnvilSaveHandler(file, saveName, storePlayerdata, new DataFixer((Wrapper.mc.getDataFixer()).version));
  }
  
  public static void Clienttick() {
    if (Saving) {
      ArrayList<Chunk> remove = new ArrayList<>();
      for (Chunk option : chunks) {
        if (Wrapper.mc.world.isChunkGeneratedAt(option.x, option.z))
          try {
            newdownload.saveChunk((World)Wrapper.INSTANCE.world(), Wrapper.mc.world.getChunk(option.x, option.z));
          } catch (MinecraftException|java.io.IOException e) {
            e.printStackTrace();
          }  
        if (newdownload.isChunkGeneratedAt(option.x, option.z))
          remove.add(option); 
      } 
      chunks.removeAll(remove);
    } 
  }
  
  public static void start() {
    ChatUtils.message("Starting world download");
    Savename = "Download Time" + ((int)System.currentTimeMillis() / 1000);
    SaveDir = new File((new File((Wrapper.INSTANCE.mc()).gameDir, "saves")).getAbsolutePath(), Savename);
    if (!SaveDir.exists()) {
      SaveDir.mkdir();
    } else {
      ChatUtils.error("This file already Exists?");
      ChatUtils.message(SaveDir.getPath());
      return;
    } 
    newdownload = new AnvilChunkLoader(new File(SaveDir.getAbsolutePath()), Wrapper.mc.getDataFixer());
    Saving = true;
    chunks.addAll((Collection<? extends Chunk>)(Wrapper.mc.world.getChunkProvider()).loadedChunks.values());
    ChatUtils.message(".minecraft/saves/" + Savename);
  }
  
  protected static NBTTagList newDoubleNBTList(double... numbers) {
    NBTTagList nbttaglist = new NBTTagList();
    for (double d0 : numbers)
      nbttaglist.appendTag((NBTBase)new NBTTagDouble(d0)); 
    return nbttaglist;
  }
  
  public static void stop() {
    ChatUtils.message("Stopped world download");
    newdownload.flush();
    Saving = false;
    ISaveHandler isavehandler = getSaveLoader(Savename, true, new File((Wrapper.INSTANCE.mc()).gameDir, "saves"));
    WorldInfo info = new WorldInfo(Wrapper.mc.world.getWorldInfo());
    NBTTagCompound player = new NBTTagCompound();
    Wrapper.mc.player.writeToNBT(player);
    info.setSpawn(Wrapper.mc.player.getPosition());
    info.playerTag = player;
    try {
      player.setTag("Pos", (NBTBase)newDoubleNBTList(new double[] { Wrapper.mc.player.posX, Wrapper.mc.player.posY, Wrapper.mc.player.posZ }));
    } catch (Exception exception) {}
    info.setAllowCommands(true);
    info.setGameType(GameType.CREATIVE);
    info.generatorOptions = "3;minecraft:air;127;decoration";
    info.setTerrainType(WorldType.FLAT);
    isavehandler.saveWorldInfoWithPlayer(info, player);
    if (newdownload.getPendingSaveCount() > 1)
      ChatUtils.warning("There are still: " + newdownload.getPendingSaveCount() + " Chunks Pending to be saved"); 
    ChatUtils.message("Finished Download!");
  }
}
