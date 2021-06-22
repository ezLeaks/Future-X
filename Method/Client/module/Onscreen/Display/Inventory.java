package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Inventory extends Module {
  static Setting BgColor;
  
  static Setting background;
  
  static Setting Hotbar;
  
  static Setting Xcarry;
  
  static Setting xpos;
  
  static Setting ypos;
  
  public Inventory() {
    super("Inventory", 0, Category.ONSCREEN, "Inventory");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(BgColor = new Setting("BgColor", this, 0.22D, 0.88D, 0.22D, 0.22D));
    Main.setmgr.add(background = new Setting("background", this, false));
    Main.setmgr.add(Hotbar = new Setting("Hotbar", this, false));
    Main.setmgr.add(Xcarry = new Setting("Xcarry", this, false));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 110.0D, -20.0D, (mc.displayHeight + 40), true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("InventorySET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("InventorySET", false);
  }
  
  public static class InventoryRUN extends PinableFrame {
    public InventoryRUN() {
      super("InventorySET", new String[0], (int)Inventory.ypos.getValDouble(), (int)Inventory.xpos.getValDouble());
    }
    
    public void setup() {
      this.x = (int)Inventory.xpos.getValDouble();
      this.y = (int)Inventory.ypos.getValDouble();
    }
    
    public void Ongui() {
      if (!getDrag().booleanValue()) {
        this.x = (int)Inventory.xpos.getValDouble();
        this.y = (int)Inventory.ypos.getValDouble();
      } else {
        Inventory.xpos.setValDouble(this.x);
        Inventory.ypos.setValDouble(this.y);
      } 
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      if (this.mc.player == null)
        return; 
      RenderHelper.enableGUIStandardItemLighting();
      if (Inventory.background.getValBoolean())
        RenderUtils.drawRectDouble(getX(), getY(), (getX() + getWidth() + 60), (getY() + 50 + (Inventory.Hotbar.getValBoolean() ? 25 : 0)), Inventory.BgColor.getcolor()); 
      int i;
      for (i = 0; i < 27; i++) {
        ItemStack itemStack = (ItemStack)this.mc.player.inventory.mainInventory.get(i + 9);
        int offsetX = getX() + i % 9 * 16;
        int offsetY = getY() + i / 9 * 16;
        this.mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
        this.mc.getRenderItem().renderItemOverlayIntoGUI(this.mc.fontRenderer, itemStack, offsetX, offsetY, null);
      } 
      if (Inventory.Hotbar.getValBoolean())
        for (i = 0; i < 9; i++) {
          ItemStack itemStack = (ItemStack)this.mc.player.inventory.mainInventory.get(i);
          int offsetX = getX() + i % 9 * 16;
          int offsetY = getY() + 48;
          this.mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
          this.mc.getRenderItem().renderItemOverlayIntoGUI(this.mc.fontRenderer, itemStack, offsetX, offsetY, null);
        }  
      if (Inventory.Xcarry.getValBoolean())
        for (i = 0; i < 5; i++) {
          ItemStack itemStack = (ItemStack)this.mc.player.inventoryContainer.getInventory().get(i);
          int offsetX = getX() + i * 16;
          int offsetY = getY() + 60;
          this.mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
          this.mc.getRenderItem().renderItemOverlayIntoGUI(this.mc.fontRenderer, itemStack, offsetX, offsetY, null);
        }  
      RenderHelper.disableStandardItemLighting();
      (this.mc.getRenderItem()).zLevel = 0.0F;
      super.onRenderGameOverlay(event);
    }
  }
}
