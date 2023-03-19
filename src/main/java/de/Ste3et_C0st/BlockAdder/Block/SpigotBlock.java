package de.Ste3et_C0st.BlockAdder.Block;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class SpigotBlock {
	
	private final ItemStack stack, dropStack;
	private final int hardness;
	private final String customName;
	private final NamespacedKey namespacedKey;
	
	public SpigotBlock(final Plugin plugin,final ItemStack stack, final String customName, final int hardness, final ItemStack dropStack) {
		this.stack = stack;
		this.hardness = hardness;
		this.customName = customName;
		this.namespacedKey = new NamespacedKey(plugin, customName);
		this.dropStack = dropStack;
	}

	public ItemStack getStack() {
		return stack;
	}

	public int getHardness() {
		return hardness;
	}
	
	public boolean isSpigotBlock(ItemDisplay display) {
		if(Objects.nonNull(display)) {
			return display.getPersistentDataContainer().has(namespacedKey, PersistentDataType.INTEGER);
		}
		return false;
	}

	public String getCustomName() {
		return customName;
	}

	public ItemStack getDropStack() {
		return dropStack;
	}
	
	//handle drop
	public void drop(Player player, Location location) {
		location.getWorld().dropItemNaturally(location, getDropStack());
	}
	
	public void place(Location location, float yaw) {
		final Location blockLocation = location.getBlock().getLocation().add(.5, .5, .5);
		final World world = location.getWorld();
		
		//ItemDisplay
		final ItemDisplay display = (ItemDisplay) world.spawnEntity(blockLocation, EntityType.ITEM_DISPLAY);
		display.setItemStack(getStack()); //set itemstack to ItemDisplay
		display.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(2f), new AxisAngle4f())); //set the scale of the item to 2f
		display.setBillboard(Billboard.FIXED); //set the Billboard to fixed to prevent rotation on player movement
		display.setItemDisplayTransform(ItemDisplayTransform.FIXED);
		display.setRotation(yaw, 0); //set yaw to rotate the display
		display.setPersistent(true);
		display.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, 1);
		
		blockLocation.getBlock().setType(Material.BARRIER);
		world.playSound(blockLocation, Sound.BLOCK_STONE_PLACE, 1, 1);
	}
}
