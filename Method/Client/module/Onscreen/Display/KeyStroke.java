package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class KeyStroke extends Module {
  static Setting TextColor;
  
  static Setting Presscolor;
  
  static Setting BgColor;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Frame;
  
  static Setting Background;
  
  static Setting Shadow;
  
  static Setting Clicks;
  
  static Setting ClicksPerSec;
  
  static Setting FontSize;
  
  public KeyStroke() {
    super("KeyStroke", 0, Category.ONSCREEN, "KeyStroke");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.3D, 0.4D, 0.4D, 1.0D));
    Main.setmgr.add(Presscolor = new Setting("Presscolor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.5D, 0.3D, 0.22D));
    Main.setmgr.add(Background = new Setting("Background", this, false));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(Clicks = new Setting("Clicks", this, false));
    Main.setmgr.add(ClicksPerSec = new Setting("ClicksPerSec", this, false));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth / 2 + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 220.0D, -20.0D, (mc.displayHeight / 2 + 250), true));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("KeyStrokeSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("KeyStrokeSET", false);
  }
  
  public static class KeyStrokeRUN extends PinableFrame {
    ArrayList<Double> clicks;
    
    boolean startclick;
    
    public KeyStrokeRUN() {
      super("KeyStrokeSET", new String[0], (int)KeyStroke.ypos.getValDouble(), (int)KeyStroke.xpos.getValDouble());
      this.clicks = new ArrayList<>();
      this.startclick = false;
    }
    
    public void setup() {
      GetSetup(this, KeyStroke.xpos, KeyStroke.ypos, KeyStroke.Frame, KeyStroke.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, KeyStroke.xpos, KeyStroke.ypos, KeyStroke.Frame, KeyStroke.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      if (this.mc.gameSettings.keyBindAttack.pressed) {
        this.startclick = true;
      } else if (this.startclick) {
        this.startclick = false;
        this.clicks.add(Double.valueOf(System.currentTimeMillis()));
      } 
      ArrayList<Double> rem = new ArrayList<>();
      for (Double click : this.clicks) {
        if (click.doubleValue() + 1000.0D < System.currentTimeMillis())
          rem.add(click); 
      } 
      this.clicks.removeAll(rem);
      int black = (new Color(0, 0, 0, 40)).getRGB();
      int rain = KeyStroke.TextColor.getcolor();
      int white = KeyStroke.Presscolor.getcolor();
      fontSelect(KeyStroke.Frame, this.mc.gameSettings.keyBindForward.getDisplayName(), (this.x + 18), this.y, this.mc.gameSettings.keyBindForward.pressed ? white : rain, KeyStroke.Shadow.getValBoolean());
      fontSelect(KeyStroke.Frame, this.mc.gameSettings.keyBindLeft.getDisplayName(), this.x, (this.y + 20), this.mc.gameSettings.keyBindLeft.pressed ? white : rain, KeyStroke.Shadow.getValBoolean());
      fontSelect(KeyStroke.Frame, this.mc.gameSettings.keyBindBack.getDisplayName(), (this.x + 20), (this.y + 20), this.mc.gameSettings.keyBindBack.pressed ? white : rain, KeyStroke.Shadow.getValBoolean());
      fontSelect(KeyStroke.Frame, this.mc.gameSettings.keyBindRight.getDisplayName(), (this.x + 40), (this.y + 20), this.mc.gameSettings.keyBindRight.pressed ? white : rain, KeyStroke.Shadow.getValBoolean());
      if (KeyStroke.Clicks.getValBoolean()) {
        if (KeyStroke.Background.getValBoolean()) {
          Gui.drawRect(this.x, this.y + 40, this.x + 20, this.y + 20, this.mc.gameSettings.keyBindAttack.pressed ? rain : black);
          Gui.drawRect(this.x + 20, this.y + 40, this.x + 40, this.y + 20, this.mc.gameSettings.keyBindUseItem.pressed ? rain : black);
        } 
        fontSelect(KeyStroke.Frame, "LMB", this.x, (this.y + 40), this.mc.gameSettings.keyBindAttack.pressed ? white : rain, KeyStroke.Shadow.getValBoolean());
        fontSelect(KeyStroke.Frame, "RMB", (this.x + 30), (this.y + 40), this.mc.gameSettings.keyBindUseItem.pressed ? white : rain, KeyStroke.Shadow.getValBoolean());
      } 
      if (KeyStroke.ClicksPerSec.getValBoolean())
        fontSelect(KeyStroke.Frame, "Clicks: " + this.clicks.size(), this.x, (this.y + 60), this.mc.gameSettings.keyBindAttack.pressed ? white : rain, KeyStroke.Shadow.getValBoolean()); 
      if (KeyStroke.Background.getValBoolean()) {
        Gui.drawRect(this.x + 15, this.y, this.x + 25, this.y + 20, this.mc.gameSettings.keyBindForward.pressed ? rain : black);
        Gui.drawRect(this.x, this.y + 20, this.x + 10, this.y + 40, this.mc.gameSettings.keyBindLeft.pressed ? rain : black);
        Gui.drawRect(this.x + 20, this.y + 20, this.x + 30, this.y + 40, this.mc.gameSettings.keyBindBack.pressed ? rain : black);
        Gui.drawRect(this.x + 40, this.y + 20, this.x + 50, this.y + 40, this.mc.gameSettings.keyBindRight.pressed ? rain : black);
      } 
      super.onRenderGameOverlay(event);
    }
  }
}
