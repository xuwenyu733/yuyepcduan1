package cn.iocoder.yudao.framework.mybatis.core.dept;


import java.lang.annotation.*;

/**
 * 部门数据权限注解
 * 用于标记需要进行部门数据权限控制的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeptDataPermission {

    /**
     * 部门ID字段名，默认为dept_id
     */
    String value() default "dept_id";

    /**
     * 是否包含子部门，默认为true
     */
    boolean includeChildren() default true;

    /**
     * 权限字符(用于多个角色匹配符合要求的权限)多个权限用逗号分隔开来
     */
    String permission() default "";

    /**
     * 自定义权限方法
     */
    String customType() default "";
}
