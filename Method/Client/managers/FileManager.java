package Method.Client.managers;

import Method.Client.Main;
import Method.Client.clickgui.ClickGui;
import Method.Client.clickgui.component.Frame;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.Profiles.Profiletem;
import Method.Client.utils.Screens.Custom.Search.SearchGuiSettings;
import Method.Client.utils.Screens.Custom.Xray.XrayGuiSettings;
import Method.Client.utils.system.Wrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileManager {
  private static final Gson gsonPretty = (new GsonBuilder()).setPrettyPrinting().create();
  
  private static final JsonParser jsonParser = new JsonParser();
  
  public static final File SaveDir = new File(String.format("%s%s%s-%s-%s%s", new Object[] { (Wrapper.INSTANCE.mc()).gameDir, File.separator, "FutureX", "1.12.2", "0.0.1", File.separator }));
  
  private static final File Mods = new File(SaveDir, "mods.json");
  
  private static final File XRAYDATA = new File(SaveDir, "xraydata.json");
  
  private static final File FRIENDS = new File(SaveDir, "friends.json");
  
  private static final File ONSCREEN = new File(SaveDir, "onscreen.json");
  
  private static final File SEARCH = new File(SaveDir, "search.json");
  
  private static final File PROFILES = new File(SaveDir, "profiles.json");
  
  public FileManager() {
    if (!SaveDir.exists())
      SaveDir.mkdir(); 
    if (!Mods.exists())
      SaveMods(); 
    if (!ONSCREEN.exists()) {
      saveframes();
    } else {
      Loadpos();
    } 
    if (!XRAYDATA.exists()) {
      saveXRayData();
    } else {
      loadXRayData();
    } 
    if (!PROFILES.exists())
      savePROFILES(); 
    if (!SEARCH.exists()) {
      saveSearchData();
    } else {
      loadSearchData();
    } 
    if (!FRIENDS.exists()) {
      saveFriends();
    } else {
      loadFriends();
    } 
  }
  
  public static void loadPROFILES() {
    try {
      BufferedReader loadJson = new BufferedReader(new FileReader(PROFILES));
      JsonObject moduleJason = (JsonObject)jsonParser.parse(loadJson);
      loadJson.close();
      for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)moduleJason.entrySet()) {
        Profiletem profiletem = new Profiletem(entry.getKey());
        ModuleManager.addModule((Module)profiletem);
        JsonObject jsonMod = (JsonObject)entry.getValue();
        profiletem.setKeys(jsonMod.get("key").getAsString());
        ((Module)profiletem).visible = jsonMod.get("Visible").getAsBoolean();
        ArrayList<Module> Modstore = new ArrayList<>();
        for (Module module : ModuleManager.modules) {
          if (!module.getCategory().equals(Category.ONSCREEN) && !module.getCategory().equals(Category.PROFILES) && 
            jsonMod.has(module.getName()))
            Modstore.add(module); 
        } 
        profiletem.setStoredModules(Modstore);
        ArrayList<Setting> Setstore = new ArrayList<>();
        for (Module module : Modstore) {
          for (Setting s : Main.setmgr.getSettingsByMod(module)) {
            if (s.getMode().equals("Slider"))
              s.setValDouble(jsonMod.get(s.getName()).getAsDouble()); 
            if (s.getMode().equals("Check"))
              s.setValBoolean(jsonMod.get(s.getName()).getAsBoolean()); 
            if (s.getMode().equals("Combo"))
              s.setValString(jsonMod.get(s.getName()).getAsString()); 
            if (s.getMode().equals("Color")) {
              s.setValDouble(jsonMod.get(s.getName() + "c").getAsDouble());
              s.setsaturation((float)jsonMod.get(s.getName() + "s").getAsDouble());
              s.setbrightness((float)jsonMod.get(s.getName() + "b").getAsDouble());
              s.setAlpha((float)jsonMod.get(s.getName() + "a").getAsDouble());
            } 
            Setstore.add(s);
          } 
        } 
        profiletem.setStoredSettings(Setstore);
      } 
    } catch (NullPointerException|IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void savePROFILES() {
    try {
      JsonObject json = new JsonObject();
      for (Module m : ModuleManager.getModulesInCategory(Category.PROFILES)) {
        JsonObject JsonMod = new JsonObject();
        JsonMod.addProperty("key", m.getKeys().toString());
        JsonMod.addProperty("Visible", Boolean.valueOf(m.visible));
        json.add(m.getName(), (JsonElement)JsonMod);
        for (Setting s : m.getStoredSettings()) {
          if (s.getMode().equals("Slider"))
            JsonMod.addProperty(s.getName(), Double.valueOf(s.getValDouble())); 
          if (s.getMode().equals("Check"))
            JsonMod.addProperty(s.getName(), Boolean.valueOf(s.getValBoolean())); 
          if (s.getMode().equals("Combo"))
            JsonMod.addProperty(s.getName(), s.getValString()); 
          if (s.getMode().equals("Color")) {
            JsonMod.addProperty(s.getName() + "c", Double.valueOf(s.getValDouble()));
            JsonMod.addProperty(s.getName() + "s", Double.valueOf(s.getSat()));
            JsonMod.addProperty(s.getName() + "b", Double.valueOf(s.getBri()));
            JsonMod.addProperty(s.getName() + "a", Double.valueOf(s.getAlpha()));
          } 
        } 
        for (Module storedModule : m.getStoredModules())
          JsonMod.addProperty(storedModule.getName(), "Toggled"); 
      } 
      PrintWriter saveJson = new PrintWriter(new FileWriter(PROFILES));
      saveJson.println(gsonPretty.toJson((JsonElement)json));
      saveJson.close();
    } catch (NullPointerException|IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void SaveMods() {
    try {
      JsonObject json = new JsonObject();
      ModuleManager.getModules().forEach(module -> Save(json, module));
      JsonObject JsonMod = new JsonObject();
      JsonMod.addProperty("PREFIX", Character.valueOf(CommandManager.cmdPrefix));
      json.add("PREFIX", (JsonElement)JsonMod);
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("VERSION", "0.0.1");
      json.add("VERSION", (JsonElement)jsonObject);
      PrintWriter saveJson = new PrintWriter(new FileWriter(Mods));
      saveJson.println(gsonPretty.toJson((JsonElement)json));
      saveJson.close();
    } catch (NullPointerException|IOException e) {
      e.printStackTrace();
    } 
  }
  
  private static void Save(JsonObject json, Module m) {
    JsonObject JsonMod = new JsonObject();
    JsonMod.addProperty("toggled", Boolean.valueOf(m.isToggled()));
    JsonMod.addProperty("key", m.getKeys().toString());
    JsonMod.addProperty("Visible", Boolean.valueOf(m.visible));
    json.add(m.getName(), (JsonElement)JsonMod);
    for (Setting s : Main.setmgr.getSettingsByMod(m)) {
      if (s.getMode().equals("Slider"))
        JsonMod.addProperty(s.getName(), Double.valueOf(s.getValDouble())); 
      if (s.getMode().equals("Check"))
        JsonMod.addProperty(s.getName(), Boolean.valueOf(s.getValBoolean())); 
      if (s.getMode().equals("Combo"))
        JsonMod.addProperty(s.getName(), s.getValString()); 
      if (s.getMode().equals("Color")) {
        JsonMod.addProperty(s.getName() + "c", Double.valueOf(s.getValDouble()));
        JsonMod.addProperty(s.getName() + "s", Double.valueOf(s.getSat()));
        JsonMod.addProperty(s.getName() + "b", Double.valueOf(s.getBri()));
        JsonMod.addProperty(s.getName() + "a", Double.valueOf(s.getAlpha()));
      } 
    } 
  }
  
  public static void LoadMods() {
    try {
      BufferedReader loadJson = new BufferedReader(new FileReader(Mods));
      JsonObject moduleJason = (JsonObject)jsonParser.parse(loadJson);
      loadJson.close();
      for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)moduleJason.entrySet()) {
        Module mods = ModuleManager.getModuleByName(entry.getKey());
        if (((String)entry.getKey()).equals("PREFIX")) {
          JsonObject jsonMod = (JsonObject)entry.getValue();
          CommandManager.cmdPrefix = jsonMod.get("PREFIX").getAsCharacter();
        } 
        Load(entry, mods);
      } 
    } catch (NullPointerException|IOException e) {
      e.printStackTrace();
    } 
  }
  
  private static void Load(Map.Entry<String, JsonElement> entry, Module m) {
    if (m != null) {
      JsonObject jsonMod = (JsonObject)entry.getValue();
      m.setKeys(jsonMod.get("key").getAsString());
      m.visible = jsonMod.get("Visible").getAsBoolean();
      for (Setting s : Main.setmgr.getSettingsByMod(m)) {
        if (s.getMode().equals("Slider"))
          s.setValDouble(jsonMod.get(s.getName()).getAsDouble()); 
        if (s.getMode().equals("Check"))
          s.setValBoolean(jsonMod.get(s.getName()).getAsBoolean()); 
        if (s.getMode().equals("Combo"))
          s.setValString(jsonMod.get(s.getName()).getAsString()); 
        if (s.getMode().equals("Color")) {
          s.setValDouble(jsonMod.get(s.getName() + "c").getAsDouble());
          s.setsaturation((float)jsonMod.get(s.getName() + "s").getAsDouble());
          s.setbrightness((float)jsonMod.get(s.getName() + "b").getAsDouble());
          s.setAlpha((float)jsonMod.get(s.getName() + "a").getAsDouble());
        } 
      } 
      if (jsonMod.get("toggled").getAsBoolean()) {
        ModuleManager.FileManagerLoader.add(m);
        m.setToggled(true);
      } 
    } 
  }
  
  public static void loadSearchData() {
    try {
      BufferedReader loadJson = new BufferedReader(new FileReader(SEARCH));
      JsonArray json = (JsonArray)jsonParser.parse(loadJson);
      loadJson.close();
      SearchGuiSettings.fromJson((JsonElement)json);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void saveSearchData() {
    try {
      JsonElement json = SearchGuiSettings.toJson();
      PrintWriter saveJson = new PrintWriter(new FileWriter(SEARCH));
      saveJson.println(gsonPretty.toJson(json));
      saveJson.close();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void Loadpos() {
    try {
      BufferedReader loadJson = new BufferedReader(new FileReader(ONSCREEN));
      JsonObject moduleJason = (JsonObject)jsonParser.parse(loadJson);
      loadJson.close();
      for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)moduleJason.entrySet()) {
        JsonObject jsonMod = (JsonObject)entry.getValue();
        for (Frame frame : ClickGui.frames) {
          if (((String)entry.getKey()).equals(frame.getName())) {
            frame.setX(jsonMod.get("x").getAsInt());
            frame.setY(jsonMod.get("y").getAsInt());
            frame.setOpen(jsonMod.get("open").getAsBoolean());
          } 
        } 
      } 
    } catch (NullPointerException|IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void loadFriends() {
    List<String> friends = read(FRIENDS);
    for (String name : friends)
      FriendManager.addFriend(name); 
  }
  
  public static void saveXRayData() {
    try {
      JsonElement json = XrayGuiSettings.toJson();
      PrintWriter saveJson = new PrintWriter(new FileWriter(XRAYDATA));
      saveJson.println(gsonPretty.toJson(json));
      saveJson.close();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void loadXRayData() {
    try {
      BufferedReader loadJson = new BufferedReader(new FileReader(XRAYDATA));
      JsonArray json = (JsonArray)jsonParser.parse(loadJson);
      loadJson.close();
      XrayGuiSettings.fromJson((JsonElement)json);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void saveframes() {
    try {
      JsonObject json = new JsonObject();
      for (Frame frame : ClickGui.frames) {
        JsonObject jsonHack = new JsonObject();
        jsonHack.addProperty("x", Integer.valueOf(frame.getX()));
        jsonHack.addProperty("y", Integer.valueOf(frame.getY()));
        jsonHack.addProperty("open", Boolean.valueOf(frame.isOpen()));
        json.add(frame.getName(), (JsonElement)jsonHack);
      } 
      PrintWriter saveJson = new PrintWriter(new FileWriter(ONSCREEN));
      saveJson.println(gsonPretty.toJson((JsonElement)json));
      saveJson.close();
    } catch (NullPointerException|IOException e) {
      e.printStackTrace();
    } 
  }
  
  public static void saveFriends() {
    write(FRIENDS, FriendManager.friendsList, true, true);
  }
  
  public static void write(File outputFile, List<String> writeContent, boolean newline, boolean overrideContent) {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(outputFile, !overrideContent));
      for (String outputLine : writeContent) {
        writer.write(outputLine);
        writer.flush();
        if (newline)
          writer.newLine(); 
      } 
    } catch (Exception ex) {
      try {
        if (writer != null)
          writer.close(); 
      } catch (Exception exception) {}
    } 
  }
  
  public static List<String> read(File inputFile) {
    ArrayList<String> readContent = new ArrayList<>();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(inputFile));
      String line;
      while ((line = reader.readLine()) != null)
        readContent.add(line); 
    } catch (Exception ex) {
      try {
        if (reader != null)
          reader.close(); 
      } catch (Exception exception) {}
    } 
    return readContent;
  }
}
