package Method.Client.utils.Screens.Override;

import Method.Client.utils.visual.ChatUtils;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;

public class Authorinsert extends GuiScreen {
  GuiTextField textbox;
  
  public void initGui() {
    this.textbox = new GuiTextField(0, this.mc.fontRenderer, (int)(this.mc.displayWidth / 5.4D), 180, 240, this.mc.displayWidth / 100);
    this.textbox.setFocused(true);
    addButton(new GuiButton(200, this.width / 2 - 50, this.height / 4 + 120, 120, 20, "Done"));
  }
  
  public void doitnow() {
    if (!this.mc.player.capabilities.isCreativeMode) {
      ChatUtils.error("Creative mode only.");
      this.mc.displayGuiScreen(null);
      return;
    } 
    ItemStack heldItem = this.mc.player.inventory.getCurrentItem();
    int heldItemID = Item.getIdFromItem(heldItem.getItem());
    int writtenBookID = Item.getIdFromItem(Items.WRITTEN_BOOK);
    if (heldItemID != writtenBookID) {
      ChatUtils.error("You must hold a written book in your main hand.");
    } else {
      heldItem.setTagInfo("author", (NBTBase)new NBTTagString(this.textbox.getText()));
      ChatUtils.message("Author Changed! Open Inventory.");
    } 
    this.mc.displayGuiScreen(null);
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    drawCenteredString(this.fontRenderer, "Change Author", this.width / 2, 40, 16777215);
    this.textbox.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    this.textbox.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  protected void keyTyped(char typedChar, int keyCode) {
    if (keyCode == 1) {
      this.mc.displayGuiScreen(null);
    } else {
      this.textbox.textboxKeyTyped(typedChar, keyCode);
    } 
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    super.actionPerformed(button);
    if (button.id == 200)
      doitnow(); 
  }
  
  public boolean doesGuiPauseGame() {
    return false;
  }
}
