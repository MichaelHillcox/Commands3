package pro.mikey.ccc.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;
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

    public static <T> List<T> readListFromCompound(ListTag tag, Function<CompoundTag, T> valueLoader) {
        List<T> list = new ArrayList<>();
        for (Tag value : tag) {
            list.add(valueLoader.apply((CompoundTag) value));
        }
        return list;
    }

    public static <T> ListTag writeListToCompound(Collection<T> list, Function<T, CompoundTag> valueWriter) {
        var listTag = new ListTag();
        for (var value : list) {
            listTag.add(valueWriter.apply(value));
        }

        return listTag;
    }
}
