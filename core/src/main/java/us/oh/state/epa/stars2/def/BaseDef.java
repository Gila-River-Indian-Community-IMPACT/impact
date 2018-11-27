package us.oh.state.epa.stars2.def;

public interface BaseDef {
    String getCode();

    void setCode(String code);

    String getDescription();

    void setDescription(String description);

    boolean isDeprecated();

    void setDeprecated(boolean deprecated);
}
