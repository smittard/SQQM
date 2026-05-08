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
package org.mitmit.controller;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.mitmit.service.RecipeService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author mittard.
 */
@Service
public class MenuCommandLineRunner implements ApplicationRunner {
    ///////////////////////////////////////////////////////////////////////////////
    //  Properties
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // Static properties
    //private static final Logger MSGLOG = getLogger("application");
    private static final Logger LOG = getLogger(MenuCommandLineRunner.class);

    private final RecipeService recipeService;
    //--------------------------------
    // protected properties

    //--------------------------------
    // getter & setter properties

    ///////////////////////////////////////////////////////////////////////////////
    //  Constructors
    ///////////////////////////////////////////////////////////////////////////////

    @Autowired
    public MenuCommandLineRunner(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    ///////////////////////////////////////////////////////////////////////////////
    //  Methods
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // public methods
    @Override
    public void run(ApplicationArguments args) throws IOException {
        StringBuilder lastGen = recipeService.getMenu();
        LOG.info("Menus de la dernière generation : {}", String.valueOf(lastGen));
        LOG.warn("Générer le menu de la semaine? (y/n)");
        Scanner sc = new Scanner(System.in);
        String input = "n";
        try {
            input = sc.next("^y?|n?$");
        } catch (final InputMismatchException e) {
            LOG.error("y pour yes ou n pour no ! C'est pas compliqué pourtant !");
        }
        if ("y".equals(input)) {
            LOG.debug("Calling algorithm");
            recipeService.generateMenu();
        }
    }

    //--------------------------------
    // protected methods

    //--------------------------------
    // private methods

    //--------------------------------
    // Getters & Setters
}
