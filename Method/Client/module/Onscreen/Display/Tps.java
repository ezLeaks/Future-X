package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.utils.system.Connection;
import java.util.Arrays;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Tps extends Module {
  static Setting TextColor;
  
  static Setting BgColor;
  
  static Setting background;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Shadow;
  
  static Setting Frame;
  
  static Setting FontSize;
  
  public Tps() {
    super("Tps", 0, Category.ONSCREEN, "Tps");
    this.nextIndex = 0;
  }
  
  private static final float[] tickRates = new float[20];
  
  private int nextIndex;
  
  private long timeLastTimeUpdate;
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
    this.nextIndex = 0;
    this.timeLastTimeUpdate = -1L;
    Arrays.fill(tickRates, 0.0F);
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(background = new Setting("background", this, false));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 210.0D, -20.0D, (mc.displayHeight + 40), true));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("TpsSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("TpsSET", false);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof net.minecraft.network.play.server.SPacketTimeUpdate)
      onTimeUpdate(); 
    return true;
  }
  
  private void onTimeUpdate() {
    if (this.timeLastTimeUpdate != -1L) {
      float timeElapsed = (float)(System.currentTimeMillis() - this.timeLastTimeUpdate) / 1000.0F;
      tickRates[this.nextIndex % tickRates.length] = MathHelper.clamp(20.0F / timeElapsed, 0.0F, 20.0F);
      this.nextIndex++;
    } 
    this.timeLastTimeUpdate = System.currentTimeMillis();
  }
  
  public static float getTickRate() {
    float numTicks = 0.0F;
    float sumTickRates = 0.0F;
    for (float tickRate : tickRates) {
      if (tickRate > 0.0F) {
        sumTickRates += tickRate;
        numTicks++;
      } 
    } 
    return MathHelper.clamp(sumTickRates / numTicks, 0.0F, 20.0F);
  }
  
  public static class TpsRUN extends PinableFrame {
    public TpsRUN() {
      super("TpsSET", new String[0], (int)Tps.ypos.getValDouble(), (int)Tps.xpos.getValDouble());
    }
    
    public void setup() {
      GetSetup(this, Tps.xpos, Tps.ypos, Tps.Frame, Tps.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, Tps.xpos, Tps.ypos, Tps.Frame, Tps.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      String tickrate = String.format("TPS: %.2f", new Object[] { Float.valueOf(Tps.getTickRate()) });
      fontSelect(Tps.Frame, tickrate, getX(), (getY() + 10), Tps.TextColor.getcolor(), Tps.Shadow.getValBoolean());
      if (Tps.background.getValBoolean())
        Gui.drawRect(this.x, this.y + 10, this.x + widthcal(Tps.Frame, tickrate), this.y + 20, Tps.BgColor.getcolor()); 
      super.onRenderGameOverlay(event);
    }
  }
}
