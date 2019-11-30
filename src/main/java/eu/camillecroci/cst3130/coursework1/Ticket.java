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

    @Column(name="start_minute")
    private int startMin;

    @Column(name="end_hour")
    private int endHour;

    @Column(name="end_minute")
    private int endMin;

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

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public void setStartTime(String time){
        int hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
        int minute = Integer.parseInt(time.substring(time.indexOf(':') + 1));
        setStartHour(hour);
        setStartMin(minute);
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMin() {
        return startMin;
    }

    public String getStartTime(){
        return startHour + ":" + startMin;
    }


    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public void setEndTime(String time){
        int hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
        int minute = Integer.parseInt(time.substring(time.indexOf(':') + 1));
        setEndHour(hour);
        setEndMin(minute);
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMin() {
        return endMin;
    }

    public String getEndTime(){
        return endHour + ":" + endMin;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

}
