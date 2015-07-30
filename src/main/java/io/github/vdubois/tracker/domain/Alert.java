package io.github.vdubois.tracker.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Alert.
 */
@Entity
@Table(name = "ALERT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName="alert")
public class Alert implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "price_lower_than", precision=10, scale=2, nullable = false)
    private BigDecimal priceLowerThan;

    @OneToOne
    private ProductToTrack productToTrack;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPriceLowerThan() {
        return priceLowerThan;
    }

    public void setPriceLowerThan(BigDecimal priceLowerThan) {
        this.priceLowerThan = priceLowerThan;
    }

    public ProductToTrack getProductToTrack() {
        return productToTrack;
    }

    public void setProductToTrack(ProductToTrack productToTrack) {
        this.productToTrack = productToTrack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Alert alert = (Alert) o;

        if ( ! Objects.equals(id, alert.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", priceLowerThan='" + priceLowerThan + "'" +
                '}';
    }
}
