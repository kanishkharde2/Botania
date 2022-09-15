/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.decor.BotaniaMushroomBlock;
import vazkii.botania.common.crafting.RecipeSerializerBase;
import vazkii.botania.common.item.ItemTwigWand;
import vazkii.botania.common.item.material.MysticalPetalItem;

public class WandOfTheForestRecipe extends ShapedRecipe {
	public static final RecipeSerializer<WandOfTheForestRecipe> SERIALIZER = new Serializer();

	public WandOfTheForestRecipe(ShapedRecipe compose) {
		super(compose.getId(), compose.getGroup(), compose.getWidth(), compose.getHeight(), compose.getIngredients(), compose.getResultItem());
	}

	@NotNull
	@Override
	public ItemStack assemble(CraftingContainer inv) {
		int first = -1;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			Item item = stack.getItem();

			int colorId;
			if (item instanceof MysticalPetalItem petal) {
				colorId = petal.color.getId();
			} else if (item instanceof BlockItem block && block.getBlock() instanceof BotaniaMushroomBlock mushroom) {
				colorId = mushroom.color.getId();
			} else {
				continue;
			}
			if (first == -1) {
				first = colorId;
			} else {
				return ItemTwigWand.setColors(getResultItem().copy(), first, colorId);
			}
		}
		return ItemTwigWand.setColors(getResultItem().copy(), first != -1 ? first : 0, 0);
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	private static class Serializer extends RecipeSerializerBase<WandOfTheForestRecipe> {
		@NotNull
		@Override
		public WandOfTheForestRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
			return new WandOfTheForestRecipe(SHAPED_RECIPE.fromJson(recipeId, json));
		}

		@NotNull
		@Override
		public WandOfTheForestRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
			return new WandOfTheForestRecipe(SHAPED_RECIPE.fromNetwork(recipeId, buffer));
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull WandOfTheForestRecipe recipe) {
			SHAPED_RECIPE.toNetwork(buffer, recipe);
		}
	}
}
