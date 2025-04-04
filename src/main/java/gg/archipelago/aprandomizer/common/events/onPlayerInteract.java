package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.attachments.APAttachmentTypes;
import gg.archipelago.aprandomizer.items.CompassReward;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class onPlayerInteract {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onPlayerBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide().isClient()) return;
        // stop all right click interactions if game has not started.
        if (APRandomizer.isJailPlayers())
            event.setCanceled(true);
        if (!event.getItemStack().getItem().equals(Items.COMPASS) || !event.getItemStack().has(DataComponents.CUSTOM_DATA)) {
            return;
        }

        BlockState block = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if (event.getItemStack().get(DataComponents.CUSTOM_DATA).copyTag().get("structure") != null && block.is(Blocks.LODESTONE))
            event.setCanceled(true);

        event.getEntity().getServer().execute(() -> {
            event.getEntity().getInventory().setChanged();
            event.getEntity().inventoryMenu.broadcastChanges();
        });
    }

    @SubscribeEvent
    static void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
        if(event.getSide().isClient())
            return;
        // stop all right click interactions if game has not started.
        if (APRandomizer.isJailPlayers())
            event.setCanceled(true);
        if (event.getItemStack().getItem().equals(Items.COMPASS) && event.getItemStack().has(DataComponents.CUSTOM_DATA)) {
            HolderLookup.Provider registries = event.getLevel().registryAccess();
            ItemStack compass = event.getItemStack();
            CompoundTag nbt = compass.get(DataComponents.CUSTOM_DATA).copyTag();

            //fetch our current compass list.
            List<CompassReward> compasses = event.getEntity().getData(APAttachmentTypes.AP_PLAYER).getUnlockedCompassRewards();

            Optional<CompassReward> currentCompassReward = nbt.read("structure", CompassReward.CODEC, registries.createSerializationContext(NbtOps.INSTANCE));
            Optional<Integer> currentCompassIndex = nbt.getInt("index");

            if (currentCompassReward.isEmpty() || currentCompassIndex.isEmpty())
                return;

            int newCompassIndex = currentCompassIndex.get() + 1;
            if (compasses.size() <= newCompassIndex) {
                newCompassIndex = 0;
            }
            nbt.putInt("index", newCompassIndex);
            compass.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            CompassReward newCompassReward = compasses.get(newCompassIndex);

            ItemManager.updateCompassLocation(newCompassReward, event.getEntity(), compass);

        }
    }
}
