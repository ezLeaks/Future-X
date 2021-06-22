package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class CombatItems extends Module {
  static Setting BgColor;
  
  static Setting background;
  
  static Setting xpos;
  
  static Setting ypos;
  
  public CombatItems() {
    super("CombatItems", 0, Category.ONSCREEN, "CombatItems");
  }
  
  public void setup() {
    this.visible = false;
    Main.setmgr.add(BgColor = new Setting("BgColor", this, 0.22D, 0.88D, 0.22D, 0.22D));
    Main.setmgr.add(background = new Setting("background", this, false));
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 110.0D, -20.0D, (mc.displayHeight + 40), true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("CombatItemsSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("CombatItemsSET", false);
  }
  
  public static class CombatItemsRUN extends PinableFrame {
    ArrayList<ItemStack> itemStacks;
    
    ArrayList<Item> Itemslist;
    
    public CombatItemsRUN() {
      super("CombatItemsSET", new String[0], (int)CombatItems.ypos.getValDouble(), (int)CombatItems.xpos.getValDouble());
      this.itemStacks = new ArrayList<>();
      this.Itemslist = new ArrayList<>();
    }
    
    public void setup() {
      this.x = (int)CombatItems.xpos.getValDouble();
      this.y = (int)CombatItems.ypos.getValDouble();
    }
    
    public void Ongui() {
      if (!getDrag().booleanValue()) {
        this.x = (int)CombatItems.xpos.getValDouble();
        this.y = (int)CombatItems.ypos.getValDouble();
      } else {
        CombatItems.xpos.setValDouble(this.x);
        CombatItems.ypos.setValDouble(this.y);
      } 
    }
    
    public void setupitems() {
      this.itemStacks.clear();
      this.Itemslist.clear();
      this.itemStacks.add(new ItemStack(Items.ARROW, 1));
      this.itemStacks.add(new ItemStack(Items.END_CRYSTAL, 1));
      this.itemStacks.add(new ItemStack(Items.GOLDEN_APPLE, 1, 1));
      this.itemStacks.add(new ItemStack(Items.TOTEM_OF_UNDYING, 1));
      this.itemStacks.add(new ItemStack(Items.EXPERIENCE_BOTTLE, 1));
      this.itemStacks.add(new ItemStack(Items.ENDER_PEARL, 1));
      this.itemStacks.add(new ItemStack(Items.CHORUS_FRUIT, 1));
      this.itemStacks.add(new ItemStack(Item.getItemById(49), 1));
      this.Itemslist.add(Items.END_CRYSTAL);
      this.Itemslist.add(Items.ARROW);
      this.Itemslist.add(Items.GOLDEN_APPLE);
      this.Itemslist.add(Items.TOTEM_OF_UNDYING);
      this.Itemslist.add(Items.EXPERIENCE_BOTTLE);
      this.Itemslist.add(Items.CHORUS_FRUIT);
      this.Itemslist.add(Item.getItemById(49));
      this.Itemslist.add(Items.TIPPED_ARROW);
      this.Itemslist.add(Items.SPECTRAL_ARROW);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      if (this.mc.player == null)
        return; 
      setupitems();
      for (ItemStack itemStack : this.mc.player.inventory.mainInventory) {
        if (this.Itemslist.contains(itemStack.getItem()))
          for (ItemStack stack : this.itemStacks) {
            if (itemStack.getItem().equals(Items.TIPPED_ARROW) || (itemStack.getItem().equals(Items.SPECTRAL_ARROW) && stack.getItem().equals(Items.ARROW)))
              stack.setCount(stack.getCount() + itemStack.getCount()); 
            if (Objects.equals(stack.getItem().getRegistryName(), itemStack.getItem().getRegistryName()))
              stack.setCount(stack.getCount() + itemStack.getCount()); 
          }  
      } 
      int offset = 0;
      RenderHelper.enableGUIStandardItemLighting();
      for (ItemStack itemStack : this.itemStacks) {
        itemStack.setCount(itemStack.getCount() - 1);
        if (itemStack.getCount() >= 1) {
          this.mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, getX() + offset, getY() - 3);
          this.mc.getRenderItem().renderItemOverlayIntoGUI(this.mc.fontRenderer, itemStack, getX() + offset, getY() - 3, null);
          offset += 19;
        } 
      } 
      if (CombatItems.background.getValBoolean())
        RenderUtils.drawRectDouble(getX(), getY(), (getX() + offset + 10), (getY() + 20), CombatItems.BgColor.getcolor()); 
      RenderHelper.disableStandardItemLighting();
      (this.mc.getRenderItem()).zLevel = 0.0F;
      super.onRenderGameOverlay(event);
    }
  }
}
