package us.oh.state.epa.stars2.webcommon.component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.context.FacesContext;
import javax.faces.validator.DoubleRangeValidator;
import javax.faces.validator.Validator;

import oracle.adf.view.faces.component.UIXEditableValue;
import oracle.adf.view.faces.component.UIXSelectOne;
import oracle.adf.view.faces.component.core.input.CoreInputText;
import oracle.adf.view.faces.component.core.input.CoreSelectInputDate;
import oracle.adf.view.faces.component.core.input.CoreSelectOneChoice;
import oracle.adf.view.faces.component.core.input.CoreSelectOneListbox;
import oracle.adf.view.faces.component.core.input.CoreSelectOneRadio;
import oracle.adf.view.faces.component.core.layout.CorePanelForm;
import oracle.adf.view.faces.component.core.layout.CorePanelHeader;
import oracle.adf.view.faces.convert.DateTimeConverter;
import oracle.adf.view.faces.convert.NumberConverter;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;
import us.oh.state.epa.stars2.database.dbObjects.workflow.EnumDetail;
import us.oh.state.epa.stars2.def.ContEquipTypeDef;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.converter.PhoneNumberConverter;

public class BuildComponent implements java.io.Serializable {
	private DataDetail dataDetail;
	private boolean required;
	private boolean showRequired;
	private boolean readOnly;
	private String id;
	private Application app;
	private String unselectedLabel;

	public final Object byDataTypeId() {
		app = FacesContext.getCurrentInstance().getApplication();
		switch (dataDetail.getDataTypeId()) {
		case 1:
			if (!dataDetail.isReadOnly()) {
				return buildInputString();
			} else {
				return buildPanelHeader();
			}
		case 2:
			return buildInputInteger();
		case 3:
			return buildInputDate();
		case 4:
			return buildInputFloat();
		case 5:
			return buildEnum();
		case 6:
			return buildInputPhoneNumber();
		case 7:
			return buildNoteInputText();
		case 8:
			return buildInputText();
		case 9:
			return buildInputText();
		case 11:
			return buildEnum();

		default:
			return buildInputText();
		}
	}

	/**
	 * @return
	 * 
	 */
	private Object buildInputPhoneNumber() {
		CoreInputText output = (CoreInputText) buildInputText();
		PhoneNumberConverter nc = new PhoneNumberConverter();
		output.setConverter(nc);
		output.setShowRequired(dataDetail.isRequired());
		return output;
	}

	/**
	 * @return
	 * 
	 */
	private Object buildInputFloat() {
		CoreInputText output = (CoreInputText) buildInputText();
		NumberConverter nc = getNumberConverter();
		nc.setIntegerOnly(false);
		output.setConverter(nc);

		output.addValidator(getDoubleRangeValidator());
		output.setShowRequired(dataDetail.isRequired());

		output.setColumns(12);
		output.setMaximumLength(12);

		return output;
	}

	/**
	 * @return
	 * 
	 */
	private Validator getDoubleRangeValidator() {
		DoubleRangeValidator vdr = new DoubleRangeValidator();
		String max = dataDetail.getMaxVal();
		if (max != null) {
			vdr.setMaximum(new Double(max));
		}
		String min = dataDetail.getMinVal();
		if (min != null) {
			vdr.setMinimum(new Double(min));
		}
		return vdr;
	}

	/**
	 * @return
	 * 
	 */
	private NumberConverter getNumberConverter() {
		NumberConverter nc = new NumberConverter();
		nc.setType("number");
		if (dataDetail.getFormatMask() != null) {
			nc.setPattern(dataDetail.getFormatMask());
		}
		return nc;
	}

	/**
	 * @return
	 */
	private Object buildInputInteger() {
		CoreInputText output = (CoreInputText) buildInputText();
		NumberConverter nc = getNumberConverter();
		nc.setIntegerOnly(true);
		output.setConverter(nc);
		output.setShowRequired(dataDetail.isRequired());
		output.addValidator(getDoubleRangeValidator());

		output.setColumns(12);
		output.setMaximumLength(12);

		return output;
	}

	private Object buildNoteInputText() {
		CoreInputText output = (CoreInputText) buildInputText();
		output.setColumns(50);
		output.setRows(5);
		output.setShowRequired(dataDetail.isRequired());
		return output;
	}

