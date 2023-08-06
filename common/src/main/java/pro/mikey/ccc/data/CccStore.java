package pro.mikey.ccc.data;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import pro.mikey.ccc.struct.Location;
import pro.mikey.ccc.utils.CompoundUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

class CccStore extends SavedData {
    private final HashMap<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final LinkedList<Location> warps = new LinkedList<>();

    private static CccStore INSTANCE;

    public CccStore() {
    }

    public HashMap<UUID, PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

    public static CccStore get(MinecraftServer server) {
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        Preconditions.checkArgument(level != null, "Overworld is null");

        if (INSTANCE == null) {
            INSTANCE = level.getDataStorage().computeIfAbsent(CccStore::deserialize, CccStore::new, "ccc-player-store");
        }

        return INSTANCE;
    }

    @Override
    @NotNull
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("playerDataMap", CompoundUtils.writeMapToCompound(
                playerDataMap,
                UUID::toString,
                PlayerData::serialize
        ));

        compoundTag.put("warps", CompoundUtils.writeListToCompound(
                warps,
                Location::serialize
        ));

        return compoundTag;
    }

    public static CccStore deserialize(CompoundTag tag) {
        var store = new CccStore();

        var playerDataMap = tag.getCompound("playerDataMap");
        store.playerDataMap.putAll(
                CompoundUtils.readMapFromCompound(
                        playerDataMap,
                        UUID::fromString,
                        (compoundTag, key) -> PlayerData.deserialize(compoundTag.getCompound(key)))
        );

        var warps = tag.getList("warps", 10);
        store.warps.addAll(CompoundUtils.readListFromCompound(warps, Location::deserialize));
        return store;
    }
}
