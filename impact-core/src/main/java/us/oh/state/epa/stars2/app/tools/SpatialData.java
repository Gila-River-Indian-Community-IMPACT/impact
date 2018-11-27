package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Shape;
import us.oh.state.epa.stars2.def.OffsetTrackingNonAttainmentAreaDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.wy.state.deq.impact.def.MapOptionsConfigDef;

@SuppressWarnings("serial")
public class SpatialData extends AppBase {
	public static final String PAGE_OUTCOME = "tools.spatialData";
	public static final String SUCCESS = "success";
	
	private SpatialDataLineItem[] importedShapes;
		
	private InfrastructureService infrastructureService;
	
	private Shape modifyShape;
	
    private boolean editMode1;
    
	public SpatialData() throws DAOException {
		super();
	}
	
	public MapOptionsConfigDef getDefaultMapOptions(){
		String configId = SystemPropertyDef.getSystemPropertyValue("APP_MAP_OPTIONS_CONFIG", null);
		MapOptionsConfigDef mapOptions = (MapOptionsConfigDef)MapOptionsConfigDef.getData().getItems().getItem(configId);
		if (mapOptions == null){
			mapOptions = new MapOptionsConfigDef();
			mapOptions.setCenterLat(43.5);
			mapOptions.setCenterLng(-107.5);
			mapOptions.setZoomLevel(7);
		}
		return mapOptions;
	}

	public String refresh() {
    	SpatialDataLineItem[] spatialDataLineItemList = null;
    	
		try {
			Shape[] importedShapes = getInfrastructureService().retrieveShapes();
			spatialDataLineItemList = new SpatialDataLineItem[importedShapes.length];
			int i = 0;
			List<Integer> indianReservationShapeIds = retrieveIndianReservationShapeIds();
			List<Integer> countyShapeIds = retrieveCountyShapeIds();
			for(Shape s: importedShapes) {
				SpatialDataLineItem item = new SpatialDataLineItem(s);
				item.setSelected(true);
				item.setNonAttainmentAreaNames(getAssociatedNonAttainmentAreas(item
						.getShapeId()));
				item.setProjectIds(getAssoicatedProjectIds(item.getShapeId()));
				item.setCountyShape(countyShapeIds.contains(item.getShapeId()));
				item.setIndianReservationShape(indianReservationShapeIds.contains(item.getShapeId()));
				spatialDataLineItemList[i++] = item;
			}
		} catch (DAOException e) {
			handleException(e);
			return null;
		}
    	setImportedShapes(spatialDataLineItemList);
    	
    	return PAGE_OUTCOME;
	}

	public final void dialogDone() {
        return;
    }

    public final String startToEditImportedShape() {
        return "dialog:shapeDetail";
    }

    public final boolean isEditAllowed() {
		boolean allowed = false;
		if (isAdmin()) {
			allowed = true;
		}
		return allowed;
    }

	public boolean isAdmin() {
		return isStars2Admin();
	}

	public final void edit() {
		this.editMode1 = true;
	}

