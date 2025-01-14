package at.martinthedragon.nucleartech.menu

import at.martinthedragon.nucleartech.api.menu.slot.ExperienceResultSlot
import at.martinthedragon.nucleartech.block.entity.ShredderBlockEntity
import at.martinthedragon.nucleartech.item.ShredderBladeItem
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.SlotItemHandler

class ShredderMenu(
    windowID: Int,
    playerInventory: Inventory,
    blockEntity: ShredderBlockEntity,
) : NTechContainerMenu<ShredderBlockEntity>(MenuTypes.shredderMenu.get(), windowID, playerInventory, blockEntity) {
    private val inv = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(::Error)

    init {
        for (i in 0 until 3)
            for (j in 0 until 3)
                addSlot(SlotItemHandler(inv, j + i * 3, 44 + j * 18, 18 + i * 18))
        addSlot(SlotItemHandler(inv, 9, 8, 108))
        addSlot(SlotItemHandler(inv, 10, 44, 108))
        addSlot(SlotItemHandler(inv, 11, 80, 108))
        for (i in 0 until 6)
            for (j in 0 until 3)
                addSlot(ExperienceResultSlot(blockEntity, playerInventory.player, inv, j + i * 3 + 12, 116 + j * 18, 18 + i * 18))
        addPlayerInventory(this::addSlot, playerInventory, 8, 140)
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        var returnStack = ItemStack.EMPTY
        val slot = slots[index]
        if (slot.hasItem()) {
            val itemStack = slot.item
            returnStack = itemStack.copy()
            if (index in 12..29) {
                if (!moveItemStackTo(itemStack, 30, slots.size, true)) return ItemStack.EMPTY
                slot.onQuickCraft(itemStack, returnStack)
            } else if (index !in 0..11) {
                var successful = false
                when {
                    itemStack.getCapability(CapabilityEnergy.ENERGY).isPresent && moveItemStackTo(itemStack, 9, 10, false) -> successful = true
                    itemStack.item is ShredderBladeItem && moveItemStackTo(itemStack, 10, 12, false) -> successful = true
                    moveItemStackTo(itemStack, 0, 9, false) -> successful = true
                }
                if (!successful && !tryMoveInPlayerInventory(index, 30, itemStack)) return ItemStack.EMPTY
            } else if (!moveItemStackTo(itemStack, 30, slots.size, false)) return ItemStack.EMPTY

            if (itemStack.isEmpty) slot.set(ItemStack.EMPTY)
            else slot.setChanged()

            if (itemStack.count == returnStack.count) return ItemStack.EMPTY

            slot.onTake(player, itemStack)
        }
        return returnStack
    }

    fun getLeftBladeState(): Int = computeBladeState(10)

    fun getRightBladeState(): Int = computeBladeState(11)

    private fun computeBladeState(slotIndex: Int): Int {
        val shredderBlade = items[slotIndex]
        if (shredderBlade.item !is ShredderBladeItem) return 0
        return when {
            !shredderBlade.isDamageableItem -> 1
            shredderBlade.damageValue < shredderBlade.maxDamage / 2 -> 1
            shredderBlade.damageValue != shredderBlade.maxDamage -> 2
            else -> 3
        }
    }

    companion object {
        fun fromNetwork(windowID: Int, playerInventory: Inventory, buffer: FriendlyByteBuf) =
            ShredderMenu(windowID, playerInventory, getBlockEntityForContainer(buffer))
    }
}
