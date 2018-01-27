environments {
    development {
        myogadani {
            strategyConfig.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/out/production/Itabashi/strategy_config.groovy"
            numberOfStrategyThreads = 1
        }

        timeSource {
            timerEventResolution = 100
        }

        master {
            marketmaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/out/production/Myogadani/market_master.json"
            productmaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/out/production/Myogadani/product_master.json"
            strategymaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/out/production/Itabashi/strategy_master.json"
            extendedattributemaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/out/production/Myogadani/extended_attribute_master.json"
        }
    }
}