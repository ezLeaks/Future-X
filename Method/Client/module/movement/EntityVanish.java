package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import java.util.Arrays;
import java.util.OptionalInt;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EntityVanish extends Module {
  Setting noDismountPlugin = Main.setmgr.add(new Setting("no Dismount Plugin", this, true));
  
  Setting dismountEntity = Main.setmgr.add(new Setting("dismoun tEntity", this, true));
  
  Setting removeEntity = Main.setmgr.add(new Setting("remove Entity", this, true));
  
  Setting respawnEntity = Main.setmgr.add(new Setting("respawn Entity", this, true));
  
  Setting sendMovePackets = Main.setmgr.add(new Setting("send Move Packets", this, true));
  
  Setting forceOnGround = Main.setmgr.add(new Setting("force On Ground", this, true));
  
  Setting setMountPosition = Main.setmgr.add(new Setting("set MountPosition", this, true));
  
  private Entity originalRidingEntity;
  
  public EntityVanish() {
    super("EntityVanish", 0, Category.MOVEMENT, "Entity Vanish");
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.IN) {
      if (packet instanceof SPacketSetPassengers && 
        hasOriginalRidingEntity() && (Minecraft.getMinecraft()).world != null) {
        SPacketSetPassengers packetSetPassengers = (SPacketSetPassengers)packet;
        if (this.originalRidingEntity.equals((Minecraft.getMinecraft()).world.getEntityByID(packetSetPassengers.getEntityId()))) {
          OptionalInt isPlayerAPassenger = Arrays.stream(packetSetPassengers.getPassengerIds()).filter(value -> ((Minecraft.getMinecraft()).world.getEntityByID(value) == (Minecraft.getMinecraft()).player)).findAny();
          if (!isPlayerAPassenger.isPresent()) {
            ChatUtils.message("You Have Been Dismounted.");
            toggle();
          } 
        } 
      } 
      if (packet instanceof SPacketDestroyEntities) {
        SPacketDestroyEntities packetDestroyEntities = (SPacketDestroyEntities)packet;
        boolean isEntityNull = Arrays.stream(packetDestroyEntities.getEntityIDs()).filter(value -> (value == this.originalRidingEntity.getEntityId())).findAny().isPresent();
        if (isEntityNull)
          ChatUtils.message("Your riding entity has been destroyed."); 
      } 
    } 
    if (side == Connection.Side.OUT && 
      this.noDismountPlugin.getValBoolean()) {
      if (packet instanceof CPacketPlayer.Position) {
        CPacketPlayer.Position cPacketPlayer = (CPacketPlayer.Position)packet;
        (Minecraft.getMinecraft()).player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(cPacketPlayer.x, cPacketPlayer.y, cPacketPlayer.z, cPacketPlayer.yaw, cPacketPlayer.pitch, cPacketPlayer.onGround));
        return false;
      } 
      return (!(packet instanceof CPacketPlayer) || packet instanceof CPacketPlayer.PositionRotation);
    } 
    return true;
  }
  
  public void onEnable() {
    super.onEnable();
    this.originalRidingEntity = null;
    Minecraft mc = Minecraft.getMinecraft();
    if (mc.player != null && mc.world != null)
      if (mc.player.isRiding()) {
        this.originalRidingEntity = mc.player.getRidingEntity();
        if (this.dismountEntity.getValBoolean()) {
          mc.player.dismountRidingEntity();
          ChatUtils.message("Dismounted entity.");
        } 
        if (this.removeEntity.getValBoolean()) {
          mc.world.removeEntity(this.originalRidingEntity);
          ChatUtils.message("Removed entity from world.");
        } 
      } else {
        ChatUtils.message("Please mount an entity before enabling this module.");
        toggle();
      }  
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (mc.world != null && mc.player != null && 
      !mc.player.isRiding() && hasOriginalRidingEntity()) {
      if (this.forceOnGround.getValBoolean())
        mc.player.onGround = true; 
      if (this.setMountPosition.getValBoolean())
        this.originalRidingEntity.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ); 
      if (this.sendMovePackets.getValBoolean())
        mc.player.connection.sendPacket((Packet)new CPacketVehicleMove(this.originalRidingEntity)); 
    } 
  }
  
  public void onDisable() {
    super.onDisable();
    if (hasOriginalRidingEntity()) {
      Minecraft mc = Minecraft.getMinecraft();
      if (this.respawnEntity.getValBoolean())
        this.originalRidingEntity.isDead = false; 
      if (!mc.player.isRiding()) {
        mc.world.spawnEntity(this.originalRidingEntity);
        mc.player.startRiding(this.originalRidingEntity, true);
        ChatUtils.message("Spawned & mounted original entity.");
      } 
      this.originalRidingEntity = null;
    } 
  }
  
  private boolean hasOriginalRidingEntity() {
    return (this.originalRidingEntity != null);
  }
}
