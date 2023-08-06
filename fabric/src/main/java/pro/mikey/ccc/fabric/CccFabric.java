package pro.mikey.ccc.fabric;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.mikey.ccc.Ccc;

public class CccFabric implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CccFabric.class);

    @Override
    public void onInitialize() {
        Ccc.init();
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> commandSourceStackCommandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        var namespace = Commands.literal("ccc");
        Ccc.commands.forEach(command -> namespace.then(command.register()));

        commandSourceStackCommandDispatcher.register(namespace);
    }
}
