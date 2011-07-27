/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anindyaju99.destruct.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author anindya
 */
public class FilterLoader {
    private HashMap<String, Class<?>> map = null;
    private List<Filter> objList = null;
    private static FilterLoader loader = null;
    public static FilterLoader getFilterLoader() {
        if (loader == null) {
            loader = new FilterLoader();
        }
        return loader;
    }
    private FilterLoader() {
        map = new HashMap<String, Class<?>>();
        objList = new ArrayList<Filter>();
    }
    public Filter loadFilter(String className)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Class<?> cls = null;
        cls = map.get(className);
        if (cls == null) {
            cls = Class.forName(className);
            map.put(className, cls);
        }
        Filter filter = (Filter)cls.newInstance();
        objList.add(filter);
        return filter;
    }

    public void endAll()
        throws Exception
    {
        Iterator<Filter> iter = objList.iterator();
        while (iter.hasNext()) {
            Filter o = iter.next();
            o.end();
        }
    }
}
