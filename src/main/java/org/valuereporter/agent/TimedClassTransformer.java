package org.valuereporter.agent;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class TimedClassTransformer implements ClassFileTransformer {
    private final static Logger log = LoggerFactory.getLogger(TimedClassTransformer.class);
    private ClassPool classPool;
    private final String basePackage;

    public TimedClassTransformer(final String basePackage) {
        log.info("Will scan package {}", basePackage);
        this.basePackage = basePackage;
        classPool = new ClassPool();
        classPool.appendSystemPath();
        try {
            classPool.appendPathList(System.getProperty("java.class.path"));

            // make sure that MonitorReporter is loaded
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] transform(ClassLoader loader, String fullyQualifiedClassName, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classBytes) throws IllegalClassFormatException {
        String className = fullyQualifiedClassName.replace("/", ".");

        //class inspection can in some cases lead to ClassCircularityError, so return early if class is not meant to be instrumented
        // similar issue fixed here: https://github.com/ActiveJpa/activejpa/issues/26
        if (!isToBeObserved(className)) return null;

        classPool.appendClassPath(new ByteArrayClassPath(className, classBytes));

        try {
            CtClass ctClass = classPool.get(className);
            if (ctClass.isFrozen()) {
               // log.debug("Skip class {}: is frozen", className);
                return null;
            }

            if (ctClass.isPrimitive() || ctClass.isArray() || ctClass.isAnnotation()
                    || ctClass.isEnum() || ctClass.isInterface()) {
                //log.debug("Skip class {}: not a class", className);
                return null;
            }

            boolean isClassModified = false;
            for (CtMethod method : ctClass.getDeclaredMethods()) {
                if (method.getModifiers() == Modifier.PUBLIC) {
                    if (method.getMethodInfo().getCodeAttribute() == null) {
                        //log.debug("Skip method " + method.getLongName());
                        continue;
                    }
                    //log.debug("Instrumenting method {}", method.getLongName());
                    method.addLocalVariable("__metricStartTime", CtClass.longType);
                    method.insertBefore("__metricStartTime = System.currentTimeMillis();");
                    String metricName = ctClass.getName() + "." + method.getName();
                    method.insertAfter("org.valuereporter.client.MonitorReporter.reportTime(\"" + metricName + "\", __metricStartTime, System.currentTimeMillis());");
                    isClassModified = true;
                }
            }
            if (!isClassModified) {
                return null;
            }
            return ctClass.toBytecode();
        } catch (Exception e) {
            log.warn("Skipipping class due to error. class {}, message {} ", className, e.getMessage(), e);
            return null;
        }
    }

    private boolean isToBeObserved(String packageName) {
        boolean toBeObserved = (packageName.startsWith(basePackage) && !packageName.startsWith("org.valuereporter"));
        return toBeObserved;
    }
}
