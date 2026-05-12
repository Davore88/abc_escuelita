package com.escuela.abc_Escuelita.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String groupName;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photoData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToMany
    @JoinTable(
        name = "student_tutor",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "tutor_id")
    )
    private List<Tutor> tutors;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Credential> credentials;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<AccessLog> accessLogs;

    // Constructors, Getters, Setters

    public Student() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public byte[] getPhotoData() { return photoData; }
    public void setPhotoData(byte[] photoData) { this.photoData = photoData; }
    public Institution getInstitution() { return institution; }
    public void setInstitution(Institution institution) { this.institution = institution; }
    public List<Tutor> getTutors() { return tutors; }
    public void setTutors(List<Tutor> tutors) { this.tutors = tutors; }
    public List<Credential> getCredentials() { return credentials; }
    public void setCredentials(List<Credential> credentials) { this.credentials = credentials; }
    public List<AccessLog> getAccessLogs() { return accessLogs; }
    public void setAccessLogs(List<AccessLog> accessLogs) { this.accessLogs = accessLogs; }
}