	private Object buildInputDate() {
		/*
		 * CoreSelectInputDate output = (CoreSelectInputDate) app
		 * .createComponent(CoreSelectInputDate.COMPONENT_TYPE);
		 */
		CoreInputText output = (CoreInputText) app
				.createComponent(CoreInputText.COMPONENT_TYPE);
		DateTimeConverter nc = new DateTimeConverter();
		nc.setPattern("MM/dd/yyyy");
		output.setConverter(nc);

		output.setLabel(buildName());
		output.setId(getId());
		output.setShortDesc(dataDetail.getDataDetailDsc());
		output.setReadOnly(readOnly || dataDetail.isReadOnly());
		output.setShowRequired(dataDetail.isRequired());

		// DateTimeConverter dtc = new DateTimeConverter();
		// dtc.setDateStyle("long");
		// output.setConverter(dtc);

		return setupBasicProperty(output);
	}

	private Object buildInputText() {
		CoreInputText output = (CoreInputText) app
				.createComponent(CoreInputText.COMPONENT_TYPE);

		output.setLabel(buildName());
		output.setId(dataDetail.getJspId());
		output.setShortDesc(dataDetail.getDataDetailDsc());
		output.setReadOnly(readOnly || dataDetail.isReadOnly());
		output.setShowRequired(dataDetail.isRequired());
		String max = dataDetail.getMaxVal();

		if (max != null) {
			output.setMaximumLength(new Integer(max));
		} else {
			output.setMaximumLength(2500);
		}

		return setupBasicProperty(output);
	}

	private Object buildInputString() {
		CoreInputText output = (CoreInputText) buildInputText();

		if (output.getMaximumLength() >= 80) {
			output.setColumns(80);
		} else {
			output.setColumns(output.getMaximumLength());
		}

		if (output.getMaximumLength() > 200) {
			output.setRows(4);
		}

		return output;

	}

	private Object buildPanelHeader() {
		CorePanelHeader output = (CorePanelHeader) app
				.createComponent(CorePanelHeader.COMPONENT_TYPE);
		
		output.setText(buildName());
		output.setId(getId());
		output.setShortDesc(dataDetail.getDataDetailDsc());
		

		return output;
	}

	@SuppressWarnings("unchecked")
	private Object buildEnum() {
		UISelectItem item;
		EnumDetail[] es = dataDetail.getEnumDetails();
		UIXSelectOne output;
		if (es.length >= 3) {
			output = buildSelectOneChoice();
			// else if (es.length > 3)
			// output = buildSelectOneListbox();
		} else {
			if (dataDetail.getEnumCd().equals(ContEquipTypeDef.OXTY)) {
				output = buildSelectOneChoice();
			} else {
				output = buildSelectOneRadio();
			}
		}

		for (EnumDetail e : es) {
			item = new UISelectItem();
			item.setItemValue(e.getEnumValue());
			item.setItemLabel(e.getEnumLabel());
			output.getChildren().add(item);
		}

		output.setId(dataDetail.getJspId());

		return setupBasicProperty(output);
	}

	/*
	 * private UIXSelectOne buildSelectOneListbox() { CoreSelectOneListbox
	 * output = (CoreSelectOneListbox) app.createComponent(
	 * CoreSelectOneListbox.COMPONENT_TYPE); output.setLabel(buildName());
	 * output.setShortDesc(dataDetail.getDataDetailDsc());
	 * output.setReadOnly(readOnly || dataDetail.isReadOnly()); return output; }
	 */

	private UIXSelectOne buildSelectOneRadio() {
		CoreSelectOneRadio output = (CoreSelectOneRadio) app
				.createComponent(CoreSelectOneRadio.COMPONENT_TYPE);
		output.setLabel(buildName());
		if (unselectedLabel != null) {
			output.setUnselectedLabel(unselectedLabel);
		}
		output.setShortDesc(dataDetail.getDataDetailDsc());
		output.setReadOnly(readOnly || dataDetail.isReadOnly());
		output.setLayout("horizontal");
		output.setShowRequired(dataDetail.isRequired());
		return output;
	}

	private UIXSelectOne buildSelectOneChoice() {
		CoreSelectOneChoice output = (CoreSelectOneChoice) app
				.createComponent(CoreSelectOneChoice.COMPONENT_TYPE);
		output.setLabel(buildName());
		if (unselectedLabel != null) {
			output.setUnselectedLabel(unselectedLabel);
		}
		output.setShortDesc(dataDetail.getDataDetailDsc());
		output.setReadOnly(readOnly || dataDetail.isReadOnly());
		output.setShowRequired(dataDetail.isRequired());
		return output;
	}

