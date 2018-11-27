package us.oh.state.epa.stars2.webcommon.output;

import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.category.CategoryDataset;

class BarChartDynamicObjectURLGenerator implements CategoryURLGenerator, java.io.Serializable {
    private ChartData[] cds;
    private String _url;
    private CategoryURLGenerator _defaultGenerator;

    public BarChartDynamicObjectURLGenerator(ChartData[] cds, String url,
            CategoryURLGenerator defaultGenerator) {
        this.cds = cds;
        _url = url;
        _defaultGenerator = defaultGenerator;
    }

    public final String generateURL(CategoryDataset data, int series, int category) {
        String qparam = null;

        for (int i = 0; i < cds.length; i++) {
            if (cds[i].getLabel().equalsIgnoreCase(Integer.toString(category))) {
                qparam = cds[i].getQparam();
                break;
            }
        }
        if (qparam != null) {
            StringBuffer b = new StringBuffer(_url);
            b.append("?");
            b.append(qparam);
            return b.toString();
        }

        return _defaultGenerator.generateURL(data, series, category);
    }
}
