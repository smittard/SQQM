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
package org.mitmit.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mitmit.model.Recipe;
import org.mitmit.model.RecipeType;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author mittard.
 */
@Service
public class RecipeService {
    ///////////////////////////////////////////////////////////////////////////////
    //  Properties
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // Static properties
    //private static final Logger MSGLOG = getLogger("application");

    private static final Logger LOG = getLogger(RecipeService.class);

    //--------------------------------
    // protected properties
    List<Recipe> recipeList = new ArrayList<>();

    //--------------------------------
    // getter & setter properties

    ///////////////////////////////////////////////////////////////////////////////
    //  Constructors
    ///////////////////////////////////////////////////////////////////////////////

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        TypeReference<List<Recipe>> typeReference = new TypeReference<List<Recipe>>(){};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/recipes.json");
        recipeList = jsonMapper.readValue(inputStream,typeReference);
    }

    ///////////////////////////////////////////////////////////////////////////////
    //  Methods
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // public methods

    public void generateMenu() {
        LOG.info("Recipes :");
        for (Recipe recipe : recipeList){
            LOG.info(recipe.toString());
        }
    }

    //--------------------------------
    // protected methods

    //--------------------------------
    // private methods

    //--------------------------------
    // Getters & Setters
}
