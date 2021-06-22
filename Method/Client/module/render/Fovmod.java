package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Fovmod extends Module {
  public float defaultFov;
  
  Setting Change;
  
  Setting Smooth;
  
  Setting FovMode;
  
  public Fovmod() {
    super("Fovmod", 0, Category.RENDER, "Fovmod");
    this.Change = Main.setmgr.add(new Setting("Change", this, 100.0D, 0.0D, 500.0D, true));
    this.Smooth = Main.setmgr.add(new Setting("Smooth", this, true));
    this.FovMode = Main.setmgr.add(new Setting("FovMode", this, "ViewModelChanger", new String[] { "ViewModelChanger", "FovChanger", "Zoom" }));
  }
  
  public void FOVModifier(EntityViewRenderEvent.FOVModifier event) {
    if (this.FovMode.getValString().equalsIgnoreCase("ViewModelChanger"))
      event.setFOV((float)this.Change.getValDouble()); 
  }
  
  public void onEnable() {
    this.defaultFov = mc.gameSettings.fovSetting;
  }
  
  public void onDisable() {
    mc.gameSettings.fovSetting = this.defaultFov;
    mc.gameSettings.smoothCamera = false;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    mc.gameSettings.smoothCamera = this.Smooth.getValBoolean();
    if (this.FovMode.getValString().equalsIgnoreCase("FovChanger"))
      mc.gameSettings.fovSetting = (float)this.Change.getValDouble(); 
    if (this.FovMode.getValString().equalsIgnoreCase("Zoom"))
      if (mc.gameSettings.fovSetting > 12.0F) {
        for (int i = 0; i < this.Change.getValDouble(); i++) {
          if (mc.gameSettings.fovSetting > 12.0F)
            mc.gameSettings.fovSetting -= 0.1F; 
        } 
      } else if (mc.gameSettings.fovSetting < this.defaultFov) {
        for (int i = 0; i < this.Change.getValDouble(); i++)
          mc.gameSettings.fovSetting += 0.1F; 
      }  
  }
}
