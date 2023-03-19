package de.Ste3et_C0st.BlockAdder;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;

import com.google.common.base.Supplier;

import de.Ste3et_C0st.BlockAdder.Block.SpigotBlock;

public class BlockAdderMain extends JavaPlugin implements Listener{

	private static BlockAdderMain INSTANCE = null;
	private final String customHeadTexture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmI2ZmIwYjU5NTc4YjkwZjcxYmJmNTFiYTQ1YTI1NmJjZmNkZTA4ZDk3ZWNlZWM5ZTgwNGE3Yzc5YjQyMjNkMCJ9fX0=";
	
	//this is the DropItemStack
	private Supplier<ItemStack> dropStack = () -> {
		final ItemStack dropStack = new ItemStack(Material.COPPER_INGOT);
		final ItemMeta meta = dropStack.getItemMeta();
		meta.setDisplayName("topaz_ore");
		dropStack.setItemMeta(meta);
		return dropStack;
	};
	
	//first own Block
	private final SpigotBlock block_variant1 = new SpigotBlock(this, getStack(customHeadTexture), "topaz_ore", 0, dropStack.get());
	
	public void onEnable() {
		INSTANCE = this;
		Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {
		INSTANCE = null;
	}
	
	public static BlockAdderMain getInstance() {
		return INSTANCE;
	}
	
	//Handle rightclick
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(event.getHand() != EquipmentSlot.HAND) return;
		if(event.hasBlock() && event.hasItem()) {
			final ItemStack stack = event.getItem();
			final ItemMeta meta = stack.getItemMeta();
			if(meta.hasDisplayName() && meta.getDisplayName().equalsIgnoreCase("topaz_ore")) {
				final Block block = event.getClickedBlock().getRelative(event.getBlockFace());
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				Bukkit.getScheduler().runTaskLater(INSTANCE, () -> {
					final float yaw = (Math.round(event.getPlayer().getLocation().getYaw() / 90f) & 0x3) * 90f;
					block_variant1.place(block.getLocation(), yaw);
				}, 1);
			}
		}
	}
	
	//Handle BlockBreak
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		final Block block = event.getBlock();
		final Optional<Entity> optEntity = block.getWorld().getNearbyEntities(BoundingBox.of(block), entity -> entity.getType() == EntityType.ITEM_DISPLAY).stream().findFirst();
		if(optEntity.isPresent()) {
			final ItemDisplay display = (ItemDisplay) optEntity.get();
			if(block_variant1.isSpigotBlock(display)) {
				block_variant1.drop(event.getPlayer(), block.getLocation());
				display.remove();
				event.setDropItems(false);
			}
		}
	}
	
	//get Custom PlayerHead Texture
	private ItemStack getStack(final String customTexture) {
		return Bukkit.getUnsafe().modifyItemStack(new ItemStack(Material.PLAYER_HEAD), "{display:{Name:\"{\\\"text\\\":\\\"CustomTexture\\\"}\"},SkullOwner:{Id:[" + "I;1201296705,1414024019,-1385893868,1321399054" + "],Properties:{textures:[{Value:\"" + customTexture + "\"}]}}}");
	}
}
