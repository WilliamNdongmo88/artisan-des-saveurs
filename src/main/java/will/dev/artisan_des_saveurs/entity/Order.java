package will.dev.artisan_des_saveurs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double subtotal;
    private double discount;
    private double total;
    private boolean freeShipping;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ToString.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductItem> items;
}

