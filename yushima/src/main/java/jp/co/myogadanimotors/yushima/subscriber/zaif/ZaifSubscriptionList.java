package jp.co.myogadanimotors.yushima.subscriber.zaif;

import jp.co.myogadanimotors.bunkyo.master.AbstractMaster;

public class ZaifSubscriptionList extends AbstractMaster<ZaifSubscription> {
    @Override
    protected ZaifSubscription create() {
        return new ZaifSubscription();
    }
}
