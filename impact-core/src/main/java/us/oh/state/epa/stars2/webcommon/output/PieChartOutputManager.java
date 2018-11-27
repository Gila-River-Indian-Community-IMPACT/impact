package us.oh.state.epa.stars2.webcommon.output;

import java.awt.Color;

import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.urls.StandardPieURLGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;

/**
 * @author $Author: kbradley $
 * @version $Revision: 1.3 $
 */
public class PieChartOutputManager extends ChartOutputManager {
    @Override
    protected boolean createLegend() {
        return createLegend;
    }

    @Override
    protected Plot buildPlot(ChartData[] cds, String vl) {
        DefaultPieDataset data = new DefaultPieDataset();

        for (int i = 0; i < cds.length; i++) {
            String label = cds[i].getLabel();

            if (label == null) {
                label = "";
            }

            data.setValue(label, cds[i].getValue());
        }

        // Create the chart object
        PiePlot plot = new PiePlot(data);
        plot.setDirection(Rotation.ANTICLOCKWISE);

        if (getClickURL() != null) {
            StandardPieURLGenerator stdGen = new StandardPieURLGenerator(
                    getClickURL(), "section");
            PieDynamicObjectURLGenerator dynGen = new PieDynamicObjectURLGenerator(
                    cds, getClickURL(), stdGen);
            plot.setURLGenerator(dynGen);
        }
        plot.setToolTipGenerator(new StandardPieToolTipGenerator());

        for (int i = 0; i < cds.length; i++) {
            Color color = cds[i].getColor();

            if (color != null) {
                plot.setSectionPaint(i, color);
            }
        }
        // if (!getShowSectionLabel())
        /*
         * if (false) plot.setSectionLabelType(PiePlot.NO_LABELS); else
         * plot.setSectionLabelType(PiePlot.VALUE_LABELS);
         */
        return plot;
    }
}
