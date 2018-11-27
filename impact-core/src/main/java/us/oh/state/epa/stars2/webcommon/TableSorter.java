package us.oh.state.epa.stars2.webcommon;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import oracle.adf.view.faces.component.UIXTable;
import oracle.adf.view.faces.component.core.data.CoreColumn;
import oracle.adf.view.faces.model.CollectionModel;
import oracle.adf.view.faces.model.SortCriterion;
import us.oh.state.epa.stars2.util.Pair;

public class TableSorter extends CollectionModel implements
		java.io.Serializable {
	
	private static final long serialVersionUID = 3053644552173685607L;

	private transient UIXTable table;
	private List<?> sortCriteria = Collections.EMPTY_LIST;
	private List<Object> data;
	private int index = -1;

	public final Object getRowKey() {
		if (isRowAvailable()) {
			return Integer.toString(index);
		}

		return null;
	}

	public final void setRowKey(Object key) {
		if (key == null) {
			index = -1;
		} else {
			index = Integer.parseInt((String) key);
		}
	}

	public final boolean isRowAvailable(int rowIndex) {
		return (data != null && rowIndex >= 0 && rowIndex < data.size());
	}

	public final Object getRowData(int rowIndex) {
		return data.get(rowIndex);
	}

	public final boolean isSortable(String property) {
		return true;
	}

	public final List<?> getSortCriteria() {
		return sortCriteria;
	}

	@SuppressWarnings("unchecked")
	public final void setSortCriteria(List criteria) {
		if (!this.sortCriteria.equals(criteria)) {
			this.sortCriteria = criteria;
			sort();
		}
	}

	public final boolean isRowAvailable() {
		return (data != null && index >= 0 && index < data.size());
	}

	public final int getRowCount() {
		return data == null ? 0 : data.size();
	}

	public final Object getRowData() {
		return data.get(index);
	}

	public final int getRowIndex() {
		return index;
	}

	public final void setRowIndex(int index) {
		this.index = index;
	}

	public final Object getWrappedData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	public final void setWrappedData(Object object) {
		if (object == null) {
			data = null;
		} else if (object instanceof List) {
			data = (List) object;
		} else if (object instanceof Object[]) {
			data = new ArrayList<Object>();
			for (Object el : (Object[]) object) {
				data.add(el);
			}
		} else {
			data = null;
		}
		index = -1;
		sortCriteria = Collections.EMPTY_LIST;
		if (table != null) {
			table.setSortCriteria(Collections.EMPTY_LIST);
		}
	}

	@SuppressWarnings("unchecked")
	private void sort() {
		if (data == null || data.size() == 0 || sortCriteria.isEmpty()
				|| table == null) {
			return;
		}

		/*
		 * To sort the collection, we will create a temporary list of pairs
		 * having one element for each record. In each pair, the first element
		 * is the sorting value for that record, the second element is a
		 * reference to the record itself.
		 */
		List<Pair<Comparable, Object>> valueAndObj = new ArrayList<Pair<Comparable, Object>>(
				data.size());
		int idx;
		Object row;
		Comparable value;

		/*
		 * Check if the name of the sort property is a valid EL-chain of Java
		 * properties (e.g., property1.property2.property3) within the objects
		 * in the underlying collection.
		 */
		final SortCriterion sortCriterion = (SortCriterion) sortCriteria.get(0);
		List<Method> sortProperties = new ArrayList<Method>();
		StringTokenizer st = new StringTokenizer(sortCriterion.getProperty(),
				".");
		Class objClass = data.get(0).getClass();
		while (st.hasMoreTokens()) {
			StringBuffer getterName = new StringBuffer(st.nextToken());
			getterName
					.setCharAt(0, Character.toUpperCase(getterName.charAt(0)));
			getterName.insert(0, "get");
			boolean propertyFound = false;
			for (Method method : objClass.getMethods()) {
				if (method.getName().equals(getterName.toString())
						&& method.getParameterTypes().length == 0) {
					sortProperties.add(method);
					objClass = method.getReturnType();
					propertyFound = true;
					break;
				}
			}

			if (!propertyFound) {
				sortProperties.clear();
				break;
			}
		}

		/*
		 * If the name of the sort property is a valid EL-chain of Java
		 * properties, calculate the sorting value by evaluating the EL. Else,
		 * sort on the text that show up in the grid. In either case, for every
		 * row, insert the sort value and a reference to the row into the
		 * temporary list.
		 */
		if (!sortProperties.isEmpty()) {
			for (idx = 0; idx < data.size(); idx++) {
				row = data.get(idx);

				Object tempVal = row;
				for (Method sortProperty : sortProperties) {
					try {
						tempVal = sortProperty.invoke(tempVal, (Object[]) null);
					} catch (Exception ex) {
						tempVal = "";
						break;
					}
				}
				value = (Comparable) tempVal;
				valueAndObj.add(new Pair<Comparable, Object>(value, row));
			}
		} else {
			/*
			 * Get a hold of the sort column
			 */
			CoreColumn sortColumn = null;
			Iterator it = ((UIComponent) table).getChildren().iterator();
			while (it.hasNext()) {
				UIComponent child = (UIComponent) it.next();
				if (child instanceof CoreColumn
						&& ((CoreColumn) child).getSortProperty().equals(
								sortCriterion.getProperty())) {
					sortColumn = (CoreColumn) child;
					break;
				}
			}

			if (sortColumn == null) {
				return;
			}

			Map<String, Object> requestMap = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestMap();
			for (idx = 0; idx < data.size(); idx++) {
				row = data.get(idx);
				requestMap.put(table.getVar(), row);
				value = FacesUtil.getComponentText(sortColumn, true);
				valueAndObj.add(new Pair<Comparable, Object>(value, row));
			}
			requestMap.remove(table.getVar());
		}

		/*
		 * Sort the temporary list
		 */
		Collections.sort(valueAndObj,
				new Comparator<Pair<Comparable, Object>>() {
					public int compare(Pair<Comparable, Object> one,
							Pair<Comparable, Object> two) {
						int retVal;

						if (one.getFirst() == null) {
							if (two.getFirst() == null) {
								retVal = 0;
							} else {
								retVal = 1;
							}
						} else if (two.getFirst() == null) {
							retVal = -1;
						} else {
							retVal = one.getFirst().compareTo(two.getFirst());
						}

						return sortCriterion.isAscending() ? retVal : -retVal;
					}
				});

		/*
		 * Copy the sorted records into the wrapped data collection
		 */
		for (idx = 0; idx < valueAndObj.size(); idx++) {
			data.set(idx, valueAndObj.get(idx).getSecond());
		}
	}

	// blkSize specifies how many rows are displayed at a time (usually
	// AppBase.getPageLimit().intValue()).
	// For example if there are 37 rows in the table and blkSize is 10,
	// it will result in showing rows 31 - 37.
	// positionToLastBlock() does nothing if all rows are being displayed.
	public final void positionToLastBlock(int blkSize) {
		if (!table.isShowAll()) {
			int i = table.getRowCount() / blkSize * blkSize;
			table.setFirst(i);
		}
	}

	// positiontoFirstBlock() is needed after using positionToLastBlock() to
	// quit displaying the last rows of the table (e.g., at Save or Cancel
	// time).
	public final void positionToFirstBlock() {
		table.setFirst(0);
	}

	public final UIXTable getTable() {
		return table;
	}

	public final void setTable(UIXTable table) {	
		this.table = table;
		if (this.sortCriteria!=null) {			
			sort();
		}
	}

	public final void clearWrappedData() {
		if (data != null) {
			data.clear();
			data = null;
		}

		if (table != null) {
			table.setFirst(0);
		}

		index = -1;
	}

	public final List<Object> getData() {
		return data;
	}

	public final void setData(List<Object> data) {
		this.data = data;
	}

	public final int getIndex() {
		return index;
	}

	public final void setIndex(int index) {
		this.index = index;
	}
}
