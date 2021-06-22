package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class Autotool extends Module {
  public BlockPos position;
  
  public EnumFacing facing;
  
  Setting silent = Main.setmgr.add(new Setting("spoof tool", this, false));
  
  Setting Select = Main.setmgr.add(new Setting("Full Inventory", this, true));
  
  Setting EchestSilk = Main.setmgr.add(new Setting("Echest Silk", this, false));
  
  public Autotool() {
    super("Autotool", 0, Category.PLAYER, "Autotool");
  }
  
  public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
    int slot = getTool(event.getPos(), this.Select.getValBoolean() ? 36 : 9);
    if (slot != -1) {
      if (this.silent.getValBoolean())
        mc.playerController.curBlockDamageMP += blockStrength(event.getPos(), mc.player.inventoryContainer.getSlot(slot).getStack()); 
      mc.player.inventory.currentItem = slot;
      mc.playerController.updateController();
      Pswap(slot);
    } 
  }
  
  public boolean onPacket(Object packet, Connection.Side side) {
    if (packet instanceof CPacketPlayerDigging) {
      CPacketPlayerDigging packet2 = (CPacketPlayerDigging)packet;
      if (packet2.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
        this.position = packet2.getPosition();
        this.facing = packet2.getFacing();
      } 
    } 
    return true;
  }
  
  private void Pswap(int slot) {
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
    mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.position, this.facing));
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
  }
  
  private int getTool(BlockPos pos, int slots) {
    int index = -1;
    float CurrentFastest = 1.0F;
    for (int i = 0; i <= slots; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack != ItemStack.EMPTY) {
        float digSpeed = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
        float destroySpeed = stack.getDestroySpeed(mc.world.getBlockState(pos));
        if (mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockEnderChest && this.EchestSilk.getValBoolean()) {
          if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0 && 
            digSpeed + destroySpeed > CurrentFastest) {
            CurrentFastest = digSpeed + destroySpeed;
            index = i;
          } 
        } else if (digSpeed + destroySpeed > CurrentFastest) {
          CurrentFastest = digSpeed + destroySpeed;
          index = i;
        } 
      } 
    } 
    return index;
  }
  
  private float blockStrength(BlockPos pos, ItemStack stack) {
    float hardness = mc.world.getBlockState(pos).getBlockHardness((World)mc.world, pos);
    if (hardness < 0.0F)
      return 0.0F; 
    return getDigSpeed(mc.world.getBlockState(pos), pos, stack) / hardness / (!canHarvestBlock(mc.world.getBlockState(pos).getBlock(), pos, stack) ? 100.0F : 30.0F);
  }
  
  private boolean canHarvestBlock(Block block, BlockPos pos, ItemStack stack) {
    IBlockState state = mc.world.getBlockState(pos);
    state = state.getBlock().getActualState(state, (IBlockAccess)mc.world, pos);
    if (state.getMaterial().isToolNotRequired())
      return true; 
    String tool = block.getHarvestTool(state);
    if (stack.isEmpty() || tool == null)
      return mc.player.canHarvestBlock(state); 
    int toolLevel = stack.getItem().getHarvestLevel(stack, tool, (EntityPlayer)mc.player, state);
    if (toolLevel < 0)
      return mc.player.canHarvestBlock(state); 
    return (toolLevel >= block.getHarvestLevel(state));
  }
  
  private float getDigSpeed(IBlockState state, BlockPos pos, ItemStack stack) {
    float f = stack.getDestroySpeed(state);
    if (f > 1.0F) {
      int i = EnchantmentHelper.getEfficiencyModifier((EntityLivingBase)mc.player);
      if (i > 0 && !stack.isEmpty())
        f += (i * i + 1); 
    } 
    if (mc.player.isPotionActive(MobEffects.HASTE))
      f *= 1.0F + (((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.HASTE))).getAmplifier() + 1) * 0.2F; 
    if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
      float f1;
      switch (((PotionEffect)Objects.requireNonNull((T)mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE))).getAmplifier()) {
        case 0:
          f1 = 0.3F;
          break;
        case 1:
          f1 = 0.09F;
          break;
        case 2:
          f1 = 0.0027F;
          break;
        default:
          f1 = 8.1E-4F;
          break;
      } 
      f *= f1;
    } 
    if (mc.player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier((EntityLivingBase)mc.player))
      f /= 5.0F; 
    if (!mc.player.onGround)
      f /= 5.0F; 
    f = ForgeEventFactory.getBreakSpeed((EntityPlayer)mc.player, state, f, pos);
    return (f < 0.0F) ? 0.0F : f;
  }
}
