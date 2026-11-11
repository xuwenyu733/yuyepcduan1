package cn.iocoder.yudao.framework.common.util.obj;


import cn.hutool.core.lang.func.Func1;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class EasyKeyUtil {

    private EasyKeyUtil() {}

    public static <T, R> String getPropertyName(Func1<T, R> getter) {
        try {
            Method writeReplace = getter.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(getter);

            String methodName = lambda.getImplMethodName();
            String prefix = null;

            if (methodName.startsWith("get")) {
                prefix = "get";
            } else if (methodName.startsWith("is")) {
                prefix = "is";
            }

            if (prefix == null) {
                throw new RuntimeException("无效的getter方法: " + methodName);
            }

            String fieldName = methodName.replace(prefix, "");
            return toLowerCaseFirstOne(fieldName);

        } catch (Exception e) {
            throw new RuntimeException("获取属性名失败", e);
        }
    }

    private static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
    }

    public static void main(String[] args) {
        // 测试示例
        class User {
            private String name;
            private int age;
            private boolean active;

            public String getName() { return name; }
            public int getAge() { return age; }
            public boolean isActive() { return active; }
        }

        System.out.println(getPropertyName(User::getName));    // 输出: name
        System.out.println(getPropertyName(User::getAge));     // 输出: age
        System.out.println(getPropertyName(User::isActive));  // 输出: active
    }
}

