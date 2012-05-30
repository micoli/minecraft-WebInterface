package org.micoli.minecraft.webInterface.entities.localPlan;

import org.micoli.minecraft.localPlan.LocalPlanUtils;
import org.micoli.minecraft.localPlan.entities.Parcel;
import org.micoli.minecraft.utils.Json;
import org.micoli.minecraft.utils.PluginEnvironment;
import org.micoli.minecraft.webInterface.WebInterface;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class ParcelExport extends Parcel {

	double baryX=0;
	
	double baryZ=0;
	
	public ParcelExport(Parcel parcel){
		this.setId(parcel.getId());
		this.setWorld(parcel.getWorld());
		this.setPrice(parcel.getPrice());
		this.setSurface(parcel.getSurface());
		this.setRegionId(parcel.getRegionId());
		this.setOwner(parcel.getOwner());
		this.setBuyStatus(parcel.getBuyStatus());
		this.setPointOfInterest(parcel.getPointOfInterest());
		this.setOwnerType(parcel.getOwnerType());
		this.setDistToPointOfInterest(parcel.getDistToPointOfInterest());

		
		
		WorldGuardPlugin worldGuardPlugin = PluginEnvironment.getWorldGuardPlugin(WebInterface.getInstance());
		if (worldGuardPlugin == null){
			return;
		}
		RegionManager regionManager = worldGuardPlugin.getRegionManager(WebInterface.getInstance().getServer().getWorld(parcel.getWorld()));
		if (regionManager == null){
			return;
		}

		ProtectedRegion protectedRegion = regionManager.getRegion(parcel.getRegionId());
		if (protectedRegion == null){
			return;
		}
		BlockVector2D barycentre = LocalPlanUtils.getBarycentre(protectedRegion);
		this.baryX = barycentre.getX();
		this.baryZ = barycentre.getZ();
	}

	public String toString(){
		return Json.exportObjectToJson(this);
	}

}
