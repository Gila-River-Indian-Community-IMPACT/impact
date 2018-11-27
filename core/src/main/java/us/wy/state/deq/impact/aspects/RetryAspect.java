package us.wy.state.deq.impact.aspects;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class RetryAspect {

	private Logger logger = Logger.getLogger(RetryAspect.class);

	private int maxRetries;
	
	@DeclareParents(value = "java.lang.Throwable+", 
			defaultImpl = DefaultRetryable.class)
	public static Retryable retryable;

	@Pointcut("execution(public * *(..))")
	public void publicMethod() {
	}

	@Pointcut("!within(us.wy.state.deq.impact.aspects..*)")
	public void notInAspects() {
	}

	@Pointcut("publicMethod() && notInAspects()")
	public void publicMethodNotInAspects() {
	}

	@Around("publicMethodNotInAspects()")
	public Object retry(ProceedingJoinPoint pjp) throws Throwable {
		Object result = null;
		int retries = 0;
		while (true) {
			try {
				result = pjp.proceed();
				break;
			} catch (Throwable t) {
				if (logger.isDebugEnabled()) {
					logger.debug("Caught throwable (" + t.hashCode() + ") " + t);
				}
				if (t instanceof Retryable) {
					if (logger.isDebugEnabled()) {
						logger.debug("instanceof Retryable = true");
					}
					if (((Retryable) t).isRetryable()) {
						if (++retries > getMaxRetries()) {
							if (logger.isDebugEnabled()) {
								logger.debug("isRetryable = true; retries " +
										"exhausted; setting retryable to " +
										"false; re-throwing ... ");
							}
							((Retryable) t).setRetryable(false);
							throw t;
						}
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("isRetryable = false; re-throwing ... ");
						}
						throw t;
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("instanceof Retryable = false; " +
								"re-throwing ... ");
					}
					throw t;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Retrying ... " + retries + "/" + getMaxRetries());
				}
			}
		}
		return result;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}
}
