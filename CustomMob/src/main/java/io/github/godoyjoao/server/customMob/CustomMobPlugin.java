package io.github.godoyjoao.server.customMob;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomMobPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

        LiteralCommandNode<CommandSourceStack> command = CustomMobCommand.createCommand(1).build();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(command);
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
