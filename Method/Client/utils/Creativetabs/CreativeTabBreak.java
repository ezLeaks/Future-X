package Method.Client.utils.Creativetabs;

import java.util.ArrayList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CreativeTabBreak extends CreativeTabs {
  ItemStack Blankspot = new ItemStack(Items.BRICK);
  
  public CreativeTabBreak() {
    super("Break");
  }
  
  public String getTabLabel() {
    return "Break";
  }
  
  public void displayAllRelevantItems(NonNullList<ItemStack> itemList) {
    ArrayList<Enchantment> AllEnchant = new ArrayList<>();
    ArrayList<Integer> AllLevel32k = new ArrayList<>();
    int Simple30 = 0;
    for (Enchantment e : Enchantment.REGISTRY) {
      if (Simple30 <= 30) {
        AllEnchant.add(e);
        AllLevel32k.add(Integer.valueOf(32767));
      } 
      Simple30++;
    } 
    this.Blankspot.setCount(-1);
    Creativetabhelper.Attributeitems(Items.DIAMOND_SWORD, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, true);
    Creativetabhelper.Attributeitems(Items.DIAMOND_PICKAXE, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, true);
    Creativetabhelper.Attributeitems(Items.DIAMOND_AXE, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, true);
    Creativetabhelper.Attributeitems(Items.DIAMOND_SHOVEL, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, true);
    Creativetabhelper.Attributeitems(Items.DIAMOND_HOE, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, true);
    Creativetabhelper.Attributeitems((Item)Items.SHEARS, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, true);
    Creativetabhelper.Attributeitems((Item)Items.BOW, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, true);
    Creativetabhelper.Attributeitems(Items.ELYTRA, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, true);
    Creativetabhelper.Attributeitems((Item)Items.FISHING_ROD, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, true);
    Creativetabhelper.Attributeitems((Item)Items.DIAMOND_HELMET, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, true);
    Creativetabhelper.Attributeitems((Item)Items.DIAMOND_CHESTPLATE, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, true);
    Creativetabhelper.Attributeitems((Item)Items.DIAMOND_LEGGINGS, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, true);
    Creativetabhelper.Attributeitems((Item)Items.DIAMOND_BOOTS, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, true);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    Creativetabhelper.Unbreakpack(Items.DIAMOND_SWORD, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack(Items.DIAMOND_PICKAXE, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack(Items.DIAMOND_AXE, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack(Items.DIAMOND_SHOVEL, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack(Items.DIAMOND_HOE, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack((Item)Items.SHEARS, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack((Item)Items.BOW, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack(Items.ELYTRA, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack((Item)Items.FISHING_ROD, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack((Item)Items.DIAMOND_HELMET, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack((Item)Items.DIAMOND_CHESTPLATE, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack((Item)Items.DIAMOND_LEGGINGS, AllEnchant, AllLevel32k, itemList);
    Creativetabhelper.Unbreakpack((Item)Items.DIAMOND_BOOTS, AllEnchant, AllLevel32k, itemList);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    Creativetabhelper.Attributeitems(Items.DIAMOND_SWORD, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, false);
    Creativetabhelper.Attributeitems(Items.DIAMOND_PICKAXE, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, false);
    Creativetabhelper.Attributeitems(Items.DIAMOND_AXE, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, false);
    Creativetabhelper.Attributeitems(Items.DIAMOND_SHOVEL, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, false);
    Creativetabhelper.Attributeitems(Items.DIAMOND_HOE, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, false);
    Creativetabhelper.Attributeitems((Item)Items.SHEARS, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, false);
    Creativetabhelper.Attributeitems((Item)Items.BOW, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, false);
    Creativetabhelper.Attributeitems(Items.ELYTRA, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, false);
    Creativetabhelper.Attributeitems((Item)Items.FISHING_ROD, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.MAINHAND, false);
    Creativetabhelper.Attributeitems((Item)Items.DIAMOND_HELMET, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, false);
    Creativetabhelper.Attributeitems((Item)Items.DIAMOND_CHESTPLATE, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, false);
    Creativetabhelper.Attributeitems((Item)Items.DIAMOND_LEGGINGS, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, false);
    Creativetabhelper.Attributeitems((Item)Items.DIAMOND_BOOTS, AllEnchant, AllLevel32k, itemList, EntityEquipmentSlot.CHEST, false);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    itemList.add(this.Blankspot);
    super.displayAllRelevantItems(itemList);
  }
  
  public ItemStack createIcon() {
    return new ItemStack(Items.ARROW);
  }
}
