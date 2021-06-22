package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Livestock extends Module {
  public Setting Dye;
  
  public Setting Shear;
  
  public Setting Breed;
  
  public Livestock() {
    super("Livestock Mod", 0, Category.MISC, "Auto Sheepmod");
    this.Dye = Main.setmgr.add(new Setting("Auto Dye", this, true));
    this.Shear = Main.setmgr.add(new Setting("Auto Shear", this, false));
    this.Breed = Main.setmgr.add(new Setting("Auto Breed", this, false));
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if ((mc.player.inventory.getCurrentItem().getItem() instanceof net.minecraft.item.ItemDye && this.Dye.getValBoolean()) || (this.Shear
      .getValBoolean() && mc.player.inventory.getCurrentItem().getItem() instanceof net.minecraft.item.ItemShears) || this.Breed.getValBoolean())
      for (Entity e : mc.world.loadedEntityList) {
        if (this.Breed.getValBoolean() && 
          e instanceof EntityAnimal) {
          EntityAnimal animal = (EntityAnimal)e;
          if (animal.getHealth() > 0.0F && 
            !animal.isChild() && !animal.isInLove() && mc.player.getDistance((Entity)animal) <= 4.5F && animal.isBreedingItem(mc.player.inventory.getCurrentItem()))
            mc.playerController.interactWithEntity((EntityPlayer)mc.player, (Entity)animal, EnumHand.MAIN_HAND); 
        } 
        if (e instanceof EntitySheep) {
          EntitySheep sheep = (EntitySheep)e;
          if (sheep.getHealth() > 0.0F && (
            this.Dye.getValBoolean() ? (sheep.getFleeceColor() != EnumDyeColor.byDyeDamage(mc.player.inventory.getCurrentItem().getMetadata())) : (
            !sheep.getSheared() && mc.player.getDistance((Entity)sheep) <= 4.5F)))
            mc.playerController.interactWithEntity((EntityPlayer)mc.player, (Entity)sheep, EnumHand.MAIN_HAND); 
        } 
      }  
  }
}
