package io.github.godoyjoao.server.customMob;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class CustomMobCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("customMob")
                .then(Commands.argument("id", IntegerArgumentType.integer(1))
                        .executes(CustomMobCommand::createCustomMob)
                );
    }

    private static int createCustomMob(CommandContext<CommandSourceStack> ctx) {
        int id = IntegerArgumentType.getInteger(ctx, "id");
        CommandSender sender = ctx.getSource().getSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Esse comando só pode ser executado por um jogador!");
            return Command.SINGLE_SUCCESS;
        }

        Location location = player.getLocation();
        World world = location.getWorld();
        Zombie zombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);

        zombie.customName(Component.text("Zumbi",TextColor.color(200,200,50)));
        zombie.setCustomNameVisible(true);
        zombie.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50.0);
        zombie.setHealth(50.0);

        return Command.SINGLE_SUCCESS;
    }
}
