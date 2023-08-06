package pro.mikey.ccc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public interface Command {
    LiteralArgumentBuilder<CommandSourceStack> register();
}
