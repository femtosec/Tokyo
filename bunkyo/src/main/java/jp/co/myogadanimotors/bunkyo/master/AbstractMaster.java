package jp.co.myogadanimotors.bunkyo.master;

import groovy.json.JsonException;
import groovy.json.JsonSlurper;
import jp.co.myogadanimotors.bunkyo.config.IConfigAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractMaster<T extends IStoredObject> implements IMaster<T> {

    private final Logger logger = LogManager.getLogger(getClass().getName());
    private final Map<Long, T> objectsById = new ConcurrentHashMap<>();

    protected abstract T create();

    public void init(IConfigAccessor configAccessor) throws MasterDataInitializeException {
        String jsonFileName = configAccessor.getString("master." + getClass().getSimpleName().toLowerCase() + ".jsonFileName", null);
        if (jsonFileName == null) {
            throw new MasterDataInitializeException("the json file is not available.");
        }

        logger.info("parsing a json file. (jsonFileName: {})", jsonFileName);

        URL jsonUrl = getClass().getClassLoader().getResource(jsonFileName);
        if (jsonUrl == null) {
            throw new MasterDataInitializeException("the json file is not available.");
        }

        Object rawJson;
        try {
            rawJson = new JsonSlurper().parse(jsonUrl);
        } catch (JsonException e) {
            throw new MasterDataInitializeException("cannot parse a json file. jsonFileName: " + jsonFileName);
        }

        if (!(rawJson instanceof ArrayList)) {
            throw new MasterDataInitializeException("cannot cast a json object to the ArrayList. jsonFileName: " + jsonFileName);
        }
        ArrayList list = (ArrayList) rawJson;

        try {
            for (Object obj : list) {
                if (!(obj instanceof Map)) {
                    throw new MasterDataInitializeException("cannot a json object to Map. jsonFileName: " + jsonFileName);
                }
                Map map = (Map) obj;

                T storedObject = create();

                for (Object key : map.keySet()) {
                    PropertyDescriptor pd = new PropertyDescriptor(key.toString(), storedObject.getClass());
                    pd.getWriteMethod().invoke(storedObject, map.get(key));
                }

                logger.info(storedObject.toString());

                objectsById.put(storedObject.getId(), storedObject);
            }
        } catch (Exception e) {
            throw new MasterDataInitializeException("cannot create the PropertyDescriptor. jsonFileName: " + jsonFileName, e);
        }
    }

    @Override
    public T get(long id) {
        return objectsById.get(id);
    }

    protected T get(Predicate<T> predicate) {
        for (Map.Entry<Long, T> entry : objectsById.entrySet()) {
            T object = entry.getValue();
            if (predicate.test(object)) return object;
        }
        return null;
    }

    @Override
    public void forEach(Consumer<T> visitor) {
        for (T obj: objectsById.values()) {
            visitor.accept(obj);
        }
    }
}
