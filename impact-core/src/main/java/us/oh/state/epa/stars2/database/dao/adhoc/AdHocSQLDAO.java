package us.oh.state.epa.stars2.database.dao.adhoc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.adhoc.DataGridCell;
import us.oh.state.epa.stars2.database.adhoc.DataGridCellDef;
import us.oh.state.epa.stars2.database.adhoc.DataGridRow;
import us.oh.state.epa.stars2.database.adhoc.DataSet;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.AdHocDAO;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class AdHocSQLDAO extends AbstractDAO implements AdHocDAO {

	private Logger logger = Logger.getLogger(AdHocSQLDAO.class);

	public boolean insert(DataGridRow dgr, String table,
			DataGridRow datasetDefinition) throws DAOException {
		DataGridCell[] dgc = dgr.getCells();
		Connection conn = null;

		try {
			conn = getConnection();
		} catch (DAOException daoe) {
			logger.error(daoe.getMessage(), daoe);
		}

		String sql = "insert into " + getSchemaQualifer() + table;
		String cols = " (";
		String vals = " values (";
		for (int i = 0; i < dgc.length; i++) {
			DataGridCell cell = dgc[i];
			if (i > 0) {
				cols = cols + ",";
				vals = vals + ",";
			}
			cols = cols + cell.getField();
			if (datasetDefinition.getCellDef(cell.getField()).getDataType()
					.equals("NUMBER")
					|| datasetDefinition.getCellDef(cell.getField())
					.getDataType().equals("numeric")) {
				if (cell.getValue().length() == 0) {
					vals = vals + "NULL";
				} else {
					vals = vals + cell.getValue();
				}
			} else if (datasetDefinition.getCellDef(cell.getField())
					.getDataType().equals("TIMESTAMP(6)")) {
				vals = vals + " TIMESTAMP '" + cell.getValue() + " 0:0:0'";
			} else if (datasetDefinition.getCellDef(cell.getField()).getDataType()
					.equals("varchar")) {
				if (cell.getValue().length() == 0) {
					vals = vals + "NULL";
				} else {
					cell.setValue(cell.getValue().replaceAll("\\'", "''"));
					vals = vals + "'" + cell.getValue() + "'";
				}
			} 
			else {
				cell.setValue(cell.getValue().replaceAll("\\'", "''"));
				vals = vals + "'" + cell.getValue() + "'";
			}
		}
		sql = sql + cols + ")" + vals + ")";
		logger.debug(sql);
		try {
			conn.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			handleException(e, conn);
		} finally {
			handleClosing(conn);
		}
		return true;
	}

	public boolean update(DataGridRow dgr, String table,
			DataGridRow datasetDefinition) throws DAOException {
		DataGridCell[] dgc = dgr.getCells();
		int updatedFields = 0;
		int whereFields = 0;
		Connection conn = null;

		try {
			conn = getConnection();
		} catch (DAOException daoe) {
			logger.error(daoe.getMessage(), daoe);
		}

		String sql = "update " + getSchemaQualifer() + table + " set ";
		String where = " where ";
		for (int i = 0; i < dgc.length; i++) {
			DataGridCell cell = dgc[i];
			if ((cell.isChanged())
					&& (!datasetDefinition.getCellDef(cell.getField())
							.isPrimaryKey())) {
				if (updatedFields > 0) {
					sql = sql + ", ";
				}
				if (datasetDefinition.getCellDef(cell.getField()).getDataType()
						.equals("NUMBER")
						|| datasetDefinition.getCellDef(cell.getField()).getDataType()
						.equals("numeric")) {
					if ((cell.getValue() == null)
							|| (cell.getValue().trim().length() == 0)) {
						sql = sql + cell.getField() + " = NULL";
					} else {
						sql = sql + cell.getField() + " = " + cell.getValue();
					}
				} else if (datasetDefinition.getCellDef(cell.getField())
						.getDataType().equals("TIMESTAMP(6)")) {
					if ((cell.getValue() == null)
							|| (cell.getValue().trim().length() == 0)) {
						sql = sql + cell.getField() + " = NULL";
					} else {
						sql = sql + cell.getField() + " = TIMESTAMP '"
								+ cell.getValue() + " 0:0:0'";
					}
				} else {
					if (cell.getValue() == null 
							|| (cell.getValue().trim().length() == 0)) {
						sql = sql + cell.getField() + " = NULL";
					} else {
						cell.setValue(cell.getValue().replaceAll("\\'", "''"));
						sql = sql + cell.getField() + " = '" + cell.getValue()
								+ "'";
					}
				}
				updatedFields++;
			} else if (datasetDefinition.getCellDef(cell.getField())
					.isPrimaryKey()) {
				// get the cell value and create the where clause
				if (whereFields > 0) {
					where = where + " and ";
				}
				if (datasetDefinition.getCellDef(cell.getField()).getDataType()
						.equals("NUMBER")) {
					where = where + cell.getField() + " = "
							+ cell.getInitialValue();
				} else if (datasetDefinition.getCellDef(cell.getField())
						.getDataType().equals("TIMESTAMP(6)")) {
					where = where + cell.getField() + " =  TIMESTAMP '"
							+ cell.getInitialValue() + " 0:0:0'";
				} else {
					where = where + cell.getField() + " = '"
							+ cell.getInitialValue() + "'";
				}
				whereFields++;
			} else {
				logger.debug("SQL has nothing to SET");
			}
		}
		
		if (updatedFields > 0) {
			sql = sql + where;
		}
		
		logger.debug(sql);

		try {
			if (updatedFields > 0) {
				int i = conn.createStatement().executeUpdate(sql);
				logger.debug(i + " rows updated");
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return false;
		} finally {
			handleClosing(conn);
		}
		return true;
	}

	public DataSet retrieve(DataSet ds) throws DAOException {
		
		Connection conn = getConnection();
		String OJ = "";

		Hashtable<String, String> hTables = new Hashtable<String, String>();
		Hashtable<String, String> hSelect = new Hashtable<String, String>();
		hTables.put(ds.getTable(), "");

		try {
			DataGridCellDef[] dgcd = ds.getDataSetDefinition().getDefCells();

			// see if we have more than one table
			OJ = getSchemaQualifer() + ds.getTable();
			for (int i = 0; i < dgcd.length; i++) {
				DataGridCellDef x = dgcd[i];
				hSelect.put(ds.getTable() + "." + x.getField(), "");
				if ((x.getPickListTable() != null)
						&& (x.getPickListTable().trim().length() > 0)) {
					hTables.put(x.getPickListTable() + " T" + i, "");
					hSelect.put("T" + i + "." + x.getPickListDisplay()
							+ " AS COL" + i, "");
					dgcd[i].setPickListColumnAlias("COL" + i);
					// add the where clause too
					OJ = OJ + " LEFT OUTER JOIN " + getSchemaQualifer()
							+ x.getPickListTable() + " T" + i + " ON "
							+ ds.getTable() + "." + x.getField() + " = " + "T"
							+ i + "." + x.getPickListColumn();
				}
			}

			String sql = generateSQLOJ(hSelect, OJ);
			if ((ds.getOrderBy() != null) && (ds.getOrderBy().length() > 0)) {
				sql = sql + " ORDER BY " + ds.getOrderBy();
			}
			logger.debug(sql);
			ResultSet rs3 = conn.createStatement().executeQuery(sql);

			String columnHeaders[] = new String[ds.getDataSetDefinition()
					.getColumnCount()];
			while (rs3.next()) {
				DataGridRow dgr = new DataGridRow();

				for (int j = 0; j < dgcd.length; j++) {
					DataGridCell dgcTemp = new DataGridCell();
					dgcTemp.setField(dgcd[j].getField());
					columnHeaders[j] = dgcTemp.getHeaderText();
					try {
						dgcTemp.setInitialValue(rs3.getString(dgcd[j]
								.getField()));
						dgcTemp.setHeaderText(dgcd[j].getHeaderText());
						// if it is a picklist then show the piclist value
						// instead
						// of the reference column value

						dgcTemp.setPickList(dgcd[j].isPickList());

						if (dgcTemp.isPickList()) {
							// be sure to reference the correct alias for this.
							dgcTemp.setDisplayValue(rs3.getString(dgcd[j]
									.getPickListColumnAlias()));
						} else {
							dgcTemp.setDisplayValue(rs3.getString(dgcd[j]
									.getField()));
						}

						dgcTemp.setDataSet(ds);
						dgcTemp.setRequired(dgcd[j].isRequired());
						dgcTemp.setField(dgcd[j].getField());
						dgcTemp.setDataType(dgcd[j].getDataType());
						dgcTemp.setMaximumLength(dgcd[j].getMaximumLength());
						if ((ds.getDataSetDefinition().getCellDef(
								dgcTemp.getField()).isPrimaryKey())
								|| ((ds.getDataSetDefinition().getCellDef(
										dgcTemp.getField()).isReadOnly()))) {
							dgcTemp.setReadOnly(true);
						}
					} catch (Exception e) {
						dgcTemp.setValue("?");
						dgcTemp.setInitialValue("");
					}
					dgr.addCell(dgcTemp);
				}
				ds.addRow(dgr);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			handleClosing(conn);
		}
		return ds;
	}

	public final List<SelectItem> retrievePickList(
			DataGridRow datasetDefinition, String field) throws DAOException {
		List<SelectItem> pickList = new ArrayList<SelectItem>();

		if (datasetDefinition.getCellDef(field).isPickList()) {
			String pickListTable = datasetDefinition.getCellDef(field)
					.getPickListTable();
			String valueColumn = datasetDefinition.getCellDef(field)
					.getPickListColumn();
			String displayColumns = datasetDefinition.getCellDef(field)
					.getPickListDisplay();
			String orderBy = datasetDefinition.getCellDef(field)
					.getOrderBy();
			Connection conn = null;

			try {
				conn = getConnection();
			} catch (DAOException daoe) {
				logger.error(daoe.getMessage(), daoe);
			}
			if ((pickListTable.length() > 0) && (valueColumn.length() > 0)
					&& (displayColumns.length() > 0)) {
				try {
					String sql = "select " + valueColumn + "," + displayColumns
							+ " from " + addSchemaToTable(pickListTable);
					if ((orderBy != null) && (orderBy.length() > 0)) {
						sql = sql + " ORDER BY " + orderBy;
					}
					logger.debug(sql);
					ResultSet rs3 = conn.createStatement().executeQuery(sql);
					while (rs3.next()) {
						try {
							pickList.add(new SelectItem(rs3.getString(1), rs3
									.getString(2)));
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
					}
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
					pickList = new ArrayList<SelectItem>();
				} finally {
					handleClosing(conn);
				}
			}
		}
		return pickList;
	}

//	private String getSchema() {
//		String schema = "";
//		if (DAOFactory.isSchemaSubstitution()) {
//			try {
//				DAOFactory daoFactory = (DAOFactory) CompMgr
//						.newInstance("app.DAOFactory");
//				schema = daoFactory
//						.getSchemaQualifier(CommonConst.READONLY_SCHEMA);
//			} catch (UnableToStartException utse) {
//				logger.error(utse.getMessage(), utse);
//			}
//		}
//		return schema;
//	}

	private String generateSQLOJ(Hashtable<String, String> select, String OJ) {
		String sql = "SELECT ";
		int selectCt = 0;
		Enumeration<String> eSelect = select.keys();
		while (eSelect.hasMoreElements()) {
			if (selectCt > 0) {
				sql = sql + ",";
			}

			sql = sql + eSelect.nextElement();
			selectCt++;
		}
		sql = sql + " FROM " + OJ;
		return sql;
	}
}
