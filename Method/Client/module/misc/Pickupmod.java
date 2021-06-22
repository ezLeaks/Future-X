package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Pickupmod extends Module {
  Setting Fast;
  
  Setting Antipickup;
  
  Setting RemoveDrops;
  
  Setting LongRange;
  
  Setting Distance;
  
  public Pickupmod() {
    super("Pickupmod", 0, Category.MISC, "Pickup tools");
    this.Fast = Main.setmgr.add(this.Fast = new Setting("Fast", this, true));
    this.Antipickup = Main.setmgr.add(new Setting("Antipickup", this, false));
    this.RemoveDrops = Main.setmgr.add(new Setting("RemoveDrops", this, false));
    this.LongRange = Main.setmgr.add(new Setting("LongRange", this, true));
    this.Distance = Main.setmgr.add(new Setting("Distance", this, 4.0D, 0.0D, 10.0D, false));
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    for (EntityItem entityItem : mc.world.getEntitiesWithinAABB(EntityItem.class, mc.player.getEntityBoundingBox().grow(this.Distance.getValDouble(), this.Distance.getValDouble(), this.Distance.getValDouble()))) {
      if (this.Antipickup.getValBoolean()) {
        if (entityItem.ticksExisted > 30) {
          entityItem.ticksExisted = 0;
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(entityItem.posX, entityItem.posY + 2.0D, entityItem.posZ, false));
        } 
        entityItem.owner = "NULL";
        entityItem.collided = false;
        entityItem.chunkCoordX = 0;
        entityItem.chunkCoordY = 0;
        entityItem.chunkCoordZ = 0;
        entityItem.dimension = 57;
        entityItem.lifespan = -1;
        entityItem.collidedHorizontally = false;
        entityItem.collidedVertically = false;
      } 
      if (this.LongRange.getValBoolean() && 
        entityItem.ticksExisted > 30) {
        entityItem.ticksExisted = 10;
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(entityItem.posX, entityItem.posY, entityItem.posZ, mc.player.onGround));
      } 
      if (this.RemoveDrops.getValBoolean()) {
        entityItem.setDead();
        entityItem.onRemovedFromWorld();
      } 
      if (this.Fast.getValBoolean()) {
        entityItem.ticksExisted = 45;
        entityItem.setNoPickupDelay();
        entityItem.collidedHorizontally = true;
        entityItem.collidedVertically = true;
        entityItem.collided = true;
      } 
    } 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.Antipickup.getValBoolean())
      return (!(packet instanceof net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent) && !(packet instanceof EntityItemPickupEvent) && !(packet instanceof net.minecraft.network.play.server.SPacketCollectItem)); 
    return true;
  }
  
  public void onItemPickup(EntityItemPickupEvent event) {
    if (this.Antipickup.getValBoolean())
      event.setCanceled(true); 
  }
}
