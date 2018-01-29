package jp.co.myogadanimotors.kohinata.master.extendedattriute;

import jp.co.myogadanimotors.kohinata.common.Constants;

public final class ExtendedAttributeDescriptor implements IExtendedAttributeDescriptor {

    private long id = Constants.NOT_SET_ID_LONG;
    private String name;
    private String type;
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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
                + ", type: " + type
                + ", description: " + description;
    }
}
