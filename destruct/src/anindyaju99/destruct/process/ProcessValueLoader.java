/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author anindya
 */
public class ProcessValueLoader {
    private HashMap<String, Class<?>> map = null;
    private List<ProcessValue> objList = null;
    private static ProcessValueLoader loader = null;
    public static ProcessValueLoader getProcessValueLoader() {
        if (loader == null) {
            loader = new ProcessValueLoader();
        }
        return loader;
    }
    private ProcessValueLoader() {
        map = new HashMap<String, Class<?>>();
        objList = new ArrayList<ProcessValue>();
    }
    public ProcessValue loadProcessValue(String className)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class<?> cls = null;
        cls = map.get(className);
        if (cls == null) {
            cls = Class.forName(className);
            map.put(className, cls);
        }
        ProcessValue pv = (ProcessValue)cls.newInstance();
        objList.add(pv);
        return pv;
    }
}
