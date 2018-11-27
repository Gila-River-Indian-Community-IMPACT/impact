package us.oh.state.epa.stars2.webcommon.output;

import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.general.PieDataset;

class PieDynamicObjectURLGenerator implements PieURLGenerator, java.io.Serializable {
    private ChartData[] cds;
    private String _url;
    private PieURLGenerator _defaultGenerator;

    public PieDynamicObjectURLGenerator(ChartData[] cds, String url,
            PieURLGenerator defaultGenerator) {
        this.cds = cds;
        _url = url;
        _defaultGenerator = defaultGenerator;
    }

    @SuppressWarnings("unchecked")
    public final String generateURL(PieDataset data, Comparable key,
            int category) {
        String qparam = null;

        for (int i = 0; i < cds.length; i++) {
            if (cds[i].getLabel().equalsIgnoreCase(key.toString())) {
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

        return _defaultGenerator.generateURL(data, key, category);
    }
}
