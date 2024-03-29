package org.micoli.minecraft.webInterface.entities.localPlan;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.bukkit.World;
import org.dynmap.DynmapCore;
import org.dynmap.DynmapWorld;
import org.dynmap.MapManager;
import org.dynmap.MapTile;
import org.dynmap.hdmap.HDMapTile;
import org.dynmap.hdmap.IsoHDPerspective;
import org.dynmap.utils.Matrix3D;
import org.dynmap.utils.Vector3D;
import org.micoli.minecraft.localPlan.LocalPlan;
import org.micoli.minecraft.localPlan.entities.Parcel;
import org.micoli.minecraft.utils.Images;
import org.micoli.minecraft.utils.Json;
import org.micoli.minecraft.utils.PluginEnvironment;
import org.micoli.minecraft.webInterface.WebInterface;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

// TODO: Auto-generated Javadoc
/**
 * The Class ParcelExporter.
 */
public class ParcelExporter {

	/** The sea level. */
	private static final int SeaLevel = 65;

	/** The buffer size. */
	private static final int BufferSize = 4;

	/** The border size. */
	private static final int BorderSize = 50;

	/**
	 * Instantiates a new parcel exporter.
	 * 
	 * @param instance
	 *            the instance
	 */
	
	public static List<ParcelExport> getAllParcels(){
		WebInterface.getInstance().logger.log("Parcels");		
		final LocalPlan localPlan = (LocalPlan) WebInterface.getInstance().getServer().getPluginManager().getPlugin("LocalPlan");
		if (localPlan == null) {
			WebInterface.getInstance().logger.log("Could not get acces to LocalPlan plugin");
			return null;
		}
		List<Parcel> listParcels = localPlan.getAllParcel();
		List<ParcelExport> listParcelExport = new ArrayList<ParcelExport>();
		for (Parcel parcel :listParcels ){
			ParcelExport parcelExport = new ParcelExport(parcel);
			parcelExport.setId(sanitizeFilename(parcelExport.getId()));
			listParcelExport.add(parcelExport);
		}
		return listParcelExport; 
	}
	
	/**
	 * Export parcels.
	 */
	public static void exportParcels() {
		File path = WebInterface.getInstance().getExportJsonPath(WebInterface.getInstance().getParcelExporterCfg());
		Json.exportObjectToJson(String.format("%s/__allparcels.json", path),ParcelExporter.getAllParcels());
		WebInterface.getInstance().logger.log("Parcels done");		
	}

