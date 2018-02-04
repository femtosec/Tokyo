environments {
    development {
        yushima {

        }

        master {
            marketmaster.jsonFileName = "market_master.json"
            productmaster.jsonFileName = "product_master.json"
            bitflyersubscriptionlist.jsonFileName = "bitflyer_subscription_list.json"
            zaifsubscriptionlist.jsonFileName = "zaif_subscription_list.json"
        }

        bitflyer {
            subscribeKey = "sub-c-52a9ab50-291b-11e5-baaa-0619f8945a4f"
        }

        zaif {
            apiUrlPrefix = "wss://ws.zaif.jp:8888/stream?currency_pair="
        }
    }
}