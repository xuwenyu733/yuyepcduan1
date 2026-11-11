package cn.iocoder.yudao.framework.mybatis.core.dept;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ParenthesedExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 部门数据权限拦截器 - 处理注解参数传递
 */
public class DeptDataPermissionInterceptor extends JsqlParserSupport implements InnerInterceptor {

    private final BaseDeptService baseDeptService;

    public DeptDataPermissionInterceptor(BaseDeptService baseDeptService) {
        this.baseDeptService = baseDeptService;
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {

        if (InterceptorIgnoreHelper.willIgnoreDataPermission(ms.getId())) {
            return;
        }

        // 检查方法是否有@DeptDataPermission注解
        if (!hasDeptDataPermissionAnnotation(ms)) {
            return;
        }

        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(this.parserSingle(mpBs.sql(), new InterceptorContext(parameter, ms)));
    }

    /**
     * 修复后的参数值提取方法
     */
    private Long extractDeptIdValue(Expression expression, Object parameter, MappedStatement ms) {
        // 情况1：直接数值（如测试SQL）
        if (expression instanceof LongValue) {
            return ((LongValue) expression).getValue();
        }

        // 情况2：JDBC参数占位符（?）
        if (expression instanceof JdbcParameter) {
            return extractDeptIdFromParameter(parameter, ms);
        }

        return null;
    }

    /**
     * 从方法参数中提取deptId值
     */
    private Long extractDeptIdFromParameter(Object parameter, MappedStatement ms) {
        if (parameter == null) {
            return null;
        }

        // 处理Map类型参数（包含@Param注解）
        if (parameter instanceof Map) {
            Map<?, ?> paramMap = (Map<?, ?>) parameter;

            // 查找明确的deptId参数
            for (Object key : paramMap.keySet()) {
                String keyStr = key.toString();
                if ("deptId".equals(keyStr) || "dept_id".equals(keyStr)) {
                    Object value = paramMap.get(key);
                    if (value instanceof Long) {
                        return (Long) value;
                    } else if (value instanceof Integer) {
                        return ((Integer) value).longValue();
                    }
                }
            }

            // 处理单个参数情况

            return null;
        }
        return null;
    }

    /**
     * 检查Mapper方法是否有@DeptDataPermission注解
     */
    private boolean hasDeptDataPermissionAnnotation(MappedStatement ms) {
        try {
            String mapperName = ms.getId().substring(0, ms.getId().lastIndexOf("."));
            String methodName = ms.getId().substring(ms.getId().lastIndexOf(".") + 1);
            Class<?> mapperClass = Class.forName(mapperName);

            for (Method method : mapperClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method.isAnnotationPresent(DeptDataPermission.class);
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        Select selectBody = select.getSelectBody();
        if (selectBody instanceof PlainSelect) {
            this.processWhereCondition((PlainSelect) selectBody, (InterceptorContext) obj);
        } else if (selectBody instanceof SetOperationList) {
            SetOperationList setOperationList = (SetOperationList) selectBody;
            List<Select> selectBodyList = setOperationList.getSelects();
            selectBodyList.forEach(s -> this.processWhereCondition((PlainSelect) s, (InterceptorContext) obj));
        }
    }

    /**
     * 处理WHERE条件，将deptId的eq条件转换为in条件
     */
    private void processWhereCondition(PlainSelect plainSelect, InterceptorContext context) {
        Expression where = plainSelect.getWhere();
        if (where != null) {
            Expression newWhere = transformDeptIdCondition(where, context);
            if (newWhere != where) {
                plainSelect.setWhere(newWhere);
            }
        }
    }

    /**
     * 递归转换部门ID条件
     */
    private Expression transformDeptIdCondition(Expression expression, InterceptorContext context) {
        // 处理EqualsTo条件
        if (expression instanceof EqualsTo) {
            EqualsTo equalsTo = (EqualsTo) expression;
            if (isDeptIdColumn(equalsTo.getLeftExpression())) {
                return createInExpression(equalsTo, context);
            }
        } else if(expression instanceof ParenthesedExpressionList) {
            ParenthesedExpressionList parenthesedExpressionList = (ParenthesedExpressionList) expression;
            List expressions = parenthesedExpressionList.getExpressions();
            List res = new ArrayList();
            for(Object item : expressions) {
                Expression left = transformDeptIdCondition((Expression) item, context);
                if(left != item) {
                    item = left;
                }
                res.add(item);
            }
            return new ParenthesedExpressionList(res);
        }

        // 处理AndExpression
        if (expression instanceof AndExpression) {
            AndExpression andExpr = (AndExpression) expression;

            Expression left = transformDeptIdCondition(andExpr.getLeftExpression(), context);
            Expression right = transformDeptIdCondition(andExpr.getRightExpression(), context);

            if (left != andExpr.getLeftExpression() || right != andExpr.getRightExpression()) {
                return new AndExpression(left, right);
            }
        }

        return expression;
    }

    /**
     * 判断是否为deptId字段
     */
    private boolean isDeptIdColumn(Expression expression) {
        if (expression instanceof Column) {
            Column column = (Column) expression;
            String columnName = column.getColumnName().toLowerCase();
            return "deptid".equals(columnName) || "dept_id".equals(columnName);
        }
        return false;
    }

    /**
     * 创建IN表达式
     */
    private Expression createInExpression(EqualsTo originalEq, InterceptorContext context) {
        InExpression inExpression = new InExpression();
        inExpression.setLeftExpression(originalEq.getLeftExpression());

        // 使用修复后的方法获取deptId值
        Long originalDeptId = extractDeptIdValue(originalEq.getRightExpression(), context.parameter, context.ms);

        if (originalDeptId != null) {
            // 查询子部门ID列表
            List<Long> subDeptIds = baseDeptService.getSubDeptIds(originalDeptId);

            // 构建表达式列表
            ExpressionList expressionList = new ExpressionList();
            expressionList.setExpressions(
                    subDeptIds.stream()
                            .map(LongValue::new)
                            .collect(Collectors.toList())
            );
            inExpression.setRightExpression(expressionList);
            return inExpression;
        }

        return originalEq;
    }

    /**
     * 拦截器上下文
     */
    private static class InterceptorContext {
        private final Object parameter;
        private final MappedStatement ms;

        public InterceptorContext(Object parameter, MappedStatement ms) {
            this.parameter = parameter;
            this.ms = ms;
        }
    }
}