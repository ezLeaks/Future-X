package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Server extends Module {
  static Setting TextColor;
  
  static Setting BgColor;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Frame;
  
  static Setting Shadow;
  
  static Setting background;
  
  static Setting FontSize;
  
  public Server() {
    super("Server", 0, Category.ONSCREEN, "Server");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(background = new Setting("background", this, false));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 170.0D, -20.0D, (mc.displayHeight + 40), true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("ServerSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("ServerSET", false);
  }
  
  public static class ServerRUN extends PinableFrame {
    public ServerRUN() {
      super("ServerSET", new String[0], (int)Server.ypos.getValDouble(), (int)Server.xpos.getValDouble());
    }
    
    public void setup() {
      GetSetup(this, Server.xpos, Server.ypos, Server.Frame, Server.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, Server.xpos, Server.ypos, Server.Frame, Server.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      String brand = (this.mc.getCurrentServerData() == null) ? "Vanilla" : (this.mc.getCurrentServerData()).gameVersion;
      fontSelect(Server.Frame, brand, getX(), (getY() + 10), Server.TextColor.getcolor(), Server.Shadow.getValBoolean());
      if (Server.background.getValBoolean())
        Gui.drawRect(this.x, this.y + 10, this.x + widthcal(Server.Frame, brand), this.y + 20, Server.BgColor.getcolor()); 
      super.onRenderGameOverlay(event);
    }
  }
}
