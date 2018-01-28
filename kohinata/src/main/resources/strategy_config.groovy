environments {
    development {
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