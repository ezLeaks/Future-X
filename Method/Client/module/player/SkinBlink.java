package Method.Client.module.player;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import java.util.Random;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SkinBlink extends Module {
  Setting mode = Main.setmgr.add(new Setting("Mode", this, "Flat", new String[] { "HORIZONTAL", "VERTICAL", "RANDOM" }));
  
  Setting slowness = Main.setmgr.add(new Setting("slowness", this, 2.0D, 1.0D, 2.0D, true));
  
  public SkinBlink() {
    super("SkinBlink", 0, Category.PLAYER, "SkinBlink");
  }
  
  public void setup() {
    this.r = new Random();
    this.len = (EnumPlayerModelParts.values()).length;
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    int i;
    switch (this.mode.getValString()) {
      case "RANDOM":
        if (mc.player.ticksExisted % this.slowness.getValDouble() != 0.0D)
          return; 
        mc.gameSettings.switchModelPartEnabled(EnumPlayerModelParts.values()[this.r.nextInt(this.len)]);
        break;
      case "VERTICAL":
      case "HORIZONTAL":
        i = (int)(mc.player.ticksExisted / this.slowness.getValDouble() % (PARTS_HORIZONTAL.length * 2));
        if (i >= PARTS_HORIZONTAL.length) {
          i -= PARTS_HORIZONTAL.length;
          mc.gameSettings.setModelPartEnabled(
              this.mode.getValString().equalsIgnoreCase("Vertical") ? PARTS_VERTICAL[i] : PARTS_HORIZONTAL[i], true);
        } 
        break;
    } 
  }
  
  private static final EnumPlayerModelParts[] PARTS_HORIZONTAL = new EnumPlayerModelParts[] { EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.JACKET, EnumPlayerModelParts.HAT, EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG, EnumPlayerModelParts.RIGHT_SLEEVE };
  
  private static final EnumPlayerModelParts[] PARTS_VERTICAL = new EnumPlayerModelParts[] { EnumPlayerModelParts.HAT, EnumPlayerModelParts.JACKET, EnumPlayerModelParts.LEFT_SLEEVE, EnumPlayerModelParts.RIGHT_SLEEVE, EnumPlayerModelParts.LEFT_PANTS_LEG, EnumPlayerModelParts.RIGHT_PANTS_LEG };
  
  private Random r;
  
  private int len;
}
