package Method.Client.module.misc;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.render.NameTags;
import Method.Client.utils.visual.RenderUtils;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Shulkerspy extends Module {
  public Shulkerspy() {
    super("Shulkerspy", 0, Category.MISC, "See others last held Shulker");
    this.Mode = Main.setmgr.add(new Setting(" Mode", this, "Dynamic", new String[] { "Dynamic", "Static" }));
    this.X = Main.setmgr.add(new Setting("X", this, 1.0D, 0.0D, 1000.0D, false, this.Mode, "Static", 2));
    this.Y = Main.setmgr.add(new Setting("Y", this, 1.0D, 0.0D, 1000.0D, false, this.Mode, "Static", 3));
    this.Scale = Main.setmgr.add(new Setting("Scale", this, 1.0D, 0.0D, 4.0D, false, this.Mode, "Dynamic", 2));
    this.Background = Main.setmgr.add(new Setting("Background", this, true));
    this.BgColor = Main.setmgr.add(new Setting("BgColor", this, 0.22D, 0.88D, 0.22D, 0.22D, this.Background, 2));
  }
  
  public static ConcurrentHashMap<String, TileEntityShulkerBox> shulkerMap = new ConcurrentHashMap<>();
  
  public Setting Mode;
  
  public Setting X;
  
  public Setting Y;
  
  public Setting Scale;
  
  public Setting Background;
  
  public Setting BgColor;
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (this.Mode.getValString().equalsIgnoreCase("Dynamic"))
      for (Entity object : mc.world.loadedEntityList) {
        if (object instanceof EntityOtherPlayerMP && 
          shulkerMap.containsKey(object.getName().toLowerCase())) {
          IInventory inv = (IInventory)shulkerMap.get(object.getName().toLowerCase());
          DrawBox((EntityLivingBase)object, inv);
        } 
      }  
  }
  
  public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
    if (this.Mode.getValString().equalsIgnoreCase("Static")) {
      int Players = 0;
      for (Entity object : mc.world.loadedEntityList) {
        if (object instanceof EntityOtherPlayerMP && 
          shulkerMap.containsKey(object.getName().toLowerCase())) {
          IInventory inv = (IInventory)shulkerMap.get(object.getName().toLowerCase());
          DrawSbox(inv, Players, (EntityLivingBase)object);
          Players++;
        } 
      } 
    } 
  }
  
  private void DrawSbox(IInventory inv, int players, EntityLivingBase object) {
    RenderHelper.enableGUIStandardItemLighting();
    if (this.Background.getValBoolean())
      RenderUtils.drawRectDouble(this.X.getValDouble(), this.Y.getValDouble() - (players * 100), this.X.getValDouble() + 88.0D + 60.0D, this.Y.getValDouble() + 50.0D - (players * 100), this.BgColor.getcolor()); 
    mc.fontRenderer.drawStringWithShadow(object.getName() + "'s Shulker", (float)this.X.getValDouble() + 45.0F, (float)this.Y.getValDouble() - 10.0F, -1);
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack itemStack = inv.getStackInSlot(i);
      int offsetX = (int)(this.X.getValDouble() + (i % 9 * 16));
      int offsetY = (int)(this.Y.getValDouble() + (i / 9 * 16)) - players * 100;
      mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, offsetX, offsetY);
      mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, offsetX, offsetY, null);
    } 
    RenderHelper.disableStandardItemLighting();
    (mc.getRenderItem()).zLevel = 0.0F;
  }
  
  public void DrawBox(EntityLivingBase e, IInventory inv) {
    int morey = ModuleManager.getModuleByName("NameTags").isToggled() ? -6 : 0;
    double scale = Math.max(this.Scale.getValDouble() * (mc.player.getDistance((Entity)e) / 20.0F), 2.0D);
    for (int i = 0; i < inv.getSizeInventory(); i++)
      NameTags.drawItem(e.posX, e.posY + e.height + 0.75D * scale, e.posZ, -2.5D + (i % 9), -((i / 9) * 1.2D) - morey, scale, inv.getStackInSlot(i)); 
  }
  
  public void onClientTick(TickEvent.ClientTickEvent event) {
    List<Entity> valids = (List<Entity>)mc.world.getLoadedEntityList().stream().filter(en -> en instanceof EntityOtherPlayerMP).filter(mp -> ((EntityOtherPlayerMP)mp).getHeldItemMainhand().getItem() instanceof ItemShulkerBox).collect(Collectors.toList());
    for (Entity valid : valids) {
      EntityOtherPlayerMP mp = (EntityOtherPlayerMP)valid;
      TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
      ItemShulkerBox shulker = (ItemShulkerBox)mp.getHeldItemMainhand().getItem();
      entityBox.blockType = shulker.getBlock();
      entityBox.setWorld((World)mc.world);
      ItemStackHelper.loadAllItems(((NBTTagCompound)Objects.<NBTTagCompound>requireNonNull(mp.getHeldItemMainhand().getTagCompound())).getCompoundTag("BlockEntityTag"), entityBox.items);
      entityBox.readFromNBT(mp.getHeldItemMainhand().getTagCompound().getCompoundTag("BlockEntityTag"));
      entityBox.setCustomName(mp.getHeldItemMainhand().hasDisplayName() ? mp.getHeldItemMainhand().getDisplayName() : (mp.getName() + "'s current shulker box"));
      shulkerMap.put(mp.getName().toLowerCase(), entityBox);
    } 
  }
}
