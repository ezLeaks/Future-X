package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.GetAmbientOcclusionLightValueEvent;
import Method.Client.utils.Patcher.Events.RenderBlockModelEvent;
import Method.Client.utils.Patcher.Events.RenderTileEntityEvent;
import Method.Client.utils.Patcher.Events.SetOpaqueCubeEvent;
import Method.Client.utils.Patcher.Events.ShouldSideBeRenderedEvent;
import Method.Client.utils.Screens.Custom.Xray.XrayGuiSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Xray extends Module {
  public static ArrayList<String> blockNames;
  
  Setting Gui = Main.setmgr.add(new Setting("Gui", this, (GuiScreen)Main.Xray));
  
  public Xray() {
    super("Xray", 0, Category.RENDER, "Xray");
    new XrayGuiSettings(new Block[] { 
          Blocks.COAL_ORE, Blocks.COAL_BLOCK, Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.GOLD_ORE, Blocks.GOLD_BLOCK, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE, 
          Blocks.REDSTONE_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, Blocks.QUARTZ_ORE, (Block)Blocks.LAVA, Blocks.MOB_SPAWNER, (Block)Blocks.PORTAL, Blocks.END_PORTAL, 
          Blocks.END_PORTAL_FRAME });
  }
  
  public void onEnable() {
    blockNames = new ArrayList<>(XrayGuiSettings.getBlockNames());
    MinecraftForge.EVENT_BUS.register(this);
    mc.renderGlobal.loadRenderers();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    mc.gameSettings.gammaSetting = 16.0F;
  }
  
  public void SetOpaqueCubeEvent(SetOpaqueCubeEvent event) {
    event.setCanceled(true);
  }
  
  public void onGetAmbientOcclusionLightValue(GetAmbientOcclusionLightValueEvent event) {
    event.setLightValue(1.0F);
  }
  
  public void onShouldSideBeRendered(ShouldSideBeRenderedEvent event) {
    event.setRendered(isVisible(event.getState().getBlock()));
  }
  
  public void onRenderBlockModel(RenderBlockModelEvent event) {
    if (!isVisible(event.getState().getBlock()))
      event.setCanceled(true); 
  }
  
  public void onRenderTileEntity(RenderTileEntityEvent event) {
    if (!isVisible(event.getTileEntity().getBlockType()))
      event.setCanceled(true); 
  }
  
  public void onDisable() {
    MinecraftForge.EVENT_BUS.unregister(this);
    mc.renderGlobal.loadRenderers();
  }
  
  private boolean isVisible(Block block) {
    String name = getName(block);
    int index = Collections.binarySearch((List)blockNames, name);
    return (index >= 0);
  }
  
  public static String getName(Block block) {
    return "" + Block.REGISTRY.getNameForObject(block);
  }
}
