package Method.Client.module;

import Method.Client.managers.CommandManager;
import Method.Client.managers.FileManager;
import Method.Client.module.Onscreen.Display.Angles;
import Method.Client.module.Onscreen.Display.Armor;
import Method.Client.module.Onscreen.Display.Biome;
import Method.Client.module.Onscreen.Display.Blockview;
import Method.Client.module.Onscreen.Display.ChunkSize;
import Method.Client.module.Onscreen.Display.CombatItems;
import Method.Client.module.Onscreen.Display.Coords;
import Method.Client.module.Onscreen.Display.Direction;
import Method.Client.module.Onscreen.Display.Durability;
import Method.Client.module.Onscreen.Display.EnabledMods;
import Method.Client.module.Onscreen.Display.Enemypos;
import Method.Client.module.Onscreen.Display.Fps;
import Method.Client.module.Onscreen.Display.Hole;
import Method.Client.module.Onscreen.Display.Hunger;
import Method.Client.module.Onscreen.Display.Inventory;
import Method.Client.module.Onscreen.Display.KeyStroke;
import Method.Client.module.Onscreen.Display.NetherCords;
import Method.Client.module.Onscreen.Display.Ping;
import Method.Client.module.Onscreen.Display.Player;
import Method.Client.module.Onscreen.Display.PlayerCount;
import Method.Client.module.Onscreen.Display.PlayerSpeed;
import Method.Client.module.Onscreen.Display.Potions;
import Method.Client.module.Onscreen.Display.Server;
import Method.Client.module.Onscreen.Display.ServerResponce;
import Method.Client.module.Onscreen.Display.Time;
import Method.Client.module.Onscreen.Display.Tps;
import Method.Client.module.Profiles.Profiletem;
import Method.Client.module.combat.AimBot;
import Method.Client.module.combat.Anchor;
import Method.Client.module.combat.AntiBot;
import Method.Client.module.combat.AntiCrystal;
import Method.Client.module.combat.AutoArmor;
import Method.Client.module.combat.AutoRespawn;
import Method.Client.module.combat.AutoTotem;
import Method.Client.module.combat.AutoTrap;
import Method.Client.module.combat.BowMod;
import Method.Client.module.combat.Burrow;
import Method.Client.module.combat.Criticals;
import Method.Client.module.combat.CrystalAura;
import Method.Client.module.combat.FireballReturn;
import Method.Client.module.combat.HoleEsp;
import Method.Client.module.combat.HoleFill;
import Method.Client.module.combat.InteractClick;
import Method.Client.module.combat.KillAura;
import Method.Client.module.combat.MoreKnockback;
import Method.Client.module.combat.Offhand;
import Method.Client.module.combat.Refill;
import Method.Client.module.combat.Regen;
import Method.Client.module.combat.SelfTrap;
import Method.Client.module.combat.Strafe;
import Method.Client.module.combat.Surrond;
import Method.Client.module.combat.TotemPop;
import Method.Client.module.combat.Trigger;
import Method.Client.module.combat.Velocity;
import Method.Client.module.combat.Webfill;
import Method.Client.module.misc.AntiCheat;
import Method.Client.module.misc.AntiCrash;
import Method.Client.module.misc.AntiHandShake;
import Method.Client.module.misc.AntiHurt;
import Method.Client.module.misc.Antipacket;
import Method.Client.module.misc.Antispam;
import Method.Client.module.misc.AutoClicker;
import Method.Client.module.misc.AutoNametag;
import Method.Client.module.misc.ChatMutator;
import Method.Client.module.misc.CoordsFinder;
import Method.Client.module.misc.DiscordRPCModule;
import Method.Client.module.misc.EchestBP;
import Method.Client.module.misc.FastSleep;
import Method.Client.module.misc.Ghost;
import Method.Client.module.misc.GuiModule;
import Method.Client.module.misc.GuiPeek;
import Method.Client.module.misc.HitEffects;
import Method.Client.module.misc.Livestock;
import Method.Client.module.misc.ModSettings;
import Method.Client.module.misc.NbtView;
import Method.Client.module.misc.NoSlowdown;
import Method.Client.module.misc.Pickupmod;
import Method.Client.module.misc.PluginsGetter;
import Method.Client.module.misc.QuickCraft;
import Method.Client.module.misc.ServerCrash;
import Method.Client.module.misc.Shulkerspy;
import Method.Client.module.misc.TimeStamp;
import Method.Client.module.misc.ToolTipPlus;
import Method.Client.module.misc.VanishDetector;
import Method.Client.module.misc.VersionSpoofer;
import Method.Client.module.movement.AntiFall;
import Method.Client.module.movement.AutoHold;
import Method.Client.module.movement.AutoSwim;
import Method.Client.module.movement.Blink;
import Method.Client.module.movement.BoatFly;
import Method.Client.module.movement.Bunnyhop;
import Method.Client.module.movement.Derp;
import Method.Client.module.movement.ElytraFly;
import Method.Client.module.movement.EntityVanish;
import Method.Client.module.movement.Entityspeed;
import Method.Client.module.movement.FastFall;
import Method.Client.module.movement.Fly;
import Method.Client.module.movement.Glide;
import Method.Client.module.movement.InvMove;
import Method.Client.module.movement.Jesus;
import Method.Client.module.movement.Jump;
import Method.Client.module.movement.Levitate;
import Method.Client.module.movement.LiquidSpeed;
import Method.Client.module.movement.LongJump;
import Method.Client.module.movement.Parkour;
import Method.Client.module.movement.Phase;
import Method.Client.module.movement.SafeWalk;
import Method.Client.module.movement.Scaffold;
import Method.Client.module.movement.Sneak;
import Method.Client.module.movement.Speed;
import Method.Client.module.movement.Spider;
import Method.Client.module.movement.Sprint;
import Method.Client.module.movement.Step;
import Method.Client.module.movement.Teleport;
import Method.Client.module.player.AntiAFK;
import Method.Client.module.player.AutoFish;
import Method.Client.module.player.AutoRemount;
import Method.Client.module.player.Autotool;
import Method.Client.module.player.BuildHeight;
import Method.Client.module.player.ChestStealer;
import Method.Client.module.player.Disconnect;
import Method.Client.module.player.FastBreak;
import Method.Client.module.player.FastLadder;
import Method.Client.module.player.FastPlace;
import Method.Client.module.player.FoodMod;
import Method.Client.module.player.FreeCam;
import Method.Client.module.player.God;
import Method.Client.module.player.LiquidInteract;
import Method.Client.module.player.NoEffect;
import Method.Client.module.player.NoServerChange;
import Method.Client.module.player.Noswing;
import Method.Client.module.player.Nowall;
import Method.Client.module.player.Nuker;
import Method.Client.module.player.PitchLock;
import Method.Client.module.player.PortalMod;
import Method.Client.module.player.Reach;
import Method.Client.module.player.SchematicaNCP;
import Method.Client.module.player.SkinBlink;
import Method.Client.module.player.SmallShield;
import Method.Client.module.player.Timer;
import Method.Client.module.player.Xcarry;
import Method.Client.module.player.YawLock;
import Method.Client.module.render.ArmorRender;
import Method.Client.module.render.BlockOverlay;
import Method.Client.module.render.BossStack;
import Method.Client.module.render.Breadcrumb;
import Method.Client.module.render.BreakEsp;
import Method.Client.module.render.ChestESP;
import Method.Client.module.render.ChunkBorder;
import Method.Client.module.render.ESP;
import Method.Client.module.render.ExtraTab;
import Method.Client.module.render.F3Spoof;
import Method.Client.module.render.Fovmod;
import Method.Client.module.render.Fullbright;
import Method.Client.module.render.ItemESP;
import Method.Client.module.render.MobOwner;
import Method.Client.module.render.MotionBlur;
import Method.Client.module.render.NameTags;
import Method.Client.module.render.NetherSky;
import Method.Client.module.render.NewChunks;
import Method.Client.module.render.NoBlockLag;
import Method.Client.module.render.NoRender;
import Method.Client.module.render.Search;
import Method.Client.module.render.SeedViewer;
import Method.Client.module.render.SkyColor;
import Method.Client.module.render.Tracers;
import Method.Client.module.render.Trail;
import Method.Client.module.render.Trajectories;
import Method.Client.module.render.Visualrange;
import Method.Client.module.render.WallHack;
import Method.Client.module.render.Xray;
import Method.Client.utils.Patcher.Events.EntityPlayerJumpEvent;
import Method.Client.utils.Patcher.Events.EventBookPage;
import Method.Client.utils.Patcher.Events.EventCanCollide;
import Method.Client.utils.Patcher.Events.GetAmbientOcclusionLightValueEvent;
import Method.Client.utils.Patcher.Events.GetLiquidCollisionBoxEvent;
import Method.Client.utils.Patcher.Events.PlayerDamageBlockEvent;
import Method.Client.utils.Patcher.Events.PlayerMoveEvent;
import Method.Client.utils.Patcher.Events.PostMotionEvent;
import Method.Client.utils.Patcher.Events.PreMotionEvent;
import Method.Client.utils.Patcher.Events.RenderBlockModelEvent;
import Method.Client.utils.Patcher.Events.RenderTileEntityEvent;
import Method.Client.utils.Patcher.Events.SetOpaqueCubeEvent;
import Method.Client.utils.Patcher.Events.ShouldSideBeRenderedEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ModuleManager {
  public static ArrayList<Module> modules = new ArrayList<>();
  
  public static ArrayList<Module> toggledModules = new ArrayList<>();
  
  public static ArrayList<Module> FileManagerLoader = new ArrayList<>();
  
  public ModuleManager() {
    addModule((Module)new Antispam());
    addModule((Module)new ChatMutator());
    addModule((Module)new TimeStamp());
    addModule((Module)new AimBot());
    addModule((Module)new AntiBot());
    addModule((Module)new HoleFill());
    addModule((Module)new AutoArmor());
    addModule((Module)new AntiCrystal());
    addModule((Module)new Anchor());
    addModule((Module)new AutoTotem());
    addModule((Module)new AutoTrap());
    addModule((Module)new Burrow());
    addModule((Module)new Criticals());
    addModule((Module)new CrystalAura());
    addModule((Module)new BowMod());
    addModule((Module)new InteractClick());
    addModule((Module)new KillAura());
    addModule((Module)new MoreKnockback());
    addModule((Module)new Regen());
    addModule((Module)new Refill());
    addModule((Module)new Strafe());
    addModule((Module)new Offhand());
    addModule((Module)new SelfTrap());
    addModule((Module)new Surrond());
    addModule((Module)new Trigger());
    addModule((Module)new Webfill());
    addModule((Module)new TotemPop());
    addModule((Module)new Velocity());
    addModule((Module)new DiscordRPCModule());
    addModule((Module)new AntiCrash());
    addModule((Module)new AntiCheat());
    addModule((Module)new AntiHurt());
    addModule((Module)new Antipacket());
    addModule((Module)new AntiHandShake());
    addModule((Module)new AutoClicker());
    addModule((Module)new AutoNametag());
    addModule((Module)new Pickupmod());
    addModule((Module)new Livestock());
    addModule((Module)new CoordsFinder());
    addModule((Module)new EchestBP());
    addModule((Module)new FastSleep());
    addModule((Module)new HitEffects());
    addModule((Module)new Derp());
    addModule((Module)new GuiModule());
    addModule((Module)new InvMove());
    addModule((Module)new ToolTipPlus());
    addModule((Module)new NbtView());
    addModule((Module)new NoSlowdown());
    addModule((Module)new Ghost());
    addModule((Module)new ModSettings());
    addModule((Module)new VersionSpoofer());
    addModule((Module)new PluginsGetter());
    addModule((Module)new GuiPeek());
    addModule((Module)new Shulkerspy());
    addModule((Module)new ServerCrash());
    addModule((Module)new VanishDetector());
    addModule((Module)new QuickCraft());
    addModule((Module)new AntiFall());
    addModule((Module)new AutoSwim());
    addModule((Module)new AutoHold());
    addModule((Module)new Bunnyhop());
    addModule((Module)new Blink());
    addModule((Module)new BoatFly());
    addModule((Module)new ElytraFly());
    addModule((Module)new Entityspeed());
    addModule((Module)new EntityVanish());
    addModule((Module)new FastFall());
    addModule((Module)new Fly());
    addModule((Module)new Glide());
    addModule((Module)new Jump());
    addModule((Module)new Jesus());
    addModule((Module)new Levitate());
    addModule((Module)new LiquidSpeed());
    addModule((Module)new LongJump());
    addModule((Module)new Parkour());
    addModule((Module)new Phase());
    addModule((Module)new SafeWalk());
    addModule((Module)new Sneak());
    addModule((Module)new Speed());
    addModule((Module)new Spider());
    addModule((Module)new Sprint());
    addModule((Module)new Step());
    addModule((Module)new Teleport());
    addModule((Module)new Armor());
    addModule((Module)new Biome());
    addModule((Module)new Coords());
    addModule((Module)new ChunkSize());
    addModule((Module)new Direction());
    addModule((Module)new Durability());
    addModule((Module)new EnabledMods());
    addModule((Module)new Enemypos());
    addModule((Module)new KeyStroke());
    addModule((Module)new Fps());
    addModule((Module)new CombatItems());
    addModule((Module)new Angles());
    addModule((Module)new Blockview());
    addModule((Module)new Hole());
    addModule((Module)new Hunger());
    addModule((Module)new Inventory());
    addModule((Module)new NetherCords());
    addModule((Module)new Ping());
    addModule((Module)new Player());
    addModule((Module)new PlayerCount());
    addModule((Module)new PlayerSpeed());
    addModule((Module)new Potions());
    addModule((Module)new Server());
    addModule((Module)new ServerResponce());
    addModule((Module)new Time());
    addModule((Module)new Tps());
    addModule((Module)new AntiAFK());
    addModule((Module)new AutoFish());
    addModule((Module)new AutoRemount());
    addModule((Module)new AutoRespawn());
    addModule((Module)new Autotool());
    addModule((Module)new BuildHeight());
    addModule((Module)new ChestStealer());
    addModule((Module)new Disconnect());
    addModule((Module)new FastBreak());
    addModule((Module)new FastLadder());
    addModule((Module)new FastPlace());
    addModule((Module)new FireballReturn());
    addModule((Module)new FoodMod());
    addModule((Module)new FreeCam());
    addModule((Module)new Reach());
    addModule((Module)new God());
    addModule((Module)new LiquidInteract());
    addModule((Module)new NoServerChange());
    addModule((Module)new Noswing());
    addModule((Module)new Nowall());
    addModule((Module)new Nuker());
    addModule((Module)new PitchLock());
    addModule((Module)new PortalMod());
    addModule((Module)new Scaffold());
    addModule((Module)new SchematicaNCP());
    addModule((Module)new SkinBlink());
    addModule((Module)new SmallShield());
    addModule((Module)new Timer());
    addModule((Module)new Xcarry());
    addModule((Module)new YawLock());
    addModule((Module)new BlockOverlay());
    addModule((Module)new Breadcrumb());
    addModule((Module)new BossStack());
    addModule((Module)new BreakEsp());
    addModule((Module)new ChestESP());
    addModule((Module)new ChunkBorder());
    addModule((Module)new ESP());
    addModule((Module)new ExtraTab());
    addModule((Module)new ArmorRender());
    addModule((Module)new HoleEsp());
    addModule((Module)new Fullbright());
    addModule((Module)new F3Spoof());
    addModule((Module)new ItemESP());
    addModule((Module)new Visualrange());
    addModule((Module)new MobOwner());
    addModule((Module)new SeedViewer());
    addModule((Module)new MotionBlur());
    addModule((Module)new NewChunks());
    addModule((Module)new NoEffect());
    addModule((Module)new NoRender());
    addModule((Module)new NoBlockLag());
    addModule((Module)new NetherSky());
    addModule((Module)new NameTags());
    addModule((Module)new Search());
    addModule((Module)new SkyColor());
    addModule((Module)new Tracers());
    addModule((Module)new Trail());
    addModule((Module)new Trajectories());
    addModule((Module)new WallHack());
    addModule((Module)new Xray());
    addModule((Module)new Fovmod());
    if (!FileManager.SaveDir.exists()) {
      addModule((Module)new Profiletem("Example"));
      addModule((Module)new Profiletem("Example2"));
    } else {
      FileManager.loadPROFILES();
    } 
  }
  
  public static void addModule(Module m) {
    modules.add(m);
  }
  
  public static ArrayList<Module> getModules() {
    return modules;
  }
  
  public static ArrayList<Module> getEnabledmodules() {
    return toggledModules;
  }
  
  public static void onKeyPressed(int key) {
    for (Module m : modules) {
      boolean NeedControl = false, NeedShift = false, NeedAlt = false;
      int Keydiff = 0;
      for (Integer mKey : m.getKeys()) {
        if (mKey.intValue() == 29) {
          NeedControl = true;
          continue;
        } 
        if (mKey.intValue() == 42) {
          NeedShift = true;
          continue;
        } 
        if (mKey.intValue() == 56) {
          NeedAlt = true;
          continue;
        } 
        Keydiff = mKey.intValue();
      } 
      if (key == Keydiff) {
        if (NeedControl && 
          !Keyboard.isKeyDown(29))
          return; 
        if (NeedShift && 
          !Keyboard.isKeyDown(42))
          return; 
        if (NeedAlt && 
          !Keyboard.isKeyDown(56))
          return; 
        m.toggle();
      } 
    } 
  }
  
  public static Module getModuleByName(String name) {
    return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
  }
  
  public static ArrayList<Module> getModulesInCategory(Category categoryIn) {
    ArrayList<Module> mods = new ArrayList<>();
    for (Module m : getSortedHacksabc(false)) {
      if (m.getCategory() == categoryIn)
        mods.add(m); 
    } 
    return mods;
  }
  
  public static void onWorldLoad(WorldEvent.Load event) {
    for (Module module : FileManagerLoader)
      module.setToggled(true); 
    FileManagerLoader.clear();
    for (Module m : toggledModules)
      m.onWorldLoad(event); 
  }
  
  public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
    for (Module m : toggledModules)
      m.onCameraSetup(event); 
  }
  
  public static void onItemPickup(EntityItemPickupEvent event) {
    for (Module m : toggledModules)
      m.onItemPickup(event); 
  }
  
  public static void onProjectileImpact(ProjectileImpactEvent event) {
    for (Module m : toggledModules)
      m.onProjectileImpact(event); 
  }
  
  public static void onAttackEntity(AttackEntityEvent event) {
    for (Module m : toggledModules)
      m.onAttackEntity(event); 
  }
  
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    for (Module m : toggledModules)
      m.onPlayerTick(event); 
  }
  
  public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    for (Module m : toggledModules)
      m.onLivingUpdate(event); 
  }
  
  public static void onRenderWorldLast(RenderWorldLastEvent event) {
    for (Module m : toggledModules)
      m.onRenderWorldLast(event); 
  }
  
  public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
    for (Module m : toggledModules)
      m.onRenderGameOverlay(event); 
  }
  
  public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
    for (Module m : toggledModules)
      m.onLeftClickBlock(event); 
  }
  
  public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    for (Module m : toggledModules)
      m.onRightClickBlock(event); 
  }
  
  public static void onClientTick(TickEvent.ClientTickEvent event) {
    for (Module m : toggledModules)
      m.onClientTick(event); 
  }
  
  public static void SetOpaqueCubeEvent(SetOpaqueCubeEvent event) {
    for (Module m : toggledModules)
      m.SetOpaqueCubeEvent(event); 
  }
  
  public static void onGetAmbientOcclusionLightValue(GetAmbientOcclusionLightValueEvent event) {
    for (Module m : toggledModules)
      m.onGetAmbientOcclusionLightValue(event); 
  }
  
  public static void onShouldSideBeRendered(ShouldSideBeRenderedEvent event) {
    for (Module m : toggledModules)
      m.onShouldSideBeRendered(event); 
  }
  
  public static void onRenderBlockModel(RenderBlockModelEvent event) {
    for (Module m : toggledModules)
      m.onRenderBlockModel(event); 
  }
  
  public static void onRenderTileEntity(RenderTileEntityEvent event) {
    for (Module m : toggledModules)
      m.onRenderTileEntity(event); 
  }
  
  public static void EventCanCollide(EventCanCollide event) {
    for (Module m : toggledModules)
      m.EventCanCollide(event); 
  }
  
  public static void ItemTooltipEvent(ItemTooltipEvent event) {
    for (Module m : toggledModules)
      m.ItemTooltipEvent(event); 
  }
  
  public static void postBackgroundTooltipRender(RenderTooltipEvent.PostBackground event) {
    for (Module m : toggledModules)
      m.postBackgroundTooltipRender(event); 
  }
  
  public static void postDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
    for (Module m : toggledModules)
      m.postDrawScreen(event); 
  }
  
  public static void RenderGameOverLayPost(RenderGameOverlayEvent.Post event) {
    for (Module m : toggledModules)
      m.RenderGameOverLayPost(event); 
  }
  
  public static void onPlayerMove(PlayerMoveEvent event) {
    for (Module m : toggledModules)
      m.onPlayerMove(event); 
  }
  
  public static void onPlayerJump(EntityPlayerJumpEvent event) {
    for (Module m : toggledModules)
      m.onPlayerJump(event); 
  }
  
  public static void RendergameOverlay(RenderGameOverlayEvent event) {
    for (Module m : toggledModules)
      m.RendergameOverlay(event); 
  }
  
  public static void ChunkeventUNLOAD(ChunkEvent.Unload event) {
    for (Module m : toggledModules)
      m.ChunkeventUNLOAD(event); 
  }
  
  public static void ChunkeventLOAD(ChunkEvent.Load event) {
    for (Module m : toggledModules)
      m.ChunkeventLOAD(event); 
  }
  
  public static void fogColor(EntityViewRenderEvent.FogColors event) {
    for (Module m : toggledModules)
      m.fogColor(event); 
  }
  
  public static void fogDensity(EntityViewRenderEvent.FogDensity event) {
    for (Module m : toggledModules)
      m.fogDensity(event); 
  }
  
  public static void DamageBlock(PlayerDamageBlockEvent event) {
    for (Module m : toggledModules)
      m.DamageBlock(event); 
  }
  
  public static void PlayerSleepInBedEvent(PlayerSleepInBedEvent event) {
    for (Module m : toggledModules)
      m.PlayerSleepInBedEvent(event); 
  }
  
  public static void onGetLiquidCollisionBox(GetLiquidCollisionBoxEvent event) {
    for (Module m : toggledModules)
      m.GetLiquidCollisionBoxEvent(event); 
  }
  
  public static void EventBookPage(EventBookPage event) {
    for (Module m : toggledModules)
      m.EventBookPage(event); 
  }
  
  public static void ClientChatReceivedEvent(ClientChatReceivedEvent event) {
    for (Module m : toggledModules)
      m.ClientChatReceivedEvent(event); 
  }
  
  public static void LivingDeathEvent(LivingDeathEvent event) {
    for (Module m : toggledModules)
      m.LivingDeathEvent(event); 
  }
  
  public static void WorldEvent(WorldEvent event) {
    for (Module m : toggledModules)
      m.WorldEvent(event); 
  }
  
  public static void GuiScreenEvent(GuiScreenEvent event) {
    for (Module m : toggledModules)
      m.GuiScreenEvent(event); 
  }
  
  public static void RendertooltipPre(RenderTooltipEvent.Pre event) {
    for (Module m : toggledModules)
      m.RendertooltipPre(event); 
  }
  
  public static void RenderPlayerEvent(RenderPlayerEvent event) {
    for (Module m : toggledModules)
      m.RenderPlayerEvent(event); 
  }
  
  public static void RenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
    for (Module m : toggledModules)
      m.RenderBlockOverlayEvent(event); 
  }
  
  public static void GetCollisionBoxesEvent(GetCollisionBoxesEvent event) {
    for (Module m : toggledModules)
      m.GetCollisionBoxesEvent(event); 
  }
  
  public static void FOVModifier(EntityViewRenderEvent.FOVModifier event) {
    for (Module m : toggledModules)
      m.FOVModifier(event); 
  }
  
  public static void DrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
    for (Module m : toggledModules)
      m.DrawBlockHighlightEvent(event); 
  }
  
  public static void PreMotionEvent(PreMotionEvent event) {
    for (Module m : toggledModules)
      m.PreMotionEvent(event); 
  }
  
  public static void PostMotionEvent(PostMotionEvent event) {
    for (Module m : toggledModules)
      m.PostMotionEvent(event); 
  }
  
  public static void BackgroundDrawnEvent(GuiScreenEvent.BackgroundDrawnEvent event) {
    for (Module m : toggledModules)
      m.BackgroundDrawnEvent(event); 
  }
  
  public static void RenderTickEvent(TickEvent.RenderTickEvent event) {
    for (Module m : toggledModules)
      m.RenderTickEvent(event); 
  }
  
  public static void onRenderPre(RenderGameOverlayEvent.Pre event) {
    for (Module m : toggledModules)
      m.onRenderPre(event); 
  }
  
  public static void onWorldUnload(WorldEvent.Unload event) {
    for (Module m : toggledModules)
      m.onWorldUnload(event); 
  }
  
  public static void GuiOpen(GuiOpenEvent event) {
    for (Module m : toggledModules)
      m.GuiOpen(event); 
  }
  
  public static void PlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
    for (Module m : toggledModules)
      m.PlayerRespawnEvent(event); 
  }
  
  public static void ClientChatEvent(ClientChatEvent event) {
    if (event.getMessage().startsWith(String.valueOf(CommandManager.cmdPrefix))) {
      CommandManager.getInstance().runCommands(CommandManager.cmdPrefix + event.getMessage().substring(1));
      event.setCanceled(true);
      event.setMessage(null);
    } 
    for (Module m : toggledModules)
      m.ClientChatEvent(event); 
  }
  
  public static ArrayList<Module> getSortedMods(boolean reverse, boolean enabled, boolean visible) {
    ArrayList<Module> list = new ArrayList<>();
    ArrayList<String> listofmods = new ArrayList<>();
    if (!enabled)
      for (Module mod : getModules()) {
        if (visible && mod.visible)
          listofmods.add(mod.getName()); 
        if (!visible)
          listofmods.add(mod.getName()); 
      }  
    if (enabled)
      for (Module mod : getEnabledmodules()) {
        if (visible && mod.visible)
          listofmods.add(mod.getName()); 
        if (!visible)
          listofmods.add(mod.getName()); 
      }  
    listofmods.sort(Comparator.comparing(String::length));
    if (reverse)
      Collections.reverse(listofmods); 
    for (String s : listofmods)
      list.add(getModuleByName(s)); 
    return list;
  }
  
  public static ArrayList<Module> getSortedHacksabc(boolean reverse) {
    ArrayList<Module> list = new ArrayList<>();
    ArrayList<String> listofcountries = new ArrayList<>();
    for (Module module : getModules())
      listofcountries.add(module.getName()); 
    Collections.sort(listofcountries);
    if (reverse)
      Collections.reverse(listofcountries); 
    for (String s : listofcountries)
      list.add(getModuleByName(s)); 
    return list;
  }
}
