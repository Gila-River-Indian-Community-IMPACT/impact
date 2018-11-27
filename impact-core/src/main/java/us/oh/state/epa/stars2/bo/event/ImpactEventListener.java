package us.oh.state.epa.stars2.bo.event;

public interface ImpactEventListener<T extends ImpactEvent> {

	void eventOccurred(T event);

}
