package me.eastpenguin.cratemanager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

class PreviewGUI(
    player: Player,
    shulker: ShulkerBox
) : InventoryHolder {

    private val inventory = Bukkit.createInventory(
        this,
        45,
        Component.text("crate preview").color(NamedTextColor.DARK_GRAY)
    )

    private val rewards = mutableListOf<CrateItem>()

    private val stainedglass = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
        val meta = itemMeta
        meta.displayName(Component.text(" "))
        itemMeta = meta
    }

    private val contentSlots = listOf(
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34
    )

    private val guiSlots = mutableListOf<Int>()

    private val shulkerSlots = mutableListOf<Int>()

    init {

        inventory.apply {

            // Exit button
            setItem(40, ItemStack(Material.BARRIER).apply {
                val meta = itemMeta
                meta.displayName(Component.text("Exit").color(NamedTextColor.DARK_RED))
                itemMeta = meta
            })

            // Fill borders
            val slots = mutableListOf<Int>().apply {
                addAll(0..9)
                addAll(listOf(17, 18, 26, 27))
                addAll(35..39)
                addAll(41..44)
            }

            slots.forEach {
                setItem(it, stainedglass)
            }

            // OP buttons
            if (player.isOp) {

                setItem(0, ItemStack(Material.PAPER).apply {
                    val meta = itemMeta
                    meta.displayName(Component.text("INFO").color(NamedTextColor.GRAY))
                    meta.lore(
                        listOf(
                            Component.text(""),
                            Component.text("-when adding rewards to your crate keep the 1st and last items in each row empty or else they wont show up in the preview.").color(NamedTextColor.GRAY),
                            Component.text("-when you add an item the weight is automatically 1, to change this use /setweight <weight>.").color(NamedTextColor.GRAY),
                            Component.text("-the first column is where you put your settings.").color(NamedTextColor.GRAY),
                            Component.text("-to set a key go into edit and place your key item in the slot in the 2nd row in the 1st column.").color(NamedTextColor.GRAY),
                            Component.text("-to add a name to your crate, name a piece of paper what you want to name it and put the paper in the slot in the 3rd row and 1st column,").color(NamedTextColor.GRAY),
                            Component.text("you need to interact with your crate before it will show up above it.").color(NamedTextColor.GRAY),
                            Component.text(""),
                            Component.text("(only operators can see this)").color(NamedTextColor.DARK_GRAY),
                            Component.text("")
                        )
                    )
                    itemMeta = meta
                })

                setItem(39, ItemStack(Material.TRIPWIRE_HOOK).apply {
                    val meta = itemMeta
                    meta.displayName(Component.text("Give crate key").color(NamedTextColor.GREEN))
                    meta.lore(
                        listOf(
                            Component.text(""),
                            Component.text("(only operators can see this)").color(NamedTextColor.DARK_GRAY),
                            Component.text("")
                        )
                    )
                    itemMeta = meta
                })

                setItem(41, ItemStack(Material.BRUSH).apply {
                    val meta = itemMeta
                    meta.displayName(Component.text("Edit").color(NamedTextColor.GREEN))
                    meta.lore(
                        listOf(
                            Component.text(""),
                            Component.text("(only operators can see this)").color(NamedTextColor.DARK_GRAY),
                            Component.text("")
                        )
                    )
                    itemMeta = meta
                })
            }

            // GUI + shulker slot mapping
            for (row in 0..2) {
                for (col in 1..7) {
                    val slot = row * 9 + col
                    shulkerSlots.add(slot)
                    guiSlots.add(contentSlots[shulkerSlots.size - 1])
                }
            }

            for (slot in shulkerSlots) {

                val item = shulker.inventory.getItem(slot) ?: continue
                if (item.type.isAir) continue

                val weight = getWeight(item)

                rewards += CrateItem(
                    slot = slot,
                    item = item.clone(),
                    weight = weight
                )
            }

            val totalWeight = rewards.sumOf { it.weight }


            for (crateItem in rewards) {

                val guiSlotIndex = shulkerSlots.indexOf(crateItem.slot)
                if (guiSlotIndex == -1) continue

                val guiSlot = guiSlots[guiSlotIndex]

                val guiItem = addChanceLore(
                    crateItem.item,
                    crateItem.weight,
                    totalWeight
                )

                setItem(guiSlot, guiItem)
            }

            CrateData.openShulkers[player.uniqueId] = shulker
            player.openInventory(inventory)
        }
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}

data class CrateItem(
    val slot: Int,
    val item: ItemStack,
    val weight: Int
)