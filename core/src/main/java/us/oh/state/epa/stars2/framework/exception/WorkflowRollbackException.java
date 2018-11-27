package us.oh.state.epa.stars2.framework.exception;

import java.rmi.RemoteException;

@SuppressWarnings("serial")
public class WorkflowRollbackException extends RemoteException {
	private int wfId;
	private int extId;

	public WorkflowRollbackException() {
		super();
	}

	public WorkflowRollbackException(String s, Throwable cause, int wfId,
			int extId) {
		super(s, cause);
		this.wfId = wfId;
		this.extId = extId;
	}

	public WorkflowRollbackException(String s) {
		super(s);
	}

	public int getWfId() {
		return wfId;
	}

	public void setWfId(int wfId) {
		this.wfId = wfId;
	}

	public int getExtId() {
		return extId;
	}

	public void setExtId(int extId) {
		this.extId = extId;
	}
	
	

}