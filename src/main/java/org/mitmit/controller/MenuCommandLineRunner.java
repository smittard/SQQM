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

import org.mitmit.service.RecipeService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.InputMismatchException;
import java.util.Scanner;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author mittard.
 */
@Service
public class MenuCommandLineRunner implements CommandLineRunner {
    ///////////////////////////////////////////////////////////////////////////////
    //  Properties
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // Static properties
    //private static final Logger MSGLOG = getLogger("application");
    private static final Logger LOG = getLogger(MenuCommandLineRunner.class);

    @Autowired
    private RecipeService recipeService;

    //--------------------------------
    // protected properties

    //--------------------------------
    // getter & setter properties

    ///////////////////////////////////////////////////////////////////////////////
    //  Constructors
    ///////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////
    //  Methods
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // public methods
    @Override
    public void run(String... args){
        LOG.warn("Sarah Qu'est ce Qu'on Mange ?");
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
