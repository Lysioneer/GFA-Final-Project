package com.gfa.straysfullstacktribes.utilities;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class DefaultValueReaderUtil {

    private static DefaultValueReaderUtil defaultValueReaderUtil;
    private static final String filename = "defaultValues.yml";
    private static Map<String, Object> values;

    public DefaultValueReaderUtil() throws DefaultValuesFileMissingException {
        load();
    }

    public static DefaultValueReaderUtil getInstance() throws DefaultValuesFileMissingException {
        if (defaultValueReaderUtil == null) {
            defaultValueReaderUtil = new DefaultValueReaderUtil();
        }
        return defaultValueReaderUtil;
    }

    private void load() throws DefaultValuesFileMissingException {
        Yaml yaml = new Yaml();
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(new ClassPathResource(filename).getFile());
        } catch (IOException e) {
            throw new DefaultValuesFileMissingException("file: " + filename + " not found");
        }
        values = yaml.load(inputStream);
    }

    public static int getInt(String chain)
            throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException, DefaultValuesFileMissingException {
        getInstance();
        return (int) traverseMap(values, getKeysChain(chain), Integer.class);
    }

    private static Object traverseMap(Object obj, List<String> keysTree, Class classType)
            throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        if (obj instanceof HashMap) {
            Object subItem = ((HashMap<?, ?>) obj).get(keysTree.get(0));
            //if hashmap doesn't contain the key throw an exception
            if (subItem == null) {
                throw new DefaultValueNotFoundException("there is no such value as: " + keysTree.get(0));
            }
            //calling the function on the next key down the chain
            return traverseMap(subItem, keysTree.subList(1, keysTree.size()), classType);
        } else {
            //if this is not the object type you were looking for throw an exception
            if (obj.getClass() != classType) {
                throw new IncorrectDefaultValueTypeException();
            }
            return obj;
        }
    }

    //convert string[] into List<string> for list functionalities
    private static List<String> getKeysChain(String chain) {
        return Arrays.asList(chain.split("\\."));
    }
}
