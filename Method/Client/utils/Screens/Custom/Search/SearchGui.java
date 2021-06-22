package Method.Client.utils.Screens.Custom.Search;

import Method.Client.Main;
import Method.Client.managers.FileManager;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.GuiScrollingList;
import org.lwjgl.input.Mouse;

public final class SearchGui extends GuiScreen {
  private ListGui listGui;
  
  private ListGui Allblocks;
  
  private GuiTextField blockNameField;
  
  private GuiButton addButton;
  
  private GuiButton removeButton;
  
  private GuiButton doneButton;
  
  private Block blockToAdd;
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public void initGui() {
    this.listGui = new ListGui(this.mc, this, SearchGuiSettings.getBlockNames(), 500, 0);
    this.Allblocks = new ListGui(this.mc, this, SearchGuiSettings.getAllBlockNames(SearchGuiSettings.getBlockNames()), 0, 0);
    this.blockNameField = new GuiTextField(1, this.mc.fontRenderer, 64, this.height - 55, 150, 18);
    this.buttonList.add(this.addButton = new GuiButton(0, 214, this.height - 56, 30, 20, "Add"));
    this.buttonList.add(this.removeButton = new GuiButton(1, this.width - 300, this.height - 56, 100, 20, "Remove Selected"));
    this.buttonList.add(new GuiButton(2, this.width - 108, 8, 100, 20, "Reset to Defaults"));
    this.buttonList.add(this.doneButton = new GuiButton(3, this.width / 2 - 100, this.height - 28, "Done"));
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    Module Search;
    if (!button.enabled)
      return; 
    switch (button.id) {
      case 0:
        SearchGuiSettings.add(this.blockToAdd);
        this.blockNameField.setText("");
        this.Allblocks.list = SearchGuiSettings.getAllBlockNames(SearchGuiSettings.getBlockNames());
        break;
      case 1:
        SearchGuiSettings.remove(this.listGui.selected);
        break;
      case 2:
        this.mc.displayGuiScreen((GuiScreen)new GuiYesNo((GuiYesNoCallback)this, "Reset to Defaults", "Are you sure?", 0));
        break;
      case 3:
        this.mc.displayGuiScreen((GuiScreen)Main.ClickGui);
        Search = ModuleManager.getModuleByName("Search");
        if (Search.isToggled())
          this.mc.renderGlobal.loadRenderers(); 
        FileManager.saveSearchData();
        break;
    } 
  }
  
  public void confirmClicked(boolean result, int id) {
    if (id == 0 && result)
      SearchGuiSettings.resetToDefaults(); 
    super.confirmClicked(result, id);
    this.mc.displayGuiScreen(this);
  }
  
  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
    int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
    this.listGui.handleMouseInput(mouseX, mouseY);
    this.Allblocks.handleMouseInput(mouseX, mouseY);
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    this.blockNameField.mouseClicked(mouseX, mouseY, mouseButton);
    if (mouseX < 550 || mouseX > this.width - 50 || mouseY < 32 || mouseY > this.height - 64)
      this.listGui.selected = -1; 
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    this.blockNameField.textboxKeyTyped(typedChar, keyCode);
    if (keyCode == 28) {
      actionPerformed(this.addButton);
    } else if (keyCode == 211) {
      actionPerformed(this.removeButton);
    } else if (keyCode == 1) {
      actionPerformed(this.doneButton);
    } 
    if (!this.blockNameField.getText().isEmpty()) {
      this.Allblocks.list = SearchGuiSettings.SearchBlocksAll(SearchGuiSettings.getBlockNames(), this.blockNameField.getText());
    } else {
      this.Allblocks.list = SearchGuiSettings.getAllBlockNames(SearchGuiSettings.getBlockNames());
    } 
  }
  
  public void updateScreen() {
    this.blockNameField.updateCursorCounter();
    this.blockToAdd = Block.getBlockFromName(this.blockNameField.getText());
    if (this.blockNameField.getText().isEmpty() && this.Allblocks.selected >= 0 && this.Allblocks.selected < this.Allblocks.list.size())
      this.blockToAdd = Block.getBlockFromName(this.Allblocks.list.get(this.Allblocks.selected)); 
    this.addButton.enabled = (this.blockToAdd != null);
    this.removeButton.enabled = (this.listGui.selected >= 0 && this.listGui.selected < this.listGui.list.size());
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    drawCenteredString(this.mc.fontRenderer, "Search (" + this.listGui.getSize() + ")", this.width / 2, 12, 16777215);
    this.listGui.drawScreen(mouseX, mouseY, partialTicks);
    this.Allblocks.drawScreen(mouseX, mouseY, partialTicks);
    this.blockNameField.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
    if (this.blockNameField.getText().isEmpty() && !this.blockNameField.isFocused())
      drawString(this.mc.fontRenderer, "block name or ID", 68, this.height - 50, 8421504); 
    drawRect(48, this.height - 56, 64, this.height - 36, -6250336);
    drawRect(49, this.height - 55, 64, this.height - 37, -16777216);
    drawRect(214, this.height - 56, 244, this.height - 55, -6250336);
    drawRect(214, this.height - 37, 244, this.height - 36, -6250336);
    drawRect(244, this.height - 56, 246, this.height - 36, -6250336);
    drawRect(214, this.height - 55, 243, this.height - 52, -16777216);
    drawRect(214, this.height - 40, 243, this.height - 37, -16777216);
    drawRect(215, this.height - 55, 216, this.height - 37, -16777216);
    drawRect(242, this.height - 55, 245, this.height - 37, -16777216);
    this.listGui.renderIconAndGetName(new ItemStack(this.blockToAdd), this.height - 52);
    this.Allblocks.renderIconAndGetName(new ItemStack(this.blockToAdd), this.height - 52);
  }
  
  private static class ListGui extends GuiScrollingList {
    private final Minecraft mc;
    
    private List<String> list;
    
    private int selected = -1;
    
    private int offsetx;
    
    public ListGui(Minecraft mc, SearchGui screen, List<String> list, int offsetx, int offsety) {
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
      ItemStack stack = new ItemStack(Objects.<Block>requireNonNull(Block.getBlockFromName(name)));
      FontRenderer fr = this.mc.fontRenderer;
      String displayName = renderIconAndGetName(stack, slotTop);
      fr.drawString(displayName + " (" + name + ")", 68 + this.offsetx, slotTop + 2, 15790320);
    }
    
    private String renderIconAndGetName(ItemStack stack, int y) {
      GlStateManager.pushMatrix();
      GlStateManager.translate((52 + this.offsetx), y, 0.0F);
      GlStateManager.scale(0.75D, 0.75D, 0.75D);
      RenderHelper.enableGUIStandardItemLighting();
      this.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popMatrix();
      return stack.getDisplayName();
    }
  }
}
