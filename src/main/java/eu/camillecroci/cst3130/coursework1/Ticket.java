package eu.camillecroci.cst3130.coursework1;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name="ticket_type")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="description")
    private String description;

    @Column(name="price")
    private double price;

    @Column(name="day")
    private String day; //change to ENUM

    @ManyToOne(targetEntity=Cinema.class)
    @JoinColumn(name="cinema_id",referencedColumnName="id")
    private Cinema cinema;

    @Column(name="start_hour")
    private int startHour;

    @Column(name="end_hour")
    private int endHour;


    public Ticket(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

}
