package Method.Client.utils.proxy.Overrides;

import Method.Client.module.movement.AutoHold;
import Method.Client.module.movement.InvMove;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;

public class MoveOverride extends MovementInputFromOptions {
  public MoveOverride(GameSettings gameSettingsIn) {
    super(gameSettingsIn);
  }
  
  public static void toggle() {
    Wrapper.mc.player.movementInput = (MovementInput)new MoveOverride(Wrapper.mc.gameSettings);
  }
  
  public void updatePlayerMoveState() {
    if (InvMove.runthething() || AutoHold.runthething()) {
      this.jump = Keyboard.isKeyDown(Wrapper.mc.gameSettings.keyBindJump.getKeyCode());
      this.sneak = Keyboard.isKeyDown(Wrapper.mc.gameSettings.keyBindSneak.getKeyCode());
      if (this.sneak) {
        this.moveStrafe = (float)(this.moveStrafe * 0.3D);
        this.moveForward = (float)(this.moveForward * 0.3D);
      } 
    } else {
      super.updatePlayerMoveState();
    } 
  }
}
