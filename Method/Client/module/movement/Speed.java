package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Speed extends Module {
  public Setting mode;
  
  Setting Icespeed;
  
  Setting motiony;
  
  Setting groundmultiplier;
  
  Setting airmultiplier;
  
  Setting motionzp;
  
  int counter;
  
  public static double G;
  
  public boolean sink;
  
  public Speed() {
    super("Speed", 0, Category.PLAYER, "Speed");
    this.mode = Main.setmgr.add(new Setting("Speed Mode", this, "Jump", new String[] { "Jump", "Forward", "Ice", "GroundHop", "Y-Port", "MoveTry", "AAC", "Hypixel BHop", "AAC_ZOOM", "Packet" }));
    this.Icespeed = Main.setmgr.add(new Setting("Icespeed", this, 1.0D, 0.1D, 5.0D, false, this.mode, "Ice", 2));
    this.motiony = Main.setmgr.add(new Setting("motiony", this, 0.0D, 0.0D, 1.5D, false, this.mode, "Forward", 2));
    this.groundmultiplier = Main.setmgr.add(new Setting("groundmultiplier", this, 0.2D, 0.001D, 0.5D, false, this.mode, "Forward", 3));
    this.airmultiplier = Main.setmgr.add(new Setting("airmultiplier", this, 1.0064D, 1.0D, 1.1D, false, this.mode, "Forward", 4));
    this.motionzp = Main.setmgr.add(new Setting("motionzp", this, 1.0D, 0.5D, 4.0D, false, this.mode, "Packet", 2));
    this.sink = false;
  }
  
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("packet") && 
      Utils.isMoving((Entity)mc.player) && mc.player.onGround) {
      boolean step = ModuleManager.getModuleByName("step").isToggled();
      double posX = mc.player.posX;
      double posY = mc.player.posY;
      double posZ = mc.player.posZ;
      double[] dir1 = Utils.directionSpeed(0.5D);
      BlockPos pos = new BlockPos(posX + dir1[0], posY, posZ + dir1[1]);
      Block block = mc.world.getBlockState(pos).getBlock();
      if (step && !(block instanceof net.minecraft.block.BlockAir)) {
        setSpeed((EntityLivingBase)mc.player, 0.0D);
        return;
      } 
      if (mc.world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).getBlock() instanceof net.minecraft.block.BlockAir)
        return; 
      setSpeed((EntityLivingBase)mc.player, 4.0D);
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX + mc.player.motionX, (mc.player.posY <= 10.0D) ? 255.0D : 1.0D, posZ + mc.player.motionZ, true));
    } 
  }
  
  public static void setSpeed(EntityLivingBase entity, double speed) {
    double[] dir = Utils.directionSpeed(speed);
    entity.motionX = dir[0];
    entity.motionZ = dir[1];
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Fast") && 
      mc.player.onGround && mc.player.moveForward > 0.0F) {
      double yaw = Math.toRadians(mc.player.rotationYaw);
      double motionX = -Math.sin(yaw);
      double motionZ = Math.cos(yaw);
      mc.player.motionX = motionX * 5.0D;
      mc.player.motionZ = motionZ * 5.0D;
      mc.player.cameraYaw = 0.15F;
      mc.player.setSprinting(true);
    } 
    if (this.mode.getValString().equalsIgnoreCase("MoveTry")) {
      if (mc.player.isSneaking() || (mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F))
        return; 
      if (mc.player.moveForward > 0.0F && !mc.player.collidedHorizontally)
        mc.player.setSprinting(true); 
      if (mc.player.onGround) {
        mc.player.motionY += 0.1D;
        mc.player.motionX *= 1.8D;
        mc.player.motionZ *= 1.8D;
        double currentSpeed = Math.sqrt(Math.pow(mc.player.motionX, 2.0D) + 
            Math.pow(mc.player.motionZ, 2.0D));
        double maxSpeed = 0.6600000262260437D;
        if (currentSpeed > maxSpeed) {
          mc.player.motionX = mc.player.motionX / currentSpeed * maxSpeed;
          mc.player.motionZ = mc.player.motionZ / currentSpeed * maxSpeed;
        } 
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Forward"))
      Forward(this.motiony.getValDouble(), (float)this.groundmultiplier.getValDouble(), this.airmultiplier.getValDouble()); 
    if (this.mode.getValString().equalsIgnoreCase("Jump")) {
      boolean boost = (Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90.0F);
      if (mc.player.moveForward > 0.0F && mc.player.hurtTime < 5)
        if (mc.player.onGround) {
          mc.player.motionY = 0.405D;
          float f = Utils.getDirection();
          mc.player.motionX -= (MathHelper.sin(f) * 0.2F);
          mc.player.motionZ += (MathHelper.cos(f) * 0.2F);
        } else {
          double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
          double speed = boost ? 1.0064D : 1.001D;
          double direction = Utils.getDirection();
          mc.player.motionX = -Math.sin(direction) * speed * currentSpeed;
          mc.player.motionZ = Math.cos(direction) * speed * currentSpeed;
        }  
      super.onClientTick(event);
    } 
    if (this.mode.getValString().equalsIgnoreCase("Ice")) {
      Blocks.ICE.slipperiness = 0.4F * (float)(0.4000000059604645D * this.Icespeed.getValDouble());
      Blocks.PACKED_ICE.slipperiness = 0.4F * (float)(0.4000000059604645D * this.Icespeed.getValDouble());
      Blocks.FROSTED_ICE.slipperiness = 0.4F * (float)(0.4000000059604645D * this.Icespeed.getValDouble());
    } 
    if (this.mode.getValString().equalsIgnoreCase("GroundHop") && 
      mc.player.onGround) {
      this.sink = !this.sink;
      this.counter++;
      if (this.counter > 3.189546D) {
        mc.player.motionX *= 0.009999999776482582D;
        mc.player.motionZ *= 0.009999999776482582D;
        mc.player.getHeldItemMainhand().damageItem(0, (EntityLivingBase)mc.player);
        mc.player.hurtTime = 62284;
        this.counter = 0;
      } 
      mc.player.motionX *= 1.8300000429153442D;
      mc.player.motionZ *= 1.8300000429153442D;
      mc.player.hurtResistantTime = 1;
      mc.player
        .motionY = mc.player.isSneaking() ? -0.02D : (mc.gameSettings.keyBindJump.pressed ? 0.43D : (this.sink ? 0.37D : 0.25D));
      if (mc.player.moveForward <= 0.0F) {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Y-Port")) {
      if (isOnLiquid())
        return; 
      if (mc.player.onGround && (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F)) {
        if (mc.player.ticksExisted % 2 != 0)
          mc.player.posY += 0.4D; 
        setSpeed((mc.player.ticksExisted % 2 == 0) ? 0.45F : 0.2F);
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("ACC")) {
      boolean boost = (Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90.0F);
      if (mc.player.moveForward > 0.0F && mc.player.hurtTime < 5)
        MoveSpeed(boost); 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Hypixel BHop")) {
      mc.player.setSprinting(true);
      if (mc.world != null && mc.player != null) {
        mc.gameSettings.keyBindJump.pressed = false;
        if (mc.gameSettings.keyBindForward.pressed && !mc.player.collidedHorizontally)
          if (mc.player.onGround) {
            mc.player.jump();
            mc.timer.elapsedTicks = 1;
            mc.player.motionX *= 1.0700000524520874D;
            mc.player.motionZ *= 1.0700000524520874D;
          } else {
            mc.player.jumpMovementFactor = 0.0265F;
            mc.timer.elapsedTicks = 1;
            double direction = getDirection();
            double speed = 1.0D;
            double currentMotion = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
            mc.player.motionX = -Math.sin(direction) * speed * currentMotion;
            mc.player.motionZ = Math.cos(direction) * speed * currentMotion;
          }  
      } 
    } 
    if (this.mode.getValString().equalsIgnoreCase("AAC_ZOOM")) {
      boolean boost = (Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90.0F);
      if (mc.player.moveForward > 0.0F && mc.player.hurtTime < 5) {
        mc.timer.elapsedTicks = 1;
        MoveSpeed(boost);
      } 
    } 
  }
  
  private void MoveSpeed(boolean boost) {
    if (mc.player.onGround) {
      mc.player.motionY = 0.405D;
      float f = (float)getDirection();
      mc.player.motionX -= (MathHelper.sin(f) * 0.2F);
      mc.player.motionZ += (MathHelper.cos(f) * 0.2F);
    } else {
      double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
      double speed = boost ? 1.0064D : 1.001D;
      double direction = getDirection();
      mc.player.motionX = -Math.sin(direction) * speed * currentSpeed;
      mc.player.motionZ = Math.cos(direction) * speed * currentSpeed;
    } 
  }
  
  public void onDisable() {
    Blocks.ICE.slipperiness = 0.98F;
    Blocks.PACKED_ICE.slipperiness = 0.98F;
    Blocks.FROSTED_ICE.slipperiness = 0.98F;
    mc.timer.elapsedTicks = 1;
    super.onDisable();
  }
  
  private boolean isOnLiquid() {
    boolean onLiquid = false;
    int y = (int)((mc.player.getEntityBoundingBox()).minY - 0.01D);
    for (int x = floor_double((mc.player.getEntityBoundingBox()).minX); x < floor_double((mc.player.getEntityBoundingBox()).maxX) + 1; x++) {
      for (int z = floor_double((mc.player.getEntityBoundingBox()).minZ); z < floor_double((mc.player.getEntityBoundingBox()).maxZ) + 1; z++) {
        Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
        if (!(block instanceof net.minecraft.block.BlockAir)) {
          if (!(block instanceof net.minecraft.block.BlockLiquid))
            return false; 
          onLiquid = true;
        } 
      } 
    } 
    return onLiquid;
  }
  
  public void setSpeed(float speed) {
    mc.player.motionX = -(Math.sin(getDirection()) * speed);
    mc.player.motionZ = Math.cos(getDirection()) * speed;
  }
  
  public static double getDirection() {
    float var1 = mc.player.rotationYaw;
    if (mc.player.moveForward < 0.0F)
      var1 += 180.0F; 
    float forward = 1.0F;
    if (mc.player.moveForward < 0.0F) {
      forward = -0.5F;
    } else if (mc.player.moveForward > 0.0F) {
      forward = 0.5F;
    } 
    if (mc.player.moveStrafing > 0.0F)
      var1 -= 90.0F * forward; 
    if (mc.player.moveStrafing < 0.0F)
      var1 += 90.0F * forward; 
    var1 *= 0.017453292F;
    return var1;
  }
  
  public static int floor_double(double value) {
    int i = (int)value;
    return (value < i) ? (i - 1) : i;
  }
  
  private void Forward(double motionY, float groundmultiplier, double airmultiplier) {
    if (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) {
      mc.player.setSprinting(true);
      if (mc.player.onGround) {
        if (motionY > 0.01D)
          mc.player.motionY = motionY; 
        float a = Converter();
        mc.player.motionX -= (MathHelper.sin(a) * groundmultiplier);
        mc.player.motionZ += (MathHelper.cos(a) * groundmultiplier);
      } else {
        double sqrt = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
        double n3 = Converter();
        mc.player.motionX = -Math.sin(n3) * airmultiplier * sqrt;
        mc.player.motionZ = Math.cos(n3) * airmultiplier * sqrt;
      } 
    } 
  }
  
  public static float Converter() {
    float rotationYaw = mc.player.rotationYaw;
    if (mc.player.moveForward < 0.0F)
      rotationYaw += 180.0F; 
    float n = 1.0F;
    if (mc.player.moveForward < 0.0F) {
      n = -0.5F;
    } else if (mc.player.moveForward > 0.0F) {
      n = 0.5F;
    } 
    if (mc.player.moveStrafing > 0.0F)
      rotationYaw -= 90.0F * n; 
    if (mc.player.moveStrafing < 0.0F)
      rotationYaw += 90.0F * n; 
    return rotationYaw * 0.017453292F;
  }
}
