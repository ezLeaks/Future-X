package Method.Client.utils.Creativetabs;

import java.util.ArrayList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CreativeTabArmor extends CreativeTabs {
  ArrayList<Enchantment> Enchants = new ArrayList<>();
  
  ArrayList<Integer> Levels = new ArrayList<>();
  
  ItemStack Blankspot = new ItemStack(Items.BRICK);
  
  public CreativeTabArmor() {
    super("Armor");
  }
  
  public String getTabLabel() {
    return "Armor";
  }
  
  public void displayAllRelevantItems(NonNullList<ItemStack> itemList) {
    this.Blankspot.setCount(-1);
    MaxVanillaArmor(itemList, 0);
    MaxVanillaArmor(itemList, 1);
    MaxVanillaArmor(itemList, 2);
    MaxVanillaArmor(itemList, 3);
    MaxVanillaArmor(itemList, 4);
    super.displayAllRelevantItems(itemList);
  }
  
  private void MaxVanillaArmor(NonNullList<ItemStack> itemList, int Switch) {
    this.Enchants.add(Enchantments.RESPIRATION);
    this.Levels.add(Integer.valueOf(3));
    this.Enchants.add(Enchantments.AQUA_AFFINITY);
    this.Levels.add(Integer.valueOf(1));
    Enchantsetup(Switch);
    Creativetabhelper.Packsize((Item)Items.DIAMOND_HELMET, this.Enchants, this.Levels, itemList);
    clearvar();
    itemList.add(this.Blankspot);
    Enchantsetup(Switch);
    Creativetabhelper.Packsize((Item)Items.DIAMOND_CHESTPLATE, this.Enchants, this.Levels, itemList);
    clearvar();
    Enchantsetup(Switch);
    Creativetabhelper.Packsize((Item)Items.DIAMOND_LEGGINGS, this.Enchants, this.Levels, itemList);
    clearvar();
    itemList.add(this.Blankspot);
    this.Enchants.add(Enchantments.DEPTH_STRIDER);
    this.Levels.add(Integer.valueOf(3));
    this.Enchants.add(Enchantments.FEATHER_FALLING);
    this.Levels.add(Integer.valueOf(4));
    this.Enchants.add(Enchantments.FROST_WALKER);
    this.Levels.add(Integer.valueOf(2));
    Enchantsetup(Switch);
    Creativetabhelper.Packsize((Item)Items.DIAMOND_BOOTS, this.Enchants, this.Levels, itemList);
    clearvar();
  }
  
  private void Enchantsetup(int Switch) {
    if (Switch == 0 || Switch == 1) {
      this.Enchants.add(Enchantments.BLAST_PROTECTION);
      this.Levels.add(Integer.valueOf(4));
    } 
    if (Switch == 0 || Switch == 2) {
      this.Enchants.add(Enchantments.FIRE_PROTECTION);
      this.Levels.add(Integer.valueOf(4));
    } 
    this.Enchants.add(Enchantments.MENDING);
    this.Levels.add(Integer.valueOf(1));
    if (Switch == 0 || Switch == 3) {
      this.Enchants.add(Enchantments.PROJECTILE_PROTECTION);
      this.Levels.add(Integer.valueOf(4));
    } 
    if (Switch == 0 || Switch == 4) {
      this.Enchants.add(Enchantments.PROTECTION);
      this.Levels.add(Integer.valueOf(4));
    } 
    this.Enchants.add(Enchantments.UNBREAKING);
    this.Levels.add(Integer.valueOf(3));
  }
  
  private void clearvar() {
    this.Enchants.clear();
    this.Levels.clear();
  }
  
  public ItemStack createIcon() {
    return new ItemStack((Item)Items.DIAMOND_CHESTPLATE);
  }
}
