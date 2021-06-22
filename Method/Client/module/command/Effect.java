package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.util.Objects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Effect extends Command {
  public Effect() {
    super("effect");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (args[0].equalsIgnoreCase("add")) {
        int id = Integer.parseInt(args[1]);
        int duration = Integer.parseInt(args[2]);
        int amplifier = Integer.parseInt(args[3]);
        if (Potion.getPotionById(id) == null) {
          ChatUtils.error("Potion is null");
          return;
        } 
        addEffect(id, duration, amplifier);
      } else if (args[0].equalsIgnoreCase("remove")) {
        int id = Integer.parseInt(args[1]);
        if (Potion.getPotionById(id) == null) {
          ChatUtils.error("Potion is null");
          return;
        } 
        removeEffect(id);
      } else if (args[0].equalsIgnoreCase("clear")) {
        clearEffects();
      } 
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  void addEffect(int id, int duration, int amplifier) {
    mc.player.addPotionEffect(new PotionEffect(Objects.<Potion>requireNonNull(Potion.getPotionById(id)), duration, amplifier));
  }
  
  void removeEffect(int id) {
    mc.player.removePotionEffect(Objects.<Potion>requireNonNull(Potion.getPotionById(id)));
  }
  
  void clearEffects() {
    for (PotionEffect effect : mc.player.getActivePotionEffects())
      mc.player.removePotionEffect(effect.getPotion()); 
  }
  
  public String getDescription() {
    return "Effect manager.";
  }
  
  public String getSyntax() {
    return "effect <add/remove/clear> <id> <duration> <amplifier>";
  }
}
