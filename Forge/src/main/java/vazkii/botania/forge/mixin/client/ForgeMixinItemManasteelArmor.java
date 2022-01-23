package vazkii.botania.forge.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;

import org.spongepowered.asm.mixin.Mixin;

import vazkii.botania.client.model.armor.ArmorModels;
import vazkii.botania.common.item.equipment.armor.manasteel.ItemManasteelArmor;

import java.util.function.Consumer;

@Mixin(ItemManasteelArmor.class)
public abstract class ForgeMixinItemManasteelArmor extends Item {
	private ForgeMixinItemManasteelArmor(Properties props) {
		super(props);
	}

	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public HumanoidModel<?> getArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
				return ArmorModels.get(stack);
			}
		});
	}
}
