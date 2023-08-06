package pro.mikey.ccc.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class WarpsCommand implements Command {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("warps")
                .then(Commands.literal("list").executes(context -> {
                    return 1;
                }));
    }
}
