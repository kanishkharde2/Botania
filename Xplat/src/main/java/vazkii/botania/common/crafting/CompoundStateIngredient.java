/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.StateIngredient;

import java.util.*;
import java.util.stream.Collectors;

public class CompoundStateIngredient implements StateIngredient {
	protected final ImmutableSet<StateIngredient> ingredients;

	protected final Set<BlockState> resolvedBlocks;

	public CompoundStateIngredient(Collection<StateIngredient> ingredients) {
		this.ingredients = ImmutableSet.copyOf(ingredients);
		this.resolvedBlocks = new HashSet<>();
	}

	@Override
	public boolean test(BlockState state) {
		return ingredients.stream().anyMatch(stateIngredient -> stateIngredient.test(state));
	}

	@Override
	public BlockState pick(RandomSource random) {
		return new ArrayList<>(getBlocks()).get(random.nextInt(getBlocks().size()));
	}

	@Override
	public JsonObject serialize() {
		JsonObject object = new JsonObject();
		object.addProperty("type", "compound");
		JsonArray array = new JsonArray();
		for (StateIngredient ingredient : ingredients) {
			array.add(ingredient.serialize());
		}
		object.add("ingredients", array);
		return object;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		ImmutableList<StateIngredient> ingredients = this.ingredients.asList();
		buffer.writeVarInt(3);
		buffer.writeVarInt(ingredients.size());
		for (StateIngredient ingredient : ingredients) {
			ingredient.write(buffer);
		}
	}

	@Override
	public List<ItemStack> getDisplayedStacks() {
		return getBlocks().stream()
				.map(BlockBehaviour.BlockStateBase::getBlock)
				.filter(b -> b.asItem() != Items.AIR)
				.map(ItemStack::new)
				.collect(Collectors.toList());
	}

	@Override
	public List<BlockState> getDisplayed() {
		return new ArrayList<>(getBlocks());
	}

	@NotNull
	protected Set<BlockState> getBlocks() {
		if (this.resolvedBlocks.isEmpty()) {
			for (StateIngredient ingredient : this.ingredients) {
				if (ingredient instanceof BlockStateIngredient stateIngredient) {
					this.resolvedBlocks.add(stateIngredient.getBlock().defaultBlockState());
				} else if (ingredient instanceof BlockStateStateIngredient stateIngredient) {
					this.resolvedBlocks.add(stateIngredient.getState());
				} else if (ingredient instanceof BlocksStateIngredient stateIngredient) {
					this.resolvedBlocks.addAll(stateIngredient.getBlocks().stream().map(Block::defaultBlockState).toList());
				}
			}
		}
		return resolvedBlocks;
	}

	public ImmutableSet<StateIngredient> getIngredients() {
		return ingredients;
	}

	@Override
	public String toString() {
		return "CompoundStateIngredient{" + getIngredients() + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CompoundStateIngredient that = (CompoundStateIngredient) o;

		return Objects.equals(ingredients, that.ingredients);
	}

	@Override
	public int hashCode() {
		return ingredients != null ? ingredients.hashCode() : 0;
	}
}
