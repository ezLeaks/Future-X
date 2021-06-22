package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Offhand extends Module {
  Setting switch_mode = Main.setmgr.add(new Setting("switch_mode", this, "Totem", new String[] { "Totem", "Crystal", "Gapple" }));
  
  Setting totem_switch = Main.setmgr.add(new Setting("totem_switch", this, 16.0D, 0.0D, 36.0D, true));
  
  Setting gapple_in_hole = Main.setmgr.add(new Setting("gapple_in_hole", this, true));
  
  Setting gapple_hole_hp = Main.setmgr.add(new Setting("gapple_hole_hp", this, 8.0D, 0.0D, 36.0D, true));
  
  Setting delay = Main.setmgr.add(new Setting("delay", this, true));
  
  boolean switching = false;
  
  int last_slot;
  
  public Offhand() {
    super("Offhand", 0, Category.COMBAT, "Offhand Item");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.currentScreen == null || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory) {
      if (this.switching) {
        swap_items(this.last_slot, 2);
        return;
      } 
      float hp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
      if (hp > this.totem_switch.getValDouble()) {
        if (this.switch_mode.getValString().equalsIgnoreCase("Crystal") && ModuleManager.getModuleByName("CrystalAura").isToggled()) {
          swap_items(get_item_slot(Items.END_CRYSTAL), 0);
          return;
        } 
        if (this.gapple_in_hole.getValBoolean() && hp > this.gapple_hole_hp.getValDouble() && is_in_hole()) {
          swap_items(get_item_slot(Items.GOLDEN_APPLE), this.delay.getValBoolean() ? 1 : 0);
          return;
        } 
        if (this.switch_mode.getValString().equalsIgnoreCase("Totem")) {
          swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), this.delay.getValBoolean() ? 1 : 0);
          return;
        } 
        if (this.switch_mode.getValString().equalsIgnoreCase("Gapple")) {
          swap_items(get_item_slot(Items.GOLDEN_APPLE), this.delay.getValBoolean() ? 1 : 0);
          return;
        } 
        if (this.switch_mode.getValString().equalsIgnoreCase("Crystal") && !ModuleManager.getModuleByName("CrystalAura").isToggled()) {
          swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), 0);
          return;
        } 
      } else {
        swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), this.delay.getValBoolean() ? 1 : 0);
        return;
      } 
      if (mc.player.getHeldItemOffhand().getItem() == Items.AIR)
        swap_items(get_item_slot(Items.TOTEM_OF_UNDYING), this.delay.getValBoolean() ? 1 : 0); 
    } 
  }
  
  public void swap_items(int slot, int step) {
    if (slot == -1)
      return; 
    if (step == 0) {
      mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    } 
    if (step == 1) {
      mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      this.switching = true;
      this.last_slot = slot;
    } 
    if (step == 2) {
      mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      this.switching = false;
    } 
    mc.playerController.updateController();
  }
  
  private boolean is_in_hole() {
    BlockPos player_block = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    return (mc.world.getBlockState(player_block.east()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(player_block.west()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(player_block.north()).getBlock() != Blocks.AIR && mc.world
      .getBlockState(player_block.south()).getBlock() != Blocks.AIR);
  }
  
  private int get_item_slot(Item input) {
    if (input == mc.player.getHeldItemOffhand().getItem())
      return -1; 
    for (int i = 36; i >= 0; i--) {
      Item item = mc.player.inventory.getStackInSlot(i).getItem();
      if (item == input) {
        if (i < 9) {
          if (input == Items.GOLDEN_APPLE)
            return -1; 
          i += 36;
        } 
        return i;
      } 
    } 
    return -1;
  }
}
