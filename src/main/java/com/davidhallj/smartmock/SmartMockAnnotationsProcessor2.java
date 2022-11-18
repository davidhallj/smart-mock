package com.davidhallj.smartmock;

import com.davidhallj.smartmock.config.SmartMockConfiguration;
import com.davidhallj.smartmock.jaxrs.JaxrsFactory;
import com.davidhallj.smartmock.jaxrs.JaxrsFactoryImpl;
import com.davidhallj.smartmock.util.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class SmartMockAnnotationsProcessor2 {

    // Singleton? Where should this live?
    private static JaxrsFactory JAXRS_FACTORY = new JaxrsFactoryImpl();

    /**
     * Crawls the test instance and creates SmartMocks for the members that are annotated as such
     * @param clazz
     * @param testInstance
     * @param junitTestMethodName - name of current junit test being executed
     */
    public void process(Class<?> clazz, Object testInstance, String junitTestMethodName) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            boolean alreadyAssigned = false;

            for(Annotation annotation : field.getAnnotations()) {

                if (annotation.annotationType() == SmartMock.class) {

                    SmartMock smartMockAnnotation = (SmartMock) annotation;

                    Object smartMock = buildMockFromAnnotation(smartMockAnnotation, field, junitTestMethodName);

                    throwIfAlreadyAssigned(field, alreadyAssigned);
                    alreadyAssigned = true;
                    try {
                        ReflectionHelper.setField(testInstance, field, smartMock);
                    } catch (Exception e) {
                        throw new RuntimeException("Problems setting field " + field.getName() + " annotated with " + annotation, e);
                    }
                }

            }
        }
    }

    public static Object buildMockFromAnnotation(SmartMock smartMock, Field annotatedField, String junitTestMethodName) {

        final SmartMockConfiguration config = SmartMockConfiguration.create(smartMock);

        final SmartMockFactory2 smartMockFactory = new SmartMockFactory2(JAXRS_FACTORY, config);

        return smartMockFactory.createSmartMock(smartMock.url(), annotatedField.getType(), junitTestMethodName);
    }

    public void throwIfAlreadyAssigned(Field field, boolean alreadyAssigned) {
        if (alreadyAssigned) {
            throw new RuntimeException(String.format("Field %s already assigned to", field.getName()));
        }
    }


}
