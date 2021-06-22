package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.ValidUtils;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BowMod extends Module {
  public Setting BowSpam = Main.setmgr.add(new Setting("BowSpam", this, false));
  
  public Setting PacketSpam = Main.setmgr.add(new Setting("PacketSpam", this, false, this.BowSpam, 2));
  
  public Setting AimBot = Main.setmgr.add(new Setting("AimBot", this, false));
  
  public Setting walls = Main.setmgr.add(new Setting("walls", this, false, this.AimBot, 6));
  
  public Setting yaw = Main.setmgr.add(new Setting("Yaw", this, 22.0D, 0.0D, 50.0D, false, this.AimBot, 4));
  
  public Setting FOV = Main.setmgr.add(new Setting("FOV", this, 90.0D, 1.0D, 180.0D, true, this.AimBot, 5));
  
  public Setting KickBow = Main.setmgr.add(new Setting("KickBow", this, false));
  
  public EntityLivingBase target;
  
  public float rangeAimVelocity = 0.0F;
  
  public BowMod() {
    super("BowMod", 0, Category.COMBAT, "BowMod");
  }
  
  public void onDisable() {
    this.target = null;
    super.onDisable();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    mc.player.inventory.getCurrentItem();
    if (!(mc.player.inventory.getCurrentItem().getItem() instanceof net.minecraft.item.ItemBow))
      return; 
    if (!mc.gameSettings.keyBindUseItem.isKeyDown())
      return; 
    if (this.KickBow.getValBoolean() && 
      mc.player.inventory.getCurrentItem().getItem() instanceof net.minecraft.item.ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 25) {
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
      mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
      mc.player.stopActiveHand();
      if (findShulker() != -1) {
        changeItem(findShulker());
      } else {
        ChatUtils.message("No shulker found in hotbar, not switching...");
        toggle();
      } 
    } 
    if (this.AimBot.getValBoolean()) {
      this.target = getClosestEntity();
      if (this.target == null)
        return; 
      float rangeCharge = mc.player.getItemInUseCount();
      this.rangeAimVelocity = rangeCharge / 20.0F;
      this.rangeAimVelocity = (this.rangeAimVelocity * this.rangeAimVelocity + this.rangeAimVelocity * 2.0F) / 3.0F;
      this.rangeAimVelocity = 1.0F;
      double posX = this.target.posX - mc.player.posX;
      double posY = this.target.posY + this.target.getEyeHeight() - 0.15D - mc.player.posY - mc.player.getEyeHeight();
      double posZ = this.target.posZ - mc.player.posZ;
      double y2 = Math.sqrt(posX * posX + posZ * posZ);
      float g = 0.006F;
      float tmp = (float)((this.rangeAimVelocity * this.rangeAimVelocity * this.rangeAimVelocity * this.rangeAimVelocity) - g * (g * y2 * y2 + 2.0D * posY * (this.rangeAimVelocity * this.rangeAimVelocity)));
      float pitch = (float)-Math.toDegrees(Math.atan(((this.rangeAimVelocity * this.rangeAimVelocity) - Math.sqrt(tmp)) / g * y2));
      float[] rot = Utils.getNeededRotations(this.target.getPositionVector(), (float)this.yaw.getValDouble(), 0.0F);
      mc.player.rotationYaw = rot[0];
      mc.player.rotationPitch = pitch;
    } 
    if (this.BowSpam.getValBoolean() && 
      mc.player.inventory.getCurrentItem().getItem() instanceof net.minecraft.item.ItemBow && 
      mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 3) {
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
      if (this.PacketSpam.getValBoolean())
        for (int var1 = 0; var1 < 10; var1++)
          mc.player.connection.sendPacket((Packet)new CPacketPlayer(true));  
      mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
      mc.player.stopActiveHand();
    } 
  }
  
  public int findShulker() {
    byte b = -1;
    for (byte b1 = 0; b1 < 9; b1 = (byte)(b1 + 1)) {
      ItemStack itemStack = (ItemStack)mc.player.inventory.mainInventory.get(b1);
      if (itemStack.getItem() instanceof net.minecraft.item.ItemShulkerBox)
        b = b1; 
    } 
    return b;
  }
  
  public void changeItem(int paramInt) {
    mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(paramInt));
    mc.player.inventory.currentItem = paramInt;
  }
  
  public boolean check(EntityLivingBase entity) {
    if (Checks(entity, this.FOV))
      return false; 
    if (!ValidUtils.pingCheck(entity))
      return false; 
    if (!this.walls.getValBoolean())
      return mc.player.canEntityBeSeen((Entity)entity); 
    return true;
  }
  
  boolean Checks(EntityLivingBase entity, Setting fov) {
    if (entity instanceof net.minecraft.entity.item.EntityArmorStand)
      return true; 
    if (!ValidUtils.isNoScreen())
      return true; 
    if (entity == mc.player)
      return true; 
    if (entity.isDead)
      return true; 
    if (ValidUtils.isBot(entity))
      return true; 
    if (ValidUtils.isFriendEnemy(entity))
      return true; 
    if (!ValidUtils.isInAttackFOV(entity, (int)fov.getValDouble()))
      return true; 
    if (entity instanceof net.minecraft.entity.player.EntityPlayer)
      return false; 
    return true;
  }
  
  EntityLivingBase getClosestEntity() {
    EntityLivingBase closestEntity = null;
    for (Object o : mc.world.loadedEntityList) {
      if (o instanceof EntityLivingBase && !(o instanceof net.minecraft.entity.item.EntityArmorStand)) {
        EntityLivingBase entity = (EntityLivingBase)o;
        if (check(entity) && (
          closestEntity == null || mc.player.getDistance((Entity)entity) < mc.player.getDistance((Entity)closestEntity)))
          closestEntity = entity; 
      } 
    } 
    return closestEntity;
  }
}
