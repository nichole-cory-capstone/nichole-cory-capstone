package host.caddy.models.yelp;

import java.util.List;

public class Yelp {
    private Data data;
    private Search search;

    public Yelp() {

    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }
}


