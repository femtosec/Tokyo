environments {
    development {
        master.strategymaster.jsonFileName = "strategy_master.json"

        strategy {
            '*' {
                defaultValueTest = 100

                maxNumberOfPendingCancelProcessing = 7
            }

            Peg {
                maxNumberOfPendingAmendProcessing = 5
            }

            Arbitrage {

            }
        }
    }
}