package at.martinthedragon.ntm.radiation

import at.martinthedragon.ntm.capabilites.IIrradiationHandlerModifiable
import net.minecraft.nbt.CompoundNBT
import net.minecraftforge.common.util.INBTSerializable

open class EntityIrradiationHandler : IIrradiationHandlerModifiable, INBTSerializable<CompoundNBT> {
    protected var irradiationLevel = 0f

    override fun setIrradiation(irradiation: Float) {
        irradiationLevel = irradiation
        onIrradiationLevelChanged()
    }

    override fun getIrradiation(): Float = irradiationLevel

    override fun serializeNBT(): CompoundNBT = CompoundNBT().apply {
        putFloat("HfrRadiation", irradiationLevel)
    }

    override fun deserializeNBT(nbt: CompoundNBT) {
        irradiationLevel = nbt.getFloat("HfrRadiation")
        onLoad()
    }

    protected fun onLoad() {}
    protected fun onIrradiationLevelChanged() {}
}