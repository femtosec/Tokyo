package jp.co.myogadanimotors.kohinata.master.strategy;

import jp.co.myogadanimotors.kohinata.common.Constants;

public final class StrategyDescriptor implements IStrategyDescriptor {

    private long id = Constants.NOT_SET_ID_LONG;
    private String name;
    private String description;

    public StrategyDescriptor() {

    }

    public StrategyDescriptor(IStrategyDescriptor strategyDescriptor) {
        id = strategyDescriptor.getId();
        name = strategyDescriptor.getName();
        description = strategyDescriptor.getDescription();
    }

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

    @Override
    public String toString() {
        return "id: " + id
                + ", name: " + name
                + ", description: " + description;
    }
}
