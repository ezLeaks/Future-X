package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class SafeWalk extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Sneak", new String[] { "Sneak", "Normal" }));
  
  Setting EdgeStop = Main.setmgr.add(new Setting("Edge Stop", this, true, this.mode, "Normal", 2));
  
  Setting SlowOnEdge = Main.setmgr.add(new Setting("Slow on Edge", this, false));
  
  public SafeWalk() {
    super("SafeWalk", 0, Category.MOVEMENT, "SafeWalk, Safe ledge");
  }
  
  public void onPlayerMove(PlayerMoveEvent event) {}
  
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Normal") && 
      mc.player.onGround && 
      !mc.gameSettings.keyBindJump.isPressed())
      if (!isCollidable(mc.world
          .getBlockState(new BlockPos(mc.player.getPositionVector().add(new Vec3d(0.0D, -0.5D, 0.0D))))
          .getBlock())) {
        if (this.SlowOnEdge.getValBoolean()) {
          mc.player.motionX -= mc.player.motionX;
          mc.player.motionZ -= mc.player.motionZ;
        } 
        if (this.EdgeStop.getValBoolean())
          mc.player.setPosition(mc.player.prevPosX, mc.player.posY, mc.player.prevPosZ); 
      }  
    if (this.mode.getValString().equalsIgnoreCase("Sneak")) {
      if (mc.player.onGround && 
        !mc.gameSettings.keyBindJump.isPressed())
        if (!isCollidable(mc.world
            .getBlockState(new BlockPos(mc.player.getPositionVector().add(new Vec3d(0.0D, -0.5D, 0.0D))))
            .getBlock())) {
          KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
          if (this.SlowOnEdge.getValBoolean()) {
            mc.player.motionX -= mc.player.motionX;
            mc.player.motionZ -= mc.player.motionZ;
          } 
          return;
        }  
      if (!Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()))
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false); 
    } 
  }
  
  public static boolean isCollidable(Block block) {
    return (block != Blocks.AIR && block != Blocks.BEETROOTS && block != Blocks.CARROTS && block != Blocks.DEADBUSH && block != Blocks.DOUBLE_PLANT && block != Blocks.FLOWING_LAVA && block != Blocks.FLOWING_WATER && block != Blocks.LAVA && block != Blocks.MELON_STEM && block != Blocks.NETHER_WART && block != Blocks.POTATOES && block != Blocks.PUMPKIN_STEM && block != Blocks.RED_FLOWER && block != Blocks.RED_MUSHROOM && block != Blocks.REDSTONE_TORCH && block != Blocks.TALLGRASS && block != Blocks.TORCH && block != Blocks.UNLIT_REDSTONE_TORCH && block != Blocks.YELLOW_FLOWER && block != Blocks.VINE && block != Blocks.WATER && block != Blocks.WEB && block != Blocks.WHEAT);
  }
}