		/**
	 * export the parcel maps.
	 * 
	 * @return the maps
	 */
	public static void exportParcelMaps() {
		WebInterface.getInstance().logger.log("Parcels maps");		
		final int planeAngle = 180;
		DynmapCore dynmapCore = PluginEnvironment.getDynmapCorePlugin(WebInterface.getInstance());
		if (dynmapCore == null) {
			WebInterface.getInstance().logger.log("Could not get acces to DynmapCore from DynmapPlugin");
			return;
		}

		IsoHDPerspective isoHDPerspective = (IsoHDPerspective) MapManager.mapman.hdmapman.perspectives.get(WebInterface.getInstance().getPerspectiveMapsExporter());
		if (isoHDPerspective == null) {
			WebInterface.getInstance().logger.log("Could not get acces to isoHDPerspective from DynmapCore");
			return;
		}

		int w = IsoHDPerspective.tileWidth;
		int h = IsoHDPerspective.tileHeight;

		Matrix3D transform = new Matrix3D(0.0, 0.0, -1.0, -1.0, 0.0, 0.0, 0.0, 1.0, 0.0);
		transform.rotateXY(planeAngle - isoHDPerspective.azimuth);
		transform.rotateYZ(planeAngle / 2 - isoHDPerspective.inclination);
		transform.shearZ(0, Math.tan(Math.toRadians(planeAngle / 2 - isoHDPerspective.inclination)));
		transform.scale(isoHDPerspective.scale, isoHDPerspective.scale, Math.sin(Math.toRadians(isoHDPerspective.inclination)));
		//plugin.logger.log("Matrix %s", Json.exportObjectToJson(transform));
		//plugin.logger.log("perspective %s", isoHDPerspective.toString());

		for (World world : WebInterface.getInstance().getServer().getWorlds()) {
			String worldName = world.getName();
			DynmapWorld dynmapWorld = dynmapCore.getMapManager().getWorld(worldName);

			RegionManager rm = PluginEnvironment.getWorldGuardPlugin(WebInterface.getInstance()).getRegionManager(world);
			if (rm == null) {
				continue;
			}
			boolean full = true;
			Map<String, ProtectedRegion> regions = rm.getRegions();
			for (ProtectedRegion region : regions.values()) {
				if (!region.getId().equalsIgnoreCase("__global__") || full) {
					int i;
					MapTile[] mapTiles;
					if(region.getId().equalsIgnoreCase("__global__") ){
						/*
						int minx = Integer.MAX_VALUE;
						int minz = Integer.MAX_VALUE;
						int maxx = Integer.MIN_VALUE;
						int maxz = Integer.MIN_VALUE;
						for (Chunk chunk : world.getLoadedChunks()){
							minx = Math.min(minx,chunk.getX());
							minz = Math.min(minz,chunk.getZ());
							maxx = Math.min(maxx,chunk.getX());
							maxz = Math.min(maxz,chunk.getZ());
						}*/
						//mapTiles = isoHDPerspective.getTiles(dynmapWorld, Integer.MIN_VALUE , SeaLevel, Integer.MIN_VALUE, Integer.MAX_VALUE, SeaLevel, Integer.MAX_VALUE);
						mapTiles = isoHDPerspective.getTiles(dynmapWorld, region.getMinimumPoint().getBlockX()-256, SeaLevel, region.getMinimumPoint().getBlockZ()-256, region.getMaximumPoint().getBlockX()+256, SeaLevel, region.getMaximumPoint().getBlockZ()+256);
					}else{
						mapTiles = isoHDPerspective.getTiles(dynmapWorld, region.getMinimumPoint().getBlockX(), SeaLevel, region.getMinimumPoint().getBlockZ(), region.getMaximumPoint().getBlockX(), SeaLevel, region.getMaximumPoint().getBlockZ());
					}
					Set<MapTile> regionTiles = new HashSet<MapTile>(Arrays.asList(mapTiles));
					for (i = 0; i < mapTiles.length; i++) {
						regionTiles.addAll(Arrays.asList(mapTiles[i].getAdjecentTiles()));
					}
					//plugin.logger.log(" %s => %d tiles %s", region.getId(), mapTiles.length, dynmapCore.getTilesFolder().getAbsolutePath());
					int minTileX = 0, minTileY = 0, maxTileX = 0, maxTileY = 0;
					i = 0;
					for (MapTile maptile : regionTiles) {
						if (i == 0) {
							maxTileX = maptile.tileOrdinalX();
							minTileX = maxTileX;
							maxTileY = maptile.tileOrdinalY();
							minTileY = maxTileY;
						} else {
							minTileX = Math.min(minTileX, maptile.tileOrdinalX());
							minTileY = Math.min(minTileY, maptile.tileOrdinalY());
							maxTileX = Math.max(maxTileX, maptile.tileOrdinalX());
							maxTileY = Math.max(maxTileY, maptile.tileOrdinalY());
						}
						i++;
					}
					int sizex = Math.abs(maxTileX - minTileX + 1);
					int sizey = Math.abs(maxTileY - minTileY + 1);
					BufferedImage exportParcel = new BufferedImage(sizex * h, sizey * w, BufferedImage.TYPE_INT_ARGB);

					for (MapTile nonHDMapTile : regionTiles) {
						HDMapTile maptile = (HDMapTile) nonHDMapTile;
						String mapTileFilename = MapManager.mapman.getTileFile(maptile).getAbsoluteFile().toString().replaceFirst("/hdmap", "/t");
						File inFile = new File(mapTileFilename);
						try {
							Images.copySrcIntoDstAt(ImageIO.read(inFile), exportParcel, (maptile.tileOrdinalX() - minTileX) * w, (sizey - (maptile.tileOrdinalY() - minTileY) - 1) * h);
						} catch (IOException e) {
							//plugin.logger.dumpStackTrace(e);
						}
					}
					File path = WebInterface.getInstance().getExportJsonPath(WebInterface.getInstance().getParcelExporterCfg());
					String filename = String.format("%s/%s__%s.png", path, worldName, sanitizeFilename(region.getId()));
					WebInterface.getInstance().logger.log("%s",filename);
					
					if(!region.getId().equalsIgnoreCase("__global__") && !(new File(filename)).exists()){
						List<BlockVector2D> points = region.getPoints();
						if (points != null && points.size() > 0) {
							if (region.getTypeName().equalsIgnoreCase("cuboid")) {
								BlockVector2D tmpPoint = points.get(points.size() - 1);
								tmpPoint = points.get(2);
								points.set(2, points.get(3));
								points.set(3, tmpPoint);
							}
	
							Graphics2D g2d = exportParcel.createGraphics();
							Polygon polygon = new Polygon();
							g2d.setColor(Color.GRAY);
							g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
							for (int j = 0; j < points.size(); j++) {
								BlockVector2D point = ParcelExporter.getPointOnMap(transform, points.get(j), minTileX, minTileY, sizey, w, h);
								polygon.addPoint((int) point.getX(), (int) point.getZ());
								// putCross(ExportParcel, point);
							}
	
							polygon = ParcelExporter.expandPolygon(polygon, BufferSize);
							Rectangle r = new Rectangle(0, 0, exportParcel.getWidth(), exportParcel.getHeight());
							Area wholeArea = new Area(r);
							Area holeArea = new Area(polygon);
							wholeArea.subtract(holeArea);
							g2d.fill(wholeArea);
	
							g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));
							final float dash1[] = { 2.0f };
							g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4.0f, dash1, 0.0f));
							g2d.setColor(Color.BLACK);
							g2d.draw(polygon);
	
							Rectangle2D bound = polygon.getBounds2D();
							int subMinX = Math.max((int) bound.getMinX() - BorderSize, 1);
							int subMinY = Math.max((int) bound.getMinY() - BorderSize, 1);
							int subMaxX = Math.min((int) bound.getMaxX() + BorderSize, exportParcel.getWidth() - 1);
							int subMaxY = Math.min((int) bound.getMaxY() + BorderSize, exportParcel.getHeight() - 1);
	
							exportParcel = exportParcel.getSubimage(subMinX, subMinY, subMaxX - subMinX, subMaxY - subMinY);
						}
					}
					Images.saveBufferedImage(exportParcel, filename, "png");

