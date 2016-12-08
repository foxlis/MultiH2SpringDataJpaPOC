package hello.persistence.entity.primary;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "primary_test")
public class Product {

    @Id
    private int id;

    private String name;

    private double price;
}

