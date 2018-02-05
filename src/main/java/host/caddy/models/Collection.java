package host.caddy.models;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "collections")
public class Collection implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="coll_id")
    private Long id;

    @Column(nullable = false)
    private String longitude;

    @Column(nullable = false)
    private String latitude;

    @ManyToOne
    @JoinColumn (name = "user_id")
    private User owner;

    public User getUser() {
        return owner;
    }


    @Column
    private String imageRef;

    @Column(nullable = false)
    private String location;

    @ManyToMany
    @JoinTable(
            name="collection_members",
            joinColumns={@JoinColumn(name="coll_id")},
            inverseJoinColumns={@JoinColumn(name="poi_id")}
    )
    private List<PointOfInterest> pointsOfInterest = new ArrayList<>();

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    private String placeId;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Collection(String longitude, String latitude, User owner, String imageRef, String location, List<PointOfInterest> pointsOfInterest, String placeId) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.owner = owner;
        this.imageRef = imageRef;
        this.location = location;
        this.pointsOfInterest = pointsOfInterest;
        this.placeId = placeId;
    }

    public Collection() {
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

    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLongitude() { return longitude; }

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
