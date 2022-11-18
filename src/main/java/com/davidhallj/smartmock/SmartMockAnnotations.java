package com.davidhallj.smartmock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SmartMockAnnotations {

    public static void init(Object testClass) {
       init(testClass, null);
    }

    public static void init(Object testClass, String methodName) {

        if (testClass == null) {
            // TODO use a better exception here
            throw new RuntimeException("testClass cannot be null");
        } else {
            //final SmartMockAnnotationsProcessor smartMockAnnotationsProcessor = new SmartMockAnnotationsProcessor();
            final SmartMockAnnotationsProcessor2 smartMockAnnotationsProcessor = new SmartMockAnnotationsProcessor2();
            smartMockAnnotationsProcessor.process(testClass.getClass(), testClass, methodName);
        }
    }

}
