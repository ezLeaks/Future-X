package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import java.util.OptionalInt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoTotem extends Module {
  private final int OFFHAND_SLOT = 45;
  
  Setting allowGui = Main.setmgr.add(new Setting("Use in Gui", this, true));
  
  Setting health = Main.setmgr.add(new Setting("health for switch", this, 10.0D, 0.0D, 35.0D, true));
  
  Setting needheal = Main.setmgr.add(new Setting("Use Health", this, false));
  
  Setting predict = Main.setmgr.add(new Setting("Predict", this, false));
  
  private boolean predicted = false;
  
  public AutoTotem() {
    super("Auto Totem", 0, Category.COMBAT, "Auto Totem");
  }
  
  @SubscribeEvent
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    if (getOffhand().getItem() == Items.TOTEM_OF_UNDYING)
      return; 
    if (mc.currentScreen != null && !this.allowGui.getValBoolean())
      return; 
    if (this.needheal.getValBoolean()) {
      if (mc.player.getHealth() < this.health.getValDouble())
        findItem().ifPresent(slot -> {
              invPickup(slot);
              invPickup(45);
            }); 
    } else if (this.predict.getValBoolean()) {
      if (this.predicted)
        findItem().ifPresent(slot -> {
              invPickup(slot);
              invPickup(45);
            }); 
    } else {
      findItem().ifPresent(slot -> {
            invPickup(slot);
            invPickup(45);
          });
    } 
    if (this.predicted) {
      ChatUtils.warning("Predicted Totem!");
      this.predicted = false;
    } 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.predict.getValBoolean() && 
      side == Connection.Side.IN && packet instanceof SPacketUpdateHealth) {
      SPacketUpdateHealth packet2 = (SPacketUpdateHealth)packet;
      if (packet2.getHealth() <= 0.0F)
        this.predicted = true; 
    } 
    return true;
  }
  
  private void invPickup(int slot) {
    MC.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)MC.player);
  }
  
  private static OptionalInt findItem() {
    for (int i = 9; i <= 44; i++) {
      if (MC.player.inventoryContainer.getSlot(i).getStack().getItem() == Items.TOTEM_OF_UNDYING)
        return OptionalInt.of(i); 
    } 
    return OptionalInt.empty();
  }
  
  private static ItemStack getOffhand() {
    return MC.player.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
  }
}
