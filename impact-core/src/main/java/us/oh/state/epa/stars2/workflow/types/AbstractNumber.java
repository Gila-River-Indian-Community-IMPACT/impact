/*
 * Created on Jul 26, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package us.oh.state.epa.stars2.workflow.types;

import java.text.DecimalFormat;

import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetail;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @author Sam Wooster
 */
abstract public class AbstractNumber extends AbstractType {
    protected String min;
    protected String max;
    protected String mask;
    protected DecimalFormat formatter;

    public void init(DataDetail dd) {
        min = dd.getMinVal();
        max = dd.getMaxVal();
        mask = dd.getFormatMask();

        if (mask != null) {
            formatter = new DecimalFormat(mask);
        } else {
            formatter = new DecimalFormat();
        }

        valid(true);
    }
}
