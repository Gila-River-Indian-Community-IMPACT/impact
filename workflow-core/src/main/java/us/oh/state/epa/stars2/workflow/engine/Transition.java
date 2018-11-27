package us.oh.state.epa.stars2.workflow.engine;

import us.oh.state.epa.stars2.database.dbObjects.workflow.TransitionData;

public class Transition {
    public static final String NOLOOP = "NL";
    public static final String LOOP = "LP";
    public static final String NO_OPERATION_VAL = "none";
    public static final int NO_OPERATION = 0;
    public static final int EQUALS = 1;
    public static final int LT = 2;
    public static final int LT_EQUALS = 3;
    public static final int GT = 4;
    public static final int GT_EQUALS = 5;
    public static final int NOT_EQUALS = 6;
    private TransitionData t;
    private String tranNm;
    private String name;
    private int operation;
    private String value;

    public Transition(String tranNm, Integer from, Integer to, String tranCd) {
        t = new TransitionData();

        t.setFromActivityTemplateId(from);
        t.setToActivityTemplateId(to);
        t.setTransitionDefCd(tranCd);
        t.setConditionVal(NO_OPERATION_VAL);

        this.tranNm = tranNm;
        name = null;
        value = null;
        operation = NO_OPERATION;
    }

    public final void setProcessTemplateId(Integer processTemplateId) {
        t.setProcessTemplateId(processTemplateId);
    }

    public final Integer getTo() {
        return t.getToActivityTemplateId();
    }

    public final Integer getFrom() {
        return t.getFromActivityTemplateId();
    }

    public final boolean hasCondition() {
        return (operation != NO_OPERATION);
    }

    public final void setCondition(String condition) {
        int loc;

        t.setConditionVal(condition);

        // 
        // The order of comparison is important - e.g. we need to match
        // != before looking for =
        //
        if ((loc = condition.indexOf("!=")) > 0) {
            name = condition.substring(0, loc);
            value = condition.substring(loc + 2);
            operation = NOT_EQUALS;
        }

        else if ((loc = condition.indexOf("<=")) > 0) {
            name = condition.substring(0, loc);
            value = condition.substring(loc + 2);
            operation = LT_EQUALS;
        }

        else if ((loc = condition.indexOf(">=")) > 0) {
            name = condition.substring(0, loc);
            value = condition.substring(loc + 2);
            operation = GT_EQUALS;
        }

        else if ((loc = condition.indexOf("=")) > 0) {
            name = condition.substring(0, loc);
            value = condition.substring(loc + 1);
            operation = EQUALS;
        }

        else if ((loc = condition.indexOf("<")) > 0) {
            name = condition.substring(0, loc);
            value = condition.substring(loc + 1);
            operation = LT;
        }

        else if ((loc = condition.indexOf(">")) > 0) {
            name = condition.substring(0, loc);
            value = condition.substring(loc + 1);
            operation = GT;
        }

    }

    public final boolean validCondition() {
        boolean ret = true;
        
        if ((operation == NO_OPERATION) || (name == null) || (value == null)) {
            ret = false;
        }

        return ret;
    }

    public final String getTransitionNm() {
        return tranNm;
    }

    public final String getName() {
        return name;
    }

    public final int getOperation() {
        return operation;
    }

    public final String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((name == null) ? 0 : name.hashCode());
        result = PRIME * result + operation;
        result = PRIME * result + ((tranNm == null) ? 0 : tranNm.hashCode());
        result = PRIME * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Transition other = (Transition) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (operation != other.operation)
            return false;
        if (tranNm == null) {
            if (other.tranNm != null)
                return false;
        } else if (!tranNm.equals(other.tranNm))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public final boolean equals(Integer from) {
        return (from.equals(t.getFromActivityTemplateId()));
    }

    public final String toString() {
        String out;

        out = "Name     : " + tranNm + "\n" + "From     : "
                + t.getFromActivityTemplateId() + "\n" + "To       : "
                +t.getToActivityTemplateId() + "\n";

        return out;
    }

    public final String toXml() {
        String out = "<Tranistion Id=\"" + name + " From=\""
                + t.getFromActivityTemplateId() + "\" To=\""
                + t.getToActivityTemplateId() + "\"/>\n";

        return out;
    }
}
