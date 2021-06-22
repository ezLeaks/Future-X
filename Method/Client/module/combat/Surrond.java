package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Surrond extends Module {
  Setting blocksPerTick = Main.setmgr.add(new Setting("blocksPerTick", this, 10.0D, 0.0D, 10.0D, true));
  
  Setting rotate = Main.setmgr.add(new Setting("rotate", this, true));
  
  Setting autoCenter = Main.setmgr.add(new Setting("autoCenter", this, true));
  
  Setting offInAir = Main.setmgr.add(new Setting("offInAir", this, true));
  
  Setting BypassCenter = Main.setmgr.add(new Setting("Bypass AutoCenter", this, true, this.autoCenter, 2));
  
  Setting Hand = Main.setmgr.add(new Setting("Hand", this, "Mainhand", new String[] { "Mainhand", "Offhand", "Both", "None" }));
  
  Setting Onerun = Main.setmgr.add(new Setting("Run Once", this, true));
  
  private int playerHotbarSlot;
  
  private int lastHotbarSlot;
  
  private int offsetStep;
  
  public static List<Block> blackList;
  
  public static List<Block> shulkerList;
  
  private static Vec3d[] SURROUND;
  
  public Surrond() {
    super("Surrond", 0, Category.COMBAT, "Surrond you with obsidian");
  }
  
  public void setup() {
    blackList = Arrays.asList(new Block[] { 
          Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, (Block)Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, 
          Blocks.ENCHANTING_TABLE });
    shulkerList = Arrays.asList(new Block[] { 
          Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, 
          Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX });
    SURROUND = new Vec3d[] { new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D) };
    this.playerHotbarSlot = -1;
    this.lastHotbarSlot = -1;
    this.offsetStep = 0;
  }
  
  public void onEnable() {
    if (this.autoCenter.getValBoolean())
      if (this.BypassCenter.getValBoolean()) {
        double lMotionX = Math.floor(mc.player.posX) + 0.5D - mc.player.posX;
        double lMotionZ = Math.floor(mc.player.posZ) + 0.5D - mc.player.posZ;
        mc.player.motionX = lMotionX / 2.0D;
        mc.player.motionZ = lMotionZ / 2.0D;
      } else {
        centerPlayer(Math.floor(mc.player.posX) + 0.5D, mc.player.posY, Math.floor(mc.player.posZ) + 0.5D);
      }  
    this.playerHotbarSlot = mc.player.inventory.currentItem;
    this.lastHotbarSlot = -1;
  }
  
  public void onDisable() {
    if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1)
      mc.player.inventory.currentItem = this.playerHotbarSlot; 
    this.playerHotbarSlot = -1;
    this.lastHotbarSlot = -1;
  }
  
  private void centerPlayer(double x, double y, double z) {
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y, z, true));
    mc.player.setPosition(x, y, z);
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.offInAir.getValBoolean() && !mc.player.onGround)
      toggle(); 
    int blocksPlaced = 0;
    while (blocksPlaced < this.blocksPerTick.getValDouble()) {
      if (this.offsetStep >= SURROUND.length) {
        this.offsetStep = 0;
        break;
      } 
      BlockPos offsetPos = new BlockPos(SURROUND[this.offsetStep]);
      BlockPos targetPos = (new BlockPos(mc.player.getPositionVector())).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
      int old_slot = -1;
      if (find_obi_in_hotbar() != mc.player.inventory.currentItem) {
        old_slot = mc.player.inventory.currentItem;
        mc.player.inventory.currentItem = find_obi_in_hotbar();
      } 
      if (Utils.trytoplace(targetPos) && 
        Utils.placeBlock(targetPos, this.rotate.getValBoolean(), this.Hand) && 
        Utils.placeBlock(targetPos, this.rotate.getValBoolean(), this.Hand))
        blocksPlaced++; 
      if (old_slot != -1)
        mc.player.inventory.currentItem = old_slot; 
      this.offsetStep++;
      if (blocksPlaced > 0 && 
        this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
        mc.player.inventory.currentItem = this.playerHotbarSlot;
        this.lastHotbarSlot = this.playerHotbarSlot;
      } 
      if (blocksPlaced == 0)
        toggle(); 
    } 
    if (this.Onerun.getValBoolean())
      toggle(); 
  }
  
  private int find_obi_in_hotbar() {
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
        Block block = ((ItemBlock)stack.getItem()).getBlock();
        if (block instanceof net.minecraft.block.BlockEnderChest)
          return i; 
        if (block instanceof net.minecraft.block.BlockObsidian)
          return i; 
      } 
    } 
    return -1;
  }
}
