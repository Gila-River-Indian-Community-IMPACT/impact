package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class StackTestNote extends Note {

	private Integer _stackTestId;

	public StackTestNote() {
		this.requiredField(_stackTestId, "stackTestId");
	}

	public StackTestNote(StackTestNote note) {
		super(note);
		setStackTestId(note.getStackTestId());
		setDirty(note.isDirty());
	}

	public final void populate(ResultSet rs) {

		try {
			super.populate(rs);
			setStackTestId(AbstractDAO.getInteger(rs, "STACK_TEST_ID"));
			setDirty(false);
		} catch (SQLException sqle) {
			logger.error("Required field error");
		}

	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((_stackTestId == null) ? 0 : _stackTestId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if ((obj == null) || !(super.equals(obj))
				|| (getClass() != obj.getClass())) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final StackTestNote other = (StackTestNote) obj;

		// Either both null or equal values.
		if (((_stackTestId == null) && (other.getStackTestId() != null))
				|| ((_stackTestId != null) && (other.getStackTestId() == null))
				|| ((_stackTestId != null) && (other.getStackTestId() != null) && !(_stackTestId
						.equals(other.getStackTestId())))) {

			return false;
		}
		return true;
	}

	public Integer getStackTestId() {
		return _stackTestId;
	}

	public void setStackTestId(Integer stackTestId) {
		this._stackTestId = stackTestId;
		this.requiredField(_stackTestId, "stackTestId");
		setDirty(true);

	}
}
