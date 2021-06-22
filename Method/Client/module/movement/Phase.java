package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.event.entity.living.LivingEvent;

public class Phase extends Module {
  Setting mode = Main.setmgr.add(new Setting("Phase Mode", this, "Noclip", new String[] { 
          "Noclip", "Simple", "Destroy", "Glitch", "VClip", "NCPDEV", "HFC", "WinterLithe", "Sand", "Packet", 
          "Skip", "Sneak", "Dpacket" }));
  
  Setting speed = Main.setmgr.add(new Setting("speed", this, 10.0D, 0.1D, 2.0D, false));
  
  Setting packets = Main.setmgr.add(new Setting("packets", this, 5.0D, 1.0D, 10.0D, true));
  
  private int resetNext;
  
  public static boolean Finalsep = false;
  
  public int vanillastage;
  
  public Phase() {
    super("Phase", 0, Category.MOVEMENT, "Phase through blocks");
  }
  
  public void onEnable() {
    Finalsep = false;
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
    mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
    mc.player.noClip = true;
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.mode.getValString().equalsIgnoreCase("Dpacket") && side == Connection.Side.OUT && 
      mc.player.collidedHorizontally && packet instanceof CPacketPlayer.Position) {
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.08D, mc.player.posZ, mc.player.onGround));
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.08D, mc.player.posZ, mc.player.onGround));
      double var2 = -Math.sin(Math.toRadians(mc.player.rotationYaw));
      double var4 = Math.cos(Math.toRadians(mc.player.rotationYaw));
      double var6 = mc.player.movementInput.moveForward * var2 + mc.player.movementInput.moveStrafe * var4;
      double var8 = mc.player.movementInput.moveForward * var4 - mc.player.movementInput.moveStrafe * var2;
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + var6, mc.player.posY, mc.player.posZ + var8, false));
      return false;
    } 
    return (!(packet instanceof CPacketPlayer) || packet instanceof CPacketPlayer.Position || !this.mode.getValString().equalsIgnoreCase("noclip"));
  }
  
  public void onDisable() {
    this.vanillastage = 0;
    Finalsep = false;
    mc.player.motionY = 0.0D;
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
    mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
    mc.player.noClip = false;
    mc.player.capabilities.isFlying = false;
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Simple")) {
      MC.player.noClip = true;
      MC.player.fallDistance = 0.0F;
      MC.player.onGround = false;
      MC.player.jumpMovementFactor = 0.32F;
      if (MC.gameSettings.keyBindJump.isKeyDown())
        MC.player.motionY += 0.3199999928474426D; 
      if (MC.gameSettings.keyBindSneak.isKeyDown())
        MC.player.motionY -= 0.3199999928474426D; 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Sneak")) {
      mc.player.fallDistance = 0.0F;
      mc.player.onGround = true;
      if (mc.gameSettings.keyBindJump.isPressed()) {
        mc.player.setVelocity(mc.player.motionX, 0.1D, mc.player.motionZ);
      } else if (mc.gameSettings.keyBindSneak.isPressed()) {
        mc.player.addVelocity(0.0D, -0.1D, 0.0D);
      } else {
        mc.player.setVelocity(mc.player.motionX, 0.0D, mc.player.motionZ);
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("noclip")) {
      if (mc.player != null)
        mc.player.noClip = false; 
      if (mc.world != null && event.getEntity() == mc.player) {
        mc.player.noClip = true;
        mc.player.onGround = false;
        mc.player.fallDistance = 0.0F;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("SAND") && 
      mc.gameSettings.keyBindJump.isKeyDown() && 
      mc.player.getRidingEntity() != null && mc.player.getRidingEntity() instanceof EntityBoat) {
      EntityBoat boat = (EntityBoat)mc.player.getRidingEntity();
      if (boat.onGround)
        boat.motionY = 0.41999998688697815D; 
    } 
    if (this.mode.getValString().equalsIgnoreCase("PACKET")) {
      Vec3d dir = direction(mc.player.rotationYaw);
      if (mc.player.onGround && mc.player.collidedHorizontally) {
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + dir.x * 9.999999747378752E-6D, mc.player.posY, mc.player.posZ + dir.z * 9.999999747378752E-5D, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + dir.x * 2.0D, mc.player.posY, mc.player.posZ + dir.z * 2.0D, mc.player.onGround));
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("SKIP")) {
      Vec3d dir = direction(mc.player.rotationYaw);
      if (mc.player.onGround && mc.player.collidedHorizontally) {
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + dir.x * 0.0010000000474974513D, mc.player.posY + 0.10000000149011612D, mc.player.posZ + dir.z * 0.0010000000474974513D, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + dir.x * 0.029999999329447746D, 0.0D, mc.player.posZ + dir.z * 0.029999999329447746D, mc.player.onGround));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + dir.x * 0.05999999865889549D, mc.player.posY, mc.player.posZ + dir.z * 0.05999999865889549D, mc.player.onGround));
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("NCPDEV")) {
      mc.player.noClip = true;
      mc.player.setPosition(mc.player.posX, mc.player.posY - 1.0E-5D, mc.player.posZ);
      mc.player.motionY = -10.0D;
    } 
    if (this.mode.getValString().equalsIgnoreCase("Glitch")) {
      mc.player.noClip = true;
      EnumFacing var4 = mc.player.getHorizontalFacing();
      mc.player.motionY = 0.0D;
      if (mc.player.movementInput.jump)
        mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ); 
      if (mc.player.movementInput.sneak) {
        mc.player.setPosition(mc.player.posX, mc.player.posY - 1.0E-5D, mc.player.posZ);
        mc.player.motionY = -1000.0D;
      } 
      if (Utils.isMoving((Entity)mc.player)) {
        if (var4.getName().equals(EnumFacing.NORTH.getName())) {
          mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ - 0.001D);
          if (isInsideBlock() && mc.player.ticksExisted % 10 == 0) {
            mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ - 0.2D);
            double[] directionSpeedVanilla = Utils.directionSpeed(1.5D);
            mc.player.motionX = directionSpeedVanilla[0];
            mc.player.motionZ = directionSpeedVanilla[1];
          } 
        } 
        if (var4.getName().equals(EnumFacing.SOUTH.getName())) {
          mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ + 0.001D);
          if (isInsideBlock() && mc.player.ticksExisted % 10 == 0) {
            mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY, mc.player.posZ + 0.2D);
            double[] directionSpeedVanilla = Utils.directionSpeed(1.5D);
            mc.player.motionX = directionSpeedVanilla[0];
            mc.player.motionZ = directionSpeedVanilla[1];
          } 
        } 
        if (var4.getName().equals(EnumFacing.WEST.getName())) {
          mc.player.setPositionAndUpdate(mc.player.posX - 0.001D, mc.player.posY, mc.player.posZ);
          if (isInsideBlock() && mc.player.ticksExisted % 10 == 0) {
            mc.player.setPositionAndUpdate(mc.player.posX - 0.2D, mc.player.posY, mc.player.posZ);
            double[] directionSpeedVanilla = Utils.directionSpeed(1.5D);
            mc.player.motionX = directionSpeedVanilla[0];
            mc.player.motionZ = directionSpeedVanilla[1];
          } 
        } 
        if (var4.getName().equals(EnumFacing.EAST.getName())) {
          mc.player.setPositionAndUpdate(mc.player.posX + 0.001D, mc.player.posY, mc.player.posZ);
          if (isInsideBlock() && mc.player.ticksExisted % 10 == 0) {
            mc.player.setPositionAndUpdate(mc.player.posX + 0.2D, mc.player.posY, mc.player.posZ);
            double[] directionSpeedVanilla = Utils.directionSpeed(1.5D);
            mc.player.motionX = directionSpeedVanilla[0];
            mc.player.motionZ = directionSpeedVanilla[1];
          } 
        } 
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Sand")) {
      double d1 = mc.player.posX;
      double y = mc.player.posY - 3.0D;
      double z = mc.player.posZ;
      if (this.vanillastage == 0) {
        int i;
        for (i = 0; i < 100; i++) {
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(d1, mc.player.posY - 1.0001D, z, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
          mc.player.setPosition(d1, mc.player.posY - 1.0001D, z);
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
        } 
        for (i = 0; i < 10; i++) {
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(d1, y, z, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
          mc.player.setPosition(d1, y, z);
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.PositionRotation(d1, y, z, mc.player.rotationYaw, mc.player.rotationPitch, mc.player.onGround));
          mc.player.setPosition(d1, y, z);
        } 
        this.vanillastage++;
      } 
      if (this.vanillastage > 0) {
        mc.player.noClip = true;
        Finalsep = true;
      } 
    } 
    double var1 = -this.speed.getValDouble();
    if (this.mode.getValString().equalsIgnoreCase("HFC")) {
      double[] directionSpeedVanilla = Utils.directionSpeed(this.speed.getValDouble());
      mc.player.motionX = directionSpeedVanilla[0];
      mc.player.motionZ = directionSpeedVanilla[1];
      EnumFacing var4 = mc.player.getHorizontalFacing();
      if (Utils.isMoving((Entity)mc.player)) {
        if (!mc.player.onGround && !isInsideBlock() && mc.player.isSneaking()) {
          mc.player.motionY = 0.0D;
        } else {
          mc.player.motionY = 100000.0D;
        } 
        if (mc.player.ticksExisted % 2 == 0) {
          if (var4.getName().equals(EnumFacing.NORTH.getName()))
            mc.player.getEntityBoundingBox().offset(0.0D, 0.0D, -3.0D); 
          if (var4.getName().equals(EnumFacing.SOUTH.getName()))
            mc.player.getEntityBoundingBox().offset(0.0D, 0.0D, 3.0D); 
          if (var4.getName().equals(EnumFacing.WEST.getName()))
            mc.player.getEntityBoundingBox().offset(-3.0D, 0.0D, 0.0D); 
          if (var4.getName().equals(EnumFacing.EAST.getName()))
            mc.player.getEntityBoundingBox().offset(3.0D, 0.0D, 0.0D); 
          for (int i = 0; i < (int)this.packets.getValDouble(); i++) {
            if (var4.getName().equals(EnumFacing.NORTH.getName()))
              Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ - 3.0D, true)); 
            if (var4.getName().equals(EnumFacing.SOUTH.getName()))
              Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ + 3.0D, true)); 
            if (var4.getName().equals(EnumFacing.WEST.getName()))
              Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX - 3.0D, mc.player.posY, mc.player.posZ, true)); 
            if (var4.getName().equals(EnumFacing.EAST.getName()))
              Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + 3.0D, mc.player.posY, mc.player.posZ, true)); 
          } 
        } else {
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(5000.0D, mc.player.posY, 5000.0D, true));
        } 
      } 
      mc.player.noClip = true;
    } else if (this.mode.getValString().equalsIgnoreCase("WinterLithe")) {
      if ((isInsideBlock() && mc.gameSettings.keyBindJump.pressed) || (!isInsideBlock() && mc.player.getCollisionBoundingBox() != null && (mc.player.getCollisionBoundingBox()).maxY > (mc.player.getCollisionBoundingBox()).minY && mc.player.isSneaking() && mc.player.collidedHorizontally)) {
        this.resetNext--;
        double mx = Math.cos(Math.toRadians((mc.player.rotationYaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((mc.player.rotationYaw + 90.0F)));
        double xOff = mc.player.moveForward * 1.2D * mx + mc.player.moveForward * 1.2D * mz;
        double zOff = mc.player.moveForward * 1.2D * mz - mc.player.moveForward * 1.2D * mx;
        if (isInsideBlock())
          this.resetNext = 1; 
        if (this.resetNext > 0)
          mc.player.getEntityBoundingBox().offset(xOff, 0.0D, zOff); 
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("Vclip")) {
      if (mc.gameSettings.keyBindJump.pressed) {
        mc.player.noClip = true;
        mc.player.setPosition(mc.player.posX, mc.player.posY + this.speed.getValDouble(), mc.player.posZ);
        mc.player.motionY = this.speed.getValDouble();
      } 
      if (mc.gameSettings.keyBindSneak.pressed) {
        mc.player.noClip = true;
        mc.player.setPosition(mc.player.posX, mc.player.posY + var1, mc.player.posZ);
        mc.player.motionY = var1;
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("MotionY") && 
      Utils.isMoving((Entity)mc.player)) {
      EnumFacing var4 = mc.player.getHorizontalFacing();
      if (var4.getName().equals(EnumFacing.NORTH.getName()))
        mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ - 3.0D); 
      if (var4.getName().equals(EnumFacing.SOUTH.getName()))
        mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ + 3.0D); 
      if (var4.getName().equals(EnumFacing.WEST.getName()))
        mc.player.setPosition(mc.player.posX - 3.0D, mc.player.posY, mc.player.posZ); 
      if (var4.getName().equals(EnumFacing.EAST.getName()))
        mc.player.setPosition(mc.player.posX + 3.0D, mc.player.posY, mc.player.posZ); 
      mc.player.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
      mc.player.motionY = -1000.0D;
      mc.player.noClip = true;
      double[] directionSpeedVanilla = Utils.directionSpeed(this.speed.getValDouble());
      mc.player.motionX = directionSpeedVanilla[0];
      mc.player.motionZ = directionSpeedVanilla[1];
    } 
    if (this.mode.getValString().equalsIgnoreCase("destroy")) {
      double[] dir = Utils.directionSpeed(1.0D);
      if (mc.player.collidedHorizontally) {
        mc.world.destroyBlock(new BlockPos(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1]), false);
        mc.world.destroyBlock(new BlockPos(mc.player.posX + dir[0], mc.player.posY + 1.0D, mc.player.posZ + dir[1]), false);
      } 
      if (Utils.isMoving((Entity)mc.player) && mc.player.onGround) {
        double[] directionSpeedVanilla = Utils.directionSpeed(0.23000000298023224D);
        mc.player.motionX = directionSpeedVanilla[0];
        mc.player.motionZ = directionSpeedVanilla[1];
      } 
    } 
  }
  
  public boolean canPhase() {
    return (!mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isInLava());
  }
  
  public static boolean isInsideBlock() {
    for (int x = (int)((AxisAlignedBB)Objects.requireNonNull((T)mc.player.getCollisionBoundingBox())).minX; x < (mc.player.getCollisionBoundingBox()).maxX + 1.0D; x++) {
      for (int y = (int)(mc.player.getCollisionBoundingBox()).minY; y < (mc.player.getCollisionBoundingBox()).maxY + 1.0D; y++) {
        for (int z = (int)(mc.player.getCollisionBoundingBox()).minZ; z < (mc.player.getCollisionBoundingBox()).maxZ + 1.0D; z++) {
          Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
          if (!(block instanceof net.minecraft.block.BlockAir)) {
            AxisAlignedBB boundingBox;
            if ((boundingBox = block.getCollisionBoundingBox(mc.world.getBlockState(new BlockPos(x, y, z)), (IBlockAccess)mc.world, new BlockPos(x, y, z))) != null && mc.player.getCollisionBoundingBox().intersects(boundingBox))
              return true; 
          } 
        } 
      } 
    } 
    return false;
  }
  
  public static Vec3d direction(float yaw) {
    return new Vec3d(Math.cos(degToRad((yaw + 90.0F))), 0.0D, Math.sin(degToRad((yaw + 90.0F))));
  }
  
  public static double degToRad(double deg) {
    return deg * 0.01745329238474369D;
  }
}
