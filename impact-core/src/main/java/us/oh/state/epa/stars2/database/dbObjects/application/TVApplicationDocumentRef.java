package us.oh.state.epa.stars2.database.dbObjects.application;

import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.TVApplicationDocumentTypeDef;

public class TVApplicationDocumentRef extends ApplicationDocumentRef {

	public TVApplicationDocumentRef() {
        super();
    }

    public TVApplicationDocumentRef(TVApplicationDocumentRef old) {
        super(old);
    }

    public TVApplicationDocumentRef(ApplicationDocumentRef adr) {
        super(adr);
    }
    	
    // Use this function to check tradeSecretAllowed.
 	@Override
    public boolean isTradeSecretAllowed() {
    	boolean ret = false;

    	DefSelectItems tempAttachmentTypesDef = TVApplicationDocumentTypeDef.getData().getItems();
    	if (tempAttachmentTypesDef != null) {
    		BaseDef attachmentDef = tempAttachmentTypesDef.getItem(getApplicationDocumentTypeCD());
    		if ((attachmentDef != null) && (attachmentDef instanceof TVApplicationDocumentTypeDef)) {
    			TVApplicationDocumentTypeDef attachmentTypeDef = (TVApplicationDocumentTypeDef)attachmentDef;
    			ret = attachmentTypeDef.isTradeSecretAllowed();
    		}
    	}

    	return ret;
    }
}
