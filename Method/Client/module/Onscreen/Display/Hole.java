package Method.Client.module.Onscreen.Display;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.Onscreen.PinableFrame;
import Method.Client.module.movement.Phase;
import Method.Client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class Hole extends Module {
  static Setting xpos;
  
  static Setting ypos;
  
  public Hole() {
    super("Hole", 0, Category.ONSCREEN, "Hole");
  }
  
  public void setup() {
    Main.setmgr.add(xpos = new Setting("xpos", this, 200.0D, -20.0D, (mc.displayWidth + 40), true));
    Main.setmgr.add(ypos = new Setting("ypos", this, 90.0D, -20.0D, (mc.displayHeight + 40), true));
  }
  
  public void onEnable() {
    PinableFrame.Toggle("HoleSET", true);
  }
  
  public void onDisable() {
    PinableFrame.Toggle("HoleSET", false);
  }
  
  public static class HoleRUN extends PinableFrame {
    public HoleRUN() {
      super("HoleSET", new String[0], (int)Hole.ypos.getValDouble(), (int)Hole.xpos.getValDouble());
    }
    
    public void setup() {
      Hole.xpos.setValDouble(this.x);
      Hole.ypos.setValDouble(this.y);
    }
    
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
      float yaw = 0.0F;
      int dir = MathHelper.floor((this.mc.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 0x3;
      switch (dir) {
        case 1:
          yaw = 90.0F;
          break;
        case 2:
          yaw = -180.0F;
          break;
        case 3:
          yaw = -90.0F;
          break;
      } 
      BlockPos northPos = traceToBlock(this.mc.getRenderPartialTicks(), yaw);
      Block north = getBlock(northPos);
      if (north != null && north != Blocks.AIR) {
        int damage = getBlockDamage(northPos);
        if (damage != 0)
          Gui.drawRect(getX() + 16, getY(), getX() + 32, getY() + 16, 1627324416); 
        drawBlock(north, (getX() + 16), getY());
      } 
      BlockPos southPos = traceToBlock(this.mc.getRenderPartialTicks(), yaw - 180.0F);
      Block south = getBlock(southPos);
      if (south != null && south != Blocks.AIR) {
        int damage = getBlockDamage(southPos);
        if (damage != 0)
          Gui.drawRect(getX() + 16, getY() + 32, getX() + 32, getY() + 48, 1627324416); 
        drawBlock(south, (getX() + 16), (getY() + 32));
      } 
      BlockPos eastPos = traceToBlock(this.mc.getRenderPartialTicks(), yaw + 90.0F);
      Block east = getBlock(eastPos);
      if (east != null && east != Blocks.AIR) {
        int damage = getBlockDamage(eastPos);
        if (damage != 0)
          Gui.drawRect(getX() + 32, getY() + 16, getX() + 48, getY() + 32, 1627324416); 
        drawBlock(east, (getX() + 32), (getY() + 16));
      } 
      BlockPos westPos = traceToBlock(this.mc.getRenderPartialTicks(), yaw - 90.0F);
      Block west = getBlock(westPos);
      if (west != null && west != Blocks.AIR) {
        int damage = getBlockDamage(westPos);
        if (damage != 0)
          Gui.drawRect(getX(), getY() + 16, getX() + 16, getY() + 32, 1627324416); 
        drawBlock(west, getX(), (getY() + 16));
      } 
    }
    
    private BlockPos traceToBlock(float partialTicks, float yaw) {
      Vec3d pos = Utils.interpolateEntity((Entity)this.mc.player, partialTicks);
      Vec3d dir = direction(yaw);
      return new BlockPos(pos.x + dir.x, pos.y, pos.z + dir.z);
    }
    
    public static Vec3d direction(float yaw) {
      return new Vec3d(Math.cos(Phase.degToRad((yaw + 90.0F))), 0.0D, Math.sin(Phase.degToRad((yaw + 90.0F))));
    }
    
    private int getBlockDamage(BlockPos pos) {
      for (DestroyBlockProgress destBlockProgress : this.mc.renderGlobal.damagedBlocks.values()) {
        if (destBlockProgress.getPosition().getX() == pos.getX() && destBlockProgress.getPosition().getY() == pos.getY() && destBlockProgress.getPosition().getZ() == pos.getZ())
          return destBlockProgress.getPartialBlockDamage(); 
      } 
      return 0;
    }
    
    private Block getBlock(BlockPos pos) {
      Block block = this.mc.world.getBlockState(pos).getBlock();
      if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN)
        return block; 
      return Blocks.AIR;
    }
    
    private void drawBlock(Block block, float x, float y) {
      ItemStack stack = new ItemStack(block);
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.translate(x, y, 0.0F);
      this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
    }
  }
}
