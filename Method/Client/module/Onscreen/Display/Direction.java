package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Direction extends Module {
  static Setting TextColor;
  
  static Setting BgColor;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Frame;
  
  static Setting Background;
  
  static Setting Shadow;
  
  static Setting FontSize;
  
  public Direction() {
    super("Direction", 0, Category.ONSCREEN, "Direction");
  }
  
  public void setup() {
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(Background = new Setting("Background", this, false));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 40.0D, -20.0D, (mc.displayHeight + 40), true));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("DirectionSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("DirectionSET", false);
  }
  
  public static class DirectionRUN extends PinableFrame {
    public DirectionRUN() {
      super("DirectionSET", new String[0], (int)Direction.ypos.getValDouble(), (int)Direction.xpos.getValDouble());
    }
    
    public void setup() {
      GetSetup(this, Direction.xpos, Direction.ypos, Direction.Frame, Direction.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, Direction.xpos, Direction.ypos, Direction.Frame, Direction.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      String direction = String.format("%s " + ChatFormatting.GRAY + "%s", new Object[] { getFacing(), getTowards() });
      if (Direction.Background.getValBoolean())
        Gui.drawRect(this.x, this.y + 10, this.x + widthcal(Direction.Frame, direction), this.y + 20, Direction.BgColor.getcolor()); 
      fontSelect(Direction.Frame, direction, getX(), (getY() + 10), Direction.TextColor.getcolor(), Direction.Shadow.getValBoolean());
      super.onRenderGameOverlay(event);
    }
    
    public String getFacing() {
      switch (MathHelper.floor((this.mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 0x7) {
        case 0:
          return "South";
        case 1:
          return "South West";
        case 2:
          return "West";
        case 3:
          return "North West";
        case 4:
          return "North";
        case 5:
          return "North East";
        case 6:
          return "East";
        case 7:
          return "South East";
      } 
      return "Invalid";
    }
    
    private String getTowards() {
      switch (MathHelper.floor((this.mc.player.rotationYaw * 8.0F / 360.0F) + 0.5D) & 0x7) {
        case 0:
          return "+Z";
        case 1:
          return "-X +Z";
        case 2:
          return "-X";
        case 3:
          return "-X -Z";
        case 4:
          return "-Z";
        case 5:
          return "+X -Z";
        case 6:
          return "+X";
        case 7:
          return "+X +Z";
      } 
      return "Invalid";
    }
  }
}
