package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.BlockUtils;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import java.util.Objects;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FastBreak extends Module {
  Setting mode;
  
  PotionEffect Haste;
  
  Setting autoBreak;
  
  Setting picOnly;
  
  Setting Blockair;
  
  Setting delay;
  
  private BlockPos renderBlock;
  
  private BlockPos lastBlock;
  
  private boolean packetCancel;
  
  public FastBreak() {
    super("FastBreak", 0, Category.PLAYER, "FastBreak");
    this.mode = Main.setmgr.add(new Setting("break mode", this, "potion", new String[] { "Potion", "Packet", "INSTANT", "NoDelay" }));
    this.Haste = new PotionEffect(Objects.<Potion>requireNonNull(Potion.getPotionById(3)));
    this.autoBreak = Main.setmgr.add(new Setting("autoBreak", this, false, this.mode, "INSTANT", 1));
    this.picOnly = Main.setmgr.add(new Setting("picOnly", this, false, this.mode, "INSTANT", 2));
    this.Blockair = Main.setmgr.add(new Setting("Blockair", this, false, this.mode, "INSTANT", 3));
    this.delay = Main.setmgr.add(new Setting("delay", this, 1.0D, 0.0D, 5.0D, true, this.mode, "INSTANT", 4));
    this.packetCancel = false;
  }
  
  public static final TimerUtils timer = new TimerUtils();
  
  private EnumFacing direction;
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (this.renderBlock != null);
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (this.mode.getValString().equalsIgnoreCase("INSTANT") && 
      side == Connection.Side.OUT && 
      packet instanceof CPacketPlayerDigging) {
      CPacketPlayerDigging digPacket = (CPacketPlayerDigging)packet;
      return (digPacket.getAction() != CPacketPlayerDigging.Action.START_DESTROY_BLOCK || !this.packetCancel);
    } 
    return true;
  }
  
  private boolean canBreak(BlockPos pos) {
    return (mc.world.getBlockState(pos).getBlock().getBlockHardness(mc.world.getBlockState(pos), (World)mc.world, pos) != -1.0F);
  }
  
  public void setTarget(BlockPos pos) {
    this.renderBlock = pos;
    this.packetCancel = false;
    mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.DOWN));
    this.packetCancel = true;
    mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN));
    this.direction = EnumFacing.DOWN;
    this.lastBlock = pos;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("INSTANT")) {
      if (this.renderBlock != null && 
        this.autoBreak.getValBoolean() && timer.isDelay((long)this.delay.getValDouble() * 1000L)) {
        if (this.picOnly.getValBoolean() && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() != Items.DIAMOND_PICKAXE)
          return; 
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.renderBlock, this.direction));
        timer.setLastMS();
      } 
      mc.playerController.blockHitDelay = 0;
    } 
    if (this.mode.getValString().equalsIgnoreCase("NoDelay"))
      mc.playerController.blockHitDelay = 0; 
    if (this.mode.getValString().equalsIgnoreCase("potion") && 
      mc.player.onGround)
      mc.player.addPotionEffect(this.Haste); 
    if (this.mode.getValString().equalsIgnoreCase("Packet")) {
      mc.player.removeActivePotionEffect(this.Haste.getPotion());
      if (mc.playerController.curBlockDamageMP > 0.7F)
        mc.playerController.curBlockDamageMP = 1.0F; 
      mc.playerController.blockHitDelay = 0;
    } 
    super.onClientTick(event);
  }
  
  public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
    if (this.mode.getValString().equalsIgnoreCase("INSTANT") && 
      canBreak(event.getPos())) {
      if (this.lastBlock == null || (event.getPos()).x != this.lastBlock.x || (event.getPos()).y != this.lastBlock.y || (event.getPos()).z != this.lastBlock.z) {
        this.packetCancel = false;
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), Objects.<EnumFacing>requireNonNull(event.getFace())));
      } 
      this.packetCancel = true;
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), Objects.<EnumFacing>requireNonNull(event.getFace())));
      this.renderBlock = event.getPos();
      this.lastBlock = event.getPos();
      this.direction = event.getFace();
      if (this.Blockair.getValBoolean()) {
        mc.playerController.onPlayerDestroyBlock(event.getPos());
        mc.world.setBlockToAir(event.getPos());
      } 
      event.setResult(Event.Result.DENY);
    } 
    if (this.mode.getValString().equalsIgnoreCase("packet")) {
      float progress = mc.playerController.curBlockDamageMP + BlockUtils.getHardness(event.getPos());
      if (progress >= 1.0F)
        return; 
      Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event
            .getPos(), mc.objectMouseOver.sideHit));
    } 
    super.onLeftClickBlock(event);
  }
  
  public void onDisable() {
    mc.player.removeActivePotionEffect(this.Haste.getPotion());
    super.onDisable();
  }
}
