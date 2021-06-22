package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Timer extends Module {
  public Setting Speed = Main.setmgr.add(new Setting("Speed", this, 2.0D, 0.1D, 5.0D, false));
  
  public Setting OnMove = Main.setmgr.add(new Setting("OnMove", this, true));
  
  public Setting mode = Main.setmgr.add(new Setting("Timer Mode", this, "Vanilla", new String[] { "Vanilla", "Even", "Odd", "Random", "PerSec" }));
  
  public Setting RandomTiming = Main.setmgr.add(new Setting("Time per sec", this, 0.5D, 0.0D, 5.0D, false, this.mode, "PerSec", 3));
  
  TimerUtils timer = new TimerUtils();
  
  public boolean switcheraro = false;
  
  Random randomno = new Random();
  
  public Timer() {
    super("Timer", 0, Category.PLAYER, "Timer");
  }
  
  public void onDisable() {
    setTickLength(50.0F);
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    if (this.mode.getValString().equalsIgnoreCase("Vanilla")) {
      if (!this.OnMove.getValBoolean())
        setTickLength((float)(50.0D / this.Speed.getValDouble())); 
      if (this.OnMove.getValBoolean() && Utils.isMoving((Entity)mc.player))
        setTickLength((float)(50.0D / this.Speed.getValDouble())); 
    } 
    if (this.mode.getValString().equalsIgnoreCase("Random")) {
      if (this.randomno.nextBoolean()) {
        if (!this.OnMove.getValBoolean())
          setTickLength((float)(50.0D / this.Speed.getValDouble())); 
        if (this.OnMove.getValBoolean() && Utils.isMoving((Entity)mc.player))
          setTickLength((float)(50.0D / this.Speed.getValDouble())); 
      } else {
        setTickLength(50.0F);
      } 
    } else if (this.mode.getValString().equalsIgnoreCase("PerSec") && 
      this.timer.isDelay((long)(this.RandomTiming.getValDouble() * 1000.0D))) {
      this.switcheraro = !this.switcheraro;
      if (this.switcheraro) {
        if (!this.OnMove.getValBoolean())
          setTickLength((float)(50.0D / this.Speed.getValDouble())); 
        if (this.OnMove.getValBoolean() && Utils.isMoving((Entity)mc.player))
          setTickLength((float)(50.0D / this.Speed.getValDouble())); 
      } else {
        setTickLength(50.0F);
      } 
      this.timer.setLastMS();
    } 
    if (this.mode.getValString().equalsIgnoreCase("Even"))
      if (mc.player.ticksExisted % 2 == 0) {
        if (!this.OnMove.getValBoolean())
          setTickLength((float)(50.0D / this.Speed.getValDouble())); 
        if (this.OnMove.getValBoolean() && Utils.isMoving((Entity)mc.player))
          setTickLength((float)(50.0D / this.Speed.getValDouble())); 
      } else {
        setTickLength(50.0F);
      }  
    if (this.mode.getValString().equalsIgnoreCase("Odd"))
      if (mc.player.ticksExisted % 2 != 0) {
        if (!this.OnMove.getValBoolean())
          setTickLength((float)(50.0D / this.Speed.getValDouble())); 
        if (this.OnMove.getValBoolean() && Utils.isMoving((Entity)mc.player))
          setTickLength((float)(50.0D / this.Speed.getValDouble())); 
      } else {
        setTickLength(50.0F);
      }  
    if (this.OnMove.getValBoolean() && !Utils.isMoving((Entity)mc.player))
      setTickLength(50.0F); 
  }
  
  private void setTickLength(float tickLength) {
    mc.timer.tickLength = 1.0F * tickLength;
  }
}
