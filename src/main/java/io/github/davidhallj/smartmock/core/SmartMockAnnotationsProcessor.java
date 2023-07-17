package io.github.davidhallj.smartmock.core;

import io.github.davidhallj.smartmock.annotations.SmartMock;
import io.github.davidhallj.smartmock.config.SmartMockConfiguration;
import io.github.davidhallj.smartmock.exception.SmartMockException;
import io.github.davidhallj.smartmock.util.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class SmartMockAnnotationsProcessor {

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

        final SmartMockConfiguration config = SmartMockConfiguration.create(smartMock, annotatedField, junitTestMethodName);

        final SmartMockFactory.StaticSmartMockFactory smartMockFactory = SmartMockFactoryBuilder.init(config);

        return smartMockFactory.createSmartMock();

    }

    public void throwIfAlreadyAssigned(Field field, boolean alreadyAssigned) {
        if (alreadyAssigned) {
            throw new SmartMockException(String.format("Field %s already assigned to", field.getName()));
        }
    }

}
