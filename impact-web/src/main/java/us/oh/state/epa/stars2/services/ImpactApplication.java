package us.oh.state.epa.stars2.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ImpactApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(ImpactResource.class);
//        classes.add(HelloWorldResource.class);
        return classes;
    }

}