package jp.co.myogadanimotors.itabashi;

import groovyjarjarcommonscli.*;
import jp.co.myogadanimotors.itabashi.strategy.StrategyFactory;
import jp.co.myogadanimotors.myogadani.Myogadani;
import jp.co.myogadanimotors.myogadani.strategy.IStrategyFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String args[]) {
        // parse command line
        Options opt = new Options()
                .addOption("h", "help", false, "print help for this application")
                .addOption("e", "environment", true, "specify execution environment. default=development");
        CommandLine cl;
        try {
            cl = new BasicParser().parse(opt, args);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        String environment = null;
        if (cl.hasOption("h")) {
            new HelpFormatter().printHelp("usage", opt);
            return;
        } else if (cl.hasOption("e")) {
            environment = cl.getOptionValue("e");
        }

        // create StrategyFactory
        IStrategyFactory strategyFactory = new StrategyFactory();

        // create Myogadani
        Myogadani myogadani = new Myogadani(environment, strategyFactory);

        // run Myogadani
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(myogadani);
        executorService.shutdown();
    }
}
