package com.escuela.abc_Escuelita.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tutors")
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photoData;

    @ManyToMany(mappedBy = "tutors")
    private List<Student> students;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<Credential> credentials;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<AccessLog> accessLogs;

    // Constructors, Getters, Setters

    public Tutor() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public byte[] getPhotoData() { return photoData; }
    public void setPhotoData(byte[] photoData) { this.photoData = photoData; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
    public List<Credential> getCredentials() { return credentials; }
    public void setCredentials(List<Credential> credentials) { this.credentials = credentials; }
    public List<AccessLog> getAccessLogs() { return accessLogs; }
    public void setAccessLogs(List<AccessLog> accessLogs) { this.accessLogs = accessLogs; }
}
