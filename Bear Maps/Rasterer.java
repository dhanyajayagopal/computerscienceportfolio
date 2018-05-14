import java.util.HashMap;
import java.util.Map;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    Map<Integer, Double> zoomLonDPP;
    Map<String, Double> rParams;

    public static final double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
    public static final int TILE_SIZE = 256;

    public Rasterer() {
        zoomLonDPP = generateLonDPP();
    }

    public Map<Integer, Double> generateLonDPP() {
        /**
         * Generates the longitudinal distance per pixel in the form of a map
         */
        Map<Integer, Double> zooms = new HashMap<>();
        Double lonDPP = 0.0;
        for (int d = 0; d < 8; d++) {
            lonDPP = ((ROOT_LRLON - ROOT_ULLON) / Math.pow(2, 8 + d));
            zooms.put(d, lonDPP);
        }
        return zooms;
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     * The grid of images must obey the following properties, where image in the
     * grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel
     * (LonDPP) possible, while still covering less than or equal to the amount of
     * longitudinal distance per pixel in the query box for the user viewport size. </li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the
     * above condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     * forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {

        this.rParams = params;
        Map<String, Object> results = new HashMap<>();

        Double userLonDPP = (params.get("lrlon") - params.get("ullon")) / params.get("w");
        Integer zoom = -1;
        for (int d = 0; d < 8; d++) {
            Double lonDPP = zoomLonDPP.get(d);
            if (userLonDPP > lonDPP) {
                zoom = d;
                break;
            }
        }
        if (zoom == -1) {
            zoom = 7;
        }
        Double lonDPP = zoomLonDPP.get(zoom);

        // find upper left x & y
        Double latDPP = ((ROOT_ULLAT - ROOT_LRLAT) / Math.pow(2, zoom)) / TILE_SIZE;
        int ulx = (int) ((params.get("ullon") - ROOT_ULLON) / (lonDPP * 256));
        int uly = (int) ((ROOT_ULLAT - params.get("ullat")) / (latDPP * 256));

        // find lower right x & y
        int lrx = (int) ((params.get("lrlon") - ROOT_ULLON) / (lonDPP * 256));
        if (params.get("lrlon") == ROOT_LRLON) {
            lrx -= 1;
        }
        int lry = (int) ((ROOT_ULLAT - params.get("lrlat")) / (latDPP * 256));

        int xSize = lrx - ulx + 1;
        int ySize = lry - uly + 1;
        String[][] renderGrid = new String[ySize][xSize];
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                renderGrid[j][i] = "d" + zoom + "_x" + (ulx + i) + "_y" + (uly + j) + ".png";
            }
        }
        results.put("render_grid", renderGrid);

        Double rasterULLON = ROOT_ULLON + (ulx * lonDPP * 256);
        results.put("raster_ul_lon", rasterULLON);
        Double rasterULLAT = ROOT_ULLAT - (uly * latDPP * 256);
        results.put("raster_ul_lat", rasterULLAT);
        Double rasterLRLON = ROOT_ULLON + ((lrx + 1) * lonDPP * 256);
        results.put("raster_lr_lon", rasterLRLON);
        if (params.get("lrlon") == ROOT_LRLON) {
            results.put("raster_lr_lon", ROOT_LRLON);
        }
        Double rasterLRLAT = ROOT_ULLAT - ((lry + 1) * latDPP * 256);
        results.put("raster_lr_lat", rasterLRLAT);

        int depth = zoom;
        results.put("depth", depth);
        boolean querySuccess = true;
        results.put("query_success", querySuccess);

        if (!verify()) {
            querySuccess = false;
            results.put("query_success", querySuccess);
        }

        return results;
    }

    public boolean verify() {

        if (rParams.get("lrlat") > rParams.get("ullat")
                || rParams.get("lrlon") < rParams.get("ullon")) {
            return false;
        }

        if (rParams.get("ullon") < ROOT_ULLON || rParams.get("lrlon") > ROOT_LRLON
                || rParams.get("ullat") > ROOT_ULLAT || rParams.get("lrlat") < ROOT_LRLAT) {
            return false;
        }

        return true;
    }
}
