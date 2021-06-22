package Method.Client.utils.Screens.Custom.Esp;

import Method.Client.Main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Mouse;

public final class EspGui extends GuiScreen {
  private static ListGui listGui;
  
  private ListGui AllMobs;
  
  private GuiTextField MobFieldName;
  
  private GuiButton addButton;
  
  private GuiButton removeButton;
  
  private GuiButton doneButton;
  
  private String MobToAdd;
  
  private String MobToRemove;
  
  public static ArrayList<String> mobs = new ArrayList<>();
  
  public static ArrayList<String> allmobs = new ArrayList<>();
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public static List<String> RemoveMobs(List<String> input) {
    allmobs.clear();
    for (ResourceLocation resourceLocation : EntityList.getEntityNameList()) {
      if (!input.contains(resourceLocation.toString()))
        allmobs.add(resourceLocation.toString()); 
    } 
    return allmobs;
  }
  
  public static List<String> MobSearch(String text) {
    ArrayList<String> Temp = new ArrayList<>();
    for (ResourceLocation object : EntityList.getEntityNameList()) {
      String s = object.toString();
      if (object.toString().contains(text))
        Temp.add(s); 
    } 
    return Temp;
  }
  
  public void initGui() {
    listGui = new ListGui(this.mc, this, mobs, this.width - this.width / 3, 0);
    this.AllMobs = new ListGui(this.mc, this, RemoveMobs(mobs), 0, 0);
    this.MobFieldName = new GuiTextField(1, this.mc.fontRenderer, 64, this.height - 55, 150, 18);
    this.buttonList.add(this.addButton = new GuiButton(0, 214, this.height - 56, 30, 20, "Add"));
    this.buttonList.add(this.removeButton = new GuiButton(1, this.width - 300, this.height - 56, 100, 20, "Remove Selected"));
    this.buttonList.add(new GuiButton(2, this.width - 108, 8, 100, 20, "Remove All"));
    this.buttonList.add(new GuiButton(20, this.width - 308, 8, 100, 20, "Add Passive"));
    this.buttonList.add(new GuiButton(40, this.width - 208, 8, 100, 20, "Add Hostile"));
    this.buttonList.add(this.doneButton = new GuiButton(3, this.width / 2 - 100, this.height - 28, "Done"));
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (!button.enabled)
      return; 
    switch (button.id) {
      case 0:
        if (this.AllMobs.selected >= 0 && this.AllMobs.selected <= this.AllMobs.list.size() && 
          !this.MobToAdd.isEmpty()) {
          mobs.add(this.MobToAdd);
          allmobs.remove(this.MobToAdd);
          this.MobToAdd = "";
        } 
        break;
      case 1:
        mobs.remove(listGui.selected);
        allmobs.add(this.MobToRemove);
        break;
      case 2:
        this.mc.displayGuiScreen((GuiScreen)new GuiYesNo((GuiYesNoCallback)this, "Reset to Defaults", "Are you sure?", 0));
        break;
      case 20:
        if (this.mc.world != null)
          for (ResourceLocation resourceLocation : EntityList.getEntityNameList()) {
            if (EntityList.createEntityByIDFromName(resourceLocation, (World)this.mc.world) instanceof net.minecraft.entity.passive.IAnimals && !(EntityList.createEntityByIDFromName(resourceLocation, (World)this.mc.world) instanceof net.minecraft.entity.monster.IMob) && 
              !listGui.list.contains(resourceLocation.toString()))
              listGui.list.add(resourceLocation.toString()); 
          }  
        break;
      case 3:
        this.mc.displayGuiScreen((GuiScreen)Main.ClickGui);
        break;
      case 40:
        if (this.mc.world != null)
          for (ResourceLocation resourceLocation : EntityList.getEntityNameList()) {
            if (EntityList.createEntityByIDFromName(resourceLocation, (World)this.mc.world) instanceof net.minecraft.entity.monster.IMob && 
              !listGui.list.contains(resourceLocation.toString()))
              listGui.list.add(resourceLocation.toString()); 
          }  
        break;
    } 
  }
  
  public void confirmClicked(boolean result, int id) {
    if (id == 0 && result)
      mobs.clear(); 
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
      this.AllMobs.list = MobSearch(this.MobFieldName.getText());
    } else {
      this.AllMobs.list = RemoveMobs(mobs);
    } 
  }
  
  public static ArrayList<String> Getmobs() {
    return new ArrayList<>(listGui.list);
  }
  
  public void updateScreen() {
    this.MobFieldName.updateCursorCounter();
    if (listGui.selected >= 0 && listGui.selected <= listGui.list.size())
      this.MobToRemove = listGui.list.get(listGui.selected); 
    if (this.AllMobs.selected >= 0 && this.AllMobs.selected < this.AllMobs.list.size())
      this.MobToAdd = this.AllMobs.list.get(this.AllMobs.selected); 
    this.addButton.enabled = (this.MobToAdd != null);
    this.removeButton.enabled = (listGui.selected >= 0 && listGui.selected < listGui.list.size());
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    drawCenteredString(this.mc.fontRenderer, "Mob (" + listGui.getSize() + ")", this.width / 2, 12, 16777215);
    listGui.drawScreen(mouseX, mouseY, partialTicks);
    this.AllMobs.drawScreen(mouseX, mouseY, partialTicks);
    this.MobFieldName.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
    if (this.MobFieldName.getText().isEmpty() && !this.MobFieldName.isFocused())
      drawString(this.mc.fontRenderer, "Mob name", 68, this.height - 50, 8421504); 
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
    
    public ListGui(Minecraft mc, EspGui screen, List<String> list, int offsetx, int offsety) {
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
      String name = this.list.get(slotIdx);
      FontRenderer fr = this.mc.fontRenderer;
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      try {
        int scale = 13;
        if (name.contains("giant") || name.contains("ghast") || name.contains("ender_dragon"))
          scale = 5; 
        GuiInventory.drawEntityOnScreen(58 + this.offsetx, slotTop + 13, scale, 0.0F, 0.0F, (EntityLivingBase)Objects.requireNonNull(EntityList.createEntityByIDFromName(new ResourceLocation(name), (World)this.mc.world)));
      } catch (Exception exception) {}
      GlStateManager.popMatrix();
      fr.drawString(" (" + name + ")", 68 + this.offsetx, slotTop + 2, 15790320);
    }
  }
}
