package org.micoli.minecraft.webInterface.entities;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;

/**
 * The Class SubData.
 */
public class SubData{
	
	/** The material. */
	public Material material;
	
	/** The max stack size. */
	public Integer maxStackSize;
	
	/** The id. */
	public Integer id;
	
	/** The max durability. */
	public short maxDurability;
	
	/** The is block. */
	public boolean isBlock;
	
	/** The list recipes. */
	List<Recipe> listRecipes;
	
	/** The icons. */
	HashMap<Integer, IconDef> icons;
}
