package at.hannibal2.skyhanni.features.fishing

import at.hannibal2.skyhanni.events.MobEvent
import at.hannibal2.skyhanni.events.SeaCreatureFishEvent
import at.hannibal2.skyhanni.features.dungeon.DungeonAPI
import at.hannibal2.skyhanni.skyhannimodule.SkyHanniModule
import at.hannibal2.skyhanni.utils.ChatUtils
import at.hannibal2.skyhanni.utils.InventoryUtils
import at.hannibal2.skyhanni.utils.ItemUtils.itemName
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
        ChatUtils.chat("[DEBUG] SeaCreatureFish 0")
        if (!isEnabled()) return

        ChatUtils.chat("[DEBUG] SeaCreatureFish 1")

        val creature = event.seaCreature
        if (creature.rarity.id > 4) return

        ChatUtils.chat("[DEBUG] SeaCreatureFish 2")

        seaCreatures.add(creature.name)

        initialPitch = LorenzUtils.getPlayer()?.cameraPitch ?: return
        smoothRotate(90f)
    }

    @SubscribeEvent
    fun onMobDeSpawn(event: MobEvent.DeSpawn.SkyblockMob) {
        seaCreatures.remove(event.mob.name)

        ChatUtils.chat("[DEBUG] MobDeSpawn")

        smoothRotate(initialPitch)
    }

    private fun smoothRotate(pitch: Float) {
        val player = LorenzUtils.getPlayer() ?: return

        val initialPitch = player.cameraPitch
        val diffPitch = initialPitch - pitch

        var time = 0

        while (time < 5) {
            val pitchToSet = initialPitch + diffPitch * (0.50 + time * 0.10)
            player.cameraPitch = pitchToSet.toFloat()

            time += 1
        }

        ChatUtils.chat("[DEBUG] smoothRotate")
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (!isEnabled() || seaCreatures.isEmpty()) return
        ChatUtils.chat("[DEBUG] Tick 1")
        if (event.phase != TickEvent.Phase.END) return
        ChatUtils.chat("[DEBUG] Tick 2")

        val player = LorenzUtils.getPlayer() ?: return
        ChatUtils.chat("[DEBUG] Tick 3")
        val item = InventoryUtils.getItemsInHotbar().indexOfFirst { it.itemName.contains("Hyperion") }

        if (item == -1) return
        ChatUtils.chat("[DEBUG] Tick 4")

        player.inventory.currentItem = item
        KeyBinding.onTick(Minecraft.getMinecraft().gameSettings.keyBindUseItem.keyCode)
    }

    private fun isEnabled() = LorenzUtils.inSkyBlock && !DungeonAPI.inDungeon() && !LorenzUtils.inKuudraFight
}
