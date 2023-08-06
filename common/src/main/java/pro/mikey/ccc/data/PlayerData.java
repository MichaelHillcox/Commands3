package pro.mikey.ccc.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import pro.mikey.ccc.struct.Location;

import java.util.LinkedList;
import java.util.Optional;

public class PlayerData {
    private final LinkedList<Location> homes = new LinkedList<>();

    private PlayerData() {

    }

    public static PlayerData get(ServerPlayer player) {
        return CccStore.get(player.server)
                .getPlayerDataMap()
                .computeIfAbsent(player.getUUID(), uuid -> new PlayerData());
    }

    public void save(ServerPlayer player) {
        CccStore.get(player.server).setDirty();
    }

    public LinkedList<Location> homes() {
        return homes;
    }

    public Optional<Location> getHome(String name) {
        return homes.stream()
                .filter(home -> home.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public void addHome(Location home) {
        homes.add(home);
    }

    public static PlayerData deserialize(CompoundTag tag) {
        var data = new PlayerData();

        var homes = tag.getList("homes", 10);
        for (var home : homes) {
            data.homes.add(Location.deserialize((CompoundTag) home));
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

}