					//plugin.logger.log(" %s(%d) => %d %d / %d %d / %d %d", region.getId(), mapTiles.length, minTileX * w, minTileY * h, maxTileX * w + w, maxTileY * h + h, sizex * w, sizey * h);
					//plugin.logger.log("-----------------");
				}
			}
		}
		WebInterface.getInstance().logger.log("Parcels maps done");		
	}

	public static String sanitizeFilename(String id) {
		return id==null?"":id.replaceAll("[:\\\\/*?|<>]", "_");
	}

	/**
	 * Expand polygon.
	 * 
	 * @param polygon
	 *            the polygon
	 * @param bufferSize
	 *            the buffer size
	 * @return the polygon
	 */
	private static Polygon expandPolygon(Polygon polygon, int bufferSize) {
		Coordinate[] coords = new Coordinate[polygon.npoints];

		for (int i = 0; i < polygon.npoints; i++) {
			coords[i] = new Coordinate(polygon.xpoints[i], polygon.ypoints[i]);
		}
		Geometry g = new GeometryFactory().createMultiPoint(coords);

		BufferOp bufOp = new BufferOp(g);
		bufOp.setEndCapStyle(BufferParameters.CAP_ROUND);
		Geometry outsideGeometry = bufOp.getResultGeometry(bufferSize).convexHull();

		// Geometry outsideGeometry = g.buffer((double)
		// bufferSize).getBoundary();
		Polygon outsidePolygon = new Polygon();
		for (int i = 0; i < outsideGeometry.getNumPoints(); i++) {
			outsidePolygon.addPoint((int) outsideGeometry.getCoordinates()[i].x, (int) outsideGeometry.getCoordinates()[i].y);
		}
		return outsidePolygon;
	}

	/**
	 * Gets the point on map.
	 * 
	 * @param transform
	 *            the transform
	 * @param point
	 *            the point
	 * @param minx
	 *            the minx
	 * @param miny
	 *            the miny
	 * @param sizey
	 *            the sizey
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @return the point on map
	 */
	private static BlockVector2D getPointOnMap(Matrix3D transform, BlockVector2D point, int minx, int miny, int sizey, int w, int h) {
		Vector3D block = new Vector3D();
		Vector3D pointInWorld = new Vector3D();
		block.x = (int) point.getX();
		block.y = SeaLevel;
		block.z = (int) point.getZ();
		transform.transform(block, pointInWorld);

		int posx = (int) (pointInWorld.x);
		int posy = (int) (pointInWorld.y);

		BlockVector2D pointOnMap = new BlockVector2D(posx - minx * w, (sizey * h) - (posy - miny * h));

		/*
		 * plugin.logger.log(
		 * "min(%5d,%5d) -> size(%5d,%5d) -> pos(%5d,%5d) -> npos(%5f%5f)",
		 * (minx*w),(miny*h), (sizex*w),(sizey*h), posx,posy,
		 * pointOnMap.getX(),pointOnMap.getZ() );
		 */
		return pointOnMap;
	}
}