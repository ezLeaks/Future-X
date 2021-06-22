package Method.Client.utils.Screens;

import Method.Client.utils.Screens.Override.BookInsert;
import Method.Client.utils.Screens.Override.ChestGuiInsert;
import Method.Client.utils.Screens.Override.ConnectingInsert;
import Method.Client.utils.Screens.Override.DeathOverride;
import Method.Client.utils.Screens.Override.DisconnectedInsert;
import Method.Client.utils.Screens.Override.EscInsert;
import Method.Client.utils.Screens.Override.SignInsert;
import java.util.ArrayList;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NewScreen {
  public static ArrayList<Screen> Screens = new ArrayList<>();
  
  public NewScreen() {
    addScreen((Screen)new EscInsert());
    addScreen((Screen)new SignInsert());
    addScreen((Screen)new ChestGuiInsert());
    addScreen((Screen)new BookInsert());
    addScreen((Screen)new DisconnectedInsert());
    addScreen((Screen)new ConnectingInsert());
    addScreen((Screen)new DeathOverride());
  }
  
  public static void addScreen(Screen m) {
    Screens.add(m);
  }
  
  public static void GuiOpen(GuiOpenEvent event) {
    for (Screen m : Screens)
      m.GuiOpen(event); 
  }
  
  public static void GuiScreenEventPost(GuiScreenEvent.ActionPerformedEvent.Post event) {
    for (Screen m : Screens)
      m.GuiScreenEventPost(event); 
  }
  
  public static void GuiScreenEventInit(GuiScreenEvent.InitGuiEvent.Post event) {
    for (Screen m : Screens)
      m.GuiScreenEventInit(event); 
  }
  
  public static void GuiScreenEventPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
    for (Screen m : Screens)
      m.GuiScreenEventPre(event); 
  }
  
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    for (Screen m : Screens)
      m.onClientTick(event); 
  }
  
  public static void DrawScreenEvent(GuiScreenEvent.DrawScreenEvent event) {
    for (Screen m : Screens)
      m.DrawScreenEvent(event); 
  }
  
  public static void onWorldUnload(WorldEvent.Unload event) {
    for (Screen m : Screens)
      m.onWorldUnload(event); 
  }
}
