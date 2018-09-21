package org.mitmit.configuration;

import java.util.List;

import org.mitmit.model.RecipeType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mittard.
 */

@Component
@ConfigurationProperties(prefix="sqqm")
public class SqqmProperties {

    private List<RecipeType> excludeRecipeType;

    private int dinnerNumber = 6;

    private int lunchNumber = 2;

    //--------------------------------
    // Getters & Setters

    public List<RecipeType> getExcludeRecipeType() {
        return excludeRecipeType;
    }

    public void setExcludeRecipeType(List<RecipeType> excludeRecipeType) {
        this.excludeRecipeType = excludeRecipeType;
    }

    public int getDinnerNumber() {
        return dinnerNumber;
    }

    public void setDinnerNumber(int dinnerNumber) {
        this.dinnerNumber = dinnerNumber;
    }

    public int getLunchNumber() {
        return lunchNumber;
    }

    public void setLunchNumber(int lunchNumber) {
        this.lunchNumber = lunchNumber;
    }
}
