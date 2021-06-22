package Method.Client;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

public class FutureXDiscordRPC {
  private static final String ClientId = "842487217473323088";
  
  private static final Minecraft mc = Minecraft.getMinecraft();
  
  private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
  
  public static DiscordRichPresence presence = new DiscordRichPresence();
  
  private static String details;
  
  private static String state;
  
  private static String version = "0.0.1";
  
  public static void init(boolean stop) {
    if (stop) {
      rpc.Discord_Shutdown();
      return;
    } 
    DiscordEventHandlers handlers = new DiscordEventHandlers();
    handlers.disconnected = ((var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + String.valueOf(var1) + ", var2: " + var2));
    rpc.Discord_Initialize("842487217473323088", handlers, true, "");
    presence.startTimestamp = System.currentTimeMillis() / 1000L;
    presence.details = "FutureX on top";
    presence.state = "Main Menu";
    presence.largeImageKey = "futurex";
    presence.largeImageText = "v" + version;
    rpc.Discord_UpdatePresence(presence);
    (new Thread(() -> {
          while (!Thread.currentThread().isInterrupted()) {
            try {
              rpc.Discord_RunCallbacks();
              details = "FutureX on top";
              state = "";
              if (mc.isIntegratedServerRunning()) {
                state = "Playing Singleplayer";
              } else if (mc.getCurrentServerData() != null) {
                if (!(mc.getCurrentServerData()).serverIP.equals(""))
                  state = "Playing " + (mc.getCurrentServerData()).serverIP; 
              } else {
                state = "Main Menu";
              } 
              if (!details.equals(presence.details) || !state.equals(presence.state))
                presence.startTimestamp = System.currentTimeMillis() / 1000L; 
              presence.details = details;
              presence.state = state;
              rpc.Discord_UpdatePresence(presence);
            } catch (Exception e2) {
              e2.printStackTrace();
            } 
            try {
              Thread.sleep(5000L);
            } catch (InterruptedException e3) {
              e3.printStackTrace();
            } 
          } 
          rpc.Discord_Shutdown();
        }"Discord-RPC-Callback-Handler")).start();
  }
}
