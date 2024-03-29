package org.example.model;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "currency")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    private String symbol;

    @Column(nullable = false)
    private String rate;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal rateFloat;

    @Column(nullable = false)
    private String chineseName;

    @Column(nullable = false)
    private LocalDateTime updatedTime = LocalDateTime.now();
}

