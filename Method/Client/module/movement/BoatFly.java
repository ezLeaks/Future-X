package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BoatFly extends Module {
  Setting Gravity;
  
  Setting AllEntities;
  
  Setting mode;
  
  Setting FakePackets;
  
  Setting BoatClip;
  
  Setting BoatClipSpeed;
  
  Setting bypass;
  
  Setting Tickdelay;
  
  Setting UpYmotion;
  
  Setting DownYmotion;
  
  Setting PlaceBypass;
  
  Setting ComplexMotion;
  
  Setting ignoreVehicleMove;
  
  Setting NoKick;
  
  Setting ignorePlayerPosRot;
  
  Setting Fakerotdist;
  
  Setting Ncptoggle;
  
  Setting PacketJump;
  
  public static Setting BoatRender;
  
  public static Setting Boatblend;
  
  double FakerotX;
  
  double FakerotZ;
  
  boolean aBoolean;
  
  String updatetexture;
  
  int tpId;
  
  private int PacketLazyTimer;
  
  int ClipLazyTimer;
  
  public BoatFly() {
    super("BoatFly", 0, Category.MOVEMENT, "Boat Fly");
    this.Gravity = Main.setmgr.add(new Setting("Gravity", this, true));
    this.AllEntities = Main.setmgr.add(new Setting("All Entities", this, true));
    this.mode = Main.setmgr.add(new Setting("Boat Mode", this, "Vanilla", new String[] { "Vanilla", "Fast", "Packet" }));
    this.FakePackets = Main.setmgr.add(new Setting("Fake Packet Spam", this, false));
    this.BoatClip = Main.setmgr.add(new Setting("BoatClip", this, "None", new String[] { "None", "Vanilla", "Fast" }));
    this.BoatClipSpeed = Main.setmgr.add(new Setting("BoatClipSpeed", this, 1.0D, 0.5D, 5.0D, false, this.BoatClip, 13));
    this.bypass = Main.setmgr.add(new Setting("bypass Mode", this, "None", new String[] { "Packet", "Vanilla", "None" }));
    this.Tickdelay = Main.setmgr.add(new Setting("Tickdelay", this, 1.0D, 0.0D, 20.0D, true, this.bypass, "Packet", 13));
    this.UpYmotion = Main.setmgr.add(new Setting("UpYmotion", this, 0.2D, 0.10000000149011612D, 2.0D, false));
    this.DownYmotion = Main.setmgr.add(new Setting("Fallmotion", this, 0.1D, 0.0D, 2.0D, false));
    this.PlaceBypass = Main.setmgr.add(new Setting("PlaceBypass", this, true));
    this.ComplexMotion = Main.setmgr.add(new Setting("Complex Y Motion", this, true));
    this.ignoreVehicleMove = Main.setmgr.add(new Setting("No Boat Motion", this, false));
    this.NoKick = Main.setmgr.add(new Setting("NoKick", this, false));
    this.ignorePlayerPosRot = Main.setmgr.add(new Setting("No Player Rotation", this, false));
    this.Fakerotdist = Main.setmgr.add(new Setting("Fakerotdist", this, 1.0D, 0.5D, 10.0D, false, this.ignorePlayerPosRot, 14));
    this.Ncptoggle = Main.setmgr.add(new Setting("Ncptoggle", this, true, this.bypass, "Packet", 13));
    this.PacketJump = Main.setmgr.add(new Setting("PacketJump", this, false));
    this.aBoolean = false;
    this.updatetexture = "NULL";
    this.tpId = 0;
    this.ClipLazyTimer = 0;
  }
  
  public static ResourceLocation[] BOAT_TEXTURES = new ResourceLocation[] { new ResourceLocation("textures/entity/boat/boat_oak.png"), new ResourceLocation("textures/entity/boat/boat_spruce.png"), new ResourceLocation("textures/entity/boat/boat_birch.png"), new ResourceLocation("textures/entity/boat/boat_jungle.png"), new ResourceLocation("textures/entity/boat/boat_acacia.png"), new ResourceLocation("textures/entity/boat/boat_darkoak.png") };
  
  public void setup() {
    Main.setmgr.add(BoatRender = new Setting("Render", this, "Defualt", new String[] { "Defualt", "Vanish", "Rainbow", "Carpet" }));
    Main.setmgr.add(Boatblend = new Setting("Boatblend", this, false));
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && this.PlaceBypass.getValBoolean() && ((
      packet instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBoat) || mc.player.getHeldItemOffhand().getItem() instanceof net.minecraft.item.ItemBoat)) {
      mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
      return false;
    } 
    if (mc.player.getRidingEntity() instanceof net.minecraft.entity.item.EntityBoat || (this.AllEntities.getValBoolean() && mc.player.getRidingEntity() != null)) {
      Entity e = mc.player.getRidingEntity();
      if (e == null)
        return true; 
      if (side == Connection.Side.OUT) {
        if (this.mode.getValString().equalsIgnoreCase("Packet") && 
          !(packet instanceof CPacketVehicleMove) && !(packet instanceof CPacketSteerBoat) && !(packet instanceof CPacketPlayer) && 
          packet instanceof CPacketEntityAction) {
          CPacketEntityAction.Action Getaction = ((CPacketEntityAction)packet).getAction();
          if (Getaction != CPacketEntityAction.Action.OPEN_INVENTORY)
            return false; 
        } 
        if (this.bypass.getValString().equalsIgnoreCase("Packet") && mc.player != null)
          if (this.Ncptoggle.getValBoolean()) {
            if (packet instanceof net.minecraft.network.play.client.CPacketInput && !mc.gameSettings.keyBindSprint.isKeyDown() && !(mc.player.getRidingEntity()).onGround) {
              this.PacketLazyTimer++;
              if (this.PacketLazyTimer > this.Tickdelay.getValDouble()) {
                this.PacketLazyTimer = 0;
                mc.player.connection.sendPacket((Packet)new CPacketUseEntity(e, EnumHand.MAIN_HAND));
              } 
            } 
          } else if (packet instanceof CPacketVehicleMove && this.PacketLazyTimer++ >= this.Tickdelay.getValDouble()) {
            mc.player.connection.sendPacket((Packet)new CPacketUseEntity(e, EnumHand.MAIN_HAND));
            this.PacketLazyTimer = 0;
          } else if (packet instanceof CPacketPlayer.Rotation || packet instanceof net.minecraft.network.play.client.CPacketInput) {
            return false;
          }  
        if (this.ignorePlayerPosRot.getValBoolean() && 
          mc.player.ticksExisted % 5 == 0) {
          MC.player.connection.sendPacket((Packet)new CPacketUseEntity(mc.player.getRidingEntity(), EnumHand.OFF_HAND));
          MC.player.connection.sendPacket((Packet)new CPacketVehicleMove(mc.player.getRidingEntity()));
          MC.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, MC.player.onGround));
          MC.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.tpId));
        } 
        if (this.bypass.getValString().equalsIgnoreCase("Vanilla")) {
          if (packet instanceof CPacketVehicleMove) {
            CPacketVehicleMove cPacketVehicleMove = (CPacketVehicleMove)packet;
            cPacketVehicleMove.pitch = (mc.player.getRidingEntity()).prevRotationPitch;
            cPacketVehicleMove.yaw = (mc.player.getRidingEntity()).prevRotationYaw;
            cPacketVehicleMove.x = Double.parseDouble(null);
            cPacketVehicleMove.y = Double.parseDouble(null);
            cPacketVehicleMove.z = Double.parseDouble(null);
          } 
          if (packet instanceof CPacketSteerBoat) {
            mc.player.motionY = 0.0D;
            e.isAirBorne = false;
            return false;
          } 
        } 
      } 
      if (side == Connection.Side.IN) {
        if (this.ignoreVehicleMove.getValBoolean()) {
          if (packet instanceof SPacketEntityVelocity) {
            Entity entity = mc.world.getEntityByID(((SPacketEntityVelocity)packet).getEntityID());
            if (entity == mc.player || entity == mc.player.getRidingEntity())
              return false; 
          } 
          if (packet instanceof SPacketEntity) {
            Entity entity = ((SPacketEntity)packet).getEntity((World)mc.world);
            if (entity == mc.player || entity == mc.player.getRidingEntity())
              return false; 
          } 
          if (packet instanceof SPacketEntityHeadLook) {
            Entity entity = ((SPacketEntityHeadLook)packet).getEntity((World)mc.world);
            if (entity == mc.player || entity == mc.player.getRidingEntity())
              return false; 
          } 
          if (packet instanceof SPacketEntityTeleport) {
            Entity entity = mc.world.getEntityByID(((SPacketEntityTeleport)packet).getEntityId());
            if (entity == mc.player || entity == mc.player.getRidingEntity())
              return false; 
          } 
          if (packet instanceof SPacketMoveVehicle && mc.player.getRidingEntity() instanceof net.minecraft.entity.item.EntityBoat)
            return false; 
        } 
        if (packet instanceof SPacketPlayerPosLook && this.ignorePlayerPosRot.getValBoolean()) {
          SPacketPlayerPosLook pp = (SPacketPlayerPosLook)packet;
          this.tpId = pp.getTeleportId();
          double d = Math.sqrt(Math.pow(this.FakerotX - pp.getX(), 2.0D) + Math.pow(this.FakerotZ - pp.getZ(), 2.0D));
          if (d >= this.Fakerotdist.getValDouble()) {
            respondToPosLook(packet);
            this.FakerotX = pp.getX();
            this.FakerotZ = pp.getZ();
            return false;
          } 
          if (mc.player.isRiding() && isBorderingChunk(mc.player.getRidingEntity(), 0.0D, 0.0D)) {
            respondToPosLook(packet);
            this.FakerotX = pp.getX();
            this.FakerotZ = pp.getZ();
            return false;
          } 
          return false;
        } 
        if (this.bypass.getValString().equalsIgnoreCase("Packet") && (
          packet instanceof SPacketMoveVehicle || packet instanceof SPacketPlayerPosLook))
          return false; 
        if (this.mode.getValString().equalsIgnoreCase("Packet")) {
          if (packet instanceof SPacketMoveVehicle) {
            SPacketMoveVehicle VehicleMove = (SPacketMoveVehicle)packet;
            return (mc.player.getDistance(VehicleMove.getX(), VehicleMove.getY(), VehicleMove.getZ()) > 15.0D);
          } 
          if (packet instanceof SPacketSetPassengers) {
            SPacketSetPassengers Setpassengers = (SPacketSetPassengers)packet;
            if (Setpassengers.getEntityId() == e.getEntityId()) {
              int[] passengerIds = Setpassengers.getPassengerIds();
              for (int i : passengerIds) {
                if (i != mc.player.getEntityId() && 
                  mc.player.isEntityAlive() && 
                  mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), false) && 
                  !(mc.currentScreen instanceof net.minecraft.client.gui.GuiDownloadTerrain) && 
                  !mc.player.isRiding())
                  return false; 
              } 
            } 
          } 
        } 
      } 
    } 
    return true;
  }
  
  public void onDisable() {
    if (mc.player.getRidingEntity() instanceof net.minecraft.entity.item.EntityBoat || (this.AllEntities.getValBoolean() && mc.player.getRidingEntity() != null)) {
      if (this.Gravity.getValBoolean() && mc.player.getRidingEntity().hasNoGravity())
        mc.player.getRidingEntity().setNoGravity(false); 
      (mc.player.getRidingEntity()).noClip = false;
    } 
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (!BoatRender.getValString().equalsIgnoreCase(this.updatetexture)) {
      this.updatetexture = BoatRender.getValString();
      if (BoatRender.getValString().equalsIgnoreCase("Defualt"))
        RenderBoat.BOAT_TEXTURES = BOAT_TEXTURES; 
      if (BoatRender.getValString().equalsIgnoreCase("Carpet"))
        RenderBoat.BOAT_TEXTURES = new ResourceLocation[] { new ResourceLocation("futurex", "carpet.png"), new ResourceLocation("futurex", "carpet.png"), new ResourceLocation("futurex", "carpet.png"), new ResourceLocation("futurex", "carpet.png"), new ResourceLocation("futurex", "carpet.png"), new ResourceLocation("futurex", "carpet.png") }; 
    } 
    if (mc.player.getRidingEntity() instanceof net.minecraft.entity.item.EntityBoat || (this.AllEntities.getValBoolean() && mc.player.getRidingEntity() != null)) {
      Entity e = mc.player.getRidingEntity();
      if (e == null)
        return; 
      e.setNoGravity(this.Gravity.getValBoolean());
      e.inWater = true;
      e.isAirBorne = false;
      if (this.mode.getValString().equalsIgnoreCase("Fast")) {
        double[] directionSpeedVanilla = Utils.directionSpeed(0.20000000298023224D);
        e.motionX = directionSpeedVanilla[0];
        e.motionZ = directionSpeedVanilla[1];
        mc.player.motionY = 0.0D;
        e.motionY = 0.0D;
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(e.posX + e.motionX, e.posY, e.posZ + e.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
        e.motionY = 0.0D;
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(e.posX + e.motionX, e.posY - 2.0D, e.posZ + e.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
        e.posY -= 0.2D;
      } 
      if (this.ComplexMotion.getValBoolean()) {
        e
          
          .motionY = mc.gameSettings.keyBindSprint.pressed ? -this.UpYmotion.getValDouble() : ((mc.player.ticksExisted % 2 != 0) ? (-this.DownYmotion.getValDouble() / 10.0D) : (mc.gameSettings.keyBindJump.pressed ? getUpyMotion() : (this.DownYmotion.getValDouble() / 10.0D)));
      } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
        e.motionY += getUpyMotion() / 20.0D;
      } else if (mc.gameSettings.keyBindSprint.isKeyDown()) {
        e.motionY -= this.DownYmotion.getValDouble() / 20.0D;
      } else {
        e.motionY = 0.0D;
      } 
      if (this.NoKick.getValBoolean())
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
          if (mc.player.ticksExisted % 8 < 2)
            (mc.player.getRidingEntity()).motionY = -0.03999999910593033D; 
        } else if (mc.player.ticksExisted % 8 < 4) {
          (mc.player.getRidingEntity()).motionY = -0.07999999821186066D;
        }  
      if (this.FakePackets.getValBoolean())
        FakePackets(); 
      if (this.BoatClip.getValString().equalsIgnoreCase("Vanilla")) {
        e.noClip = true;
        e.onGround = false;
        e.entityCollisionReduction = 1.0F;
      } 
      if (this.BoatClip.getValString().equalsIgnoreCase("Fast"))
        Boatclip(e); 
    } 
    super.onClientTick(event);
  }
  
  private double getUpyMotion() {
    if (this.PacketJump.getValBoolean())
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_RIDING_JUMP)); 
    return this.UpYmotion.getValDouble();
  }
  
  private void FakePackets() {
    mc.player.connection.sendPacket((Packet)new CPacketVehicleMove());
    mc.player.connection.sendPacket((Packet)new CPacketSteerBoat(true, true));
    mc.player.connection.sendPacket((Packet)new CPacketVehicleMove());
    mc.player.connection.sendPacket((Packet)new CPacketSteerBoat(true, true));
  }
  
  public void respondToPosLook(Object packet) {
    if (mc.world != null && mc.player != null && 
      packet instanceof SPacketPlayerPosLook && ((NetHandlerPlayClient)Objects.requireNonNull((T)mc.getConnection())).doneLoadingTerrain) {
      SPacketPlayerPosLook packetIn = (SPacketPlayerPosLook)packet;
      double d0 = packetIn.getX();
      double d2 = packetIn.getZ();
      if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.X))
        d0 += mc.player.posX; 
      if (packetIn.getFlags().contains(SPacketPlayerPosLook.EnumFlags.Z))
        d2 += mc.player.posZ; 
      mc.getConnection().sendPacket((Packet)new CPacketConfirmTeleport(packetIn.getTeleportId()));
      mc.getConnection().sendPacket((Packet)new CPacketPlayer.PositionRotation(d0, (mc.player.getEntityBoundingBox()).minY, d2, packetIn.yaw, packetIn.pitch, false));
    } 
  }
  
  private void Boatclip(Entity e) {
    CPacketVehicleMove packetVehicleMove = new CPacketVehicleMove(e);
    e.onGround = false;
    if (mc.gameSettings.keyBindBack.isKeyDown()) {
      packetVehicleMove.y = -2.0D;
      if (mc.player.posY > 0.0D) {
        mc.player.motionX -= getMotionX(mc.player.rotationYaw);
        mc.player.motionZ -= getMotionZ(mc.player.rotationYaw);
      } 
    } 
    Packet();
    this.aBoolean = !this.aBoolean;
    ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)packetVehicleMove);
    if (mc.player.posY < 0.0D) {
      this.ClipLazyTimer++;
    } else {
      this.ClipLazyTimer = 0;
    } 
    if (this.ClipLazyTimer > 20 && mc.player.posY < 0.0D) {
      this.ClipLazyTimer = 21;
      if (mc.player.isRiding())
        mc.player.dismountRidingEntity(); 
      double oldMotionX = mc.player.motionX;
      double oldMotionY = mc.player.motionY;
      double oldMotionZ = mc.player.motionZ;
      if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) && !mc.gameSettings.keyBindJump.isKeyDown()) {
        mc.player.motionX = getMotionX(mc.player.cameraYaw) * 0.26D;
        mc.player.motionZ = getMotionZ(mc.player.cameraYaw) * 0.26D;
      } 
      Packet();
      mc.getConnection().sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, 3.0D + mc.player.posY, mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
      mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
      mc.player.motionX = oldMotionX;
      mc.player.motionY = oldMotionY;
      mc.player.motionZ = oldMotionZ;
    } 
  }
  
  public boolean isBorderingChunk(Entity entity, double motX, double motZ) {
    return mc.world.getChunk((int)(entity.posX + motX) >> 4, (int)(entity.posZ + motZ) >> 4) instanceof net.minecraft.world.chunk.EmptyChunk;
  }
  
  private void Packet() {
    ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX + mc.player.motionX, mc.player.posY + (this.aBoolean ? 0.0625D : (mc.gameSettings.keyBindJump.isKeyDown() ? 0.0624D : 1.0E-8D)) - (this.aBoolean ? 0.0625D : (mc.gameSettings.keyBindSneak.isKeyDown() ? 0.0624D : 2.0E-8D)), mc.player.posZ + mc.player.motionZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
  }
  
  private double getMotionX(float yaw) {
    return MathHelper.sin(-yaw * 0.017453292F * 1.0F) * this.BoatClipSpeed.getValDouble();
  }
  
  private double getMotionZ(float yaw) {
    return (MathHelper.cos(yaw * 0.017453292F) * 1.0F) * this.BoatClipSpeed.getValDouble();
  }
}
