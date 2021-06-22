package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Potions extends Module {
  static Setting TextColor;
  
  static Setting BgColor;
  
  static Setting Shadow;
  
  static Setting name;
  
  static Setting amplifer;
  
  static Setting duration;
  
  static Setting Frame;
  
  static Setting background;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting FontSize;
  
  public Potions() {
    super("Potions", 0, Category.ONSCREEN, "Potions");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(TextColor = new Setting("TextColor", this, 0.0D, 1.0D, 1.0D, 1.0D));
    Main.setmgr.add(BgColor = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(background = new Setting("background", this, false));
    Main.setmgr.add(name = new Setting("name", this, true));
    Main.setmgr.add(amplifer = new Setting("amplifer", this, false));
    Main.setmgr.add(duration = new Setting("duration", this, false));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 160.0D, -20.0D, (mc.displayHeight + 40), true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("PotionsSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("PotionsSET", false);
  }
  
  public static class PotionsRUN extends PinableFrame {
    public PotionsRUN() {
      super("PotionsSET", new String[0], (int)Potions.ypos.getValDouble(), (int)Potions.xpos.getValDouble());
    }
    
    public void setup() {
      GetSetup(this, Potions.xpos, Potions.ypos, Potions.Frame, Potions.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, Potions.xpos, Potions.ypos, Potions.Frame, Potions.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      int offset = 0;
      for (PotionEffect activePotionEffect : this.mc.player.getActivePotionEffects()) {
        String effect = Potions.name.getValBoolean() ? (activePotionEffect.getEffectName().substring(7) + " ") : "";
        String amp = Potions.amplifer.getValBoolean() ? ("x" + activePotionEffect.getAmplifier() + " ") : "";
        String dur = Potions.duration.getValBoolean() ? ((activePotionEffect.getDuration() / 20) + " ") : "";
        String all = effect + "" + amp + "" + dur;
        fontSelect(Potions.Frame, all, getX(), (getY() + 10 - offset), Potions.TextColor.getcolor(), Potions.Shadow.getValBoolean());
        if (Potions.background.getValBoolean())
          Gui.drawRect(this.x, this.y + 10 - offset, this.x + widthcal(Potions.Frame, effect + amp + dur), this.y + 20 - offset, Potions.BgColor.getcolor()); 
        offset += 10;
      } 
      super.onRenderGameOverlay(event);
    }
  }
}
