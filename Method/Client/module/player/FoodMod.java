package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.combat.AutoArmor;
import Method.Client.utils.BlockUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FoodMod extends Module {
  Setting Souphunger = Main.setmgr.add(new Setting("Hunger", this, 10.0D, 0.0D, 20.0D, true));
  
  Setting Soup = Main.setmgr.add(new Setting("Soup", this, false));
  
  Setting AntiHunger = Main.setmgr.add(new Setting("AntiHunger", this, false));
  
  Setting AutoEat = Main.setmgr.add(new Setting("AutoEat", this, false));
  
  Setting SetFoodLevelMax = Main.setmgr.add(new Setting("SetFoodLevelMax", this, false));
  
  int slotBefore = -1;
  
  int bestSlot = -1;
  
  int eating = 40;
  
  private int oldSlot = -1;
  
  public FoodMod() {
    super("FoodMod", 0, Category.PLAYER, "FoodMod");
  }
  
  public void onEnable() {
    this.oldSlot = -1;
    this.bestSlot = -1;
    super.onEnable();
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && this.AntiHunger.getValBoolean() && 
      packet instanceof CPacketPlayer) {
      CPacketPlayer packet2 = (CPacketPlayer)packet;
      packet2.onGround = (mc.player.fallDistance >= 0.0F || mc.playerController.getIsHittingBlock());
    } 
    return true;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.SetFoodLevelMax.getValBoolean())
      mc.player.getFoodStats().setFoodLevel(20); 
    if (this.AutoEat.getValBoolean())
      if (this.oldSlot == -1) {
        if (!canEat())
          return; 
        float bestSaturation = 0.0F;
        for (int i = 0; i < 9; i++) {
          ItemStack stack = mc.player.inventory.getStackInSlot(i);
          if (isFood(stack)) {
            ItemFood food = (ItemFood)stack.getItem();
            float saturation = food.getSaturationModifier(stack);
            if (saturation > bestSaturation) {
              bestSaturation = saturation;
              this.bestSlot = i;
            } 
          } 
        } 
        if (this.bestSlot != -1)
          this.oldSlot = mc.player.inventory.currentItem; 
      } else {
        if (!canEat()) {
          stop();
          return;
        } 
        if (!isFood(mc.player.inventory.getStackInSlot(this.bestSlot))) {
          stop();
          return;
        } 
        mc.player.inventory.currentItem = this.bestSlot;
        mc.gameSettings.keyBindUseItem.pressed = true;
      }  
    if (this.AntiHunger.getValBoolean())
      mc.player.onGround = false; 
    if (this.Soup.getValBoolean()) {
      for (int i = 0; i < 36; i++) {
        ItemStack stack = mc.player.inventory.getStackInSlot(i);
        if (stack.getItem() == Items.BOWL && i != 9) {
          ItemStack emptyBowlStack = mc.player.inventory.getStackInSlot(9);
          boolean swap = (!AutoArmor.isNullOrEmpty(emptyBowlStack) && emptyBowlStack.getItem() != Items.BOWL);
          windowClick_PICKUP((i < 9) ? (36 + i) : i);
          windowClick_PICKUP(9);
          if (swap)
            windowClick_PICKUP((i < 9) ? (36 + i) : i); 
        } 
      } 
      int soupInHotbar = findSoup(0, 9);
      if (soupInHotbar != -1) {
        if (!shouldEatSoup()) {
          stopIfEating();
          return;
        } 
        if (this.oldSlot == -1)
          this.oldSlot = mc.player.inventory.currentItem; 
        mc.player.inventory.currentItem = soupInHotbar;
        mc.gameSettings.keyBindUseItem.pressed = true;
        processRightClick();
        return;
      } 
      stopIfEating();
      int soupInInventory = findSoup(9, 36);
      if (soupInInventory != -1)
        windowClick_QUICK_MOVE(soupInInventory); 
    } 
    if (!this.Soup.getValBoolean()) {
      if (this.eating < 41) {
        this.eating++;
        if (this.eating <= 1)
          mc.player.inventory.currentItem = this.bestSlot; 
        mc.gameSettings.keyBindUseItem.pressed = true;
        if (this.eating >= 38) {
          mc.gameSettings.keyBindUseItem.pressed = true;
          if (this.slotBefore != -1)
            mc.player.inventory.currentItem = this.slotBefore; 
          this.slotBefore = -1;
        } 
        return;
      } 
      float bestRestoration = 0.0F;
      this.bestSlot = -1;
      int PrevSlot = mc.player.inventory.currentItem;
      for (int i = 0; i < 9; i++) {
        ItemStack item = mc.player.inventory.getStackInSlot(i);
        float restoration = 0.0F;
        if (item.getItem() instanceof ItemFood)
          restoration = ((ItemFood)item.getItem()).getSaturationModifier(item); 
        if (restoration > bestRestoration) {
          bestRestoration = restoration;
          this.bestSlot = i;
        } 
      } 
      if (this.bestSlot == -1)
        return; 
      if (mc.player.getFoodStats().getFoodLevel() >= this.Souphunger.getValDouble())
        return; 
      this.slotBefore = mc.player.inventory.currentItem;
      if (this.slotBefore == -1)
        return; 
      mc.player.inventory.currentItem = PrevSlot;
      mc.player.stopActiveHand();
      mc.player.inventory.currentItem = PrevSlot;
      this.eating = 0;
      super.onClientTick(event);
    } 
  }
  
  private int findSoup(int startSlot, int endSlot) {
    for (int i = startSlot; i < endSlot; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack.getItem() instanceof net.minecraft.item.ItemSoup)
        return i; 
    } 
    return -1;
  }
  
  private boolean canEat() {
    if (!mc.player.canEat(false))
      return false; 
    if (mc.objectMouseOver != null) {
      Entity entity = mc.objectMouseOver.entityHit;
      if (entity instanceof net.minecraft.entity.passive.EntityVillager || entity instanceof net.minecraft.entity.passive.EntityTameable)
        return false; 
      BlockPos pos = mc.objectMouseOver.getBlockPos();
      if (pos != null) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (block instanceof net.minecraft.block.BlockContainer || block instanceof net.minecraft.block.BlockWorkbench)
          return false; 
      } 
    } 
    return true;
  }
  
  private boolean isFood(ItemStack stack) {
    return stack.getItem() instanceof ItemFood;
  }
  
  private void stop() {
    mc.gameSettings.keyBindUseItem.pressed = false;
    mc.player.inventory.currentItem = this.oldSlot;
    this.oldSlot = -1;
  }
  
  private boolean shouldEatSoup() {
    if (mc.player.getHealth() > 13.0D)
      return false; 
    if (Wrapper.mc.currentScreen != null)
      return false; 
    if (Wrapper.mc.objectMouseOver != null) {
      Entity entity = Wrapper.mc.objectMouseOver.entityHit;
      if (entity instanceof net.minecraft.entity.passive.EntityVillager || entity instanceof net.minecraft.entity.passive.EntityTameable)
        return false; 
      Wrapper.mc.objectMouseOver.getBlockPos();
      return !(getBlock(Wrapper.mc.objectMouseOver
          .getBlockPos()) instanceof net.minecraft.block.BlockContainer);
    } 
    return true;
  }
  
  private void stopIfEating() {
    if (this.oldSlot == -1)
      return; 
    mc.gameSettings.keyBindUseItem.pressed = true;
    mc.player.inventory.currentItem = this.oldSlot;
    this.oldSlot = -1;
  }
  
  public static Block getBlock(BlockPos pos) {
    return BlockUtils.getState(pos).getBlock();
  }
  
  public static ItemStack windowClick_PICKUP(int slot) {
    return getPlayerController().windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
  }
  
  private static PlayerControllerMP getPlayerController() {
    return mc.playerController;
  }
  
  public static void processRightClick() {
    getPlayerController().processRightClick((EntityPlayer)mc.player, mc.player.world, EnumHand.MAIN_HAND);
  }
  
  public static ItemStack windowClick_QUICK_MOVE(int slot) {
    return getPlayerController().windowClick(0, slot, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
  }
}
