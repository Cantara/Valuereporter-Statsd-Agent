package org.valuereporter.agent.crawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.ImplementedMethod;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by baardl on 28.05.14.
 */
public class PublicMethodFinder {
    private static final Logger log = LoggerFactory.getLogger(PublicMethodFinder.class);

    public static ArrayList<String> getClassNamesFromPackage(String packageName) throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL packageURL;
        ArrayList<String> names = new ArrayList<>();;

        packageName = packageName.replace(".", "/");
        packageURL = classLoader.getResource(packageName);

        if(packageURL.getProtocol().equals("jar")){
            String jarFileName;
            JarFile jf ;
            Enumeration<JarEntry> jarEntries;
            String entryName;

            // build jar file name, then loop through zipped entries
            jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
            jarFileName = jarFileName.substring(5,jarFileName.indexOf("!"));
            log.debug(">"+jarFileName);
            jf = new JarFile(jarFileName);
            jarEntries = jf.entries();
            while(jarEntries.hasMoreElements()){
                entryName = jarEntries.nextElement().getName();
                log.trace("getName from jar {}", entryName);
                if(entryName.startsWith(packageName)) {
                    names.add(entryName.replace("/", "."));
                }
            }

            // loop through files in classpath
        }else{
            //TODO filter at "classes" directory.
            //Do nothing for now.
            log.error("Classpath lookup are currently not supported. Only JAR lookup is supported. \n" +
                    " This is due to abillity to find the starting point. ");
            /*
            URI uri = new URI(packageURL.toString());
            log.debug("uri {}", uri);
            File folder = new File(uri.getPath());
            // won't work with path which contains blank (%20)
            // File folder = new File(packageURL.getFile());
            File[] contenuti = folder.listFiles();
            String entryName;
            for(File actual: contenuti){
                entryName = actual.getName();
                log.debug("getName from classpath {}", entryName);
//                entryName = entryName.substring(0, entryName.lastIndexOf('.'));
                names.add(entryName);
            }
            */
        }
        return names;
    }

    public static List<Class> findClasses(ArrayList<String> names) {

        List<Class> classes = new ArrayList<Class>();
        for (String fileName : names) {

            if (fileName.endsWith(".class")) {
                log.debug("file {}", fileName);
                Class clazz = findClass(fileName);
                if (clazz != null) {
                    classes.add(clazz);
                }

            } else {
                log.trace("File is not a class {}", fileName);
            }

        }
        return classes;
    }

    private static Class findClass(String fileName) {
        Class clazz = null;
        String className = null;
        try {
            className = fileName.substring(0, fileName.length() - 6);
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.warn("No class found for {}", className, e);
        }
        return clazz;
    }

    public static List<ImplementedMethod> findPublicMethods(Class clazz) {
        List<ImplementedMethod> publicMethods = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getModifiers() == Modifier.PUBLIC) {
                log.trace("Public method {} in class {}", method.getName(), clazz.getName());
                publicMethods.add(new ImplementedMethod(method.getName(), method.getDeclaringClass().getName()));
            }
        }
        return publicMethods;
    }
}
