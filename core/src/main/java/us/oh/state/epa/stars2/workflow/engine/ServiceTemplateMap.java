package us.oh.state.epa.stars2.workflow.engine;

/**
 * <p>
 * Title: ServiceTemplateMap
 * </p>
 * 
 * <p>
 * Description: This is a utility class that associates a service Id with the
 * activity template Def Id for whatever workflow process we are executing on
 * behalf of this service.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author
 * @version 1.0
 */

public class ServiceTemplateMap {
    private Integer serviceId;
    private Integer activityTemplateDefId;

    /**
     * Constructor.
     * 
     * @param serviceId
     *            Service Id.
     * @param actTemplateDefId
     *            Activity template def Id.
     */
    public ServiceTemplateMap(Integer serviceId, Integer actTemplateDefId) {
        this.serviceId = serviceId;
        this.activityTemplateDefId = actTemplateDefId;
    }

    /**
     * Returns the service Id.
     * 
     * @return Integer service Id.
     */
    public final Integer getServiceId() {
        return serviceId;
    }

    /**
     * Returns the activity template def Id.
     * 
     * @return Activity template def Id.
     */
    public final Integer getActivityTemplateDefId() {
        return activityTemplateDefId;
    }
}
