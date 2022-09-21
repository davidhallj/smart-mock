//package com.davidhallj.smartmock.junit;
//
//import com.davidhallj.smartmock.SmartMockAnnotations;
//
//public class SmartMockJunitRunner extends BlockJUnit4ClassRunner {
//
//    public SmartMockJunitRunner(Class<?> aClass) throws InitializationError {
//        super(aClass);
//    }
//
//    @Override
//    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
//        SmartMockAnnotations.init(target, method.getName());
//        return super.withBefores(method, target, statement);
//    }
//
//}
//
