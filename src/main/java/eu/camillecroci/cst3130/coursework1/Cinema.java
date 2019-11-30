package eu.camillecroci.cst3130.coursework1;


import javax.persistence.*;

@Entity
@Table(name="cinema")
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="cinema_name")
    private String name;

    @Column(name="address")
    private String address;

    @Column(name="phone")
    private String phone;

    @Column(name="company_name")
    private String companyName;

    @Column(name="cinema_url_name")
    private String cinemaNameUrl;

    public Cinema(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCinemaNameUrl() {
        return cinemaNameUrl;
    }

    public void setCinemaNameUrl(String cinemaNameUrl) {
        this.cinemaNameUrl = cinemaNameUrl;
    }
}
