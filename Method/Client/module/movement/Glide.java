package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.BlockUtils;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Wrapper;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Glide extends Module {
  Setting Mode;
  
  Setting Damage;
  
  Setting fallSpeed;
  
  Setting moveSpeed;
  
  Setting minHeight;
  
  Setting ypos;
  
  Setting ymotion;
  
  Setting MotionY;
  
  TimerUtils timer;
  
  public Glide() {
    super("Glide", 0, Category.MOVEMENT, "Glide");
    this.Mode = Main.setmgr.add(new Setting("Mode", this, "Falling", new String[] { "Falling", "Constant", "Flat", "ACC", "NCP", "Matrix", "Simple", "Randomadd" }));
    this.Damage = Main.setmgr.add(new Setting("Damage", this, false));
    this.fallSpeed = Main.setmgr.add(new Setting("fallSpeed", this, 0.25D, 0.005D, 0.25D, false, this.Mode, "Falling", 2));
    this.moveSpeed = Main.setmgr.add(new Setting("moveSpeed", this, 1.0D, 0.5D, 5.0D, false));
    this.minHeight = Main.setmgr.add(new Setting("minHeight", this, 0.0D, 0.0D, 2.0D, false, this.Mode, "Falling", 2));
    this.ypos = Main.setmgr.add(new Setting("ypos", this, 1.0D, 1.0D, 5.0D, false, this.Mode, "Randomadd", 2));
    this.ymotion = Main.setmgr.add(new Setting("ymotion", this, 1.0D, 1.0D, 5.0D, false, this.Mode, "Randomadd", 2));
    this.MotionY = Main.setmgr.add(new Setting("MotionY", this, 12.0D, 0.0D, 100.0D, false, this.Mode, "Constant", 2));
    this.timer = new TimerUtils();
  }
  
  public void onEnable() {
    if (this.Damage.getValBoolean()) {
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 6.0D, mc.player.posZ, true));
      mc.player.motionX *= 0.2D;
      mc.player.motionZ *= 0.2D;
      mc.player.swingArm(EnumHand.MAIN_HAND);
    } 
    if (this.Mode.getValString().equalsIgnoreCase("Flat"))
      mc.player.motionY = 0.19D; 
    super.onEnable();
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    EntityPlayerSP player = mc.player;
    if (this.Mode.getValString().equalsIgnoreCase("Randomadd") && mc.player.motionY < -0.01D && !mc.player.onGround) {
      double firstrandom = Math.random() / this.ymotion.getValDouble() / 10.0D;
      double secondrandom = Math.random() / this.ypos.getValDouble() / 10.0D;
      if (this.ymotion.getValDouble() > 0.0D)
        mc.player.motionY += firstrandom; 
      if (this.ypos.getValDouble() > 0.0D)
        mc.player.posY += secondrandom; 
    } 
    if (this.Mode.getValString().equalsIgnoreCase("Constant"))
      mc.player.motionY = -this.MotionY.getValDouble() / 60.0D; 
    if (this.Mode.getValString().equalsIgnoreCase("Simple")) {
      if (mc.gameSettings.keyBindJump.pressed)
        mc.player.setPosition(mc.player.posX, mc.player.posY + 0.009999999776482582D, mc.player.posZ); 
      mc.player.motionY = -0.10000000149011612D;
      if (mc.gameSettings.keyBindSneak.pressed)
        mc.player.setPosition(mc.player.posX, mc.player.posY - 0.5D, mc.player.posZ); 
    } 
    if (this.Mode.getValString().equalsIgnoreCase("Matrix") && 
      mc.player.fallDistance > 3.0F) {
      if (mc.player.ticksExisted % 3 == 0)
        mc.player.motionY = -0.1D; 
      if (mc.player.ticksExisted % 4 == 0)
        mc.player.motionY = -0.2D; 
    } 
    if (this.Mode.getValString().equalsIgnoreCase("NCP")) {
      mc.player.onGround = true;
      mc.player.capabilities.isFlying = true;
      tpRel(mc.player.motionX, mc.player.motionY = -0.0222D, mc.player.motionZ);
      tpPacket(mc.player.motionX, mc.player.motionY - 9.0D, mc.player.motionZ);
    } 
    if (this.Mode.getValString().equalsIgnoreCase("Flat")) {
      if (!player.capabilities.isFlying && player.fallDistance > 0.0F && !player.isSneaking())
        player.motionY = 0.0D; 
      if ((Wrapper.INSTANCE.mcSettings()).keyBindSneak.isKeyDown())
        player.motionY = -0.11D; 
      if ((Wrapper.INSTANCE.mcSettings()).keyBindJump.isKeyDown()) {
        ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        player.onGround = false;
      } 
      if (this.timer.delay(50.0F)) {
        player.onGround = false;
        this.timer.setLastMS();
      } 
    } 
    if (this.Mode.getValString().equalsIgnoreCase("ACC")) {
      if ((mc.player.motionY < 0.0D && mc.player.isAirBorne) || mc.player.onGround) {
        mc.player.motionY = -0.125D;
        mc.player.jumpMovementFactor *= 1.01227F;
        mc.player.noClip = true;
        mc.player.fallDistance = 0.0F;
        mc.player.onGround = true;
        mc.player.moveStrafing = (float)(mc.player.moveStrafing + 0.800000011920929D * this.moveSpeed.getValDouble());
        mc.player.jumpMovementFactor += 0.2F;
        mc.player.velocityChanged = true;
      } 
    } else if (this.Mode.getValString().equalsIgnoreCase("Falling")) {
      WorldClient worldClient = mc.world;
      if (!player.isAirBorne || player.isInWater() || player.isInLava() || player
        .isOnLadder() || player.motionY >= 0.0D)
        return; 
      if (this.minHeight.getValDouble() > 0.0D) {
        AxisAlignedBB box = player.getEntityBoundingBox();
        box = box.union(box.offset(0.0D, -this.minHeight.getValDouble(), 0.0D));
        if (worldClient.collidesWithAnyBlock(box))
          return; 
        BlockPos min = new BlockPos(new Vec3d(box.minX, box.minY, box.minZ));
        BlockPos max = new BlockPos(new Vec3d(box.maxX, box.maxY, box.maxZ));
        Stream<BlockPos> stream = StreamSupport.stream(BlockPos.getAllInBox(min, max).spliterator(), true);
        if (stream.map(BlockUtils::getBlock)
          .anyMatch(b -> b instanceof net.minecraft.block.BlockLiquid))
          return; 
      } 
      player.motionY = Math.max(player.motionY, -this.fallSpeed.getValDouble());
      player.jumpMovementFactor = (float)(player.jumpMovementFactor * this.moveSpeed.getValDouble());
    } 
    super.onClientTick(event);
  }
  
  public static void tpRel(double x, double y, double z) {
    EntityPlayerSP player = mc.player;
    player.setPosition(player.posX + x, player.posY + y, player.posZ + z);
  }
  
  public static void tpPacket(double x, double y, double z) {
    EntityPlayerSP player = mc.player;
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(player.posX + x, player.posY + y, player.posZ + z, false));
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(player.posX, player.posY, player.posZ, false));
    Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(player.posX, player.posY, player.posZ, true));
  }
}
