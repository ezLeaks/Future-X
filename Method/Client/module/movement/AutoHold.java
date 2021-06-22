package Method.Client.module.movement;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.proxy.Overrides.MoveOverride;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class AutoHold extends Module {
  private static boolean Toggled;
  
  private static boolean Unloaded = false;
  
  public Setting unloadedChunk;
  
  public static Setting w;
  
  public static Setting a;
  
  public static Setting s;
  
  public static Setting d;
  
  public static Setting lc;
  
  public static Setting rc;
  
  public static Setting Space;
  
  public static boolean runthething() {
    if (Toggled) {
      if (Unloaded)
        return false; 
      mc.player.movementInput.moveStrafe = 0.0F;
      mc.player.movementInput.moveForward = 0.0F;
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), (w.getValBoolean() || Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())));
      if (w.getValBoolean() || Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
        mc.player.movementInput.moveForward++;
        mc.player.movementInput.forwardKeyDown = true;
      } else {
        mc.player.movementInput.forwardKeyDown = false;
      } 
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), (s.getValBoolean() || Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())));
      if (s.getValBoolean() || Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
        mc.player.movementInput.moveForward--;
        mc.player.movementInput.backKeyDown = true;
      } else {
        mc.player.movementInput.backKeyDown = false;
      } 
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), (a.getValBoolean() || Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())));
      if (a.getValBoolean() || Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
        mc.player.movementInput.moveStrafe++;
        mc.player.movementInput.leftKeyDown = true;
      } else {
        mc.player.movementInput.leftKeyDown = false;
      } 
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), (d.getValBoolean() || Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())));
      if (d.getValBoolean() || Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
        mc.player.movementInput.moveStrafe--;
        mc.player.movementInput.rightKeyDown = true;
      } else {
        mc.player.movementInput.rightKeyDown = false;
      } 
      KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
      mc.player.movementInput.jump = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
      mc.player.movementInput.jump = Space.getValBoolean();
      return true;
    } 
    return false;
  }
  
  public void setup() {
    Main.setmgr.add(this.unloadedChunk = new Setting("Stop for unloaded", this, false));
    Main.setmgr.add(w = new Setting("w", this, true));
    Main.setmgr.add(a = new Setting("a", this, false));
    Main.setmgr.add(s = new Setting("s", this, false));
    Main.setmgr.add(d = new Setting("d", this, false));
    Main.setmgr.add(lc = new Setting("lc", this, false));
    Main.setmgr.add(rc = new Setting("rc", this, false));
    Main.setmgr.add(Space = new Setting("Space", this, false));
  }
  
  public AutoHold() {
    super("AutoHold", 0, Category.MOVEMENT, "Auto Walk + More!");
  }
  
  public void onEnable() {
    MoveOverride.toggle();
    Toggled = true;
  }
  
  public void onDisable() {
    Toggled = false;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.unloadedChunk.getValBoolean())
      Unloaded = !mc.world.getChunk(mc.player.getPosition()).isLoaded(); 
  }
}
