import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.SpatialIndexFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GeoCoder implements Serializable {

    private SpatialIndexFeatureCollection countries;
    private GeometryFactory gf;
    final static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    final static private String countryFile = "/home/arthur/IdeaProjects/TwitterKNN/src/main/resources/natural_earth_2/ne_50m_admin_1_states_provinces_lakes.shp";

    public GeoCoder() throws IOException {
        // load the country shapefile
        URL countryURL = DataUtilities.fileToURL(new File(countryFile));
        HashMap<String, Object> params = new HashMap<>();
        params.put("url", countryURL);
        DataStore ds = DataStoreFinder.getDataStore(params);
        if (ds == null) {
            throw new IOException("couldn't open " + params.get("url"));
        }
        Name name = ds.getNames().get(0);
        this.countries = new SpatialIndexFeatureCollection(ds.getFeatureSource(name).getFeatures());
        this.gf = new GeometryFactory();
    }

    private SimpleFeatureCollection lookup(Point p) {
        Filter f = ff.contains(ff.property("the_geom"), ff.literal(p));
        return countries.subCollection(f);

    }

    public String calcState(double y, double x) {
        Point london = gf.createPoint(new Coordinate(y, x));

        SimpleFeatureCollection features = this.lookup(london);
        SimpleFeatureIterator itr = features.features();
        try {
            while (itr.hasNext()) {
                SimpleFeature f = itr.next();
                //System.out.println("the state " + f.getAttribute(6).toString());
                return f.getAttribute(6).toString();
            }
        } finally {
            itr.close();
        }
        return null;
    }

    // simple test
    /*public static void main(String[] args) throws IOException {
        GeometryFactory gf = new GeometryFactory();

        GeoCoder geocoder = new GeoCoder();

        Point london = gf.createPoint(new Coordinate(-122.4319137, 37.7693453));

        SimpleFeatureCollection features = geocoder.lookup(london);
        SimpleFeatureIterator itr = features.features();
        try {
            while (itr.hasNext()) {
                SimpleFeature f = itr.next();
                System.out.println("the state " + f.getAttribute(6));
            }
        } finally {
            itr.close();
        }
    }*/
}