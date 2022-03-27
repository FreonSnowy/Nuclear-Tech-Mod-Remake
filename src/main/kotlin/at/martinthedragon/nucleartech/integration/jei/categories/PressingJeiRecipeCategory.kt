package at.martinthedragon.nucleartech.integration.jei.categories

import at.martinthedragon.nucleartech.ModBlockItems
import at.martinthedragon.nucleartech.NuclearTech
import at.martinthedragon.nucleartech.ntm
import at.martinthedragon.nucleartech.recipes.PressingRecipe
import com.mojang.blaze3d.vertex.PoseStack
import mezz.jei.api.constants.VanillaTypes
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient

class PressingJeiRecipeCategory(guiHelper: IGuiHelper) : IRecipeCategory<PressingRecipe> {
    private val background = guiHelper.createDrawable(GUI_RESOURCE, 0, 0, 81, 54)
    private val icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, ItemStack(ModBlockItems.steamPress.get()))
    private val pressArrow = guiHelper.drawableBuilder(GUI_RESOURCE, 0, 54, 18, 16).buildAnimated(20, IDrawableAnimated.StartDirection.TOP, false)

    override fun getUid() = UID

    override fun getRecipeClass(): Class<out PressingRecipe> = PressingRecipe::class.java

    override fun getTitle() = TranslatableComponent("jei.${NuclearTech.MODID}.category.pressing")

    override fun getBackground(): IDrawable = background

    override fun getIcon(): IDrawable = icon

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: PressingRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 37).addItemStack(recipe.result)
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(Ingredient.of(recipe.stampType.tag))
        builder.addSlot(RecipeIngredientRole.OUTPUT, 64, 19).addItemStack(recipe.resultItem)
    }

    override fun draw(recipe: PressingRecipe, matrixStack: PoseStack, mouseX: Double, mouseY: Double) {
        pressArrow.draw(matrixStack, 0, 19)
        drawExperience(recipe, matrixStack)
    }

    private fun drawExperience(recipe: PressingRecipe, matrixStack: PoseStack) {
        val experience = recipe.experience
        if (experience > 0) {
            val experienceString = TranslatableComponent("jei.${NuclearTech.MODID}.category.pressing.experience", experience)
            val fontRenderer = Minecraft.getInstance().font
            val stringWidth = fontRenderer.width(experienceString)
            fontRenderer.draw(matrixStack, experienceString, (background.width - stringWidth).toFloat(), 0F, -0x7F7F80)
        }
    }

    companion object {
        val GUI_RESOURCE = ntm("textures/gui/jei_press.png")
        val UID = ntm("pressing")
    }
}
