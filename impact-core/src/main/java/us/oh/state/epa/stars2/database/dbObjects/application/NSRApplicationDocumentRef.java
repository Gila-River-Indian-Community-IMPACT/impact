package us.oh.state.epa.stars2.database.dbObjects.application;

import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.DefSelectItems;

public class NSRApplicationDocumentRef extends ApplicationDocumentRef {

	public NSRApplicationDocumentRef() {
        super();
    }

    public NSRApplicationDocumentRef(NSRApplicationDocumentRef old) {
        super(old);
    }

    public NSRApplicationDocumentRef(ApplicationDocumentRef adr) {
        super(adr);
    }
    	
    // Use this function to check tradeSecretAllowed.
 	@Override
    public boolean isTradeSecretAllowed() {
    	boolean ret = false;

    	DefSelectItems tempAttachmentTypesDef = ApplicationDocumentTypeDef.getData().getItems();
    	if (tempAttachmentTypesDef != null) {
    		BaseDef attachmentDef = tempAttachmentTypesDef.getItem(getApplicationDocumentTypeCD());
    		if ((attachmentDef != null) && (attachmentDef instanceof ApplicationDocumentTypeDef)) {
    			ApplicationDocumentTypeDef attachmentTypeDef = (ApplicationDocumentTypeDef)attachmentDef;
    			ret = attachmentTypeDef.isTradeSecretAllowed();
    		}
    	}

    	return ret;
    }
}
