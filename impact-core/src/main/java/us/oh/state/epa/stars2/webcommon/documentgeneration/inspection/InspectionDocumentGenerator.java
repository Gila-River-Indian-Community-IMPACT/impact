package us.oh.state.epa.stars2.webcommon.documentgeneration.inspection;

import us.oh.state.epa.stars2.webcommon.documentgeneration.DocumentBuilder;
import us.oh.state.epa.stars2.webcommon.documentgeneration.DocumentGenerator;

public class InspectionDocumentGenerator extends DocumentGenerator {

	@Override
	protected DocumentBuilder createDocumentBuilder() {
		return new InspectionInfoBuilder();
	}
}
