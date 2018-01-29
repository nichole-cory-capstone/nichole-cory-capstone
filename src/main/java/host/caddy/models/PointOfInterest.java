package host.caddy.models;

import javax.persistence.*;

@Entity
@Table(name = "poi_table")
public class PointOfInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="poi_id")
    private int id;

    @Column(name = "yelp_id")
    private String yelpId;

    @Column(name= "place_id",nullable = false)
    private String placeId;

    @ManyToOne
    @JoinColumn (name = "coll_id")
    private Collection collection;

    public PointOfInterest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYelpId() {
        return yelpId;
    }

    public void setYelpId(String yelpId) {
        this.yelpId = yelpId;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }


}
