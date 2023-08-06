package pro.mikey.ccc.struct;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record Location(
    UUID uuid,
    String name,
    GlobalPos pos,
    float yaw,
    float pitch
) {
    public static Location create(String name, ServerPlayer player) {
        return new Location(
            UUID.randomUUID(),
            name,
            GlobalPos.of(player.level().dimension(), player.blockPosition()),
            player.getYRot(),
            player.getXRot()
        );
    }

    public boolean teleportTo(ServerPlayer player) {
        var vehicle = player.getVehicle();

        if (vehicle != null) {
            player.stopRiding();
        }

        GlobalPos pos = this.pos();
        ServerLevel playerLevel = player.server.getLevel(pos.dimension());
        if (playerLevel == null) {
            return false;
        }

        // Store XP
        var xp = player.experienceLevel;
        player.teleportTo(playerLevel, pos.pos().getX() + .5D, pos.pos().getY() + .1D, pos.pos().getZ() + .5D, this.yaw(), this.pitch());
        player.setExperienceLevels(xp);

        return true;
    }

    public CompoundTag serialize() {
        var tag = new CompoundTag();
        tag.putUUID("uuid", uuid);
        tag.putString("name", name);

        DataResult<Tag> result = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos);
        tag.put("pos", result.result().orElseThrow().copy());

        tag.putFloat("yaw", yaw);
        tag.putFloat("pitch", pitch);
        return tag;
    }

    public static Location deserialize(CompoundTag tag) {
        var pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.get("pos")).result().orElseThrow();

        return new Location(
            tag.getUUID("uuid"),
            tag.getString("name"),
            pos,
            tag.getFloat("yaw"),
            tag.getFloat("pitch")
        );
    }
}
