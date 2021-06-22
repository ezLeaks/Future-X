package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Wrapper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AntiAFK extends Module {
  boolean switcheraro;
  
  Setting spin = Main.setmgr.add(new Setting("spin", this, false));
  
  Setting delay = Main.setmgr.add(new Setting("delay", this, 1.0D, 0.0D, 60.0D, false));
  
  Setting swing = Main.setmgr.add(new Setting("swing", this, true));
  
  Setting walk = Main.setmgr.add(new Setting("walk", this, false));
  
  TimerUtils timer = new TimerUtils();
  
  public AntiAFK() {
    super("AntiAFK", 0, Category.PLAYER, "Preforms action to not be kicked!");
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.timer.isDelay((long)(this.delay.getValDouble() * 1000.0D))) {
      this.switcheraro = !this.switcheraro;
      if (this.switcheraro) {
        if (this.spin.getValBoolean())
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketPlayer.Rotation(Utils.random(-160, 160), Utils.random(-160, 160), true)); 
        if (this.swing.getValBoolean())
          Wrapper.INSTANCE.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND)); 
        if (this.walk.getValBoolean()) {
          int c = Utils.random(0, 10);
          if (c == 4)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true); 
          if (c == 1)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true); 
          if (c == 2)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), true); 
          if (c == 3)
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true); 
        } 
      } else if (this.walk.getValBoolean()) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
      } 
      this.timer.setLastMS();
    } 
  }
}
