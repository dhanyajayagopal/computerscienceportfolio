public class Location implements Comparable<Location> {

    Node self;
    double bestD;
    Location parent;
    GraphDB g;
    Node goal;

    public Location(Node s, double d, Location p, GraphDB g, Node goal) {
        self = s;
        bestD = d;
        parent = p;
        this.g = g;
        this.goal = goal;
    }

    @Override
    public int compareTo(Location o) {
        double dist = bestD + g.distance(self.id, goal.id);
        double other = o.bestD + g.distance(o.self.id, goal.id);
        double diff = (dist - other);
        if (diff > 0) {
            return 1;
        } else if (diff == 0) {
            return 0;
        } else {
            return -1;
        }
    }

}
