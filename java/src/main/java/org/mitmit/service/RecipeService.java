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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.mitmit.configuration.SqqmProperties;
import org.mitmit.model.Recipe;
import org.mitmit.model.RecipeType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
    private List<Recipe> initialRecipeList = new ArrayList<>();
    private List<Recipe> workRecipeList = new ArrayList<>();
    private ObjectMapper jsonMapper;
    private ObjectWriter jsonWriter;
    private List<RecipeType> filteredRecipe = new ArrayList<RecipeType>(EnumSet.allOf(RecipeType.class));

    private final SqqmProperties props;

    @Autowired
    public RecipeService(SqqmProperties props) {
        this.props = props;
    }

    //--------------------------------
    // getter & setter properties

    ///////////////////////////////////////////////////////////////////////////////
    //  Constructors
    ///////////////////////////////////////////////////////////////////////////////

    @PostConstruct
    public void init() throws IOException {
        jsonMapper = new ObjectMapper();
        jsonWriter = jsonMapper.writer().withDefaultPrettyPrinter();
        filteredRecipe.removeAll(props.getExcludeRecipeType());
        filteredRecipe.remove(RecipeType.MIDI);
        LOG.debug("exclude-recipe-type : {}", props.getExcludeRecipeType());
        LOG.info("liste des catégories : {}", filteredRecipe.toString());
    }

    ///////////////////////////////////////////////////////////////////////////////
    //  Methods
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // public methods

    /**
     *
     * @return A string containing the previous menus generated
     * @throws IOException for IOFile exceptions
     */
    public StringBuilder getMenu() throws IOException {
        File previousWeek = new File("./menu_semaine.json");
        List<Recipe> overview = readJson(previousWeek);
        return prettyPrint(overview);
    }

    /**
     * Extract Weekly menus from working base and write it into JSON file
     * @throws IOException for IOFile exceptions
     */
    public void generateMenu() throws IOException {
        this.getWork();
        List<Recipe> menu = new ArrayList<>();

        int dinnersCount = props.getDinnerNumber();
        List<RecipeType> mylist = new ArrayList<>(filteredRecipe);
        LOG.debug("Generation de {} diners", props.getDinnerNumber());
        while (dinnersCount != 0) {
            if (mylist.isEmpty()) {
                mylist = new ArrayList<>(filteredRecipe);
            }
            int random = ThreadLocalRandom.current().nextInt(mylist.size());
            RecipeType type = mylist.get(random);
            LOG.debug("Get Type {}", type);
            Recipe plat = getMatchingRecipe(type);
            if (plat != null) menu.add(plat);
            mylist.remove(type);
            dinnersCount --;
        }

        int lunchsCount = props.getLunchNumber();
        LOG.debug("Generation de {} midis", lunchsCount);
        while (lunchsCount != 0) {
            Recipe plat = getMatchingRecipe(RecipeType.MIDI);
            if (plat != null) menu.add(plat);
            lunchsCount --;
        }

        LOG.info("le menu ==> {}", prettyPrint(menu));
        jsonWriter.writeValue(new File("./menu_semaine.json"), menu);

        this.saveWork();
    }

    //--------------------------------
    // protected methods

    //--------------------------------
    // private methods

    /**
     * Load working base
     * @throws IOException for IOFile exceptions
     */
    private void getWork() throws IOException {
        LOG.debug("Load working base");
        File initialFile = new File("./recipes.json");
        initialRecipeList = readJson(initialFile);
        File workFile = new File("./recipesWork.json");
        if (! workFile.exists()) {
            LOG.warn("Init recipes list");
            workFile = initialFile;
        }
        workRecipeList = readJson(workFile);
        if (workRecipeList.isEmpty()) {
            LOG.warn("Reset recipes full list");
            workFile = new File("./recipes.json");
            workRecipeList = readJson(workFile);
        }
        LOG.debug("workRecipeList loaded==> {}", prettyPrint(workRecipeList));
    }

    /**
     * Load list from JSON file
     * @param input the JSON file to load
     * @return List of elements readed from JSON file
     * @throws IOException for IOFile exceptions
     */
    private List<Recipe> readJson (File input) throws IOException {
        InputStream inputStream = new FileInputStream(input);
        TypeReference<List<Recipe>> typeReference = new TypeReference<List<Recipe>>(){};
        return jsonMapper.readValue(inputStream,typeReference);
    }

    /**
     * This method extract a random recipe from working base for given parameter
     * @param recipeType required recipe type
     * @return A random recipe found in this type
     */
    private Recipe getMatchingRecipe(RecipeType recipeType) {
        Recipe result = null;
        //Get all recipes from type recipeType
        List<Recipe> filteredResult = workRecipeList.stream()
                .filter(recipe -> recipeType.equals(recipe.getType()))
                .collect(Collectors.toList());
        if (! filteredResult.isEmpty()) {
            //Get random elt from the filtered list
            result = filteredResult.get(ThreadLocalRandom.current().nextInt(filteredResult.size()));
            LOG.debug("Recipe returned for type {} ==> {}", result.getType(), result.getTitle());
            //Remove the get elt from working list
            workRecipeList.remove(result);
            LOG.debug("workRecipeList after remove ==> {}", workRecipeList);
        } else {
            LOG.warn("Reset Recipe list for this type.");
            List<Recipe> resultToPopulate = initialRecipeList.stream()
                    .filter(recipe -> recipeType.equals(recipe.getType()))
                    .collect(Collectors.toList());
            //Get random elt from the list
            result = resultToPopulate.get(ThreadLocalRandom.current().nextInt(resultToPopulate.size()));
            LOG.debug("Recipe returned for type {} ==> {}", result.getType(), result.getTitle());
            //Remove the get elt from list
            resultToPopulate.remove(result);
            //Update working list to populate this type
            workRecipeList.addAll(resultToPopulate);
            LOG.debug("workRecipeList after populate ==> {}", workRecipeList);
        }
        return result;
    }

    /**
     * Save Working list into JSON file
     * @throws IOException for IOFile exceptions
     */
    private void saveWork() throws IOException {
        List<Recipe> recipeWork = new ArrayList<>(workRecipeList);
        LOG.debug("les recettes restantes ==> {}", prettyPrint(recipeWork));
        jsonWriter.writeValue(new File("./recipesWork.json"), recipeWork);
    }

    /**
     *
     * @param overview the List to print
     * @return formated String
     */
    private StringBuilder prettyPrint(List<Recipe> overview) {
        StringBuilder res = new StringBuilder("\n");
        if (overview.isEmpty()) res.append("Empty List");
        for (Recipe r: overview) {
            res.append("- ");
            res.append(r.getTitle());
            res.append(" (");
            res.append(r.getType());
            res.append(")\n");
        }
        return res;
    }

    //--------------------------------
    // Getters & Setters
}
