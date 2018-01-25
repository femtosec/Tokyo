package jp.co.myogadanimotors.myogadani.store.master;

import groovy.json.JsonException;
import groovy.json.JsonSlurper;
import jp.co.myogadanimotors.myogadani.config.IConfigAccessor;
import jp.co.myogadanimotors.myogadani.store.BaseStore;
import jp.co.myogadanimotors.myogadani.store.IStoredObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public abstract class AbstractDataMaster<T extends IStoredObject> extends BaseStore<T> {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    protected abstract T create();

    public void init(IConfigAccessor configAccessor) throws MasterDataInitializeException {
        String jsonFileLocation = configAccessor.getString("master." + getClass().getSimpleName().toLowerCase() + ".jsonFileLocation", null);
        if (jsonFileLocation == null) {
            throw new MasterDataInitializeException("the json file is not available.");
        }

        logger.info("parsing a json file. (jsonFileLocation: {})", jsonFileLocation);

        Object rawJson;
        try {
            rawJson = new JsonSlurper().parse(new File(jsonFileLocation));
        } catch (JsonException e) {
            throw new MasterDataInitializeException("cannot parse a json file. jsonFileLocation: " + jsonFileLocation);
        }

        if (!(rawJson instanceof ArrayList)) {
            throw new MasterDataInitializeException("cannot cast a json object to the ArrayList. jsonFileLocation: " + jsonFileLocation);
        }
        ArrayList list = (ArrayList) rawJson;

        try {
            for (Object obj : list) {
                if (!(obj instanceof Map)) {
                    throw new MasterDataInitializeException("cannot a json object to Map. jsonFileLocation: " + jsonFileLocation);
                }
                Map map = (Map) obj;

                T masterData = create();

                for (Object key : map.keySet()) {
                    PropertyDescriptor pd = new PropertyDescriptor(key.toString(), masterData.getClass());
                    pd.getWriteMethod().invoke(masterData, map.get(key));
                }

                logger.info(masterData.toString());

                put(masterData);
            }
        } catch (Exception e) {
            throw new MasterDataInitializeException("cannot create the PropertyDescriptor. jsonFileLocation: " + jsonFileLocation, e);
        }
    }
}
