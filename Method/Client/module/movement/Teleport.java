package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

public class Teleport extends Module {
  Setting mode = Main.setmgr.add(new Setting("Tp Mode", this, "Reach", new String[] { "Reach", "Flight" }));
  
  Setting math = Main.setmgr.add(new Setting("Speed", this, false));
  
  Setting Path = Main.setmgr.add(new Setting("Path", this, 0.0D, 1.0D, 1.0D, 0.22D));
  
  Setting Land = Main.setmgr.add(new Setting("Land", this, 0.22D, 1.0D, 1.0D, 0.22D));
  
  Setting TpMode = Main.setmgr.add(new Setting("Tp Draw", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  public boolean passPacket = false;
  
  private BlockPos teleportPosition = null;
  
  private boolean canDraw;
  
  private int delay;
  
  float reach = 0.0F;
  
  public Teleport() {
    super("Teleport", 0, Category.MOVEMENT, "Teleport around");
  }
  
  public void onEnable() {
    if (this.mode.getValString().equalsIgnoreCase("Reach"))
      this.reach = (float)mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue(); 
    super.onEnable();
  }
  
  public void onDisable() {
    if (this.mode.getValString().equalsIgnoreCase("Flight")) {
      mc.player.noClip = false;
      this.passPacket = false;
      this.teleportPosition = null;
      return;
    } 
    this.canDraw = false;
    mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(500.0D);
    super.onDisable();
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (side == Connection.Side.OUT && this.mode.getValString().equalsIgnoreCase("Flight") && 
      packet instanceof CPacketPlayer)
      return this.passPacket; 
    return true;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Flight")) {
      RayTraceResult object = (Wrapper.INSTANCE.mc()).objectMouseOver;
      if (object == null)
        return; 
      EntityPlayerSP player = mc.player;
      GameSettings settings = Wrapper.INSTANCE.mcSettings();
      if (!this.passPacket) {
        if (settings.keyBindAttack.isKeyDown() && object.typeOfHit == RayTraceResult.Type.BLOCK) {
          if (Utils.isBlockMaterial(object.getBlockPos(), Blocks.AIR))
            return; 
          this.teleportPosition = object.getBlockPos();
          this.passPacket = true;
        } 
        return;
      } 
      player.noClip = false;
      if (settings.keyBindSneak.isKeyDown() && player.onGround)
        Mathteleport(); 
      return;
    } 
    if (((!Mouse.isButtonDown(0) && (Wrapper.INSTANCE.mc()).inGameHasFocus) || !(Wrapper.INSTANCE.mc()).inGameHasFocus) && mc.player.getItemInUseCount() == 0) {
      mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(100.0D);
      this.canDraw = true;
    } else {
      this.canDraw = false;
      mc.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).setBaseValue(this.reach);
    } 
    if (this.teleportPosition != null && this.delay == 0 && Mouse.isButtonDown(1)) {
      Mathteleport();
      this.delay = 5;
    } 
    if (this.delay > 0)
      this.delay--; 
    super.onClientTick(event);
  }
  
