package com.sk89q.craftbook.sponge.mechanics;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.me4502.modularframework.module.Module;
import com.me4502.modularframework.module.guice.ModuleConfiguration;
import com.sk89q.craftbook.core.util.ConfigValue;
import com.sk89q.craftbook.core.util.CraftBookException;
import com.sk89q.craftbook.core.util.PermissionNode;
import com.sk89q.craftbook.core.util.documentation.DocumentationProvider;
import com.sk89q.craftbook.sponge.mechanics.types.SpongeBlockMechanic;
import com.sk89q.craftbook.sponge.util.BlockUtil;
import com.sk89q.craftbook.sponge.util.SpongePermissionNode;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.cause.Named;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.permission.PermissionDescription;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;

@Module(moduleName = "LightStone", onEnable="onInitialize", onDisable="onDisable")
public class LightStone extends SpongeBlockMechanic implements DocumentationProvider {

    @Inject
    @ModuleConfiguration
    public ConfigurationNode config;

    private SpongePermissionNode permissionNode = new SpongePermissionNode("craftbook.lightstone.use", "Allows usage of the LightStone mechanic", PermissionDescription.ROLE_USER);

    private ConfigValue<ItemStack> lightstoneItem = new ConfigValue<>("lightstone-item", "The item that triggers the LightStone mechanic.", ItemStack.builder().itemType(ItemTypes.GLOWSTONE_DUST).build(), TypeToken.of(ItemStack.class));

    @Override
    public void onInitialize() throws CraftBookException {
        super.onInitialize();

        permissionNode.register();

        lightstoneItem.load(config);
    }

    @Listener
    public void onPlayerInteract(InteractBlockEvent.Secondary event, @Named(NamedCause.SOURCE) Player player) {
        int lightLevel = BlockUtil.getLightLevel(event.getTargetBlock().getLocation().get());
        if (lightLevel >= 0 && permissionNode.hasPermission(player) && player.getItemInHand().isPresent() && player.getItemInHand().get().getItem() == lightstoneItem.getValue().getItem()) {
            player.sendMessage(getCurrentLine(lightLevel));
            event.setCancelled(true);
        }
    }

    private static Text getCurrentLine(int lightLevel) {
        Text.Builder builder = Text.builder();
        builder.append(Text.of(TextColors.YELLOW, "LightStone: ["));
        TextColor color;
        if (lightLevel >= 9)
            color = TextColors.GREEN;
        else
            color = TextColors.DARK_RED;

        for (int i = 0; i < lightLevel; i++)
            builder.append(Text.of(color, "|"));

        for (int i = lightLevel; i < 15; i++)
            builder.append(Text.of(TextColors.BLACK, "|"));
        builder.append(Text.of(TextColors.YELLOW, ']'));
        builder.append(Text.of(TextColors.WHITE, " " + lightLevel + " L"));
        return builder.build();
    }

    @Override
    public boolean isValid(Location location) {
        return BlockUtil.getLightLevel(location) >= 0;
    }

    @Override
    public String getPath() {
        return "mechanics/lightstone";
    }

    @Override
    public String[] getMainDocumentation() {
        return new String[]{
                "The LightStone mechanic allows you to get the current light level for a block. Right click any block while holding GlowStone Dust to see the light level.",
                "",
                "If the light level is high enough that monsters won't spawn, the bar is shown as green. If they are able to spawn at the clicked location, it will be shown as red."
        };
    }

    @Override
    public ConfigValue<?>[] getConfigurationNodes() {
        return new ConfigValue<?>[] {
                lightstoneItem
        };
    }

    @Override
    public PermissionNode[] getPermissionNodes() {
        return new PermissionNode[] {
                permissionNode
        };
    }
}