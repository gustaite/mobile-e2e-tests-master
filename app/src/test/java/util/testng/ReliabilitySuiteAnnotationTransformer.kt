package util.testng

import org.testng.IAnnotationTransformer
import org.testng.annotations.ITestAnnotation
import java.lang.reflect.Constructor
import java.lang.reflect.Method

class ReliabilitySuiteAnnotationTransformer : IAnnotationTransformer {
    override fun transform(tAnnotation: ITestAnnotation, tClass: Class<*>?, tConstructor: Constructor<*>?, tMethod: Method?) {
        val invocationsCount = ReliabilitySuite.invocationThreadPoolSize
        tAnnotation.invocationCount = invocationsCount
        tAnnotation.threadPoolSize = invocationsCount
    }
}
