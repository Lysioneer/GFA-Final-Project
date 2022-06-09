package com.gfa.straysfullstacktribes.models;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Kingdom {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(nullable = false)
    private String name;
    @NotNull
    @Column(nullable = false)
    private Long corX;
    @NotNull
    @Column(nullable = false)
    private Long corY;
    @ManyToOne
    private AppUser appUser;

    //Resources
    @NotNull
    @Column(nullable = false)
    private Long goldAmount;
    @NotNull
    @Column(nullable = false)
    private Long foodAmount;
    @NotNull
    @Column(nullable = false)
    private int goldProduction;
    @NotNull
    @Column(nullable = false)
    private int foodProduction;
    @NotNull
    @Column(nullable = false)
    private Long updatedAt;

    //TODO: update annotations for nulls(@Column only for db)
    //TODO: For resources update checking negative values

    public Kingdom(String name,
                   Long corX,
                   Long corY,
                   AppUser appUser,
                   Long goldAmount,
                   Long foodAmount,
                   int goldProduction,
                   int foodProduction) {
        this.name = name;
        this.corX = corX;
        this.corY = corY;
        this.appUser = appUser;
        this.goldAmount = goldAmount;
        this.foodAmount = foodAmount;
        this.goldProduction = goldProduction;
        this.foodProduction = foodProduction;
        this.updatedAt = System.currentTimeMillis()/1000;
    }
    public void setGoldAmount(Long goldAmount) {
        if (goldAmount < 0)
            throw new IllegalArgumentException("GoldAmount cannot be negative.");
        this.goldAmount = goldAmount;
    }

    public void setFoodAmount(Long foodAmount) {
        if (foodAmount < 0)
            throw new IllegalArgumentException("FoodAmount cannot be negative.");
        this.foodAmount = foodAmount;
    }

    public void setGoldProduction(int goldProduction) {
        if (goldProduction < 0)
            throw new IllegalArgumentException("GoldProduction cannot be negative.");
        this.goldProduction = goldProduction;
    }

    public void setFoodProduction(int foodProduction) {
        /*if (foodProduction < 0)
            throw new IllegalArgumentException("FoodProduction cannot be negative.");*/
        this.foodProduction = foodProduction;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCorX(Long corX) {
        this.corX = corX;
    }

    public void setCorY(Long corY) {
        this.corY = corY;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
}