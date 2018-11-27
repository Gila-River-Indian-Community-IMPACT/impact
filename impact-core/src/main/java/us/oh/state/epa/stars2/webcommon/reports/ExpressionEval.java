package us.oh.state.epa.stars2.webcommon.reports;

import java.util.ArrayList;

import com.bestcode.mathparser.IMathParser;
import com.bestcode.mathparser.MathParserFactory;

public class ExpressionEval implements java.io.Serializable {
    static private String mark = "__"; // Two underscores
    static private boolean translate = true; // should lower case be preserved.
    private IMathParser p;
    private String formula = null;
    private String err = null;
    
    ExpressionEval(IMathParser p) {
        this.p = p;
    }
    
    public static ExpressionEval createFromFactory() {
        return new ExpressionEval(MathParserFactory.create());
    }
    
    public void setExpression(String exp) {
        formula = exp;
        p.setExpression(lowerToUpper(exp));
    }
    
    static public String lowerToUpper(String exp) {
        /*
         * Each lower case character is replaced with String mark placed after
         * it and the character is then replaced with its upper case
         */
        String ret = exp;

        if (translate) {
            StringBuffer expB = new StringBuffer(exp);
            int ndx = 0;
            while (ndx < expB.length()) {
                char c = expB.charAt(ndx);
                if (Character.isLowerCase(c) && c != Character.toUpperCase(c)) {
                    expB.insert(ndx + 1, mark);
                    expB.setCharAt(ndx, Character.toUpperCase(c));
                    ndx = ndx + 2;
                }
                ndx++;
            }
            ret = expB.toString();
        }
        return ret;
    }
    
    static public String upperToLower(String exp) {
        String ret = exp;

        if (translate) {
            StringBuffer expB = new StringBuffer(exp);
            int ndx = 1; // mark can not start the string
            while (ndx < expB.length()) {
                int i = expB.indexOf(mark, ndx);
                if (i < 0)
                    break;
                // Delete mark
                expB.delete(i, i + 2);
                // Change character to lower
                expB
                        .setCharAt(i - 1, Character.toLowerCase(expB
                                .charAt(i - 1)));
                ndx = i;
            }
            ret = expB.toString();
        }

        return ret;
    }
    
    public void setVariable(String s, double v) {
        err = null;
        try {
        p.setVariable(lowerToUpper(s), v);
        } catch(Exception e) {
            err = "Variable " + s + " not located in formula \"" + formula + "\"";
        }
    }
    
    public double getValue() {
        err = null;
        double v = 0d;
        try {
        v = p.getValue();
        } catch(Exception e) {
            err = "Failed to evaluate formula \"" + formula + "\"";
        }
        if(err == null && v <= 0) {
            err = "Formula evaluated to a negative or zero factor value.  Value of the variable(s) out of expected range(s) or defective formula.";
        }
        if(err == null && (v >= Double.POSITIVE_INFINITY || v <= Double.NEGATIVE_INFINITY)) {
            err = "Formula evaluated to positive or negataive infinity.  Value of the variable(s) out of expected range(s) or defective formula.";
        }
        return v;
    }
    
    public String[] getVariables() {
        err = null;
        int count = 0;
        boolean foundAnother = true;
        while(count < 50 && foundAnother) {
            count++;
            foundAnother = false;
            try {
                p.parse();
            } catch (Exception e) {
                foundAnother = true;
                String errMsg = e.getMessage();
                int i1 = errMsg.indexOf('<');
                int i2 = errMsg.indexOf('>');
                if(i1 < 0 || i2 < 0 || i2 < i1) {
                    err = "Unable to parse formula \"" + formula + "\", " +
                    upperToLower(errMsg);
                } else {
                    StringBuffer  v = new StringBuffer(errMsg.substring(i1+1, i2).trim());
                    boolean more = true;
                    while(more && v.length() > 0) {
                        if(v.charAt(0) == ' ' || v.charAt(0) == '(' ||
                                v.charAt(0) == '<' || v.charAt(0) == '{' ||
                                v.charAt(0) == '[') {
                            v.deleteCharAt(0);
                        } else more = false;
                    }
                    more = true;
                    while(more && v.length() > 0) {
                        int l = v.length() -1;
                        if(v.charAt(l) == ' ' || v.charAt(l) == ')' ||
                                v.charAt(l) == '>' || v.charAt(l) == '}' ||
                                v.charAt(l) == ']') {
                            v.deleteCharAt(l);
                        } else more = false;
                    }
                    try {
                        p.setVariable(v.toString(), 0d);
                    } catch (Exception e2) {
                        err = "Exception doing setVariable of " +
                                upperToLower(v.toString()) + " into formula \"" +
                                formula + "\", " +  upperToLower(e2.getMessage());
                    }
                }
            }
        }

        ArrayList<String> usedVars = new ArrayList<String>();
        if(err == null) {
            String[] vars = null;
            vars = p.getVariables();
            // Put values in variables to test formula

            for(int i=0; i < vars.length; i++) {
                try {
                if(!p.isVariableUsed(vars[i])) continue;
                usedVars.add(vars[i]);
                } catch(Exception e)  {
                    err = "Exception examining variable \"" +
                            upperToLower(vars[i].toString()) + " in formula \"" +
                            formula + "\"";
                }
            }
        }
        return usedVars.toArray(new String[0]);
    }

    public String getErr() {
        return err;
    }
}
