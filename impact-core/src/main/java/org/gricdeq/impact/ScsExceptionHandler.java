package org.gricdeq.impact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.oh.state.epa.stars2.webcommon.DisplayUtil;

public class ScsExceptionHandler {

	private static final String CONTACT_HELP_DESK = "Please contact the Help Desk if you need assistance.";
	
	private static final Logger log = LoggerFactory.getLogger(ScsExceptionHandler.class);

	public static void handleException(net.exchangenetwork.wsdl.sharedcromerr.usermgmt._1.SharedCromerrException e) {
		try {
			org.gricdeq.scs.schema.sharedcromerr.usermgmt._1.SharedCromerrFault fault = e.getFaultInfo();
			if (null != fault) {
				org.gricdeq.scs.schema.sharedcromerr.usermgmt._1.SharedCromerrErrorCode errorCode = fault.getErrorCode();
				switch (errorCode) {
				case E_ACCESS_DENIED:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ACCOUNT_EXPIRED:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ACCOUNT_LOCKED:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INSUFFICIENT_PRIVILEGES:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INTERNAL_ERROR:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_ARGUMENT:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_CREDENTIAL:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_DATAFLOW_NAME:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_SIGNATURE:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_TOKEN:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_REACHED_MAX_NUMBER_OF_ATTEMPTS:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_TOKEN_EXPIRED:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_UNKNOWN:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_UNKNOWN_USER:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WEAK_PASSWORD:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WRONG_ANSWER:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WRONG_ID_PASSWORD:
					DisplayUtil.displayError(fault.getDescription());
					break;
				default:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
				}
				DisplayUtil.displayError(CONTACT_HELP_DESK);
			} else {
				log.error("Unable to get fault info / error code for SharedCromerrException: " + e,e);
				DisplayUtil.displayError("System error occurred: " + e.getMessage());
			}
		} catch (Exception e2) {
			// sometimes, DisplayUtil throws exceptions because the user has not established
			// an ADF context yet
			log.error("SCS error occurred: " + e,e);
		}
		
	}

	public static void handleException(net.exchangenetwork.wsdl.sharedcromerr.signandcor._1.SharedCromerrException e) {
		try {
			org.gricdeq.scs.schema.sharedcromerr.signandcor._1.SharedCromerrFault fault = e.getFaultInfo();
			if (null != fault) {
				org.gricdeq.scs.schema.sharedcromerr.signandcor._1.SharedCromerrErrorCode errorCode = fault.getErrorCode();
				switch (errorCode) {
				case E_ACCESS_DENIED:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ACCOUNT_EXPIRED:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ACCOUNT_LOCKED:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INSUFFICIENT_PRIVILEGES:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INTERNAL_ERROR:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_ARGUMENT:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_CREDENTIAL:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_DATAFLOW_NAME:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_SIGNATURE:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_TOKEN:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_REACHED_MAX_NUMBER_OF_ATTEMPTS:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_TOKEN_EXPIRED:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_UNKNOWN:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_UNKNOWN_USER:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WEAK_PASSWORD:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WRONG_ANSWER:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WRONG_ID_PASSWORD:
					DisplayUtil.displayError(fault.getDescription());
					break;
				default:
					log.error("Unspecified SCS error occurred: ", e);
					DisplayUtil.displayError(fault.getDescription());
				}
				DisplayUtil.displayError(CONTACT_HELP_DESK);
			} else {
				log.error("Unable to get fault info / error code for SharedCromerrException: " + e,e);
				DisplayUtil.displayError("System error occurred: " + e.getMessage());
			}
		} catch (Exception e2) {
			// sometimes, DisplayUtil throws exceptions because the user has not established
			// an ADF context yet
			log.error("SCS error occurred: " + e,e);
		}
	}
	
	public static void handleException(net.exchangenetwork.wsdl.sharedcromerr.portal._1.SharedPortalException e) {
		try {
			org.gricdeq.scs.schema.sharedcromerr.portal._1.SharedPortalFault fault = e.getFaultInfo();
			if (null != fault) {
				org.gricdeq.scs.schema.sharedcromerr.portal._1.SharedPortalErrorCode errorCode = fault.getErrorCode();
				switch(errorCode) {
				case E_ACCESS_DENIED:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ACCOUNT_EXPIRED:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ACCOUNT_LOCKED:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ANSWERS_ALREADY_EXIST:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_AUTH_METHOD:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INSUFFICIENT_PRIVILEGES:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INTERNAL_ERROR:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_ANSWER_RESET_CODE:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_ARGUMENT:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_CREDENTIAL:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_TOKEN:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_MAX_NUMBER_OF_RESET_ATTEMPTS_REACHED:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_REACHED_MAX_NUMBER_OF_ATTEMPTS:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ROLE_ALREADY_EXISTS:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_TOKEN_EXPIRED:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_UNKNOWN_USER:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_USER_ALREADY_EXISTS:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WEAK_PASSWORD:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WRONG_ANSWER:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WRONG_ID_PASSWORD:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WRONG_USER_ID:
					DisplayUtil.displayError(fault.getDescription());
					break;
				default:
					log.error("Unspecified SCS error occurred: ", e);
					DisplayUtil.displayError(fault.getDescription());
				}
				DisplayUtil.displayError(CONTACT_HELP_DESK);
			} else {
				log.error("Unable to get fault info / error code for SharedCromerrException: " + e,e);
				DisplayUtil.displayError("System error occurred: " + e.getMessage());
			}
		} catch (Exception e2) {
			// sometimes, DisplayUtil throws exceptions because the user has not established
			// an ADF context yet
			log.error("SCS error occurred: " + e,e);
		}
	}
	
	public static void handleException(net.exchangenetwork.wsdl.sharedcromerr.secondfactorauth._1.SharedCromerrException e) {
		try {
			org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.SharedCromerrFault fault = e.getFaultInfo();
			if (null != fault) {
				org.gricdeq.scs.schema.sharedcromerr.secondfactorauth._1.SharedCromerrErrorCode errorCode = fault.getErrorCode();
				switch (errorCode) {
				case E_ACCESS_DENIED:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ACCOUNT_EXPIRED:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_ACCOUNT_LOCKED:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INSUFFICIENT_PRIVILEGES:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INTERNAL_ERROR:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_ARGUMENT:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_CREDENTIAL:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_DATAFLOW_NAME:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_SIGNATURE:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_INVALID_TOKEN:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_REACHED_MAX_NUMBER_OF_ATTEMPTS:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_TOKEN_EXPIRED:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_UNKNOWN:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_UNKNOWN_USER:
					log.error(fault.getDescription(),e);
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WEAK_PASSWORD:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WRONG_ANSWER:
					DisplayUtil.displayError(fault.getDescription());
					break;
				case E_WRONG_ID_PASSWORD:
					DisplayUtil.displayError(fault.getDescription());
					break;
				default:
					log.error("Unspecified SCS error occurred: ", e);
					DisplayUtil.displayError(fault.getDescription());
				}
				DisplayUtil.displayError(CONTACT_HELP_DESK);
			} else {
				log.error("Unable to get fault info / error code for SharedCromerrException: " + e,e);
				DisplayUtil.displayError("System error occurred: " + e.getMessage());
			}
		} catch (Exception e2) {
			// sometimes, DisplayUtil throws exceptions because the user has not established
			// an ADF context yet
			log.error("SCS error occurred: " + e,e);
		}
	}

}
