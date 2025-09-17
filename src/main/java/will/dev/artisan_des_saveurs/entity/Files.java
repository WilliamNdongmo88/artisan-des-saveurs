package will.dev.artisan_des_saveurs.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Getter
@Setter
@Table(name = "files")
public class Files {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String filePath; // URL publique nginx

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // clé étrangère vers Product
    @JsonBackReference
    private Product product;

    public Files(){}

    public Files(String filePath){
        this.filePath = filePath;
    }
}
