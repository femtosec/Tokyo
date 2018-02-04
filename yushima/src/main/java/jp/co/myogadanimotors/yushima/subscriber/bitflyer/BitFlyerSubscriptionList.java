package jp.co.myogadanimotors.yushima.subscriber.bitflyer;

import jp.co.myogadanimotors.bunkyo.master.AbstractMaster;

public class BitFlyerSubscriptionList extends AbstractMaster<BitFlyerSubscription> {
    @Override
    protected BitFlyerSubscription create() {
        return new BitFlyerSubscription();
    }
}
