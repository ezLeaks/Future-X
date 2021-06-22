package Method.Client.utils.Factory;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public class MethodConfigGui extends GuiConfig {
  public MethodConfigGui(GuiScreen parent) {
    super(parent, getConfigElements(), "futurex", false, false, GuiConfig.getAbridgedConfigPath(MethodConfig.getString()));
  }
  
  private static List<IConfigElement> getConfigElements() {
    return new ArrayList<>(MethodConfig.getConfigElements());
  }
  
  public void initGui() {
    if (this.entryList == null || this.needsRefresh) {
      this.entryList = new GuiConfigEntries(this, this.mc) {
          protected void drawContainerBackground(@Nonnull Tessellator tessellator) {
            if (this.mc.world == null)
              super.drawContainerBackground(tessellator); 
          }
        };
      this.needsRefresh = false;
    } 
    super.initGui();
  }
  
  public void drawDefaultBackground() {
    drawWorldBackground(0);
  }
}