  private void Mathteleport() {
    if (this.math.getValBoolean()) {
      double[] playerPosition = { mc.player.posX, mc.player.posY, mc.player.posZ };
      double[] blockPosition = { (this.teleportPosition.getX() + 0.5F), this.teleportPosition.getY() + getOffset(mc.world.getBlockState(this.teleportPosition).getBlock(), this.teleportPosition) + 1.0D, (this.teleportPosition.getZ() + 0.5F) };
      Utils.teleportToPosition(playerPosition, blockPosition, 0.25D, 0.0D, true, true);
      mc.player.setPosition(blockPosition[0], blockPosition[1], blockPosition[2]);
      this.teleportPosition = null;
    } else {
      double x = this.teleportPosition.getX();
      double y = (this.teleportPosition.getY() + 1);
      double z = this.teleportPosition.getZ();
      mc.player.setPosition(x, y, z);
      for (int i = 0; i < 1; i++)
        Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Position(x, y, z, mc.player.onGround)); 
    } 
  }
  
  public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    if (!this.mode.getValString().equalsIgnoreCase("Flight"))
      return; 
    EntityPlayerSP player = mc.player;
    GameSettings settings = Wrapper.INSTANCE.mcSettings();
    if (!this.passPacket) {
      player.noClip = true;
      player.fallDistance = 0.0F;
      player.onGround = true;
      player.capabilities.isFlying = false;
      player.motionX = 0.0D;
      player.motionY = 0.0D;
      player.motionZ = 0.0D;
      float speed = 0.5F;
      if (settings.keyBindJump.isKeyDown())
        player.motionY += speed; 
      if (settings.keyBindSneak.isKeyDown())
        player.motionY -= speed; 
      double d7 = (player.rotationYaw + 90.0F);
      boolean flag4 = settings.keyBindForward.isKeyDown();
      boolean flag6 = settings.keyBindBack.isKeyDown();
      boolean flag8 = settings.keyBindLeft.isKeyDown();
      boolean flag10 = settings.keyBindRight.isKeyDown();
      if (flag4) {
        if (flag8) {
          d7 -= 45.0D;
        } else if (flag10) {
          d7 += 45.0D;
        } 
      } else if (flag6) {
        d7 += 180.0D;
        if (flag8) {
          d7 += 45.0D;
        } else if (flag10) {
          d7 -= 45.0D;
        } 
      } else if (flag8) {
        d7 -= 90.0D;
      } else if (flag10) {
        d7 += 90.0D;
      } 
      if (flag4 || flag8 || flag6 || flag10) {
        player.motionX = Math.cos(Math.toRadians(d7));
        player.motionZ = Math.sin(Math.toRadians(d7));
      } 
    } 
    super.onLivingUpdate(event);
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Flight")) {
      if (this.teleportPosition == null)
        return; 
      if (this.teleportPosition.getY() == (new BlockPos((Entity)mc.player)).down().getY()) {
        RenderUtils.RenderBlock(this.TpMode.getValString(), RenderUtils.Standardbb(this.teleportPosition), this.Path.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
        return;
      } 
      RenderUtils.RenderBlock(this.TpMode.getValString(), RenderUtils.Standardbb(this.teleportPosition), this.Land.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      return;
    } 
    RayTraceResult object = (Wrapper.INSTANCE.mc()).objectMouseOver;
    if (object == null)
      return; 
    object.getBlockPos();
    if (this.canDraw) {
      for (float offset = -2.0F; offset < 18.0F; offset++) {
        double[] mouseOverPos = { object.getBlockPos().getX(), (object.getBlockPos().getY() + offset), object.getBlockPos().getZ() };
        if (BlockTeleport(mouseOverPos))
          break; 
      } 
    } else if (object.entityHit != null) {
      for (float offset = -2.0F; offset < 18.0F; offset++) {
        double[] mouseOverPos = { object.entityHit.posX, object.entityHit.posY + offset, object.entityHit.posZ };
        if (BlockTeleport(mouseOverPos))
          break; 
      } 
    } else {
      this.teleportPosition = null;
    } 
    super.onRenderWorldLast(event);
  }
  
  private boolean BlockTeleport(double[] mouseOverPos) {
    BlockPos blockBelowPos = new BlockPos(mouseOverPos[0], mouseOverPos[1], mouseOverPos[2]);
    if (canRenderBox(mouseOverPos)) {
      RenderUtils.RenderBlock(this.TpMode.getValString(), RenderUtils.Standardbb(new BlockPos(mouseOverPos[0], mouseOverPos[1], mouseOverPos[2])), this.Path.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      if ((Wrapper.INSTANCE.mc()).inGameHasFocus) {
        this.teleportPosition = blockBelowPos;
        return true;
      } 
      this.teleportPosition = null;
    } 
    return false;
  }
  
  public boolean canRenderBox(double[] mouseOverPos) {
    boolean canTeleport = false;
    Block blockBelowPos = mc.world.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] - 1.0D, mouseOverPos[2])).getBlock();
    Block blockPos = mc.world.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1], mouseOverPos[2])).getBlock();
    Block blockAbovePos = mc.world.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] + 1.0D, mouseOverPos[2])).getBlock();
    boolean validBlockBelow = (blockBelowPos.getCollisionBoundingBox(mc.world.getBlockState(new BlockPos(mouseOverPos[0], mouseOverPos[1] - 1.0D, mouseOverPos[2])), (IBlockAccess)mc.world, new BlockPos(mouseOverPos[0], mouseOverPos[1] - 1.0D, mouseOverPos[2])) != null);
    boolean validBlock = isValidBlock(blockPos);
    boolean validBlockAbove = isValidBlock(blockAbovePos);
    if (validBlockBelow && validBlock && validBlockAbove)
      canTeleport = true; 
    return canTeleport;
  }
  
  public double getOffset(Block block, BlockPos pos) {
    IBlockState state = mc.world.getBlockState(pos);
    double offset = 0.0D;
    if (block instanceof BlockSlab && !((BlockSlab)block).isDouble()) {
      offset -= 0.5D;
    } else if (block instanceof net.minecraft.block.BlockEndPortalFrame) {
      offset -= 0.20000000298023224D;
    } else if (block instanceof net.minecraft.block.BlockBed) {
      offset -= 0.4399999976158142D;
    } else if (block instanceof net.minecraft.block.BlockCake) {
      offset -= 0.5D;
    } else if (block instanceof net.minecraft.block.BlockDaylightDetector) {
      offset -= 0.625D;
    } else if (block instanceof net.minecraft.block.BlockRedstoneComparator || block instanceof net.minecraft.block.BlockRedstoneRepeater) {
      offset -= 0.875D;
    } else if (block instanceof net.minecraft.block.BlockChest || block == Blocks.ENDER_CHEST) {
      offset -= 0.125D;
    } else if (block instanceof net.minecraft.block.BlockLilyPad) {
      offset -= 0.949999988079071D;
    } else if (block == Blocks.SNOW_LAYER) {
      offset -= 0.875D;
      offset += (0.125F * (((Integer)state.getValue((IProperty)BlockSnow.LAYERS)).intValue() - 1));
    } else if (isValidBlock(block)) {
      offset--;
    } 
    return offset;
  }
  
  public boolean isValidBlock(Block block) {
    return (block == Blocks.PORTAL || block == Blocks.SNOW_LAYER || block instanceof net.minecraft.block.BlockTripWireHook || block instanceof net.minecraft.block.BlockTripWire || block instanceof net.minecraft.block.BlockDaylightDetector || block instanceof net.minecraft.block.BlockRedstoneComparator || block instanceof net.minecraft.block.BlockRedstoneRepeater || block instanceof net.minecraft.block.BlockSign || block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockPressurePlate || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFlower || block instanceof net.minecraft.block.BlockMushroom || block instanceof net.minecraft.block.BlockDoublePlant || block instanceof net.minecraft.block.BlockReed || block instanceof net.minecraft.block.BlockSapling || block == Blocks.CARROTS || block == Blocks.WHEAT || block == Blocks.NETHER_WART || block == Blocks.POTATOES || block == Blocks.PUMPKIN_STEM || block == Blocks.MELON_STEM || block == Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE || block == Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE || block == Blocks.REDSTONE_WIRE || block instanceof net.minecraft.block.BlockTorch || block == Blocks.LEVER || block instanceof net.minecraft.block.BlockButton);
  }
}
