/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.lens;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.ForceRelayBlock;

public class WarpLens extends Lens {

	@Override
	public boolean collideBurst(ManaBurst burst, HitResult pos, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		Entity entity = burst.entity();
		if (entity.level.isClientSide || burst.isFake() || pos.getType() != HitResult.Type.BLOCK) {
			return shouldKill;
		}

		BlockPos hit = ((BlockHitResult) pos).getBlockPos();
		if (entity.level.getBlockState(hit).is(BotaniaBlocks.pistonRelay)) {
			ForceRelayBlock.WorldData data = ForceRelayBlock.WorldData.get(entity.level);
			BlockPos dest = data.mapping.get(hit);

			if (dest != null) {
				entity.setPos(dest.getX() + 0.5, dest.getY() + 0.5, dest.getZ() + 0.5);
				burst.setCollidedAt(dest);

				burst.setWarped(true);

				return false;
			}
		}
		return shouldKill;
	}
}
