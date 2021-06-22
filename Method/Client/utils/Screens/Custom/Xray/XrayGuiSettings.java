package Method.Client.utils.Screens.Custom.Xray;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import net.minecraft.block.Block;

public class XrayGuiSettings {
  public static ArrayList<String> blockNames = new ArrayList<>();
  
  public static String[] defaultNames;
  
  public static String getName(Block block) {
    return "" + Block.REGISTRY.getNameForObject(block);
  }
  
  public XrayGuiSettings(Block... blocks) {
    Arrays.<Block>stream(blocks).parallel().map(XrayGuiSettings::getName)
      .distinct().sorted().forEachOrdered(blockNames::add);
    defaultNames = blockNames.<String>toArray(new String[0]);
  }
  
  public static List<String> getBlockNames() {
    return Collections.unmodifiableList(blockNames);
  }
  
  public static List<String> getAllBlockNames(List<String> blockNames) {
    ArrayList<String> Allblocks = new ArrayList<>();
    for (Block block : Block.REGISTRY) {
      if (!blockNames.contains(block.getLocalizedName()))
        Allblocks.add(getName(block)); 
    } 
    return Allblocks;
  }
  
  public static void add(Block block) {
    String name = getName(block);
    if (Collections.binarySearch((List)blockNames, name) >= 0)
      return; 
    blockNames.add(name);
    Collections.sort(blockNames);
  }
  
  public static void remove(int index) {
    if (index < 0 || index >= blockNames.size())
      return; 
    blockNames.remove(index);
  }
  
  public static void resetToDefaults() {
    blockNames.clear();
    blockNames.addAll(Arrays.asList(defaultNames));
  }
  
  public static List<String> SearchBlocksAll(List<String> blockNames, String text) {
    ArrayList<String> Allblocks = new ArrayList<>();
    for (Block block : Block.REGISTRY) {
      if (!blockNames.contains(block.getLocalizedName()) && 
        block.getLocalizedName().toLowerCase().contains(text.toLowerCase()))
        Allblocks.add(getName(block)); 
    } 
    return Allblocks;
  }
  
  public static void fromJson(JsonElement json) {
    if (!json.isJsonArray())
      return; 
    blockNames.clear();
    StreamSupport.stream(json.getAsJsonArray().spliterator(), true)
      .filter(JsonElement::isJsonPrimitive)
      .filter(e -> e.getAsJsonPrimitive().isString())
      .map(e -> Block.getBlockFromName(e.getAsString()))
      .filter(Objects::nonNull).map(XrayGuiSettings::getName).distinct()
      .sorted().forEachOrdered(s -> blockNames.add(s));
  }
  
  public static JsonElement toJson() {
    JsonArray json = new JsonArray();
    blockNames.forEach(s -> json.add((JsonElement)new JsonPrimitive(s)));
    return (JsonElement)json;
  }
}
