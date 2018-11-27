package us.wy.state.deq.impact;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("impactApp")
public class App implements ApplicationContextAware {

	private static ApplicationContext appContext;

    public void setApplicationContext(ApplicationContext globalAppContext)
        throws BeansException {
        appContext = globalAppContext;
    }

    public static ApplicationContext getApplicationContext() {
        return appContext;
    }
}
