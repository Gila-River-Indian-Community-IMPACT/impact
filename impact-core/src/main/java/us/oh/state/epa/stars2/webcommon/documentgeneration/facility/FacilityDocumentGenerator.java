package us.oh.state.epa.stars2.webcommon.documentgeneration.facility;

import us.oh.state.epa.stars2.webcommon.documentgeneration.DocumentBuilder;
import us.oh.state.epa.stars2.webcommon.documentgeneration.DocumentGenerator;

public class FacilityDocumentGenerator extends DocumentGenerator {

	@Override
	protected DocumentBuilder createDocumentBuilder() {
		return new FacilityInfoBuilder();
	}
}
