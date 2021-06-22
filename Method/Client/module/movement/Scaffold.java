package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.BlockUtils;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Scaffold extends Module {
  BlockPos blockDown1 = null;
  
  BlockPos blockDown2 = null;
  
  BlockPos blockDown3 = null;
  
  Setting Towermode = Main.setmgr.add(new Setting("Towermode", this, "Tower", new String[] { "Tower", "Onjump", "Timer", "ACC", "NCP", "Spartan", "TP", "Long", "None" }));
  
  Setting Placecolor = Main.setmgr.add(new Setting("Placecolor", this, 0.0D, 1.0D, 1.0D, 0.22D));
  
  Setting TimerVal = Main.setmgr.add(new Setting("TimerVal", this, 1.0D, 0.0D, 3.0D, false, this.Towermode, "Timer", 8));
  
  Setting radius = Main.setmgr.add(new Setting("Radius", this, 0.0D, 0.0D, 5.0D, true));
  
  Setting Sprint = Main.setmgr.add(new Setting("Sprint place", this, false));
  
  Setting Towerspeed = Main.setmgr.add(new Setting("Towerspeed", this, 1.0D, 0.0D, 1.0D, false, this.Towermode, "Tower", 8));
  
  Setting TowerDelay = Main.setmgr.add(new Setting("TowerDelay", this, 100.0D, 0.0D, 1000.0D, true, this.Towermode, "Tower", 9));
  
  Setting TowerFeet = Main.setmgr.add(new Setting("Tower Feet Look", this, true));
  
  Setting SneakPlace = Main.setmgr.add(new Setting("SneakPlace", this, true));
  
  Setting DrawMode = Main.setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  private final TimerUtils timer = new TimerUtils();
  
  public Scaffold() {
    super("Scaffold", 0, Category.MOVEMENT, "Scaffolds");
  }
  
  public void onDisable() {
    mc.timer.tickLength = 50.0F;
    super.onDisable();
  }
  
  public void onEnable() {
    mc.timer.tickLength = 50.0F;
    super.onEnable();
  }
  
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    int newSlot = findSlotWithBlock();
    if (newSlot != -1) {
      Custom(newSlot);
    } else {
      ChatUtils.error("No blocks found in hotbar!");
      toggle();
    } 
    if (this.TowerFeet.getValBoolean() && !this.Towermode.getValString().equalsIgnoreCase("None"))
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Rotation(mc.player.rotationYaw, 90.0F, mc.player.onGround)); 
    if (this.Towermode.getValString().equalsIgnoreCase("Tower") && this.timer.isDelay((long)this.TowerDelay.getValDouble()) && mc.gameSettings.keyBindJump.isKeyDown()) {
      mc.player.motionY = -0.2800000011920929D;
      float towerMotion = 0.42F;
      mc.player.setVelocity(0.0D, 0.41999998688697815D * this.Towerspeed.getValDouble(), 0.0D);
      this.timer.setLastMS();
    } 
    if (this.Towermode.getValString().equalsIgnoreCase("Onjump")) {
      mc.rightClickDelayTimer = 0;
      if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.motionY < 0.0D) {
        mc.player.motionY *= 1.48D;
        if (mc.player.onGround) {
          mc.player.setJumping(false);
          mc.player.jump();
        } 
      } 
    } 
    if (this.Towermode.getValString().equalsIgnoreCase("NCP")) {
      double blockBelow = -2.0D;
      if (mc.player.onGround && mc.gameSettings.keyBindJump.pressed)
        mc.player.motionY = 0.41999998688697815D; 
      if (mc.player.motionY < 0.1D && !(mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add(0.0D, blockBelow, 0.0D)).getBlock() instanceof net.minecraft.block.BlockAir))
        mc.player.motionY = -10.0D; 
    } 
    if (this.Towermode.getValString().equalsIgnoreCase("TP") && 
      mc.gameSettings.keyBindJump.isKeyDown() && mc.player.onGround) {
      mc.player.motionY -= 0.2300000051036477D;
      mc.player.setPosition(mc.player.posX, mc.player.posY + 1.1D, mc.player.posZ);
    } 
    if (this.Towermode.getValString().equalsIgnoreCase("Spartan")) {
      double blockBelow = -2.0D;
      if (mc.player.onGround && mc.gameSettings.keyBindJump.pressed)
        mc.player.motionY = 0.41999998688697815D; 
      if (mc.player.motionY < 0.0D && !(mc.world.getBlockState((new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).add(0.0D, blockBelow, 0.0D)).getBlock() instanceof net.minecraft.block.BlockAir))
        mc.player.motionY = -10.0D; 
    } 
    if (this.Towermode.getValString().equalsIgnoreCase("ACC")) {
      if (mc.player.onGround && mc.gameSettings.keyBindJump.pressed)
        mc.player.motionY = 0.395D; 
      mc.player.motionY -= 0.002300000051036477D;
    } 
    if (this.Towermode.getValString().equalsIgnoreCase("Long") && mc.gameSettings.keyBindJump.isKeyDown())
      if (Utils.isMoving((Entity)mc.player)) {
        if (isOnGround(0.76D) && !isOnGround(0.75D) && 
          mc.player.motionY > 0.23D && 
          mc.player.motionY < 0.25D) {
          double round = Math.round(mc.player.posY);
          mc.player.motionY = round - mc.player.posY;
        } 
        if (isOnGround(1.0E-4D)) {
          mc.player.motionY = 0.42D;
          mc.player.motionX *= 0.9D;
          mc.player.motionZ *= 0.9D;
        } else if (mc.player.posY >= Math.round(mc.player.posY) - 1.0E-4D && 
          mc.player.posY <= Math.round(mc.player.posY) + 1.0E-4D) {
          mc.player.motionY = 0.0D;
        } 
      } else {
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
        mc.player.jumpMovementFactor = 0.0F;
        double x = mc.player.posX;
        double y = mc.player.posY - 1.0D;
        double z = mc.player.posZ;
        BlockPos blockBelow = new BlockPos(x, y, z);
        if (mc.world.getBlockState(blockBelow).getBlock() == Blocks.AIR) {
          mc.player.motionY = 0.4196D;
          mc.player.motionX *= 0.75D;
          mc.player.motionZ *= 0.75D;
        } 
      }  
    if (this.Towermode.getValString().equalsIgnoreCase("Timer")) {
      if (!mc.player.onGround)
        mc.timer.tickLength = (float)(50.0D / this.TimerVal.getValDouble()); 
      mc.rightClickDelayTimer = 0;
      if (mc.player.onGround) {
        mc.player.motionY = 0.3932D;
        mc.timer.tickLength = 50.0F;
      } 
    } 
  }
  
  public static boolean isOnGround(double height) {
    return !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
  }
  
  public void XZmodify(double x, double z) {
    mc.player.motionX = x;
    mc.player.motionZ = z;
  }
  
  private void Custom(int NewSlot) {
    int StartingItem = mc.player.inventory.currentItem;
    mc.player.inventory.currentItem = NewSlot;
    if (mc.gameSettings.keyBindSprint.isKeyDown() && this.Sprint.getValBoolean()) {
      float X = MathHelper.sin((float)Math.toRadians(mc.player.rotationYaw)) * 0.03F;
      float Z = MathHelper.cos((float)Math.toRadians(mc.player.rotationYaw)) * 0.03F;
      if (mc.gameSettings.keyBindForward.isKeyDown())
        XZmodify(-X, Z); 
      if (mc.gameSettings.keyBindBack.isKeyDown())
        XZmodify(X, -Z); 
      if (mc.gameSettings.keyBindLeft.isKeyDown())
        XZmodify(X, Z); 
      if (mc.gameSettings.keyBindRight.isKeyDown())
        XZmodify(-X, -Z); 
      this.blockDown1 = (new BlockPos((Entity)mc.player)).down(2);
      if (mc.world.getBlockState(this.blockDown1).getMaterial().isReplaceable())
        Blockplace(EnumHand.MAIN_HAND, this.blockDown1); 
      if (Math.abs(mc.player.motionX) > 0.03D && mc.world.getBlockState(new BlockPos(this.blockDown1.getX() + mc.player.motionX / Math.abs(mc.player.motionX), (this.blockDown1.getY() - 1), this.blockDown1.getZ())).getMaterial().isReplaceable()) {
        Blockplace(EnumHand.MAIN_HAND, new BlockPos(this.blockDown1.getX() + mc.player.motionX / Math.abs(mc.player.motionX), (this.blockDown1.getY() - 1), this.blockDown1.getZ()));
      } else if (Math.abs(mc.player.motionZ) > 0.03D && mc.world.getBlockState(new BlockPos(this.blockDown1.getX(), (this.blockDown1.getY() - 1), this.blockDown1.getZ() + mc.player.motionZ / Math.abs(mc.player.motionZ))).getMaterial().isReplaceable()) {
        Blockplace(EnumHand.MAIN_HAND, new BlockPos(this.blockDown1.getX(), (this.blockDown1.getY() - 1), this.blockDown1.getZ() + mc.player.motionZ / Math.abs(mc.player.motionZ)));
      } 
      return;
    } 
    if (this.radius.getValDouble() == 0.0D) {
      this.blockDown2 = (new BlockPos((Entity)mc.player)).down();
      if (mc.world.getBlockState(this.blockDown2).getMaterial().isReplaceable() && !mc.player.getEntityBoundingBox().intersects((new AxisAlignedBB(this.blockDown2)).expand(0.05D, 0.05D, 0.05D)))
        Blockplace(EnumHand.MAIN_HAND, this.blockDown2); 
      if (Math.abs(mc.player.motionX) > 0.033D && mc.world.getBlockState(new BlockPos(this.blockDown2.getX() + mc.player.motionX / Math.abs(mc.player.motionX), this.blockDown2.getY(), this.blockDown2.getZ())).getMaterial().isReplaceable()) {
        Blockplace(EnumHand.MAIN_HAND, new BlockPos(this.blockDown2.getX() + mc.player.motionX / Math.abs(mc.player.motionX), this.blockDown2.getY(), this.blockDown2.getZ()));
      } else if (Math.abs(mc.player.motionZ) > 0.033D && mc.world.getBlockState(new BlockPos(this.blockDown2.getX(), this.blockDown2.getY(), this.blockDown2.getZ() + mc.player.motionZ / Math.abs(mc.player.motionZ))).getMaterial().isReplaceable()) {
        Blockplace(EnumHand.MAIN_HAND, new BlockPos(this.blockDown2.getX(), this.blockDown2.getY(), this.blockDown2.getZ() + mc.player.motionZ / Math.abs(mc.player.motionZ)));
      } 
      return;
    } 
    ArrayList<BlockPos> WidePlace = new ArrayList<>();
    for (int i = (int)-this.radius.getValDouble(); i <= this.radius.getValDouble(); i++) {
      for (int j = (int)-this.radius.getValDouble(); j <= this.radius.getValDouble(); j++)
        WidePlace.add(new BlockPos(mc.player.posX + i, mc.player.posY - 1.0D, mc.player.posZ + j)); 
    } 
    for (BlockPos blockPos3 : WidePlace) {
      if (mc.world.getBlockState(blockPos3).getMaterial().isReplaceable()) {
        this.blockDown3 = blockPos3;
        Blockplace(EnumHand.MAIN_HAND, blockPos3);
      } 
    } 
    mc.player.inventory.currentItem = StartingItem;
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    try {
      if (this.blockDown1 != null)
        RenderUtils.RenderBlock(this.DrawMode.getValString(), RenderUtils.Standardbb(this.blockDown1), this.Placecolor.getcolor(), Double.valueOf(this.LineWidth.getValDouble())); 
      if (this.blockDown2 != null && this.radius.getValDouble() == 0.0D)
        RenderUtils.RenderBlock(this.DrawMode.getValString(), RenderUtils.Standardbb(this.blockDown2), this.Placecolor.getcolor(), Double.valueOf(this.LineWidth.getValDouble())); 
      if (this.blockDown3 != null)
        RenderUtils.RenderBlock(this.DrawMode.getValString(), RenderUtils.Standardbb(this.blockDown3), this.Placecolor.getcolor(), Double.valueOf(this.LineWidth.getValDouble())); 
    } catch (Exception exception) {}
  }
  
  public int findSlotWithBlock() {
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      Block block = Block.getBlockFromItem(stack.getItem()).getDefaultState().getBlock();
      if (stack.getItem() instanceof net.minecraft.item.ItemBlock && block.isFullBlock(BlockUtils.getBlock((new BlockPos((Entity)mc.player)).down()).getDefaultState()) && block != Blocks.SAND && block != Blocks.GRAVEL)
        return i; 
    } 
    return -1;
  }
  
  public void Blockplace(EnumHand enumHand, BlockPos blockPos) {
    Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    for (EnumFacing enumFacing : EnumFacing.values()) {
      BlockPos offset = blockPos.offset(enumFacing);
      EnumFacing opposite = enumFacing.getOpposite();
      if (Utils.canBeClicked(offset)) {
        Vec3d Vec3d = (new Vec3d((Vec3i)offset)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
        if (vec3d.squareDistanceTo(Vec3d) <= 18.0625D) {
          float[] array = Utils.getNeededRotations(Vec3d, 0.0F, 0.0F);
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(array[0], array[1], mc.player.onGround));
          if (this.SneakPlace.getValBoolean())
            mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING)); 
          mc.playerController.processRightClickBlock(mc.player, mc.world, offset, opposite, Vec3d, enumHand);
          mc.player.swingArm(enumHand);
          if (this.SneakPlace.getValBoolean())
            mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING)); 
          return;
        } 
      } 
    } 
  }
}
