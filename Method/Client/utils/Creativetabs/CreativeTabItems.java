package Method.Client.utils.Creativetabs;

import java.util.ArrayList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CreativeTabItems extends CreativeTabs {
  ArrayList<Enchantment> Enchants = new ArrayList<>();
  
  ArrayList<Integer> Levels = new ArrayList<>();
  
  ItemStack Blankspot = new ItemStack(Items.BRICK);
  
  public CreativeTabItems() {
    super("Items");
  }
  
  public String getTabLabel() {
    return "Items";
  }
  
  public void displayAllRelevantItems(NonNullList<ItemStack> itemList) {
    this.Blankspot.setCount(-1);
    this.Enchants.add(Enchantments.SHARPNESS);
    this.Levels.add(Integer.valueOf(32767));
    Creativetabhelper.Packsize(Items.DIAMOND_SWORD, this.Enchants, this.Levels, itemList);
    clearvar();
    itemList.add(this.Blankspot);
    this.Enchants.add(Enchantments.SHARPNESS);
    this.Levels.add(Integer.valueOf(5));
    this.Enchants.add(Enchantments.KNOCKBACK);
    this.Levels.add(Integer.valueOf(2));
    this.Enchants.add(Enchantments.FIRE_ASPECT);
    this.Levels.add(Integer.valueOf(2));
    this.Enchants.add(Enchantments.LOOTING);
    this.Levels.add(Integer.valueOf(3));
    this.Enchants.add(Enchantments.SWEEPING);
    this.Levels.add(Integer.valueOf(3));
    this.Enchants.add(Enchantments.SMITE);
    this.Levels.add(Integer.valueOf(5));
    this.Enchants.add(Enchantments.BANE_OF_ARTHROPODS);
    this.Levels.add(Integer.valueOf(5));
    Mendundbr();
    Creativetabhelper.Packsize(Items.DIAMOND_SWORD, this.Enchants, this.Levels, itemList);
    clearvar();
    PicRepeat();
    this.Enchants.add(Enchantments.SILK_TOUCH);
    this.Levels.add(Integer.valueOf(1));
    Creativetabhelper.Packsize(Items.DIAMOND_PICKAXE, this.Enchants, this.Levels, itemList);
    clearvar();
    PicRepeat();
    itemList.add(this.Blankspot);
    Creativetabhelper.Packsize(Items.DIAMOND_PICKAXE, this.Enchants, this.Levels, itemList);
    clearvar();
    this.Enchants.add(Enchantments.BANE_OF_ARTHROPODS);
    this.Levels.add(Integer.valueOf(5));
    this.Enchants.add(Enchantments.EFFICIENCY);
    this.Levels.add(Integer.valueOf(5));
    this.Enchants.add(Enchantments.FORTUNE);
    this.Levels.add(Integer.valueOf(3));
    this.Enchants.add(Enchantments.SHARPNESS);
    this.Levels.add(Integer.valueOf(5));
    this.Enchants.add(Enchantments.SMITE);
    this.Levels.add(Integer.valueOf(5));
    Mendundbr();
    Creativetabhelper.Packsize(Items.DIAMOND_AXE, this.Enchants, this.Levels, itemList);
    clearvar();
    PicRepeat();
    itemList.add(this.Blankspot);
    Creativetabhelper.Packsize(Items.DIAMOND_SHOVEL, this.Enchants, this.Levels, itemList);
    clearvar();
    Mendundbr();
    Creativetabhelper.Packsize(Items.DIAMOND_HOE, this.Enchants, this.Levels, itemList);
    clearvar();
    itemList.add(this.Blankspot);
    this.Enchants.add(Enchantments.EFFICIENCY);
    this.Levels.add(Integer.valueOf(5));
    Mendundbr();
    Creativetabhelper.Packsize((Item)Items.SHEARS, this.Enchants, this.Levels, itemList);
    clearvar();
    this.Enchants.add(Enchantments.PUNCH);
    this.Levels.add(Integer.valueOf(2));
    this.Enchants.add(Enchantments.POWER);
    this.Levels.add(Integer.valueOf(5));
    Bowrepeat(itemList);
    itemList.add(this.Blankspot);
    this.Enchants.add(Enchantments.PUNCH);
    this.Levels.add(Integer.valueOf(32767));
    this.Enchants.add(Enchantments.POWER);
    this.Levels.add(Integer.valueOf(32767));
    Bowrepeat(itemList);
    this.Enchants.add(Enchantments.MENDING);
    this.Levels.add(Integer.valueOf(1));
    this.Enchants.add(Enchantments.UNBREAKING);
    this.Levels.add(Integer.valueOf(3));
    Creativetabhelper.Packsize(Items.ELYTRA, this.Enchants, this.Levels, itemList);
    clearvar();
    itemList.add(this.Blankspot);
    this.Enchants.add(Enchantments.MENDING);
    this.Levels.add(Integer.valueOf(32767));
    this.Enchants.add(Enchantments.UNBREAKING);
    this.Levels.add(Integer.valueOf(32767));
    Creativetabhelper.Packsize(Items.ELYTRA, this.Enchants, this.Levels, itemList);
    clearvar();
    this.Enchants.add(Enchantments.LUCK_OF_THE_SEA);
    this.Levels.add(Integer.valueOf(3));
    this.Enchants.add(Enchantments.LURE);
    this.Levels.add(Integer.valueOf(3));
    Mendundbr();
    Creativetabhelper.Packsize((Item)Items.FISHING_ROD, this.Enchants, this.Levels, itemList);
    clearvar();
    itemList.add(this.Blankspot);
    this.Enchants.add(Enchantments.LUCK_OF_THE_SEA);
    this.Levels.add(Integer.valueOf(32767));
    this.Enchants.add(Enchantments.LURE);
    this.Levels.add(Integer.valueOf(32767));
    Mendundbr();
    Creativetabhelper.Packsize((Item)Items.FISHING_ROD, this.Enchants, this.Levels, itemList);
    clearvar();
    super.displayAllRelevantItems(itemList);
  }
  
  private void PicRepeat() {
    this.Enchants.add(Enchantments.EFFICIENCY);
    this.Levels.add(Integer.valueOf(5));
    this.Enchants.add(Enchantments.FORTUNE);
    this.Levels.add(Integer.valueOf(3));
    this.Enchants.add(Enchantments.MENDING);
    this.Levels.add(Integer.valueOf(1));
    this.Enchants.add(Enchantments.UNBREAKING);
    this.Levels.add(Integer.valueOf(3));
  }
  
  private void Mendundbr() {
    this.Enchants.add(Enchantments.MENDING);
    this.Levels.add(Integer.valueOf(1));
    this.Enchants.add(Enchantments.UNBREAKING);
    this.Levels.add(Integer.valueOf(3));
  }
  
  private void Bowrepeat(NonNullList<ItemStack> itemList) {
    this.Enchants.add(Enchantments.FLAME);
    this.Levels.add(Integer.valueOf(1));
    this.Enchants.add(Enchantments.INFINITY);
    this.Levels.add(Integer.valueOf(1));
    this.Enchants.add(Enchantments.MENDING);
    this.Levels.add(Integer.valueOf(1));
    this.Enchants.add(Enchantments.UNBREAKING);
    this.Levels.add(Integer.valueOf(3));
    Creativetabhelper.Packsize((Item)Items.BOW, this.Enchants, this.Levels, itemList);
    clearvar();
  }
  
  private void clearvar() {
    this.Enchants.clear();
    this.Levels.clear();
  }
  
  public ItemStack createIcon() {
    return new ItemStack(Items.DIAMOND_AXE);
  }
}
