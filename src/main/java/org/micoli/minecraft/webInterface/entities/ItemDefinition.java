package org.micoli.minecraft.webInterface.entities;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.micoli.minecraft.utils.Images;
import org.micoli.minecraft.utils.Json;

/**
 * The Class QDItemDefinition.
 */
public class ItemDefinition {
	
	/** The material. */
	private Material material;

	private static BufferedImage allIcons;
	
	/** The scale. */
	private Boolean scale = false;
	
	/** The path. */
	private File path;

	/** The icons. */
	private HashMap<Integer, IconDef> icons = new HashMap<Integer, IconDef>();

	/**
	 * Gets the material.
	 *
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Sets the material.
	 *
	 * @param material the new material
	 */
	public void setMaterial(Material material) {
		this.material = material ;
	}

	/**
	 * @return the allIcons
	 */
	public static BufferedImage getAllIcons() {
		return allIcons;
	}

	/**
	 * @param allIcons the allIcons to set
	 */
	public static void setAllIcons(BufferedImage allIcons) {
		ItemDefinition.allIcons = allIcons;
	}

	/**
	 * Instantiates a new qD item definition.
	 *
	 * @param mat the mat
	 */
	public ItemDefinition(Material mat) {
		this.material = mat;
	}

	/**
	 * Export.
	 */
	public void export() {
		path = ItemDefinitionExporter.plugin.getExportJsonPath();
		//ServerLogger.log("==>export  " + material.name());

		exportData();
		Iterator<Integer> iterator = icons.keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			exportIcon(key);
		}
	}

	/**
	 * Gets the icon.
	 *
	 * @param data the data
	 * @return the icon
	 */
	public BufferedImage getIcon(Integer data) {
		BufferedImage tiles = null;
		if (icons.get(data).filename == null) {
			ItemDefinitionExporter.plugin.logger.log("No filename found  " + icons.get(data).filename + "-->" + icons.get(data).subName);
			return null;
		}
		if (icons.get(data).filename.equalsIgnoreCase("terrain")) {
			tiles = ItemDefinitionExporter.imgTerrain;
		} else if (icons.get(data).filename.equalsIgnoreCase("items")) {
			tiles = ItemDefinitionExporter.imgItems;
		} else if (icons.get(data).filename.equalsIgnoreCase("special")) {
			tiles = ItemDefinitionExporter.imgSpecial;
		}
		if (tiles == null) {
			ItemDefinitionExporter.plugin.logger.log("Tiles not found " + icons.get(data).filename + "-->" + icons.get(data).subName);
			return null;
		}
		BufferedImage bi = tiles.getSubimage(icons.get(data).x * 16, icons.get(data).y * 16, 16, 16);
		if (scale) {
			AffineTransform tx = new AffineTransform();
			tx.scale(2, 2);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			BufferedImage biNew = new BufferedImage((int) (bi.getWidth() * 2), (int) (bi.getHeight() * 2), bi.getType());
			bi = op.filter(bi, biNew);
		}
		// ServerLogger.log("export X %d=>%d=>%d",mat.getId(),data,mat.getId()*16,Math.min(32,data)*16);

		Images.copySrcIntoDstAt(bi, getAllIcons(), material.getId() * 16, Math.min(16, data) * 16);
		return bi;
	}

	/**
	 * Export icon.
	 *
	 * @param data the data
	 */
	private void exportIcon(Integer data) {
		File pngWriter = new File(String.format("%s/%s_%03d.png", path.getAbsoluteFile(), material.name(), data));
		if (this.icons.get(data).filename != null) {
			try {
				BufferedImage buf = getIcon(data);
				if (buf != null) {
					ImageIO.write(buf, "png", pngWriter);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Export data.
	 */
	public void exportData() {
		
		SubData sd = new SubData();

		sd.material = material;
		sd.maxStackSize = material.getMaxStackSize();
		sd.id = material.getId();
		sd.maxDurability = material.getMaxDurability();
		sd.isBlock = material.isBlock();
		sd.icons = icons;
		sd.listRecipes = ItemDefinitionExporter.plugin.getServer().getRecipesFor(new ItemStack(material, 1));
		Json.exportObjectToJson(path.getAbsoluteFile() + "/" + material.name() + ".json",sd);
	}

	/**
	 * Adds the item.
	 *
	 * @param data the data
	 * @param filename the filename
	 * @param subName the sub name
	 * @param x the x
	 * @param y the y
	 */
	public void addItem(Integer data, String filename, String subName, Integer x, Integer y) {
		IconDef def = new IconDef();
		def.data = data;
		def.filename = filename;
		def.subName = subName;
		def.x = x;
		def.y = y;
		icons.put(data, def);
	}
}