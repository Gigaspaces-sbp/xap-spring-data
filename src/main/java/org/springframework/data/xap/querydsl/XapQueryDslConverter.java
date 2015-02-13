package org.springframework.data.xap.querydsl;

import com.gigaspaces.query.ISpaceQuery;
import com.google.common.collect.ImmutableList;
import com.j_spaces.core.client.SQLQuery;
import com.mysema.query.support.SerializerBase;
import com.mysema.query.types.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Leonid_Poliakov
 */
public class XapQueryDslConverter<T> extends SerializerBase<XapQueryDslConverter<T>> implements Visitor<Void, Void> {
    private static final Templates TEMPLATES = XapQueryDslTemplates.DEFAULT;
    private static final String LISTS_SEPARATOR = ", ";
    private static final String PAGING_ROW_NUM = " rownum <= ";
    private static final String ORDER_BY = " order by ";
    private static final String ORDER_ASC = " asc";
    private static final String ORDER_DESC = " desc";

    private Class<T> type;
    private List<Object> parameters;

    public XapQueryDslConverter(Class<T> type) {
        super(TEMPLATES);
        this.type = type;
        this.parameters = new LinkedList<>();
    }

    @Nullable
    @Override
    public Void visit(FactoryExpression<?> factoryExpression, @Nullable Void context) {
        super.visit(factoryExpression, context);
        return null;
    }

    @Nullable
    @Override
    public Void visit(Operation<?> operation, @Nullable Void context) {
        super.visit(operation, context);
        return null;
    }

    @Nullable
    @Override
    public Void visit(ParamExpression<?> paramExpression, @Nullable Void context) {
        super.visit(paramExpression, context);
        return null;
    }

    @Nullable
    @Override
    public Void visit(Path<?> path, @Nullable Void context) {
        final PathType pathType = path.getMetadata().getPathType();
        final Template template = TEMPLATES.getTemplate(pathType);

        if (template != null && path.getType() == String.class) {
            String fullPath = path.toString();
            String root = path.getRoot().toString();
            handleTemplate(template, ImmutableList.of(root, fullPath.substring(root.length() + 1)));
        } else {
            super.visit(path, context);
        }

        return null;
    }

    @Nullable
    @Override
    public Void visit(SubQueryExpression<?> subQueryExpression, @Nullable Void context) {
        return null;
    }

    @Nullable
    @Override
    public Void visit(TemplateExpression<?> templateExpression, @Nullable Void context) {
        super.visit(templateExpression, context);
        return null;
    }

    @Override
    public void visitConstant(Object constant) {
        if (constant instanceof Collection) {
            Collection collection = (Collection) constant;
            String prefix = "";
            for (Object element : collection) {
                append(prefix);
                visitConstant(element);
                prefix = LISTS_SEPARATOR;
            }
        } else {
            append("?");
            parameters.add(constant);
        }
    }

    public ISpaceQuery<T> convert(Predicate predicate, Pageable pageable, OrderSpecifier<?>... orders) {
        // append where clause
        if (predicate != null) {
            handle(predicate);
        }

        if (pageable != null) {
            // append row num and order clause
            append(PAGING_ROW_NUM);
            append(String.valueOf(pageable.getOffset() + pageable.getPageSize()));
            append(ORDER_BY);
            String prefix = "";
            for (Sort.Order order : pageable.getSort()) {
                append(prefix);
                prefix = LISTS_SEPARATOR;

                append(order.getProperty());
                append(order.getDirection() == Sort.Direction.ASC ? ORDER_ASC : ORDER_DESC);
            }
        } else if (orders != null && orders.length > 0) {
            // append order clause
            append(ORDER_BY);
            String prefix = "";
            for (final OrderSpecifier<?> order : orders) {
                append(prefix);
                prefix = LISTS_SEPARATOR;

                handle(order.getTarget());
                append(order.getOrder() == Order.ASC ? ORDER_ASC : ORDER_DESC);
            }
        }

        SQLQuery<T> query = new SQLQuery<>(type, toString());
        query.setParameters(parameters.toArray());
        return query;
    }
}