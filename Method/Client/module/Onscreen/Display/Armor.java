package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Armor extends Module {
  static Setting Rotation;
  
  static Setting xpos;
  
  static Setting ypos;
  
  static Setting Background;
  
  static Setting Numbervals;
  
  static Setting Reverse;
  
  static Setting Shadow;
  
  static Setting Frame;
  
  static Setting Color;
  
  static Setting FontSize;
  
  public Armor() {
    super("Armor", 0, Category.ONSCREEN, "Armor");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(Color = new Setting("BGColor", this, 0.01D, 0.0D, 0.3D, 0.22D));
    Main.setmgr.add(Rotation = new Setting("Rotation", this, false));
    Main.setmgr.add(Numbervals = new Setting("Numbervals", this, false));
    Main.setmgr.add(Reverse = new Setting("Reverse", this, false));
    Main.setmgr.add(Background = new Setting("Background", this, false));
    Main.setmgr.add(Shadow = new Setting("Shadow", this, true));
    Main.setmgr.add(Frame = new Setting("Font", this, "Times", fontoptions()));
    Main.setmgr.add(FontSize = new Setting("FontSize", this, 22.0D, 10.0D, 40.0D, true));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth / 2 + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 10.0D, -20.0D, (mc.displayHeight / 2 + 40), true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("ArmorSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("ArmorSET", false);
  }
  
  public static class ArmorRUN extends PinableFrame {
    public ArmorRUN() {
      super("ArmorSET", new String[0], (int)Armor.ypos.getValDouble(), (int)Armor.xpos.getValDouble());
    }
    
    public void setup() {
      GetSetup(this, Armor.xpos, Armor.ypos, Armor.Frame, Armor.FontSize);
    }
    
    public void Ongui() {
      GetInit(this, Armor.xpos, Armor.ypos, Armor.Frame, Armor.FontSize);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      int space = 0;
      if (!Armor.Reverse.getValBoolean())
        for (int i = 0; i <= 3; i++)
          space = Armordisplay(space, i);  
      if (Armor.Reverse.getValBoolean())
        for (int i = 3; i >= 0; i--)
          space = Armordisplay(space, i);  
      super.onRenderGameOverlay(event);
    }
    
    private int Armordisplay(int space, int i) {
      ItemStack stack = this.mc.player.inventoryContainer.getSlot(8 - i).getStack();
      if (stack != ItemStack.EMPTY) {
        RenderHelper.enableGUIStandardItemLighting();
        if (Armor.Rotation.getValBoolean()) {
          if (Armor.Background.getValBoolean())
            Gui.drawRect(this.x, this.y + 10, this.x + 72, this.y + 25, Armor.Color.getcolor()); 
          this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, getX() + space, getY() + 10);
          this.mc.getRenderItem().renderItemOverlays(this.mc.fontRenderer, stack, getX() + space, getY() + 10);
          if (Armor.Numbervals.getValBoolean())
            fontSelect(Armor.Frame, String.valueOf(stack.getMaxDamage() - stack.getItemDamage()), (getX() + space), getY(), (stack.getMaxDamage() - stack.getItemDamage() < 30) ? -65536 : -1, Armor.Shadow.getValBoolean()); 
        } else {
          if (Armor.Background.getValBoolean())
            Gui.drawRect(this.x, this.y + 10, this.x + 15, this.y + 80, Armor.Color.getcolor()); 
          this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, getX(), getY() + space + 10);
          this.mc.getRenderItem().renderItemOverlays(this.mc.fontRenderer, stack, getX(), getY() + space + 10);
          if (Armor.Numbervals.getValBoolean())
            fontSelect(Armor.Frame, String.valueOf(stack.getItemDamage()), (getX() + 10), (getY() + 10 + space), -1, Armor.Shadow.getValBoolean()); 
        } 
        RenderHelper.disableStandardItemLighting();
        space += 18;
      } 
      return space;
    }
  }
}
