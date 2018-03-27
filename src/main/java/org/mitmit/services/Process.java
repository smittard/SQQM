package org.mitmit.services;

import org.mitmit.business.RecipeWorker;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.Scanner;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author mittard.
 */
@Component
public class Process implements CommandLineRunner {
    ///////////////////////////////////////////////////////////////////////////////
    //  Properties
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // Static properties
    private static final Logger LOG = getLogger(Process.class);

    //--------------------------------
    // protected properties

    //--------------------------------
    // getter & setter properties

    ///////////////////////////////////////////////////////////////////////////////
    //  Constructors
    ///////////////////////////////////////////////////////////////////////////////

    public Process() {
        super();
    }

    ///////////////////////////////////////////////////////////////////////////////
    //  Methods
    ///////////////////////////////////////////////////////////////////////////////

    //--------------------------------
    // public methods
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Sarah Qu'est ce Qu'on Mange ?");
        System.out.println("Générer le menu de la semaine? (y/n)");
        Scanner sc = new Scanner(System.in);
        String input = "n";
        try {
            input = sc.next("^y?|n?$");
        } catch (final InputMismatchException e) {
            LOG.error("y pour yes ou n pour no ! C'est pas compliqué pourtant !");
        }
        if ("y".equals(input)) {
            System.out.println("C'est parti !!!");
            this.getWorker();
        }
    }


    //--------------------------------
    // protected methods

    //--------------------------------
    // private methods
    private Runnable getWorker() {
        return new RecipeWorker();
    }

    //--------------------------------
    // Getters & Setters
}
