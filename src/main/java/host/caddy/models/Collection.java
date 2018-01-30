package host.caddy.models;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "collections")
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="coll_id")
    private int id;

    @Column(nullable = false)
    private String longitude;

    @Column(nullable = false)
    private String latitude;

    @ManyToMany(cascade = ALL)
    @JoinTable(
            name="collection_members",
            joinColumns={@JoinColumn(name="coll_id")},
            inverseJoinColumns={@JoinColumn(name="poi_id")}
    )

    @ManyToOne
    @JoinColumn (name = "user_id")
    private User owner;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "collection")
//    private List<Image> images;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "collection")
    private List<PointOfInterest> pointOfInterestList;

    public Collection() {
    }

    public User getUser() {
        return owner;
    }

    public void setUser(User user) {
        this.owner = user;
    }

//    public List<Image> getImages() {
//        return images;
//    }
//
//    public void setImages(List<Image> images) {
//        this.images = images;
//    }

    public List<PointOfInterest> getPointOfInterestList() {
        return pointOfInterestList;
    }

    public void setPointOfInterestList(List<PointOfInterest> pointOfInterestList) {
        this.pointOfInterestList = pointOfInterestList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
