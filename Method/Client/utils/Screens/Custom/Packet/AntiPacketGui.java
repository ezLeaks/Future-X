package Method.Client.utils.Screens.Custom.Packet;

import Method.Client.Main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Mouse;

public final class AntiPacketGui extends GuiScreen {
  private static ListGui listGui;
  
  private ListGui AllMobs;
  
  private GuiTextField MobFieldName;
  
  private GuiButton addButton;
  
  private GuiButton removeButton;
  
  private GuiButton doneButton;
  
  private String PacketToAdd;
  
  private String PacketToRemove;
  
  public static ArrayList<String> Packets = new ArrayList<>();
  
  public static ArrayList<String> AllPackets = new ArrayList<>();
  
  public static ArrayList<AntiPacketPacket> ListOfPackets = new ArrayList<>();
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public static List<String> removePackets(List<String> input) {
    AllPackets.clear();
    for (AntiPacketPacket listOfPacket : ListOfPackets) {
      if (!input.contains(listOfPacket.packet.getClass().getSimpleName()))
        AllPackets.add(listOfPacket.packet.getClass().getSimpleName()); 
    } 
    return AllPackets;
  }
  
  public static List<String> PacketSearch(String text) {
    ArrayList<String> Temp = new ArrayList<>();
    for (AntiPacketPacket listOfPacket : ListOfPackets) {
      String s = listOfPacket.packet.getClass().getSimpleName();
      if (listOfPacket.packet.getClass().getSimpleName().toLowerCase().contains(text.toLowerCase()))
        Temp.add(s); 
    } 
    return Temp;
  }
  
  public void initGui() {
    ListSetup();
    listGui = new ListGui(this.mc, this, Packets, this.width - this.width / 3, 0);
    this.AllMobs = new ListGui(this.mc, this, removePackets(Packets), 0, 0);
    this.MobFieldName = new GuiTextField(1, this.mc.fontRenderer, 64, this.height - 55, 150, 18);
    this.buttonList.add(this.addButton = new GuiButton(0, 214, this.height - 56, 30, 20, "Add"));
    this.buttonList.add(this.removeButton = new GuiButton(1, this.width - 300, this.height - 56, 100, 20, "Remove Selected"));
    this.buttonList.add(new GuiButton(2, this.width - 108, 8, 100, 20, "Remove All"));
    this.buttonList.add(new GuiButton(20, this.width - 308, 8, 100, 20, "Add Server"));
    this.buttonList.add(new GuiButton(40, this.width - 208, 8, 100, 20, "Add Client"));
    this.buttonList.add(this.doneButton = new GuiButton(3, this.width / 2 - 100, this.height - 28, "Done"));
  }
  
  public static ArrayList<AntiPacketPacket> GetPackets() {
    ArrayList<AntiPacketPacket> list = new ArrayList<>();
    for (AntiPacketPacket listOfPacket : ListOfPackets) {
      for (String s : listGui.list) {
        if (s.equalsIgnoreCase(listOfPacket.packet.getClass().getSimpleName()))
          list.add(listOfPacket); 
      } 
    } 
    return list;
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (!button.enabled)
      return; 
    switch (button.id) {
      case 0:
        if (this.AllMobs.selected >= 0 && this.AllMobs.selected <= this.AllMobs.list.size() && 
          !this.PacketToAdd.isEmpty() && 
          !Packets.contains(this.PacketToAdd)) {
          Packets.add(this.PacketToAdd);
          AllPackets.remove(this.PacketToAdd);
          this.PacketToAdd = "";
        } 
        break;
      case 1:
        Packets.remove(listGui.selected);
        AllPackets.add(this.PacketToRemove);
        break;
      case 2:
        this.mc.displayGuiScreen((GuiScreen)new GuiYesNo((GuiYesNoCallback)this, "Reset to Defaults", "Are you sure?", 0));
        break;
      case 20:
        if (this.mc.world != null)
          for (AntiPacketPacket listOfPacket : ListOfPackets) {
            if (listOfPacket.packet.getClass().getSimpleName().startsWith("S") && 
              !listGui.list.contains(listOfPacket.packet.getClass().getSimpleName()))
              listGui.list.add(listOfPacket.packet.getClass().getSimpleName()); 
          }  
        break;
      case 3:
        this.mc.displayGuiScreen((GuiScreen)Main.ClickGui);
        break;
      case 40:
        if (this.mc.world != null)
          for (AntiPacketPacket listOfPacket : ListOfPackets) {
            if (listOfPacket.packet.getClass().getSimpleName().startsWith("C") && 
              !listGui.list.contains(listOfPacket.packet.getClass().getSimpleName()))
              listGui.list.add(listOfPacket.packet.getClass().getSimpleName()); 
          }  
        break;
    } 
  }
  
