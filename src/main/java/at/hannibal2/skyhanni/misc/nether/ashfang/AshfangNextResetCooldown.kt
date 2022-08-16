package at.hannibal2.skyhanni.misc.nether.ashfang

import at.hannibal2.skyhanni.SkyHanniMod
import at.hannibal2.skyhanni.utils.GuiRender.renderString
import at.hannibal2.skyhanni.utils.LorenzUtils
import net.minecraft.client.Minecraft
import net.minecraft.entity.item.EntityArmorStand
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import java.text.DecimalFormat

class AshfangNextResetCooldown {

    private var spawnTime = 1L

    @SubscribeEvent
    fun renderOverlay(event: ClientTickEvent) {
        if (!isEnabled()) return

        if (Minecraft.getMinecraft().theWorld.loadedEntityList.any {
                it is EntityArmorStand && it.posY > 145 &&
                        (it.name.contains("§c§9Ashfang Acolyte§r") || it.name.contains("§c§cAshfang Underling§r"))
            }) {
            spawnTime = System.currentTimeMillis()
        }
    }

    @SubscribeEvent
    fun renderOverlay(event: RenderGameOverlayEvent.Post) {
        if (!isEnabled()) return
        if (spawnTime == -1L) return

        val remainingTime = spawnTime + 46_100 - System.currentTimeMillis()
        if (remainingTime > 0) {
            val remaining = (remainingTime.toFloat() / 1000)
            val format = DecimalFormat("0.0").format(remaining + 0.1)
            SkyHanniMod.feature.abilities.ashfangNextResetCooldownPos.renderString("§cAshfang next reset in: §a${format}s")
        } else {
            spawnTime = -1
        }
    }

    @SubscribeEvent
    fun renderOverlay(event: WorldEvent.Load) {
        spawnTime = -1
    }

    private fun isEnabled(): Boolean {
        return LorenzUtils.inSkyblock && SkyHanniMod.feature.abilities.ashfangNextResetCooldown
    }
}