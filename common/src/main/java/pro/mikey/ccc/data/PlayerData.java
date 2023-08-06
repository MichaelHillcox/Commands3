package pro.mikey.ccc.data;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import pro.mikey.ccc.struct.Home;
import pro.mikey.ccc.utils.CompoundUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

public class PlayerData {
    private LinkedList<Home> homes = new LinkedList<>();

    private PlayerData() {

    }

    public static PlayerData get(ServerPlayer player) {
        return Store.get(player.server)
                .playerDataMap
                .computeIfAbsent(player.getUUID(), uuid -> new PlayerData());
    }

    public void save(ServerPlayer player) {
        Store.get(player.server).setDirty();
    }

    public LinkedList<Home> homes() {
        return homes;
    }

    public Optional<Home> getHome(String name) {
        return homes.stream()
                .filter(home -> home.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public void addHome(Home home) {
        homes.add(home);
    }

    public static PlayerData deserialize(CompoundTag tag) {
        var data = new PlayerData();

        var homes = tag.getList("homes", 10);
        for (var home : homes) {
            data.homes.add(Home.deserialize((CompoundTag) home));
        }

        return data;
    }

    public CompoundTag serialize() {
        var tag = new CompoundTag();
        var list = new ListTag();
        for (var home : homes) {
            list.add(home.serialize());
        }
        tag.put("homes", list);
        return tag;
    }

    private static class Store extends SavedData {
        private HashMap<UUID, PlayerData> playerDataMap = new HashMap<>();

        private static Store INSTANCE;

        public Store() {
        }

        @Override
        public CompoundTag save(CompoundTag compoundTag) {
            compoundTag.put("playerDataMap", CompoundUtils.writeMapToCompound(
                    playerDataMap,
                    UUID::toString,
                    PlayerData::serialize
            ));

            return compoundTag;
        }

        public static Store get(MinecraftServer server) {
            ServerLevel level = server.getLevel(Level.OVERWORLD);
            Preconditions.checkArgument(level != null, "Overworld is null");

            if (INSTANCE == null) {
                INSTANCE = level.getDataStorage().computeIfAbsent(Store::deserialize, Store::new, "ccc-player-store");
            }

            return INSTANCE;
        }

        public static Store deserialize(CompoundTag tag) {
            var store = new Store();
            var playerDataMap = tag.getCompound("playerDataMap");
            store.playerDataMap.putAll(
                    CompoundUtils.readMapFromCompound(
                            playerDataMap,
                            UUID::fromString,
                            (compoundTag, key) -> PlayerData.deserialize(compoundTag.getCompound(key))
                    )
            );
            return store;
        }
    }
}
