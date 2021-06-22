package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class BossStack extends Module {
  Setting mode = Main.setmgr.add(new Setting("Stack mode", this, "Stack", new String[] { "Stack", "Minimize" }));
  
  Setting scale = Main.setmgr.add(new Setting("Scale", this, 0.5D, 0.5D, 4.0D, false));
  
  private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
  
  public BossStack() {
    super("BossStack", 0, Category.RENDER, "BossStack");
  }
  
  public void RenderGameOverLayPost(RenderGameOverlayEvent.Post event) {
    if (event.getType() != RenderGameOverlayEvent.ElementType.BOSSINFO)
      return; 
    if (this.mode.getValString().equalsIgnoreCase("Minimize")) {
      Map<UUID, BossInfoClient> map = (mc.ingameGUI.getBossOverlay()).mapBossInfos;
      if (map == null)
        return; 
      ScaledResolution scaledresolution = new ScaledResolution(mc);
      int i = scaledresolution.getScaledWidth();
      int j = 12;
      for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
        BossInfoClient info = entry.getValue();
        String text = info.getName().getFormattedText();
        j = Collapse(event, i, j, info, text);
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("Stack")) {
      Map<UUID, BossInfoClient> map = (mc.ingameGUI.getBossOverlay()).mapBossInfos;
      HashMap<String, Pair<BossInfoClient, Integer>> to = new HashMap<>();
      for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {
        Pair<BossInfoClient, Integer> p;
        String s = ((BossInfoClient)entry.getValue()).getName().getFormattedText();
        if (to.containsKey(s)) {
          p = to.get(s);
          p = new Pair<>(p.getKey(), Integer.valueOf(((Integer)p.getValue()).intValue() + 1));
        } else {
          p = new Pair<>(entry.getValue(), Integer.valueOf(1));
        } 
        to.put(s, p);
      } 
      ScaledResolution scaledresolution = new ScaledResolution(mc);
      int i = scaledresolution.getScaledWidth();
      int j = 12;
      for (Map.Entry<String, Pair<BossInfoClient, Integer>> entry : to.entrySet()) {
        String text = entry.getKey();
        BossInfoClient info = (BossInfoClient)((Pair)entry.getValue()).getKey();
        int a = ((Integer)((Pair)entry.getValue()).getValue()).intValue();
        text = text + " x" + a;
        j = Collapse(event, i, j, info, text);
      } 
    } 
  }
  
  public void onRenderPre(RenderGameOverlayEvent.Pre event) {
    if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO)
      event.setCanceled(true); 
  }
  
  private int Collapse(RenderGameOverlayEvent.Post event, int i, int j, BossInfoClient info, String text) {
    int k = (int)(i / this.scale.getValDouble() / 2.0D - 91.0D);
    GlStateManager.scale(this.scale.getValDouble(), this.scale.getValDouble(), 1.0D);
    if (!event.isCanceled()) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      mc.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
      mc.ingameGUI.getBossOverlay().render(k, j, (BossInfo)info);
      mc.fontRenderer.drawStringWithShadow(text, (float)(i / this.scale.getValDouble() / 2.0D - (mc.fontRenderer.getStringWidth(text) / 2)), (j - 9), 16777215);
    } 
    GlStateManager.scale(1.0D / this.scale.getValDouble(), 1.0D / this.scale.getValDouble(), 1.0D);
    j += 10 + mc.fontRenderer.FONT_HEIGHT;
    return j;
  }
}
