package us.wy.state.deq.impact.database.dao.envite;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.core.Application;

public class EnviteApplication extends Application {
    private Set<Object> singletons = Collections.emptySet();
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    public void setSingletons(final Set<Object> singletons) {
        this.singletons = singletons;
    }
}
