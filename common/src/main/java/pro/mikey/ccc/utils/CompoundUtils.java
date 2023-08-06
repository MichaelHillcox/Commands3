package pro.mikey.ccc.utils;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CompoundUtils {
    public static  <K, V> CompoundTag writeMapToCompound(Map<K, V> map, Function<K, String> keyWriter, Function<V, CompoundTag> valueWriter) {
        var tag = new CompoundTag();
        for (var entry : map.entrySet()) {
            tag.put(keyWriter.apply(entry.getKey()), valueWriter.apply(entry.getValue()));
        }

        return tag;
    }

    public static  <K, V> Map<K, V> readMapFromCompound(CompoundTag tag, Function<String, K> keyLoader, BiFunction<CompoundTag, String, V> valueLoader) {
        HashMap<K, V> map = new HashMap<>();
        for (String key : tag.getAllKeys()) {
            map.put(keyLoader.apply(key), valueLoader.apply(tag, key));
        }

        return map;
    }
}
