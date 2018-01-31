package host.caddy.models;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "poi_table")
public class PointOfInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="poi_id")
    private int id;

    @Column(name= "place_id",nullable = false)
    private String placeId;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy="pointsOfInterest")
    private List<Collection> collections;


    public PointOfInterest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }
}
