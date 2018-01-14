package jp.co.myogadanimotors.myogadani.store.masterdata;

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

public abstract class AbstractMasterDataStore<T extends IStoredObject> extends BaseStore<T> {

    private final Logger logger = LogManager.getLogger(getClass().getName());

    protected abstract T create();

    public void init(IConfigAccessor configAccessor) throws Exception {
        String jsonFileLocation = configAccessor.getString("masterdata." + getClass().getSimpleName().toLowerCase() + ".jsonFileLocation", null);
        if (jsonFileLocation == null) {
            throw new Exception("the json file is not available.");
        }

        ArrayList<Map<String, Object>> root;
        try {
            logger.info("parsing a json file. (jsonFileLocation: {})", jsonFileLocation);
            root = (ArrayList<Map<String, Object>>) (new JsonSlurper().parse(new File(jsonFileLocation)));
        } catch (Exception e) {
            throw new Exception("cannot parse a json file. jsonFileLocation: " + jsonFileLocation, e);
        }

        for (Map<String, Object> map : root) {
            T obj = create();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                PropertyDescriptor pd = new PropertyDescriptor(entry.getKey(), obj.getClass());
                pd.getWriteMethod().invoke(obj, entry.getValue());
            }

            logger.info(obj.toString());

            put(obj);
        }
    }
}
