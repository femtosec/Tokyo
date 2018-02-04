environments {
    development {
        kohinata {
            numberOfStrategyThreads = 1
        }

        timeSource {
            timerEventResolution = 100
        }

        master {
            marketmaster.jsonFileName = "market_master.json"
            productmaster.jsonFileName = "product_master.json"
            extendedattributemaster.jsonFileName = "extended_attribute_master.json"
        }
    }
}