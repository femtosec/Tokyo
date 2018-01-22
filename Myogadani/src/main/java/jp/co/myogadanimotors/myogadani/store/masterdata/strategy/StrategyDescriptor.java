package jp.co.myogadanimotors.myogadani.store.masterdata.strategy;

import jp.co.myogadanimotors.myogadani.common.Constants;
import jp.co.myogadanimotors.myogadani.store.BaseStore;

public class StrategyDescriptor implements IStrategyDescriptor {

    private long id = Constants.NOT_SET_ID_LONG;
    private String name;
    private String description;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
