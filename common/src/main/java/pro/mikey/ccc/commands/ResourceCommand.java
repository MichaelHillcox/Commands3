package pro.mikey.ccc.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public abstract class ResourceCommand<T> {
    public void registerInner(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder
                .then(Commands.literal("list").executes(this::list))
                .then(Commands.literal("add").executes(this::add))
                .then(Commands.literal("edit").executes(this::edit))
                .then(Commands.literal("remove").executes(this::remove))
                .then(Commands.literal("clear").executes(this::clear));
    }

    abstract T holder();

    abstract void actionOn();

    abstract void byProduct();

    abstract int list(CommandContext<CommandSourceStack> ctx);

    abstract int remove(CommandContext<CommandSourceStack> ctx);

    abstract int add(CommandContext<CommandSourceStack> ctx);

    abstract int edit(CommandContext<CommandSourceStack> ctx);

    abstract int clear(CommandContext<CommandSourceStack> ctx);
}