	public final void save() {

		DisplayUtil.clearQueuedMessages();

		try {
			boolean updated = 
					getInfrastructureService().modifyShape(modifyShape);
			if (!updated) {
				String msg = "Update geographic shape failed.";
				DisplayUtil.displayError(msg);
				logger.error(msg);
				return;
			} else {
				modifyShape = 
						getInfrastructureService().retrieveShape(modifyShape.getShapeId());
			}

		} catch (RemoteException re) {
			handleException(re);
			return;
		}

		DisplayUtil.displayInfo("Shape updated successfully");

		this.editMode1 = false;
		
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public void cancel() {
		editMode1 = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void delete() {

		DisplayUtil.clearQueuedMessages();
		
		try {
			// if you come here then the shape is not referenced by any
			// non-attainment area or active projects in the system, but
			// some in-active projects might still be referring to this
			// shape. 
			// Check if there are any projects like that, if yes, then
			// display a graceful message to the users instead of blowing
			// up on their face due to referential integrity errors.
			List<String> projectIdList = getInactiveProjectIdsAssociatedWithShape(modifyShape
					.getShapeId());
			if (null != projectIdList && !projectIdList.isEmpty()) {
				String s = StringUtils.join(
						projectIdList.toArray(new String[0]), ",");
				DisplayUtil
						.displayError("Cannot delete shape because it is being referenced by the following project(s) that are not active: "
								+ s);
				return;
			}	
			
			getInfrastructureService().removeShape(modifyShape);
			DisplayUtil.displayInfo("Shape deleted successfully.");
		} catch (DAOException e) {
			logger.error("Delete geographic shape failed", e);
			DisplayUtil.displayError("Delete shape failed");
		} finally {
			refresh();
			closeDialog();
		}
	}

	public final void closeDialog() {
		setEditMode1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

    public Shape getModifyShape() {
		return modifyShape;
	}

	public void setModifyShape(Shape modifyShape) {
		this.modifyShape = modifyShape;
	}

	public boolean isEditMode1() {
		return editMode1;
	}

	public void setEditMode1(boolean editMode1) {
		this.editMode1 = editMode1;
	}

	public SpatialDataLineItem[] getImportedShapes() {
		return importedShapes;
	}

	public void setImportedShapes(SpatialDataLineItem[] importedShapes) {
		this.importedShapes = importedShapes;
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}
	
	public void selectAllShapes() {
		for(Shape s: this.importedShapes) {
			s.setSelected(true);
		}
	}
	
	public void unSelectAllShapes() {
		for(Shape s: this.importedShapes) {
			s.setSelected(false);
		}
	}

	public List<String> getAssociatedNonAttainmentAreas(Integer shapeId) {
		List<String> ret = new ArrayList<String>();
		List<OffsetTrackingNonAttainmentAreaDef> nonAttainmentAreaList = OffsetTrackingNonAttainmentAreaDef
				.getDefListItems();
		for(OffsetTrackingNonAttainmentAreaDef d: nonAttainmentAreaList) {
			if(null != d.getAreaShape() && d.getAreaShape().equals(shapeId)) { 
				ret.add(d.getDescription());
			}
		}
			
		return ret;
	}
	
	public List<String> getAssoicatedProjectIds(Integer shapeId) {
		List<String> ret = new ArrayList<String>();
		try {
			ret = getInfrastructureService()
					.retrieveProjectIdsAssociatedWithShape(shapeId);
		} catch (DAOException de) {
			DisplayUtil.displayError("An error occured when retrieving shape "
					+ shapeId);
			handleException(de);
		}
		return ret;
	}
	
	@SuppressWarnings("unused")
	public class SpatialDataLineItem extends Shape {
		private List<String> nonAttainmentAreaNames = new ArrayList<String>();
		private List<String> projectIds = new ArrayList<String>();
		
		private boolean countyShape;
		private boolean indianReservationShape;
		
		public SpatialDataLineItem() {
			super();
		}
		
		public SpatialDataLineItem(Shape s) {
			super(s);
		}
		
		public List<String> getNonAttainmentAreaNames() {
			return nonAttainmentAreaNames;
		}

		public void setNonAttainmentAreaNames(List<String> nonAttainmentAreaNames) {
			this.nonAttainmentAreaNames = nonAttainmentAreaNames;
		}

		public List<String> getProjectIds() {
			return projectIds;
		}

		public void setProjectIds(List<String> projectIds) {
			this.projectIds = projectIds;
		}

		public String getNonAttainmentAreas() {
			String ret = "";
			if(null != this.nonAttainmentAreaNames) {
				ret = StringUtils
						.join(this.nonAttainmentAreaNames
								.toArray(new String[0]), ",");
			}
			return ret;
		}

		public String getProjects() {
			String ret = "";
			if(null != this.projectIds) {
				ret = StringUtils.join(this.projectIds.toArray(new String[0]),
						",");
			}
			return ret;
		}
		
		public String getInfoWindowContent() {
			
			String ret = 
					"<div>"
					+ "<h1>" + getLabel() + "</h1>"
					+ "<div>"
					+ "<table>"
					+ "<tr>"
					+ "<td><b>Description:<b></td>"
					+ "<td>" + getDescription() + "</td>"
					+ "</tr>"
					+ "<tr>"
					+ "<td><b>Area(KM<sup>2</sup>):<b></td>"
					+ "<td>" + getArea() + "</td>" 
					+ "</tr>"
					+ "<tr>"
					+ "<td><b>Perimeter(KM):</b></td>"
					+ "<td>" + getLength() + "</td>" 
					+ "</tr>"
					+ "<tr>"
					+ "<td><b>Non-Attainment Area:</b></td>"
					+ "<td>" + getNonAttainmentAreas() + "</td>" 
					+ "</tr>"
					+ "<tr>"
					+ "<td><b>Project Area:</b></td>"
					+ "<td>" + getProjects() + "</td>" 
					+ "</tr>"
					+ "</table>"
					+ "</div>"
					+ "</div>"
					;
			
			return ret;
		}
		
		public boolean isAssociatedWithNonAttainmentArea() {
			return (null != this.nonAttainmentAreaNames 
							&& !this.nonAttainmentAreaNames.isEmpty()) ? true : false;
		}
		
		public boolean isAssociatedWithProject() {
			return (null != this.projectIds 
							&& !this.projectIds.isEmpty()) ? true : false;
		}
		
		public boolean isCountyShape() {
			return countyShape;
		}

		public void setCountyShape(boolean countyShape) {
			this.countyShape = countyShape;
		}

		public boolean isIndianReservationShape() {
			return indianReservationShape;
		}

		public void setIndianReservationShape(boolean indianReservationShape) {
			this.indianReservationShape = indianReservationShape;
		}
	}
	
	public final boolean isDeleteAllowed() {
		boolean allowed = false;
		if (isAdmin()
				&& !((SpatialDataLineItem)this.modifyShape).isAssociatedWithNonAttainmentArea()
				&& !((SpatialDataLineItem)this.modifyShape).isAssociatedWithProject()) {
			allowed = true;
		}
		return allowed;
    }
	
	public List<String> getInactiveProjectIdsAssociatedWithShape(Integer shapeId) {
		List<String> ret = new ArrayList<String>();
		try {
			ret = getInfrastructureService()
					.retrieveInactiveProjectIdsAssociatedWithShape(shapeId);
		} catch (DAOException daoe) {
			DisplayUtil
					.displayError("An error occured when trying to retrieve project ids associated with this shape");
			handleException(daoe);
		}
		return ret;
	}

	public List<Integer> retrieveIndianReservationShapeIds(){
		List<Integer> indianReservationShapIds = new ArrayList<Integer>();
		try {
			indianReservationShapIds = getInfrastructureService().retrieveIndianReservationShapeIds();
		} catch (DAOException e){
			DisplayUtil.displayError("System error. Please contact system administrator.");
			handleException(e);
		}
		return indianReservationShapIds;
	}
	
	public List<Integer> retrieveCountyShapeIds(){
		List<Integer> countyShapeIds = new ArrayList<Integer>();
		try {
			CountyDef[] tempArray = getInfrastructureService().retrieveCounties();
			for (CountyDef cd : tempArray){
				if (cd.getShapeId() != null){
					countyShapeIds.add(cd.getShapeId());
				}
			}
		} catch (RemoteException e){
			DisplayUtil.displayError("System error. Please contact system administrator.");
		}
		return countyShapeIds;
	}

}
