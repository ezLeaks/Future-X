package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.RenderBlockModelEvent;
import Method.Client.utils.Patcher.Events.RenderTileEntityEvent;
import Method.Client.utils.system.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class NoBlockLag extends Module {
  Setting StopCollision;
  
  Setting antiSound;
  
  Setting antiPiston;
  
  Setting antiSign;
  
  Setting Storage;
  
  Setting Spawner;
  
  Setting Beacon;
  
  Setting MobHead;
  
  Setting Falling;
  
  Setting Banner;
  
  Setting Gentity;
  
  Setting Object;
  
  Setting Grass;
  
  Setting Painting;
  
  Setting Fire;
  
  Setting NoChunk;
  
  ArrayList<BlockPos> modifiedsigns;
  
  private Set<SoundEvent> sounds;
  
  public NoBlockLag() {
    super("NoBlockLag", 0, Category.RENDER, "NoBlockLag");
    this.StopCollision = Main.setmgr.add(new Setting("StopCollision", this, false));
    this.antiSound = Main.setmgr.add(new Setting("antiSound", this, true));
    this.antiPiston = Main.setmgr.add(new Setting("antiPiston", this, false));
    this.antiSign = Main.setmgr.add(new Setting("antiSign", this, false));
    this.Storage = Main.setmgr.add(new Setting("Storage", this, false));
    this.Spawner = Main.setmgr.add(new Setting("Spawner", this, false));
    this.Beacon = Main.setmgr.add(new Setting("Beacon", this, false));
    this.MobHead = Main.setmgr.add(new Setting("MobHead", this, false));
    this.Falling = Main.setmgr.add(new Setting("Falling", this, false));
    this.Banner = Main.setmgr.add(new Setting("Banner", this, false));
    this.Gentity = Main.setmgr.add(new Setting("Global Entity", this, false));
    this.Object = Main.setmgr.add(new Setting("objects", this, false));
    this.Grass = Main.setmgr.add(new Setting("Grass", this, false));
    this.Painting = Main.setmgr.add(new Setting("Paintings", this, false));
    this.Fire = Main.setmgr.add(new Setting("Fire", this, false));
    this.NoChunk = Main.setmgr.add(new Setting("NoChunk", this, false));
    this.modifiedsigns = new ArrayList<>();
  }
  
  public void setup() {
    this.sounds = new HashSet<>();
  }
  
  public void onRenderTileEntity(RenderTileEntityEvent event) {
    Block block = event.getTileEntity().getBlockType().getBlockState().getBlock();
    if (BlockCheck(block))
      event.setCanceled(true); 
  }
  
  public void onRenderBlockModel(RenderBlockModelEvent event) {
    Block block = event.getState().getBlock();
    if (BlockCheck(block))
      event.setCanceled(true); 
  }
  
  public void DrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
    if (this.StopCollision.getValBoolean() && 
      BlockCheck(mc.world.getBlockState(event.getTarget().getBlockPos()).getBlock()))
      event.setCanceled(true); 
  }
  
  public boolean BlockCheck(Block block) {
    if (this.antiPiston.getValBoolean() && (block instanceof net.minecraft.block.BlockPistonMoving || block instanceof net.minecraft.block.BlockPistonExtension))
      return true; 
    if (this.Storage.getValBoolean() && (block instanceof net.minecraft.block.BlockChest || block instanceof net.minecraft.block.BlockEnderChest || block instanceof net.minecraft.block.BlockDispenser || block instanceof net.minecraft.block.BlockFurnace || block instanceof net.minecraft.block.BlockHopper || block instanceof net.minecraft.block.BlockShulkerBox || block instanceof net.minecraft.block.BlockBrewingStand))
      return true; 
    if (this.Spawner.getValBoolean() && block instanceof net.minecraft.block.BlockMobSpawner)
      return true; 
    if (this.Beacon.getValBoolean() && block instanceof net.minecraft.block.BlockBeacon)
      return true; 
    if (this.Banner.getValBoolean() && block instanceof net.minecraft.block.BlockBanner)
      return true; 
    if (this.Fire.getValBoolean() && block instanceof net.minecraft.block.BlockFire)
      return true; 
    if (this.Grass.getValBoolean() && (block instanceof net.minecraft.block.BlockDoublePlant || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockDeadBush))
      return true; 
    if (this.MobHead.getValBoolean() && block instanceof net.minecraft.block.BlockSkull)
      return true; 
    return (this.Falling.getValBoolean() && block instanceof net.minecraft.block.BlockFalling);
  }
  
  public void onEnable() {
    this.modifiedsigns.clear();
    mc.renderGlobal.loadRenderers();
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (this.antiSign.getValBoolean())
      for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
        if (tileEntity instanceof TileEntitySign) {
          TileEntitySign tileEntitySign = (TileEntitySign)tileEntity;
          if (!this.modifiedsigns.contains(tileEntity.getPos())) {
            this.modifiedsigns.add(tileEntity.getPos());
            int lenght = 0;
            for (ITextComponent line : tileEntitySign.signText)
              lenght = line.getUnformattedText().length(); 
            String[] array = { "METHOD", "Sign length", "" + lenght + "", "" };
            for (int j = 0; j < tileEntitySign.signText.length; j++)
              tileEntitySign.signText[j] = (ITextComponent)new TextComponentString(array[j]); 
            continue;
          } 
          ITextComponent[] arrayOfITextComponent = tileEntitySign.signText;
          int i = arrayOfITextComponent.length;
          byte b = 0;
          if (b < i) {
            ITextComponent line = arrayOfITextComponent[b];
            if (!line.getUnformattedText().startsWith("METHOD"))
              this.modifiedsigns.remove(tileEntity.getPos()); 
          } 
        } 
      }  
    super.onRenderWorldLast(event);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if ((packet instanceof net.minecraft.network.play.server.SPacketSpawnGlobalEntity && this.Gentity.getValBoolean()) || (packet instanceof net.minecraft.network.play.server.SPacketSpawnObject && this.Object.getValBoolean()) || (packet instanceof net.minecraft.network.play.server.SPacketSpawnPainting && this.Painting.getValBoolean()))
      return false; 
    if (this.antiSound.getValBoolean() && packet instanceof SPacketSoundEffect) {
      SPacketSoundEffect sPacketSoundEffect = (SPacketSoundEffect)packet;
      if (this.sounds.contains(sPacketSoundEffect.getSound()))
        return false; 
      this.sounds.add(sPacketSoundEffect.getSound());
    } 
    if (side == Connection.Side.IN)
      return (!(packet instanceof net.minecraft.network.play.server.SPacketChunkData) || !this.NoChunk.getValBoolean()); 
    return true;
  }
}
