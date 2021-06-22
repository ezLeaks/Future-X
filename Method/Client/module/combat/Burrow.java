package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Burrow extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "JUMP", new String[] { "JUMP", "GLITCH", "TP" }));
  
  Setting glitchY = Main.setmgr.add(new Setting("glitchY", this, 0.5D, 0.1D, 1.5D, false, this.mode, "GLITCH", 1));
  
  Setting tpHeight = Main.setmgr.add(new Setting("tpHeight", this, 0.5D, 0.0D, 10.0D, false, this.mode, "TP", 1));
  
  Setting delay = Main.setmgr.add(new Setting("delay", this, 200.0D, 1.0D, 500.0D, false));
  
  Setting Rotate = Main.setmgr.add(new Setting("Rotate", this, true));
  
  Setting Instant = Main.setmgr.add(new Setting("Instant", this, true));
  
  Setting Center = Main.setmgr.add(new Setting("Center", this, true));
  
  Setting CenterBypass = Main.setmgr.add(new Setting("CenterBypass", this, true, this.Center, 5));
  
  Setting OffGround = Main.setmgr.add(new Setting("OffGround", this, true));
  
  private final TimerUtils timer = new TimerUtils();
  
  public Burrow() {
    super("Burrow", 0, Category.COMBAT, "Burrow into hole");
  }
  
  private int find_obi_in_hotbar() {
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
        Block block = ((ItemBlock)stack.getItem()).getBlock();
        if (block instanceof net.minecraft.block.BlockObsidian)
          return i; 
      } 
    } 
    return -1;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.timer.isDelay((long)this.delay.getValDouble()) && 
      find_obi_in_hotbar() != -1) {
      double posy = mc.player.posY;
      int current = mc.player.inventory.currentItem;
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(find_obi_in_hotbar()));
      mc.player.inventory.currentItem = find_obi_in_hotbar();
      BlockPos positionToPlaceAt = (new BlockPos(mc.player.getPositionVector())).down();
      if (place(positionToPlaceAt, mc)) {
        if (this.OffGround.getValBoolean())
          mc.player.onGround = false; 
        switch (this.mode.getValString()) {
          case "JUMP":
            mc.player.jump();
            break;
          case "GLITCH":
            mc.player.motionY = this.glitchY.getValDouble();
            break;
          case "TP":
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - this.tpHeight.getValDouble(), mc.player.posZ, mc.player.onGround));
            break;
        } 
      } 
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(current));
      mc.player.inventory.currentItem = current;
      if (this.Instant.getValBoolean())
        mc.player.posY = posy; 
      toggle();
    } 
  }
  
  public void onEnable() {
    if (mc.player != null)
      if (find_obi_in_hotbar() != -1) {
        if (this.Center.getValBoolean())
          if (this.CenterBypass.getValBoolean()) {
            double lMotionX = Math.floor(mc.player.posX) + 0.5D - mc.player.posX;
            double lMotionZ = Math.floor(mc.player.posZ) + 0.5D - mc.player.posZ;
            mc.player.motionX = lMotionX / 2.0D;
            mc.player.motionZ = lMotionZ / 2.0D;
          } else {
            double[] newPos = { Math.floor(mc.player.posX) + 0.5D, mc.player.posY, Math.floor(mc.player.posZ) + 0.5D };
            CPacketPlayer.Position middleOfPos = new CPacketPlayer.Position(newPos[0], newPos[1], newPos[2], mc.player.onGround);
            if (!mc.world.isAirBlock((new BlockPos(newPos[0], newPos[1], newPos[2])).down()) && 
              mc.player.posX != middleOfPos.x && mc.player.posZ != middleOfPos.z) {
              mc.player.connection.sendPacket((Packet)middleOfPos);
              mc.player.setPosition(newPos[0], newPos[1], newPos[2]);
            } 
          }  
        mc.player.jump();
        this.timer.setLastMS();
      } else {
        ChatUtils.message("You dont have obsidian to use");
        toggle();
      }  
  }
  
  private EnumFacing calcSide(BlockPos pos) {
    for (EnumFacing side : EnumFacing.values()) {
      BlockPos sideOffset = pos.offset(side);
      IBlockState offsetState = mc.world.getBlockState(sideOffset);
      if (offsetState.getBlock().canCollideCheck(offsetState, false) && 
        !offsetState.getMaterial().isReplaceable())
        return side; 
    } 
    return null;
  }
  
  private boolean place(BlockPos pos, Minecraft mc) {
    Block block = mc.world.getBlockState(pos).getBlock();
    EnumFacing direction = calcSide(pos);
    if (direction == null)
      return false; 
    boolean activated = block.onBlockActivated((World)mc.world, pos, mc.world.getBlockState(pos), (EntityPlayer)mc.player, EnumHand.MAIN_HAND, direction, 0.0F, 0.0F, 0.0F);
    if (activated)
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING)); 
    EnumFacing otherSide = direction.getOpposite();
    BlockPos sideOffset = pos.offset(direction);
    if (this.Rotate.getValBoolean()) {
      float[] angle = Utils.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() + 0.5F), (pos.getZ() + 0.5F)));
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], angle[1], mc.player.onGround));
    } 
    mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(sideOffset, otherSide, EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F));
    mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
    if (activated)
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING)); 
    return true;
  }
}
