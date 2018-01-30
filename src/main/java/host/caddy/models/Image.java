package host.caddy.models;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="img_id")
    private int id;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn (name = "user_id")
    private User owner;

    public Image() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }

//    public Collection getCollection() {
//        return collection;
//    }
//
//    public void setCollection(Collection collection) {
//        this.collection = collection;
//    }
}
