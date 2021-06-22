package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Jesus extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Solid", new String[] { "Solid", "BOUNCE", "FrostWalker", "BunnyHop", "Aac" }));
  
  Setting offset = Main.setmgr.add(new Setting("offset", this, 0.05D, 0.0D, 0.9D, false));
  
  Setting Blockdist = Main.setmgr.add(new Setting("Top Water", this, false, this.mode, "FrostWalker", 2));
  
  Setting NoDrown = Main.setmgr.add(new Setting("NoDrown", this, false));
  
  public TimerUtils Delayer = new TimerUtils();
  
  int noDown;
  
  int start;
  
  int cooldownSpeed;
  
  public Jesus() {
    super("Jesus", 0, Category.MOVEMENT, "Jesus, Walk on water");
  }
  
  public void onEnable() {
    this.noDown = 0;
  }
  
  public void onDisable() {
    this.noDown = 0;
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.NoDrown.getValBoolean())
      return (!(packet instanceof net.minecraft.network.play.client.CPacketPlayerAbilities) || !canSave()); 
    return true;
  }
  
  private boolean canSave() {
    boolean swinging = mc.player.isSwingInProgress;
    Vec3d prevmotion = new Vec3d(mc.player.motionX, mc.player.motionY, mc.player.motionZ);
    boolean moving = (prevmotion.x != 0.0D || !mc.player.collidedVertically || mc.gameSettings.keyBindJump.isPressed() || prevmotion.z != 0.0D);
    mc.player.inWater = false;
    return (mc.player.isInWater() && !swinging && !moving);
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Aac") && 
      mc.player.isInWater()) {
      this.start++;
      if (this.start < 4)
        return; 
      this.noDown++;
      this.cooldownSpeed++;
      mc.gameSettings.keyBindJump.pressed = (this.noDown < 2);
      mc.gameSettings.keyBindJump.pressed = true;
      if (this.noDown >= 3.5F)
        this.noDown = 0; 
      if (this.cooldownSpeed >= 3) {
        mc.player.motionX *= 1.1699999570846558D;
        mc.player.motionZ *= 1.1699999570846558D;
        this.cooldownSpeed = 0;
        mc.gameSettings.keyBindJump.pressed = false;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("BunnyHop") && 
      mc.player.isInWater()) {
      mc.player.jumpMovementFactor = 0.1F;
      mc.player.motionY = 0.42D;
    } 
    if (this.mode.getValString().equalsIgnoreCase("FrostWalker") && this.Delayer.isDelay(200L)) {
      for (BlockPos b : BlockPos.getAllInBox((int)mc.player.posX - 3, (int)mc.player.posY - 2, (int)mc.player.posZ - 3, (int)mc.player.posX + 3, (int)mc.player.posY + 2, (int)mc.player.posZ + 3)) {
        if ((mc.world.getBlockState(b).getBlock() != Blocks.WATER && mc.world.getBlockState(b).getBlock() != Blocks.FLOWING_WATER && mc.world.getBlockState(b).getBlock() != Blocks.LAVA && mc.world.getBlockState(b).getBlock() != Blocks.FLOWING_LAVA) || (
          this.Blockdist.getValBoolean() && mc.world.getBlockState(b.up()).getBlock() == Blocks.AIR))
          continue; 
        mc.world.setBlockState(b, Blocks.FROSTED_ICE.getDefaultState());
        mc.world.scheduleUpdate(b, Blocks.FROSTED_ICE, MathHelper.getInt(mc.player.getRNG(), 6, 12));
      } 
      this.Delayer.setLastMS();
    } 
    if (this.mode.getValString().equalsIgnoreCase("Solid")) {
      BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
      Block block = mc.world.getBlockState(blockPos).getBlock();
      if (block.getBlockState().getBlock() == Blocks.WATER || block.getBlockState().getBlock() == Blocks.FLOWING_WATER || block.getBlockState().getBlock() == Blocks.LAVA || block.getBlockState().getBlock() == Blocks.FLOWING_LAVA || mc.player.isInWater()) {
        mc.player.motionY = 0.0D;
        mc.player.jumpMovementFactor = 0.1F;
        if (mc.player.ticksExisted % 2 == 0) {
          mc.player.setPosition(mc.player.posX, mc.player.posY + 2.8471E-6D, mc.player.posZ);
        } else {
          mc.player.setPosition(mc.player.posX, mc.player.posY - 2.8471E-6D, mc.player.posZ);
        } 
        mc.player.onGround = true;
      } 
    } 
    super.onClientTick(event);
    if (this.mode.getValString().equalsIgnoreCase("BOUNCE") && 
      !mc.player.isSneaking() && !mc.player.noClip && 
      !mc.gameSettings.keyBindJump.isKeyDown() && isOnLiquid(this.offset.getValDouble()))
      mc.player.motionY = 0.10000000149011612D; 
  }
  
  public static boolean isOnLiquid(double offset) {
    if (mc.player.fallDistance >= 3.0F)
      return false; 
    AxisAlignedBB bb = (mc.player.getRidingEntity() != null) ? mc.player.getRidingEntity().getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(0.0D, -offset, 0.0D) : mc.player.getEntityBoundingBox().contract(0.0D, 0.0D, 0.0D).offset(0.0D, -offset, 0.0D);
    boolean onLiquid = false;
    int y = (int)bb.minY;
    for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
      for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
        Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
        if (block != Blocks.AIR) {
          if (!(block instanceof net.minecraft.block.BlockLiquid))
            return false; 
          onLiquid = true;
        } 
      } 
    } 
    return onLiquid;
  }
}
