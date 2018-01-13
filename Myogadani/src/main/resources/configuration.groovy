environments {
    development {
        myogadani {
            extendedAttributesFileName = "extended_attributes.json"
            numberOfStrategyThreads = 2
        }

        masterdata {
            marketmaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/out/production/Myogadani/market_master.json"
            productmaster.jsonFileLocation = "/C:/Users/alpha/IdeaProjects/Narashino/out/production/Myogadani/product_master.json"
        }

        strategy {
            '*' {
                test = true
            }

            peg {

            }
        }
    }
}