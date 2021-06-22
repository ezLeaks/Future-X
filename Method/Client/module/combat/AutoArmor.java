package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoArmor extends Module {
  private int timer;
  
  Setting useEnchantments = Main.setmgr.add(new Setting("Enchantments", this, true));
  
  Setting swapWhileMoving = Main.setmgr.add(new Setting("SwapWhileMoving", this, true));
  
  Setting delay = Main.setmgr.add(new Setting("Delay", this, 1.0D, 0.0D, 5.0D, true));
  
  Setting nocurse = Main.setmgr.add(new Setting("No Binding", this, true));
  
  Setting Elytra = Main.setmgr.add(new Setting("Elytra Over Chest", this, true));
  
  Setting Edam = Main.setmgr.add(new Setting("Elytra Damage", this, 2.0D, 0.0D, 320.0D, true));
  
  boolean ElytraSwitch = false;
  
  public AutoArmor() {
    super("Auto Armor", 0, Category.COMBAT, "Puts on any Armor");
  }
  
  public void onEnable() {
    this.ElytraSwitch = false;
    this.timer = 0;
    super.onEnable();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.timer > 0) {
      this.timer--;
      return;
    } 
    if ((Wrapper.INSTANCE.mc()).currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && 
      !((Wrapper.INSTANCE.mc()).currentScreen instanceof net.minecraft.client.renderer.InventoryEffectRenderer))
      return; 
    InventoryPlayer inventory = mc.player.inventory;
    if (!this.swapWhileMoving.getValBoolean() && (mc.player.movementInput.moveForward != 0.0F || mc.player.movementInput.moveStrafe != 0.0F))
      return; 
    int[] bestArmorSlots = new int[4];
    int[] bestArmorValues = new int[4];
    for (int type = 0; type < 4; type++) {
      bestArmorSlots[type] = -1;
      ItemStack stack = inventory.armorItemInSlot(type);
      if (this.Elytra.getValBoolean() && type == 2 && 
        stack.getItem() instanceof net.minecraft.item.ItemElytra) {
        if (stack.isEmpty())
          this.ElytraSwitch = false; 
        if (stack.getItem().getDamage(stack) > stack.getItem().getMaxDamage(stack) - this.Edam.getValDouble())
          this.ElytraSwitch = false; 
      } else if (!isNullOrEmpty(stack) && stack
        .getItem() instanceof ItemArmor) {
        ItemArmor item = (ItemArmor)stack.getItem();
        bestArmorValues[type] = getArmorValue(item, stack);
      } 
    } 
    for (int slot = 0; slot < 36; slot++) {
      ItemStack stack = inventory.getStackInSlot(slot);
      if (stack.getItem() instanceof net.minecraft.item.ItemElytra && this.Elytra.getValBoolean() && !this.ElytraSwitch) {
        if (stack.getItem().getDamage(stack) <= stack.getItem().getMaxDamage(stack) - this.Edam.getValDouble()) {
          bestArmorSlots[2] = slot;
          this.ElytraSwitch = true;
        } 
      } else if (!isNullOrEmpty(stack) && stack
        .getItem() instanceof ItemArmor) {
        if (!this.nocurse.getValBoolean() || 
          !EnchantmentHelper.hasBindingCurse(stack)) {
          ItemArmor item = (ItemArmor)stack.getItem();
          int armorType = item.armorType.getIndex();
          int armorValue = getArmorValue(item, stack);
          if (armorValue > bestArmorValues[armorType]) {
            bestArmorSlots[armorType] = slot;
            bestArmorValues[armorType] = armorValue;
          } 
        } 
      } 
    } 
    ArrayList<Integer> types = new ArrayList<>(Arrays.asList(new Integer[] { Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3) }));
    Collections.shuffle(types);
    for (Iterator<Integer> iterator = types.iterator(); iterator.hasNext(); ) {
      int i = ((Integer)iterator.next()).intValue();
      int j = bestArmorSlots[i];
      if (j == -1)
        continue; 
      if (inventory.armorItemInSlot(i).getItem() instanceof net.minecraft.item.ItemElytra && this.Elytra.getValBoolean()) {
        ItemStack stack = inventory.armorItemInSlot(i);
        if (stack.getItem().getDamage(stack) > stack.getItem().getMaxDamage(stack) - this.Edam.getValDouble()) {
          (Wrapper.INSTANCE.mc()).playerController.windowClick(0, 8 - i, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
          this.ElytraSwitch = false;
        } else {
          continue;
        } 
      } 
      ItemStack oldArmor = inventory.armorItemInSlot(i);
      if (!isNullOrEmpty(oldArmor) && inventory
        .getFirstEmptyStack() == -1)
        continue; 
      if (j < 9)
        j += 36; 
      if (!isNullOrEmpty(oldArmor))
        (Wrapper.INSTANCE.mc()).playerController.windowClick(0, 8 - i, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player); 
      (Wrapper.INSTANCE.mc()).playerController.windowClick(0, j, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
    } 
    super.onClientTick(event);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && packet instanceof net.minecraft.network.play.client.CPacketClickWindow)
      this.timer = (int)this.delay.getValDouble(); 
    return true;
  }
  
  public static boolean isNullOrEmpty(ItemStack stack) {
    return (stack == null || stack.isEmpty());
  }
  
  int getArmorValue(ItemArmor item, ItemStack stack) {
    int armorPoints = item.damageReduceAmount;
    int prtPoints = 0;
    int armorToughness = (int)item.toughness;
    int armorType = item.getArmorMaterial().getDamageReductionAmount(EntityEquipmentSlot.LEGS);
    if (this.useEnchantments.getValBoolean()) {
      Enchantment protection = Enchantments.PROTECTION;
      int prtLvl = EnchantmentHelper.getEnchantmentLevel(protection, stack);
      DamageSource dmgSource = DamageSource.causePlayerDamage((EntityPlayer)mc.player);
      prtPoints = protection.calcModifierDamage(prtLvl, dmgSource);
    } 
    return armorPoints * 5 + prtPoints * 3 + armorToughness + armorType;
  }
}
