package us.oh.state.epa.stars2.webcommon.output;

import java.awt.Color;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * @author $Author: kbradley $
 * @version $Revision: 1.3 $
 */
@SuppressWarnings("serial")
public class BarChartOutputManager extends ChartOutputManager {
    @Override
    protected boolean createLegend() {
        return false;
    }

    @Override
    protected Plot buildPlot(ChartData[] cds, String vl) {
        String[] categories = new String[cds.length];
        Long[][] data = new Long[1][cds.length];

        for (int i = 0; i < cds.length; i++) {
            data[0][i] = cds[i].getValue().longValue();
            String label = cds[i].getLabel();

            if (label == null) {
                label = " ";
            }
            categories[i] = label;
        }
        // DefaultCategoryDataset dataset = new DefaultCategoryDataset(series,
        // categories, data);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        CategoryAxis categoryAxis = new CategoryAxis("");
        ValueAxis valueAxis = new NumberAxis(vl);
        BarRenderer renderer = new BarRenderer();
        renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());

        if (getClickURL() != null) {
            StandardCategoryURLGenerator stdGen = new StandardCategoryURLGenerator(
                    getClickURL(), "series", "section");
            BarChartDynamicObjectURLGenerator dynGen = new BarChartDynamicObjectURLGenerator(
                    cds, getClickURL(), stdGen);
            renderer.setItemURLGenerator(dynGen);
        }

        Color[] colors = new Color[cds.length];
        for (int i = 0; i < cds.length; i++) {
            colors[i] = cds[i].getColor();
        }
        // renderer.setCategoriesPaint(colors);

        Plot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);

        return plot;
    }
}
