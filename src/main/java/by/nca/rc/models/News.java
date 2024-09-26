package by.nca.rc.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "news")
public class News {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @NotBlank(message = "Title should not be blank")
    @NotEmpty(message = "Title should not be empty")
    @Size(min = 2, max = 150, message = "Title should be between 2 and 150 characters")
    private String title;

    @Column(name = "text")
    @NotBlank(message = "Text should not be blank")
    @NotEmpty(message = "Text should not be empty")
    @Size(min = 2, max = 2000, message = "Text should be between 2 and 2000 characters")
    private String text;

    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "last_edit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastEditDate;

    @Column(name = "inserted_by_id")
    private Long insertedById;

    @Column(name = "updated_by_id")
    private Long updatedById;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL)
    private List<Remark> remarks;
}
