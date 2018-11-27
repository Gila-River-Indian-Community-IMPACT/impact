package us.oh.state.epa.stars2.app.facility;

import java.util.List;

import oracle.adf.view.faces.component.UIXTable;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;

public class FacilityRUM  extends TaskBase  {
    public FacilityRUM() {
        super();
    }

    @Override
    public final void doSelected(UIXTable table) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final String findOutcome(String url, String ret) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final String getDoSelectedButtonText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final Integer getExternalId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public final boolean isDoSelectedButton(ProcessActivity activity) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public final void setExternalId(Integer externalId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public final List<ValidationMessage> validate(Integer inActivityTemplateId) {
        // TODO Auto-generated method stub
        return null;
    }
}
