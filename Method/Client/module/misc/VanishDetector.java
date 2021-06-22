package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class VanishDetector extends Module {
  private final HashMap<UUID, String> Hashmap;
  
  DecimalFormat decimalFormat;
  
  Setting EntityMove;
  
  Setting EntityBedloc;
  
  Setting StopRemove;
  
  Setting Soundpos;
  
  Setting BlockChanges;
  
  public VanishDetector() {
    super("VanishDetector", 0, Category.MISC, "Staff Vanish Detector");
    this.decimalFormat = new DecimalFormat("0.00");
    this.EntityMove = Main.setmgr.add(new Setting("EntityMove", this, false));
    this.EntityBedloc = Main.setmgr.add(new Setting("Entity Bed ", this, true, this.EntityMove, 1));
    this.StopRemove = Main.setmgr.add(new Setting("Stop Entity Remove", this, true));
    this.Soundpos = Main.setmgr.add(new Setting("Sound pos", this, false));
    this.BlockChanges = Main.setmgr.add(new Setting("BlockChanges", this, false));
    this.Hashmap = new HashMap<>();
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.IN) {
      if (this.EntityMove.getValBoolean() && 
        packet instanceof SPacketEntity.S17PacketEntityLookMove) {
        SPacketEntity.S17PacketEntityLookMove packet1 = (SPacketEntity.S17PacketEntityLookMove)packet;
        if (packet1.getEntity((World)mc.world) instanceof net.minecraft.client.entity.EntityOtherPlayerMP) {
          ChatUtils.message("Player: " + packet1.getEntity((World)mc.world).getName() + " pos X:" + this.decimalFormat.format((packet1.getEntity((World)mc.world)).posX) + " Y: " + this.decimalFormat.format((packet1.getEntity((World)mc.world)).posY) + " Z: " + this.decimalFormat.format((packet1.getEntity((World)mc.world)).posZ));
          if (this.EntityBedloc.getValBoolean())
            ChatUtils.message("Player: " + packet1.getEntity((World)mc.world).getName() + " Bed " + ((EntityPlayer)packet1.getEntity((World)mc.world)).bedLocation); 
        } 
      } 
      if (this.Soundpos.getValBoolean() && 
        packet instanceof SPacketSoundEffect) {
        SPacketSoundEffect packet1 = (SPacketSoundEffect)packet;
        if (packet1.getCategory() == SoundCategory.PLAYERS && 
          ((SPacketSoundEffect)packet).posY < 250) {
          boolean found = false;
          for (Entity entity : mc.world.loadedEntityList) {
            if (Math.sqrt(Math.pow(entity.posX - packet1.posX, 2.0D) + Math.pow(entity.posY - packet1.posY, 2.0D) + Math.pow(entity.posZ - packet1.posZ, 2.0D)) < 8.0D) {
              found = true;
              break;
            } 
          } 
          for (TileEntity entity : mc.world.loadedTileEntityList) {
            if (Math.sqrt(Math.pow(((entity.getPos()).x - packet1.posX), 2.0D) + Math.pow(((entity.getPos()).y - packet1.posY), 2.0D) + Math.pow(((entity.getPos()).z - packet1.posZ), 2.0D)) < 8.0D) {
              found = true;
              break;
            } 
          } 
          if (!found)
            ChatUtils.message("Sound near: X: " + packet1.posX + " Y: " + packet1.posY + " Z: " + packet1.posZ); 
        } 
      } 
      if (packet instanceof net.minecraft.network.play.server.SPacketDestroyEntities && this.StopRemove.getValBoolean())
        return false; 
      if (this.BlockChanges.getValBoolean() && 
        packet instanceof SPacketBlockChange) {
        SPacketBlockChange packet1 = (SPacketBlockChange)packet;
        boolean found = false;
        for (Entity entity : mc.world.loadedEntityList) {
          if (Math.sqrt(Math.pow(entity.posX - (packet1.getBlockPosition()).x, 2.0D) + Math.pow(entity.posY - (packet1.getBlockPosition()).y, 2.0D) + Math.pow(entity.posZ - (packet1.getBlockPosition()).z, 2.0D)) < 8.0D) {
            found = true;
            break;
          } 
        } 
        for (TileEntity entity : mc.world.loadedTileEntityList) {
          if (Math.sqrt(Math.pow(((entity.getPos()).x - (packet1.getBlockPosition()).x), 2.0D) + Math.pow(((entity.getPos()).y - (packet1.getBlockPosition()).y), 2.0D) + Math.pow(((entity.getPos()).z - (packet1.getBlockPosition()).z), 2.0D)) < 8.0D) {
            found = true;
            break;
          } 
        } 
        if (found)
          ChatUtils.message("BlockChange: X: " + (packet1.getBlockPosition()).x + " Y: " + (packet1.getBlockPosition()).y + " Z: " + (packet1.getBlockPosition()).z); 
      } 
    } 
    if (packet instanceof SPacketPlayerListItem) {
      SPacketPlayerListItem sPacketPlayerListItem = (SPacketPlayerListItem)packet;
      if (sPacketPlayerListItem.getAction() == SPacketPlayerListItem.Action.UPDATE_LATENCY) {
        HashSet<UUID> set = new HashSet<>();
        for (SPacketPlayerListItem.AddPlayerData addPlayerData : sPacketPlayerListItem.getEntries())
          set.add(addPlayerData.getProfile().getId()); 
        (new Thread(() -> {
              for (UUID uuid : set)
                this.Hashmap.put(uuid, uuid.toString()); 
            })).start();
      } 
    } 
    return true;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    synchronized (this.Hashmap) {
      for (Map.Entry<UUID, String> entry : this.Hashmap.entrySet())
        ChatUtils.message("PlayerPreviewElement " + (String)entry.getValue() + " has gone into vanish (" + entry.getKey() + ")"); 
      this.Hashmap.clear();
    } 
  }
}
