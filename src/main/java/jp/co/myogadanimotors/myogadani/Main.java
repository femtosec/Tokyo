package jp.co.myogadanimotors.myogadani;

import groovyjarjarcommonscli.*;
import jp.co.myogadanimotors.bunkyo.config.ConfigAccessor;
import jp.co.myogadanimotors.bunkyo.master.MasterDataInitializeException;
import jp.co.myogadanimotors.kohinata.Kohinata;
import jp.co.myogadanimotors.kohinata.master.strategy.StrategyMaster;
import jp.co.myogadanimotors.kohinata.strategy.IStrategyFactory;
import jp.co.myogadanimotors.myogadani.strategy.StrategyFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
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

        // start
        Main main = new Main();
        main.start(environment);
    }

    private void start(String environment) {
        // initialize logger
        // todo: to be implemented
        Logger logger = LogManager.getLogger(getClass().getName());

        // initialize config accessor
        ConfigAccessor strategyConfigAccessor = new ConfigAccessor();
        try {
            strategyConfigAccessor.parse(environment, getClass().getClassLoader().getResource("strategy_config.groovy"));
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            return;
        }

        // initialize master data stores
        StrategyMaster strategyMaster = new StrategyMaster();
        try {
            strategyMaster.init(strategyConfigAccessor);
        } catch (MasterDataInitializeException e) {
            logger.error(e.getMessage(), e);
            return;
        }

        // create StrategyFactory
        IStrategyFactory strategyFactory = new StrategyFactory();

        // create Kohinata
        Kohinata kohinata = new Kohinata(environment, strategyMaster, strategyConfigAccessor, strategyFactory);

        // run Kohinata
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(kohinata);
        executorService.shutdown();
    }
}