  public void confirmClicked(boolean result, int id) {
    if (id == 0 && result)
      Packets.clear(); 
    super.confirmClicked(result, id);
    this.mc.displayGuiScreen(this);
  }
  
  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
    int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
    listGui.handleMouseInput(mouseX, mouseY);
    this.AllMobs.handleMouseInput(mouseX, mouseY);
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    this.MobFieldName.mouseClicked(mouseX, mouseY, mouseButton);
    if (mouseX < 550 || mouseX > this.width - 50 || mouseY < 32 || mouseY > this.height - 64)
      listGui.selected = -1; 
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    this.MobFieldName.textboxKeyTyped(typedChar, keyCode);
    if (keyCode == 28) {
      actionPerformed(this.addButton);
    } else if (keyCode == 211) {
      actionPerformed(this.removeButton);
    } else if (keyCode == 1) {
      actionPerformed(this.doneButton);
    } 
    if (!this.MobFieldName.getText().isEmpty()) {
      this.AllMobs.list = PacketSearch(this.MobFieldName.getText());
    } else {
      this.AllMobs.list = removePackets(Packets);
    } 
  }
  
  public static ArrayList<String> Getmobs() {
    return new ArrayList<>(listGui.list);
  }
  
  public void updateScreen() {
    this.MobFieldName.updateCursorCounter();
    if (listGui.selected >= 0 && listGui.selected <= listGui.list.size())
      this.PacketToRemove = listGui.list.get(listGui.selected); 
    if (this.AllMobs.selected >= 0 && this.AllMobs.selected < this.AllMobs.list.size())
      this.PacketToAdd = this.AllMobs.list.get(this.AllMobs.selected); 
    this.addButton.enabled = (this.PacketToAdd != null);
    this.removeButton.enabled = (listGui.selected >= 0 && listGui.selected < listGui.list.size());
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    drawCenteredString(this.mc.fontRenderer, "Packet (" + listGui.getSize() + ")", this.width / 2, 12, 16777215);
    listGui.drawScreen(mouseX, mouseY, partialTicks);
    this.AllMobs.drawScreen(mouseX, mouseY, partialTicks);
    this.MobFieldName.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
    if (this.MobFieldName.getText().isEmpty() && !this.MobFieldName.isFocused())
      drawString(this.mc.fontRenderer, "Packet Name", 68, this.height - 50, 8421504); 
    drawRect(48, this.height - 56, 64, this.height - 36, -6250336);
    drawRect(49, this.height - 55, 64, this.height - 37, -16777216);
    drawRect(214, this.height - 56, 244, this.height - 55, -6250336);
    drawRect(214, this.height - 37, 244, this.height - 36, -6250336);
    drawRect(244, this.height - 56, 246, this.height - 36, -6250336);
    drawRect(214, this.height - 55, 243, this.height - 52, -16777216);
    drawRect(214, this.height - 40, 243, this.height - 37, -16777216);
    drawRect(215, this.height - 55, 216, this.height - 37, -16777216);
    drawRect(242, this.height - 55, 245, this.height - 37, -16777216);
  }
  
  private static class ListGui extends GuiScrollingList {
    private final Minecraft mc;
    
    private List<String> list;
    
    private int selected = -1;
    
    private int offsetx;
    
    public ListGui(Minecraft mc, AntiPacketGui screen, List<String> list, int offsetx, int offsety) {
      super(mc, screen.width / 4, screen.height, 32 + offsety, screen.height - 64, 50 + offsetx, 16, screen.width, screen.height);
      this.offsetx = offsetx;
      this.mc = mc;
      this.list = list;
    }
    
    protected int getSize() {
      return this.list.size();
    }
    
    protected void elementClicked(int index, boolean doubleClick) {
      if (index >= 0 && index < this.list.size())
        this.selected = index; 
    }
    
    protected boolean isSelected(int index) {
      return (index == this.selected);
    }
    
    protected void drawBackground() {
      Gui.drawRect(50 + this.offsetx, this.top, 66 + this.offsetx, this.bottom, -1);
    }
    
    protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
      FontRenderer fr = this.mc.fontRenderer;
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      fr.drawString(" (" + (String)this.list.get(slotIdx) + ")", 68 + this.offsetx, slotTop + 2, 15790320);
    }
  }
  
  private void ListSetup() {
    ListOfPackets.clear();
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketEncryptionResponse()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new C00Handshake()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketLoginStart()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketDisconnect()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEnableCompression()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEncryptionRequest()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketLoginSuccess()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketAnimation()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketChatMessage()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketClickWindow()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketClientSettings()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketClientStatus()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketCloseWindow()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketConfirmTeleport()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketConfirmTransaction()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketCreativeInventoryAction()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketCustomPayload()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketEnchantItem()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketEntityAction()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketHeldItemChange()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketInput()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketKeepAlive()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketPlaceRecipe()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketPlayer()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketPlayerAbilities()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketPlayerDigging()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketPlayerTryUseItem()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketPing()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketPlayerTryUseItemOnBlock()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketRecipeInfo()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketResourcePackStatus()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketServerQuery()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketSeenAdvancements()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketSpectate()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketSteerBoat()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketTabComplete()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketUpdateSign()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketUseEntity()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new CPacketVehicleMove()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketAdvancementInfo()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketAnimation()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketBlockAction()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketBlockBreakAnim()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketBlockChange()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketCamera()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketChangeGameState()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketChat()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketChunkData()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketCloseWindow()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketCollectItem()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketCombatEvent()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketConfirmTransaction()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketCooldown()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketCustomPayload()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketDestroyEntities()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketDisconnect()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketDisplayObjective()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEffect()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntity()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntityAttach()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntityEffect()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntityEquipment()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntityHeadLook()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntityMetadata()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntityProperties()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntityStatus()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntityTeleport()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketEntityVelocity()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketExplosion()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketHeldItemChange()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketJoinGame()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketKeepAlive()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketMaps()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketMoveVehicle()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketMultiBlockChange()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketOpenWindow()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketParticles()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketPlaceGhostRecipe()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketPlayerAbilities()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketPlayerListHeaderFooter()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketPlayerListItem()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketPlayerPosLook()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketRecipeBook()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketRemoveEntityEffect()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketResourcePackSend()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketRespawn()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketScoreboardObjective()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSelectAdvancementsTab()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketServerDifficulty()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSetExperience()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSetPassengers()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSetSlot()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSignEditorOpen()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSoundEffect()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSpawnExperienceOrb()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSpawnGlobalEntity()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSpawnMob()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSpawnObject()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSpawnPainting()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSpawnPlayer()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketSpawnPosition()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketStatistics()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketTabComplete()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketTeams()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketTimeUpdate()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketTitle()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketUnloadChunk()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketUpdateBossInfo()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketUpdateHealth()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketUpdateScore()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketUpdateTileEntity()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketUseBed()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketWindowItems()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketWindowProperty()));
    ListOfPackets.add(new AntiPacketPacket((Packet)new SPacketWorldBorder()));
  }
}
