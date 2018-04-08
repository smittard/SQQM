package org.mitmit.model;

/**
 * @author mittard.
 */
public enum RecipeType {
    PATES_RIZ("PATES_RIZ"),
    SALADE("SALADE"),
    TARTE_QUICHE("TARTE_QUICHE");

    private String typeName;

    RecipeType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "RecipeType{" +
                "typeName='" + typeName + '\'' +
                '}';
    }

}
