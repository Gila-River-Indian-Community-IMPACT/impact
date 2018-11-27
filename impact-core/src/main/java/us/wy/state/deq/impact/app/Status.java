package us.wy.state.deq.impact.app;

public enum Status {
	SUCCESS("Success"),
	FAILURE("Failure"),
	PENDING("Pending"),
	CANCELED("Canceled");
	
    private String value;

    private Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}