	private String buildName() {
		StringBuffer name = new StringBuffer(dataDetail.getDataDetailLbl());
		String ulbl = dataDetail.getUnitDefLbl();
		if (ulbl != null) {
			name.append(" (");
			name.append(ulbl);
			name.append(")");
		}
		name.append(" :");
		return name.toString();
	}

	private Object setupBasicProperty(UIXEditableValue output) {
		output.setValue(dataDetail.getDataDetailVal());
		output.setRequired(required && dataDetail.isRequired());
		output.setRendered(dataDetail.isVisible());

		return output;
	}

	public final DataDetail getDataDetail() {
		return dataDetail;
	}

	public final void setDataDetail(DataDetail dataDetail) {
		this.dataDetail = dataDetail;
	}

	public final boolean isReadOnly() {
		return readOnly;
	}

	public final void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public final boolean isRequired() {
		return required;
	}

	public final void setRequired(boolean required) {
		this.required = required;
	}

	public static LinkedHashMap<String, String> getDataToHashMap(
			CorePanelForm data) {
		LinkedHashMap<String, String> tdata = new LinkedHashMap<String, String>();
		boolean missingRequiredValue = false;
		if (data != null) {
			List<?> children = data.getChildren();
			if (children != null) {
				Object[] ds = children.toArray();
				for (Object d : ds) {
					if (d instanceof CorePanelHeader) {
						continue;
					}
					UIXEditableValue t = (UIXEditableValue) d;
					if (t.isRequired() && t.getValue() == null)
						missingRequiredValue = false;

					if (d instanceof CoreInputText) {
						CoreInputText td = (CoreInputText) d;
						if (td.getValue() != null
								&& td.getValue().toString().length() != 0) {
							if (td.getValue() instanceof Date) {
								Date tDate = (Date) td.getValue();
								put(tdata, td.getLabel(),
										new Long(tDate.getTime()).toString());
							} else
								put(tdata, td.getLabel(), td.getValue()
										.toString());
						} else {
							put(tdata, td.getLabel(), null);
						}
					} else if (d instanceof CoreSelectInputDate) {
						CoreSelectInputDate td = (CoreSelectInputDate) d;
						Date tDate = (Date) td.getValue();
						tDate = FacesUtil.convertYear(new Timestamp(tDate
								.getTime()));
						if (tDate != null) {
							put(tdata, td.getLabel(),
									new Long(tDate.getTime()).toString());
						} else {
							put(tdata, td.getLabel(), null);
						}
					} else if (d instanceof CoreSelectOneChoice) {
						CoreSelectOneChoice td = (CoreSelectOneChoice) d;
						put(tdata, td.getLabel(), (String) td.getValue());
					} else if (d instanceof CoreSelectOneRadio) {
						CoreSelectOneRadio td = (CoreSelectOneRadio) d;
						put(tdata, td.getLabel(), (String) td.getValue());
					} else if (d instanceof CoreSelectOneListbox) {
						CoreSelectOneListbox td = (CoreSelectOneListbox) d;
						put(tdata, td.getLabel(), (String) td.getValue());
					}
				}
			}
		}
		tdata.put("missingRequiredValue", (missingRequiredValue) ? "Y" : "N");
		return tdata;
	}

	private static void put(LinkedHashMap<String, String> tdata, String label,
			String value) {
		int end;
		if (label.contains(" (")) {
			end = label.indexOf(" (");
		} else if (label.contains(" :")) {
			end = label.indexOf(" :");
		} else {
			end = label.length();
		}
		label = label.substring(0, end);

		if (value == null) {
			value = "";
		}

		tdata.put(label, value);
	}

	public final String getId() {
		if (id == null && dataDetail.getDataDetailId() != null) {
			id = dataDetail.getJspId();
		} else {
			id = null;
		}
		return id;
	}

	public final void setId(String id) {
		this.id = id;
	}

	public final String getUnselectedLabel() {
		return unselectedLabel;
	}

	public final void setUnselectedLabel(String unselectedLabel) {
		this.unselectedLabel = unselectedLabel;
	}

	public static void cleanUp(UIComponent data) {
		if (data != null) {
			UIComponent p = data.getParent();
			if (p != null) {
				List<?> cs = p.getChildren();
				cs.remove(data);
			}
		}
	}

	public boolean isShowRequired() {
		return showRequired;
	}

	public void setShowRequired(boolean showRequired) {
		this.showRequired = showRequired;
	}
}
