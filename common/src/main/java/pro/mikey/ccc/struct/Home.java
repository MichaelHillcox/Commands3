package pro.mikey.ccc.struct;

import com.mojang.serialization.DataResult;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record Home(
    UUID uuid,
    String name,
    GlobalPos pos,
    float yaw,
    float pitch
) {
    public static Home create(String name, ServerPlayer player) {
        return new Home(
            UUID.randomUUID(),
            name,
            GlobalPos.of(player.level().dimension(), player.blockPosition()),
            player.getYRot(),
            player.getXRot()
        );
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

    public static Home deserialize(CompoundTag tag) {
        var pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.get("pos")).result().orElseThrow();

        return new Home(
            tag.getUUID("uuid"),
            tag.getString("name"),
            pos,
            tag.getFloat("yaw"),
            tag.getFloat("pitch")
        );
    }
}
