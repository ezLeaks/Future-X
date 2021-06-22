package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.utils.system.Connection;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class ServerResponce extends Module {
  static Setting Delay;
  
  static Setting TextColor;
  
  static Setting BgColor;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Shadow;
  
  static Setting Frame;
  
  static Setting FontSize;
  
  static Setting Background;
  
  public ServerResponce() {
    super("ServerResponce", 0, Category.ONSCREEN, "ServerResponce");
  }
  
  private static long serverLastUpdated = 0L;
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(Delay = new Setting("Delay", this, 1.0D, 1.0D, 10.0D, true));
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(Background = new Setting("Background", this, false));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth / 2 + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 180.0D, -20.0D, (mc.displayHeight / 2 + 40), true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("ServerResponceSET", true);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.IN)
      serverLastUpdated = System.currentTimeMillis(); 
    return true;
  }
  
  public void onDisable() {
    PinableFrame.Toggle("ServerResponceSET", false);
  }
  
  public static class ServerResponceRUN extends PinableFrame {
    String text;
    
    public ServerResponceRUN() {
      super("ServerResponceSET", new String[0], (int)ServerResponce.ypos.getValDouble(), (int)ServerResponce.xpos.getValDouble());
      this.text = "Server Not Responding! ";
    }
    
    public void setup() {
      GetSetup(this, ServerResponce.xpos, ServerResponce.ypos, ServerResponce.Frame, ServerResponce.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, ServerResponce.xpos, ServerResponce.ypos, ServerResponce.Frame, ServerResponce.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      if (this.mc.isSingleplayer())
        return; 
      if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof net.minecraft.client.gui.GuiChat))
        return; 
      if (ServerResponce.Delay.getValDouble() * 1000.0D > (System.currentTimeMillis() - ServerResponce.serverLastUpdated))
        return; 
      if (shouldPing())
        if (isDown("1.1.1.1", 80, 1111)) {
          this.text = "Your internet is offline! ";
        } else {
          this.text = "Server Not Responding! ";
        }  
      this.text = this.text.replaceAll("! .*", "! " + timeDifference() + "s");
      if (ServerResponce.Background.getValBoolean())
        Gui.drawRect(this.x, this.y + 10, this.x + widthcal(ServerResponce.Frame, this.text), this.y + 20, ServerResponce.BgColor.getcolor()); 
      fontSelect(ServerResponce.Frame, this.text, getX(), (getY() + 10), ServerResponce.TextColor.getcolor(), ServerResponce.Shadow.getValBoolean());
      super.onRenderGameOverlay(event);
    }
    
    private double timeDifference() {
      return (System.currentTimeMillis() - ServerResponce.serverLastUpdated) / 1000.0D;
    }
    
    public static boolean isDown(String host, int port, int timeout) {
      try (Socket socket = new Socket()) {
        socket.connect(new InetSocketAddress(host, port), timeout);
        return false;
      } catch (IOException e) {
        return true;
      } 
    }
    
    private static long startTime = 0L;
    
    private boolean shouldPing() {
      if (startTime == 0L)
        startTime = System.currentTimeMillis(); 
      if (startTime + 1000L <= System.currentTimeMillis()) {
        startTime = System.currentTimeMillis();
        return true;
      } 
      return false;
    }
  }
}
