/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2021 Pixel Entertainment LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.xcodiq.pixel.library.util.item;

import com.cryptomorin.xseries.XMaterial;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import gg.xcodiq.pixel.library.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ItemBuilder {

	private final ItemStack itemStack;
	private final ItemFactory itemFactory;
	private ItemMeta itemMeta;

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param itemStack the item stack
	 */
	public ItemBuilder(ItemStack itemStack) {
		this.itemFactory = Bukkit.getItemFactory();
		this.itemStack = itemStack;
		this.itemMeta = this.itemFactory.getItemMeta(itemStack.getType());
	}

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param type the type
	 */
	public ItemBuilder(XMaterial type) {
		this(new ItemStack(type.parseMaterial() == null ? Material.AIR : type.parseMaterial()));
	}

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param type   the type
	 * @param amount the amount
	 */
	public ItemBuilder(XMaterial type, int amount) {
		this(new ItemStack(type.parseMaterial() == null ? Material.AIR : type.parseMaterial(), amount));
	}

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param type   the type
	 * @param amount the amount
	 * @param damage the damage
	 */
	public ItemBuilder(XMaterial type, int amount, short damage) {
		this(new ItemStack(type.parseMaterial() == null ? Material.AIR : type.parseMaterial(), amount, damage));
	}

	/**
	 * Instantiates a new Item builder.
	 *
	 * @param type   the type
	 * @param amount the amount
	 * @param damage the damage
	 */
	public ItemBuilder(XMaterial type, int amount, int damage) {
		this(new ItemStack(type.parseMaterial() == null ? Material.AIR : type.parseMaterial(), amount, (short) damage));
	}

	/**
	 * Build item stack.
	 *
	 * @return the item stack
	 */
	public ItemStack toItemStack() {
		itemStack.setItemMeta(itemFactory.asMetaFor(itemMeta, itemStack));
		return itemStack;
	}

	/**
	 * Sets unbreakable.
	 *
	 * @param unbreakable the unbreakable
	 *
	 * @return the unbreakable
	 */
	public ItemBuilder setUnbreakable(boolean unbreakable) {
		this.itemMeta.setUnbreakable(unbreakable);
		return this;
	}

	/**
	 * Add enchantment item builder.
	 *
	 * @param ench  the ench
	 * @param level the level
	 *
	 * @return the item builder
	 */
	public ItemBuilder addEnchantment(Enchantment ench, int level) {
		this.itemMeta.addEnchant(ench, level, false);
		return this;
	}

	/**
	 * Add ench item builder.
	 *
	 * @param ench  the ench
	 * @param level the level
	 *
	 * @return the item builder
	 */
	public ItemBuilder addEnch(Enchantment ench, int level) {
		return this.addEnchantment(ench, level);
	}

	/**
	 * Add enchantments item builder.
	 *
	 * @param enchantments the enchantments
	 *
	 * @return the item builder
	 */
	public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
		enchantments.forEach(this::addEnchantment);
		return this;
	}

	/**
	 * Add unsafe enchantment item builder.
	 *
	 * @param ench  the ench
	 * @param level the level
	 *
	 * @return the item builder
	 */
	public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
		this.itemMeta.addEnchant(ench, level, true);
		return this;
	}

	/**
	 * Add unsafe ench item builder.
	 *
	 * @param ench  the ench
	 * @param level the level
	 *
	 * @return the item builder
	 */
	public ItemBuilder addUnsafeEnch(Enchantment ench, int level) {
		return this.addUnsafeEnchantment(ench, level);
	}

	/**
	 * Sets amount.
	 *
	 * @param amount the amount
	 *
	 * @return the amount
	 */
	public ItemBuilder setAmount(int amount) {
		this.itemStack.setAmount(amount);
		return this;
	}

	/**
	 * Amount item builder.
	 *
	 * @param amount the amount
	 *
	 * @return the item builder
	 */
	public ItemBuilder amount(int amount) {
		return this.setAmount(amount);
	}

	/**
	 * Remove enchantment item builder.
	 *
	 * @param ench the ench
	 *
	 * @return the item builder
	 */
	public ItemBuilder removeEnchantment(Enchantment ench) {
		this.itemMeta.removeEnchant(ench);
		return this;
	}

	/**
	 * Remove ench item builder.
	 *
	 * @param ench the ench
	 *
	 * @return the item builder
	 */
	public ItemBuilder removeEnch(Enchantment ench) {
		return this.removeEnchantment(ench);
	}

	/**
	 * Sets durability.
	 *
	 * @param durability the durability
	 *
	 * @return the durability
	 */
	public ItemBuilder setDurability(short durability) {
		this.itemStack.setDurability(durability);
		return this;
	}

	/**
	 * Durability item builder.
	 *
	 * @param durability the durability
	 *
	 * @return the item builder
	 */
	public ItemBuilder durability(short durability) {
		return this.setDurability(durability);
	}

	/**
	 * Sets durability.
	 *
	 * @param durability the durability
	 *
	 * @return the durability
	 */
	public ItemBuilder setDurability(int durability) {
		this.itemStack.setDurability((short) durability);
		return this;
	}

	/**
	 * Durability item builder.
	 *
	 * @param durability the durability
	 *
	 * @return the item builder
	 */
	public ItemBuilder durability(int durability) {
		return this.setDurability(durability);
	}

	/**
	 * Sets type.
	 *
	 * @param type the type
	 *
	 * @return the type
	 */
	@Deprecated
	public ItemBuilder setType(XMaterial type) {
		this.itemStack.setType(type.parseMaterial() == null ? Material.AIR : type.parseMaterial());
		return this;
	}

	/**
	 * Type item builder.
	 *
	 * @param type the type
	 *
	 * @return the item builder
	 */
	@Deprecated
	public ItemBuilder type(XMaterial type) {
		return this.setType(type);
	}

	/**
	 * Sets data.
	 *
	 * @param data the data
	 *
	 * @return the data
	 */
	@Deprecated
	public ItemBuilder setData(MaterialData data) {
		this.itemStack.setData(data);
		return this;
	}

	/**
	 * Data item builder.
	 *
	 * @param data the data
	 *
	 * @return the item builder
	 */
	@Deprecated
	public ItemBuilder data(MaterialData data) {
		return this.setData(data);
	}

	/**
	 * Sets item meta.
	 *
	 * @param itemMeta the item meta
	 *
	 * @return the item meta
	 */
	@Deprecated
	public ItemBuilder setItemMeta(ItemMeta itemMeta) {
		this.itemStack.setItemMeta(itemMeta);
		this.itemMeta = itemMeta;
		return this;
	}

	/**
	 * Meta item builder.
	 *
	 * @param itemMeta the item meta
	 *
	 * @return the item builder
	 */
	@Deprecated
	public ItemBuilder meta(ItemMeta itemMeta) {
		return this.setItemMeta(itemMeta);
	}

	/**
	 * Sets display name.
	 *
	 * @param name the name
	 *
	 * @return the display name
	 */
	public ItemBuilder setDisplayName(String name) {
		this.itemMeta.setDisplayName(name);
		return this;
	}

	/**
	 * Name item builder.
	 *
	 * @param name the name
	 *
	 * @return the item builder
	 */
	public ItemBuilder name(String name) {
		return this.setDisplayName(name);
	}

	/**
	 * Sets name.
	 *
	 * @param name the name
	 *
	 * @return the name
	 */
	public ItemBuilder setName(String name) {
		return this.setDisplayName(name);
	}

	/**
	 * Add white space lore item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder addWhiteSpaceLore() {
		addLore(" ");
		return this;
	}

	/**
	 * Sets lore.
	 *
	 * @param lore the lore
	 *
	 * @return the lore
	 */
	public ItemBuilder setLore(List<String> lore) {
		this.itemMeta.setLore(lore);
		return this;
	}

	/**
	 * Add lore item builder.
	 *
	 * @param lore the lore
	 *
	 * @return the item builder
	 */
	public ItemBuilder addLore(String lore) {
		if (this.itemMeta.hasLore()) {
			List<String> lores = this.itemMeta.getLore();
			lores.add(lore);
			this.itemMeta.setLore(lores);
		} else {
			this.itemMeta.setLore(Collections.singletonList(lore));
		}
		return this;
	}

	/**
	 * Sets lore.
	 *
	 * @param lore the lore
	 *
	 * @return the lore
	 */
	public ItemBuilder setLore(String... lore) {
		this.itemMeta.setLore(Arrays.asList(lore));
		return this;
	}

	/**
	 * Add item flags item builder.
	 *
	 * @param itemFlags the item flags
	 *
	 * @return the item builder
	 */
	public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
		this.itemMeta.addItemFlags(itemFlags);
		return this;
	}

	/**
	 * Add item flag item builder.
	 *
	 * @param itemFlags the item flags
	 *
	 * @return the item builder
	 */
	public ItemBuilder addItemFlag(ItemFlag... itemFlags) {
		this.itemMeta.addItemFlags(itemFlags);
		return this;
	}

	/**
	 * Clear flags item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder clearFlags() {
		itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
		return this;
	}

	/**
	 * Sets owning player.
	 *
	 * @param player the player
	 *
	 * @return the owning player
	 */
	public ItemBuilder setOwningPlayer(OfflinePlayer player) {
		if (this.itemStack.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
			((SkullMeta) this.itemMeta).setOwner(player.getName());
		}
		return this;
	}

	private GameProfile createProfileWithTexture(String texture) {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		PropertyMap propertyMap = profile.getProperties();
		propertyMap.put("textures", new Property("textures", texture));
		return profile;
	}

	public ItemBuilder setSkullURL(String url) {
		if (itemStack.getType() != XMaterial.PLAYER_HEAD.parseMaterial())
			throw new IllegalArgumentException("ItemStack is not SKULL_ITEM");

		if (!ReflectionUtil.set(itemMeta.getClass(), itemMeta, "profile", this.createProfileWithTexture(url)))
			throw new IllegalStateException("Unable to inject GameProfile");

		return this;
	}

	public ItemBuilder skullURL(String url) {
		return this;
	}

	/**
	 * Skull item builder.
	 *
	 * @param owner the owner
	 *
	 * @return the item builder
	 */
	public ItemBuilder skull(OfflinePlayer owner) {
		return this.setOwningPlayer(owner);
	}

	/**
	 * Is potion boolean.
	 *
	 * @return the boolean
	 */
	private boolean isPotion() {
		return XMaterial.matchXMaterial(itemStack.getType()) == XMaterial.POTION;
	}

	/**
	 * Add custom effect item builder.
	 *
	 * @param effect    the effect
	 * @param overwrite the overwrite
	 *
	 * @return the item builder
	 */
	public ItemBuilder addCustomEffect(PotionEffect effect, boolean overwrite) {
		if (this.isPotion()) {
			((PotionMeta) this.itemMeta).addCustomEffect(effect, overwrite);
		}
		return this;
	}

	/**
	 * Effect item builder.
	 *
	 * @param effect    the effect
	 * @param overwrite the overwrite
	 *
	 * @return the item builder
	 */
	public ItemBuilder effect(PotionEffect effect, boolean overwrite) {
		return this.addCustomEffect(effect, overwrite);
	}

	/**
	 * Remove custom effect item builder.
	 *
	 * @param type the type
	 *
	 * @return the item builder
	 */
	public ItemBuilder removeCustomEffect(PotionEffectType type) {
		if (this.isPotion()) {
			((PotionMeta) this.itemMeta).removeCustomEffect(type);
		}
		return this;
	}

	/**
	 * Remove effect item builder.
	 *
	 * @param type the type
	 *
	 * @return the item builder
	 */
	public ItemBuilder removeEffect(PotionEffectType type) {
		return this.removeCustomEffect(type);
	}

	/**
	 * Clear custom effects item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder clearCustomEffects() {
		if (this.isPotion()) {
			((PotionMeta) this.itemMeta).clearCustomEffects();
		}
		return this;
	}

	/**
	 * Clear effects item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder clearEffects() {
		return this.clearCustomEffects();
	}

	/**
	 * Sets map scaling.
	 *
	 * @param value the value
	 *
	 * @return the map scaling
	 */
	public ItemBuilder setMapScaling(boolean value) {
		if (XMaterial.matchXMaterial(this.itemStack.getType()) == XMaterial.MAP) {
			((MapMeta) this.itemMeta).setScaling(value);
		}
		return this;
	}

	/**
	 * Map scaling item builder.
	 *
	 * @param value the value
	 *
	 * @return the item builder
	 */
	public ItemBuilder mapScaling(boolean value) {
		return this.setMapScaling(value);
	}

	/**
	 * Is leather armor boolean.
	 *
	 * @return the boolean
	 */
	private boolean isLeatherArmor() {
		XMaterial material = XMaterial.matchXMaterial(this.itemStack.getType());
		return material == XMaterial.LEATHER_HELMET ||
				material == XMaterial.LEATHER_CHESTPLATE ||
				material == XMaterial.LEATHER_LEGGINGS ||
				material == XMaterial.LEATHER_BOOTS;
	}

	/**
	 * Sets armor color.
	 *
	 * @param color the color
	 *
	 * @return the armor color
	 */
	public ItemBuilder setArmorColor(Color color) {
		if (this.isLeatherArmor()) {
			((LeatherArmorMeta) this.itemMeta).setColor(color);
		}
		return this;
	}

	/**
	 * Reset armor color item builder.
	 *
	 * @return the item builder
	 */
	public ItemBuilder resetArmorColor() {
		if (this.isLeatherArmor()) {
			((LeatherArmorMeta) this.itemMeta)
					.setColor(this.itemFactory.getDefaultLeatherColor());
		}
		return this;
	}

	/**
	 * Is book boolean.
	 *
	 * @return the boolean
	 */
	private boolean isBook() {
		XMaterial material = XMaterial.matchXMaterial(this.itemStack.getType());
		return material == XMaterial.WRITABLE_BOOK || material == XMaterial.WRITTEN_BOOK;
	}

	/**
	 * Add book page item builder.
	 *
	 * @param pages the pages
	 *
	 * @return the item builder
	 */
	public ItemBuilder addBookPage(String... pages) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).addPage(pages);
		}
		return this;
	}

	/**
	 * Author item builder.
	 *
	 * @param author the author
	 *
	 * @return the item builder
	 */
	public ItemBuilder author(String author) {
		return this.setBookAuthor(author);
	}

	/**
	 * Sets book author.
	 *
	 * @param author the author
	 *
	 * @return the book author
	 */
	public ItemBuilder setBookAuthor(String author) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setAuthor(author);
		}
		return this;
	}

	/**
	 * Sets page.
	 *
	 * @param page the page
	 * @param data the data
	 *
	 * @return the page
	 */
	public ItemBuilder setPage(int page, String data) {
		return this.setBookPage(page, data);
	}

	/**
	 * Sets book page.
	 *
	 * @param page the page
	 * @param data the data
	 *
	 * @return the book page
	 */
	public ItemBuilder setBookPage(int page, String data) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setPage(page, data);
		}
		return this;
	}

	/**
	 * Title item builder.
	 *
	 * @param title the title
	 *
	 * @return the item builder
	 */
	public ItemBuilder title(String title) {
		return this.setBookTitle(title);
	}

	/**
	 * Sets book title.
	 *
	 * @param title the title
	 *
	 * @return the book title
	 */
	public ItemBuilder setBookTitle(String title) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setTitle(title);
		}
		return this;
	}

	/**
	 * Sets pages.
	 *
	 * @param pages the pages
	 *
	 * @return the pages
	 */
	public ItemBuilder setPages(String... pages) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setPages(pages);
		}
		return this;
	}

	/**
	 * Sets pages.
	 *
	 * @param pages the pages
	 *
	 * @return the pages
	 */
	public ItemBuilder setPages(List<String> pages) {
		if (this.isBook()) {
			((BookMeta) this.itemMeta).setPages(pages);
		}
		return this;
	}
}
