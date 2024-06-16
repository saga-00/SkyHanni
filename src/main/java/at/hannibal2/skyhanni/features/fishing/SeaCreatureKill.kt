package at.hannibal2.skyhanni.features.fishing

import at.hannibal2.skyhanni.events.MobEvent
import at.hannibal2.skyhanni.events.SeaCreatureFishEvent
import at.hannibal2.skyhanni.features.dungeon.DungeonAPI
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.ChatUtils
import at.hannibal2.skyhanni.utils.InventoryUtils
import at.hannibal2.skyhanni.utils.LorenzUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@SkyHanniModule
object SeaCreatureKill {

    private var seaCreatures = mutableSetOf<String>()
    private var initialPitch = 0f

    @SubscribeEvent
    fun onSeaCreatureFish(event: SeaCreatureFishEvent) {
        if (!isEnabled()) return

        val creature = event.seaCreature
        if (creature.rarity.id > 4) return

        seaCreatures.add(creature.name)

        initialPitch = LorenzUtils.getPlayer()?.cameraPitch ?: return
        smoothRotate(-90f)
    }

    @SubscribeEvent
    fun onMobDeSpawn(event: MobEvent.DeSpawn.SkyblockMob) {
        if (!isEnabled()) return

        val creature = SeaCreatureManager.allFishingMobs[event.mob.name] ?: return
        seaCreatures.remove(creature.name)

        if (seaCreatures.isEmpty()) smoothRotate(initialPitch)
    }

    private fun smoothRotate(pitch: Float) {
        val player = LorenzUtils.getPlayer() ?: return

        val initialPitch = player.cameraPitch
        val diffPitch = initialPitch - pitch

        var time = 0

        repeat(5) {
                val pitchToSet = initialPitch + diffPitch * (0.50 + time * 0.10)
                player.rotationPitch = pitchToSet.toFloat()

                time += 1
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!isEnabled() || seaCreatures.isEmpty()) return
        if (event.phase != TickEvent.Phase.END) return

        val player = LorenzUtils.getPlayer() ?: return
        val item = InventoryUtils.getItemsInHotbar().indexOfFirst { it.displayName.contains("Hyperion") }
        if (item == -1) return

        player.inventory.currentItem = item
        KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindUseItem.keyCode)
    }

    private fun isEnabled() = LorenzUtils.inSkyBlock && !DungeonAPI.inDungeon() && !LorenzUtils.inKuudraFight
}
