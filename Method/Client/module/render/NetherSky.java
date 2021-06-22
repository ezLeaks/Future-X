package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NetherSky extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Glint", new String[] { "Glint", "Method" }));
  
  Setting OverlayColor = Main.setmgr.add(new Setting("OverlayColor", this, 0.0D, 1.0D, 1.0D, 0.62D));
  
  private static ISpaceRenderer skyboxSpaceRenderer;
  
  private boolean wasChanged;
  
  public void setup() {
    skyboxSpaceRenderer = new SkyboxSpaceRenderer();
  }
  
  public NetherSky() {
    super("NetherSky", 0, Category.RENDER, "NetherSky");
  }
  
  public void onEnable() {
    this.wasChanged = false;
  }
  
  public void onDisable() {
    disableBackgroundRenderer(mc.player.world);
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (!this.wasChanged) {
      enableBackgroundRenderer(mc.player.world);
      this.wasChanged = true;
    } 
  }
  
  public void onWorldLoad(WorldEvent.Load event) {
    this.wasChanged = false;
  }
  
  public void onWorldUnload(WorldEvent.Unload event) {
    this.wasChanged = false;
  }
  
  private void enableBackgroundRenderer(World world) {
    if (world.provider.getDimensionType() == DimensionType.NETHER)
      world.provider.setSkyRenderer(new IRenderHandler() {
            public void render(float partialTicks, WorldClient world, Minecraft mc) {
              NetherSky.skyboxSpaceRenderer.render(NetherSky.this.mode);
            }
          }); 
  }
  
  private void disableBackgroundRenderer(World world) {
    if (world.provider.getDimensionType() == DimensionType.NETHER)
      world.provider.setSkyRenderer(new IRenderHandler() {
            public void render(float partialTicks, WorldClient world, Minecraft mc) {}
          }); 
  }
  
  public class SkyboxSpaceRenderer implements ISpaceRenderer {
    public void render(Setting mode) {
      GlStateManager.disableFog();
      GlStateManager.disableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.depthMask(false);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      for (int i = 0; i < 6; i++) {
        if (mode.getValString().equalsIgnoreCase("Glint"))
          NetherSky.mc.getTextureManager().bindTexture(new ResourceLocation("futurex", "N.png")); 
        if (mode.getValString().equalsIgnoreCase("Method"))
          NetherSky.mc.getTextureManager().bindTexture(new ResourceLocation("futurex", "method.png")); 
        GlStateManager.pushMatrix();
        if (i == 1)
          GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F); 
        if (i == 2) {
          GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
          GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        } 
        if (i == 3)
          GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F); 
        if (i == 4) {
          GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
          GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        } 
        if (i == 5) {
          GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
          GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } 
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        double size = 100.0D;
        float a = ColorUtils.colorcalc(NetherSky.this.OverlayColor.getcolor(), 24);
        float r = ColorUtils.colorcalc(NetherSky.this.OverlayColor.getcolor(), 16);
        float g = ColorUtils.colorcalc(NetherSky.this.OverlayColor.getcolor(), 8);
        float b = ColorUtils.colorcalc(NetherSky.this.OverlayColor.getcolor(), 0);
        bufferbuilder.pos(-size, -size, -size).tex(0.0D, 0.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(-size, -size, size).tex(0.0D, 1.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size, -size, size).tex(1.0D, 1.0D).color(r, g, b, a).endVertex();
        bufferbuilder.pos(size, -size, -size).tex(1.0D, 0.0D).color(r, g, b, a).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
      } 
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.enableAlpha();
      GlStateManager.enableAlpha();
    }
  }
  
  public static interface ISpaceRenderer {
    void render(Setting param1Setting);
  }
}
