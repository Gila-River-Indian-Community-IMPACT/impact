package us.oh.state.epa.stars2.scheduler.jobs;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import us.oh.state.epa.stars2.bo.FullComplianceEvalService;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FullComplianceEval;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

public class AddMissingFceDirs implements StatefulJob {

	private transient Logger logger;

	public AddMissingFceDirs() {
		logger = Logger.getLogger(this.getClass());
	}

	public void execute(JobExecutionContext context) throws JobExecutionException {

		FullComplianceEval[] fceList = null;

		try {
			FullComplianceEvalService fceBO = ServiceFactory.getInstance().getFullComplianceEvalService();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -2);
			Timestamp beginSched = new Timestamp(cal.getTimeInMillis());
			fceList = fceBO.retrieveFceBySearch(null, null, null, null, null, null, null, beginSched, null, null, null,
					null, null, null, null, null, null);
			for (FullComplianceEval fce : fceList) {
				fceBO.addMissingFceDir(fce);
			}
		} catch (ServiceFactoryException sfe) {
			logger.error(sfe);
		} catch (RemoteException re) {
			logger.error("AddMissingFceDirs() failed with " + re.getMessage(), re);
		}
	}
	
}
