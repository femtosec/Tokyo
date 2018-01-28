environments {
    development {
        myogadani {
            strategyConfig.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/Itabashi/out/production/resources/strategy_config.groovy"
            numberOfStrategyThreads = 1
        }

        timeSource {
            timerEventResolution = 100
        }

        master {
            marketmaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/Myogadani/out/production/resources/market_master.json"
            productmaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/Myogadani/out/production/resources/product_master.json"
            strategymaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/Itabashi/out/production/resources/strategy_master.json"
            extendedattributemaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/Myogadani/out/production/resources/extended_attribute_master.json"
        }
    }
}