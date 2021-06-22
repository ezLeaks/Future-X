package Method.Client.module.combat;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SelfTrap extends Module {
  private BlockPos trap_pos;
  
  public Setting place_mode;
  
  public Setting rotate;
  
  public Setting Hand;
  
  public SelfTrap() {
    super("SelfTrap", 0, Category.COMBAT, "SelfTrap");
    this.place_mode = Main.setmgr.add(new Setting("cage", this, "Extra", new String[] { "Extra", "Face", "Normal", "Feet" }));
    this.rotate = Main.setmgr.add(new Setting("rotate", this, false));
    this.Hand = Main.setmgr.add(new Setting("Hand", this, "Mainhand", new String[] { "Mainhand", "Offhand", "Both", "None" }));
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    Vec3d pos = Utils.interpolateEntity((Entity)mc.player, mc.getRenderPartialTicks());
    this.trap_pos = new BlockPos(pos.x, pos.y + 2.0D, pos.z);
    if (is_trapped()) {
      toggle();
      return;
    } 
    Utils.ValidResult result = Utils.valid(this.trap_pos);
    if (result == Utils.ValidResult.AlreadyBlockThere && !mc.world.getBlockState(this.trap_pos).getMaterial().isReplaceable())
      return; 
    if (result == Utils.ValidResult.NoNeighbors) {
      BlockPos[] tests = { this.trap_pos.north(), this.trap_pos.south(), this.trap_pos.east(), this.trap_pos.west(), this.trap_pos.up(), this.trap_pos.down().west() };
      for (BlockPos pos_ : tests) {
        Utils.ValidResult result_ = Utils.valid(pos_);
        if (result_ != Utils.ValidResult.NoNeighbors && result_ != Utils.ValidResult.NoEntityCollision)
          if (Utils.placeBlock(pos_, this.rotate.getValBoolean(), this.Hand))
            return;  
      } 
      return;
    } 
    Utils.placeBlock(this.trap_pos, this.rotate.getValBoolean(), this.Hand);
  }
  
  public boolean is_trapped() {
    if (this.trap_pos == null)
      return false; 
    IBlockState state = mc.world.getBlockState(this.trap_pos);
    return (state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.LAVA);
  }
}
