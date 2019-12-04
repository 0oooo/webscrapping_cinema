package eu.camillecroci.cst3130.coursework1;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="screening")
public class Screening {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="screening_datetime")
    private Date screeningDate;

    @Column(name="url")
    private String url;

    @Column(name="details")
    private String details;

    @ManyToOne(targetEntity=Movie.class)
    @JoinColumn(name="movie_id", referencedColumnName="id")
    private Movie movie;

    @ManyToOne(targetEntity=Cinema.class)
    @JoinColumn(name="cinema_id",referencedColumnName="id")
    private Cinema cinema;

    public Screening(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getScreeningDate() {
        return screeningDate;
    }

    public void setScreeningDate(Date screeningDate) {
        this.screeningDate = screeningDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }
}
