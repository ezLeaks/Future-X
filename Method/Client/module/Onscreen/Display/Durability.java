package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Durability extends Module {
  static Setting TextColor;
  
  static Setting BgColor;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Frame;
  
  static Setting Background;
  
  static Setting Shadow;
  
  static Setting FontSize;
  
  public Durability() {
    super("Durability", 0, Category.ONSCREEN, "Durability");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(Background = new Setting("Background", this, false));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 20.0D, -20.0D, (mc.displayHeight + 40), true));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("DurabilitySET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("DurabilitySET", false);
  }
  
  public static class DurabilityRUN extends PinableFrame {
    public DurabilityRUN() {
      super("DurabilitySET", new String[0], (int)Durability.ypos.getValDouble(), (int)Durability.xpos.getValDouble());
    }
    
    public void setup() {
      GetSetup(this, Durability.xpos, Durability.ypos, Durability.Frame, Durability.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, Durability.xpos, Durability.ypos, Durability.Frame, Durability.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      ItemStack stack = this.mc.player.getHeldItemMainhand();
      if (!stack.isEmpty() && (stack.getItem() instanceof net.minecraft.item.ItemTool || stack.getItem() instanceof net.minecraft.item.ItemArmor || stack.getItem() instanceof net.minecraft.item.ItemSword)) {
        String Durability = "Durability: " + (stack.getMaxDamage() - stack.getItemDamage());
        if (Durability.Background.getValBoolean())
          Gui.drawRect(this.x, this.y + 10, this.x + widthcal(Durability.Frame, Durability), this.y + 22, Durability.BgColor.getcolor()); 
        fontSelect(Durability.Frame, Durability, getX(), (getY() + 10), Durability.TextColor.getcolor(), Durability.Shadow.getValBoolean());
      } 
      super.onRenderGameOverlay(event);
    }
  }
}
