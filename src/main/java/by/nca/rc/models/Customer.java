package by.nca.rc.models;

import by.nca.rc.security.model.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "customer")
public class Customer {

    public Customer(String username, String password, String name, String surname, String parentName,
                    Date creationDate, Date lastEditDate, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.parentName = parentName;
        this.creationDate = creationDate;
        this.lastEditDate = lastEditDate;
        this.roles = roles;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    @NotBlank(message = "Username should not be blank")
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 2, max = 40, message = "Username should be between 2 and 40 characters")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    @NotBlank(message = "Name should not be blank")
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    private String name;

    @Column(name = "surname")
    @NotBlank(message = "Surname should not be blank")
    @NotEmpty(message = "Surname should not be empty")
    @Size(min = 2, max = 20, message = "Surname should be between 2 and 20 characters")
    private String surname;

    @Column(name = "parent_name")
    @NotBlank(message = "Parent should not be blank")
    @NotEmpty(message = "Parent name should not be empty")
    @Size(min = 2, max = 20, message = "Parent name should be between 2 and 20 characters")
    private String parentName;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "last_edit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastEditDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "customer_role",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
