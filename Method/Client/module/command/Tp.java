package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;

public class Tp extends Command {
  public Tp() {
    super("Tp");
  }
  
  public void runCommand(String s, String[] args) {
    try {
      if (args.length < 1) {
        ChatUtils.error("Invalid syntax.");
      } else if (args.length < 2) {
        EntityPlayer target = mc.world.getPlayerEntityByName(args[0]);
        if (target == null) {
          ChatUtils.error("Player §7" + args[0] + " §ccan not be found.");
          return;
        } 
        double x = target.posX;
        double y = target.posY;
        double z = target.posZ;
        float pitch = target.rotationPitch;
        float yaw = target.rotationYaw;
        mc.player.setPositionAndRotation(x, y, z, yaw, pitch);
        ChatUtils.message("Teleported §7" + mc.player.getName() + "§e to §9" + x + "§e, §9" + y + "§e, §9" + z + "§e.");
      } else if (args.length < 3) {
        ChatUtils.error("Invalid syntax.");
      } else {
        double x = mc.player.posX;
        double y = mc.player.posY;
        double z = mc.player.posZ;
        float pitch = mc.player.rotationPitch;
        float yaw = mc.player.rotationYaw;
        try {
          x = parseMath(args[0], x);
        } catch (NullPointerException|NumberFormatException e) {
          ChatUtils.error("§7" + args[0] + " §cis not a valid number.");
          return;
        } 
        try {
          y = parseMath(args[1], y);
        } catch (NullPointerException|NumberFormatException e) {
          ChatUtils.error("§7" + args[1] + " §cis not a valid number.");
          return;
        } 
        try {
          z = parseMath(args[2], z);
        } catch (NullPointerException|NumberFormatException e) {
          ChatUtils.error("§7" + args[2] + " §cis not a valid number.");
          return;
        } 
        if (args.length > 3)
          try {
            yaw = (float)parseMath(args[3], yaw);
          } catch (NullPointerException|NumberFormatException e) {
            ChatUtils.error("§7" + args[3] + " §cis not a valid number.");
            return;
          }  
        if (args.length > 4)
          try {
            pitch = (float)parseMath(args[4], pitch);
          } catch (NullPointerException|NumberFormatException e) {
            ChatUtils.error("§7" + args[4] + " §cis not a valid number.");
            return;
          }  
        if (args.length > 5)
          ChatUtils.warning("Too many arguments."); 
        mc.player.setPositionAndRotation(x, y, z, yaw, pitch);
        ChatUtils.message("Teleported §7" + mc.player.getName() + "§e to §9" + x + "§e, §9" + y + "§e, §9" + z + "§e.");
        return;
      } 
    } catch (Exception e) {
      ChatUtils.error("Usage: " + getSyntax());
    } 
  }
  
  public static void updateSlot(int slot, ItemStack stack) {
    ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).sendPacket((Packet)new CPacketCreativeInventoryAction(slot, stack));
  }
  
  private static double parseMath(String input, double old) {
    if (input.length() < 1)
      throw new NumberFormatException(); 
    if (input.charAt(0) == '~') {
      if (input.length() > 2 && input.charAt(1) == '+') {
        String coord = input.substring(2);
        return old + Double.parseDouble(coord);
      } 
      if (input.length() > 2 && input.charAt(1) == '-') {
        String coord = input.substring(2);
        return old - Double.parseDouble(coord);
      } 
      if (input.length() != 1)
        throw new NumberFormatException(); 
      return old;
    } 
    return Double.parseDouble(input);
  }
  
  public String getDescription() {
    return "Tp to position or player";
  }
  
  public String getSyntax() {
    return "tp <<x> <y> <z> [yaw] [pitch]|<player>>";
  }
}
