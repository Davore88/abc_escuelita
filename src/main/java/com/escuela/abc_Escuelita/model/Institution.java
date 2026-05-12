package com.escuela.abc_Escuelita.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "institutions")
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL)
    private List<AdminUser> admins;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL)
    private List<Student> students;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL)
    private List<AccessLog> accessLogs;

    @OneToMany(mappedBy = "institution", cascade = CascadeType.ALL)
    private List<Announcement> announcements;

    // Constructors, Getters, and Setters

    public Institution() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<AdminUser> getAdmins() { return admins; }
    public void setAdmins(List<AdminUser> admins) { this.admins = admins; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
    public List<AccessLog> getAccessLogs() { return accessLogs; }
    public void setAccessLogs(List<AccessLog> accessLogs) { this.accessLogs = accessLogs; }
    public List<Announcement> getAnnouncements() { return announcements; }
    public void setAnnouncements(List<Announcement> announcements) { this.announcements = announcements; }
}
