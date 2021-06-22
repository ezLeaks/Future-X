package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Fps extends Module {
  static Setting TextColor;
  
  static Setting BgColor;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Shadow;
  
  static Setting Frame;
  
  static Setting Background;
  
  static Setting FontSize;
  
  public Fps() {
    super("Fps", 0, Category.ONSCREEN, "Fps");
  }
  
  public void setup() {
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(Background = new Setting("Background", this, false));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth / 2 + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 70.0D, -20.0D, (mc.displayHeight / 2 + 40), true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("FpsSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("FpsSET", false);
  }
  
  public static class FpsRUN extends PinableFrame {
    public FpsRUN() {
      super("FpsSET", new String[0], (int)Fps.ypos.getValDouble(), (int)Fps.xpos.getValDouble());
    }
    
    public void setup() {
      GetSetup(this, Fps.xpos, Fps.ypos, Fps.Frame, Fps.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, Fps.xpos, Fps.ypos, Fps.Frame, Fps.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      String framerate = "FPS: " + Minecraft.getDebugFPS();
      if (Fps.Background.getValBoolean())
        Gui.drawRect(this.x, this.y + 10, this.x + widthcal(Fps.Frame, framerate), this.y + 20, Fps.BgColor.getcolor()); 
      fontSelect(Fps.Frame, framerate, getX(), (getY() + 10), Fps.TextColor.getcolor(), Fps.Shadow.getValBoolean());
      super.onRenderGameOverlay(event);
    }
  }
}
