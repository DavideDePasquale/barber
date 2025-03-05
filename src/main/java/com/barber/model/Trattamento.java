package com.barber.model;

import com.barber.enumeration.ETrattamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trattamenti")
public class Trattamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ETrattamento tipotrattamento;
    private int durataMinuti;
    @Column(nullable = false)
    private double prezzo;


    @PrePersist
    @PreUpdate
    private void calcolaDurataDaEnum(){
        if (tipotrattamento != null){
            this.durataMinuti = tipotrattamento.getDurataMinuti();
        }
    }

}
