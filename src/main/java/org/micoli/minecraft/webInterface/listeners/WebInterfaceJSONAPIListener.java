package org.micoli.minecraft.webInterface.listeners;

import java.util.List;

import org.micoli.minecraft.webInterface.WebInterface;
import org.micoli.minecraft.webInterface.entities.localPlan.ParcelExport;
import org.micoli.minecraft.webInterface.entities.localPlan.ParcelExporter;

import com.alecgorge.minecraft.jsonapi.dynamic.API_Method;
import com.alecgorge.minecraft.jsonapi.dynamic.JSONAPIMethodProvider;

public class WebInterfaceJSONAPIListener implements JSONAPIMethodProvider{
	WebInterface plugin;
	public WebInterfaceJSONAPIListener() {
		this.plugin = WebInterface.getInstance();
	}
	
	
	@API_Method(
		namespace = "webInterface",
		name="localplanParcels",
		argumentDescriptions = {}
	)
	public List<ParcelExport> localplanParcels(){
		return ParcelExporter.getAllParcels();
	}
}
