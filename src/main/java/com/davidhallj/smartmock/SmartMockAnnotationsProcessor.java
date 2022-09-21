package com.davidhallj.smartmock;

import com.davidhallj.smartmock.util.ReflectionHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class SmartMockAnnotationsProcessor {

    public void process(Class<?> clazz, Object testInstance, String methodName) {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            boolean alreadyAssigned = false;
            for(Annotation annotation : field.getAnnotations()) {

                if (annotation.annotationType() == SmartMock.class) {

                    SmartMock smartMockAnnotation = (SmartMock) annotation;

                    Object smartMock = buildMockFromAnnotation(smartMockAnnotation, field, methodName);

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

    public static Object buildMockFromAnnotation(SmartMock smartMock, Field annotatedField, String methodName) {

        final SmartMockFactory smartMockFactory = SmartMockFactory.builder()
                //.exceptionResolver(new WebExceptionResolver()) // More work to make this configurable via annotations
                //.testResourcesDir(DEFAULT_TEST_RESOURCES_DIR) // Not configurable via annotations yet
                .cacheRootDir(smartMock.cacheRootDir())
                .cacheNamingStrategy(smartMock.cacheNamingStrategy())
                .executionStrategy(smartMock.executionStrategy())
                .cacheWriteStrategy(smartMock.cacheWriteStrategy())
                .build();

        return smartMockFactory.createSmartMock(smartMock.url(), annotatedField.getType(), methodName);
    }

    public void throwIfAlreadyAssigned(Field field, boolean alreadyAssigned) {
        if (alreadyAssigned) {
            throw new RuntimeException(String.format("Field %s already assigned to", field.getName()));
        }
    }


}
