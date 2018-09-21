package org.mitmit.model;

/**
 * @author mittard.
 */
public enum RecipeType {
    NONE("NONE"),
    PATES_RIZ("PATES_RIZ"),
    SALADE("SALADE"),
    SOUPE("SOUPE"),
    TARTE_QUICHE_CAKE("TARTE_QUICHE_CAKE"),
    MIDI("MIDI"),
    GRATIN_CROQUE_OEUFS("GRATIN_CROQUE_OEUFS")
    ;

    private String typeName;

    RecipeType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }

}
