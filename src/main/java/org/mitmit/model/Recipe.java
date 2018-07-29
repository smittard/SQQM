// Copyright (c) 2000-2016 Viaccess SA.
// Les Collines de l Arche - Tour Opera C, 92057 Paris La Defense, FRANCE
// All rights reserved.
//
// This software is the confidential and proprietary information of
// Viaccess SA. (Confidential Information). You shall not
// disclose such Confidential Information and shall use it only in
// accordance with the terms of the license agreement you entered into
// with Viaccess.
//
package org.mitmit.model;

/**
 * @author mittard.
 */

public class Recipe {
    ///////////////////////////////////////////////////////////////////////////////
    //  Properties
    ///////////////////////////////////////////////////////////////////////////////

    private Long id;
    private String title;
    private RecipeType type;

    ///////////////////////////////////////////////////////////////////////////////
    //  Constructors
    ///////////////////////////////////////////////////////////////////////////////

    public Recipe() {
    }

    ///////////////////////////////////////////////////////////////////////////////
    //  Methods
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // public methods

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(RecipeType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public RecipeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                '}';
    }

    //--------------------------------
    // protected methods

    //--------------------------------
    // private methods

    //--------------------------------
    // Getters & Setters
}
