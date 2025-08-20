package com.tunaforce.order.repository.querydsl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tunaforce.order.entity.QOrder;
import com.tunaforce.order.repository.querydsl.dto.response.OrderDetailsQuerydslResponseDto;
import com.tunaforce.order.repository.querydsl.dto.response.QOrderDetailsQuerydslResponseDto;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.tunaforce.order.entity.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderQuerydslRepositoryImpl implements OrderQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderDetailsQuerydslResponseDto> findHubOrderPage(Pageable pageable, List<UUID> companyIds) {
        Predicate[] whereClause = {
                order.receiveCompanyId.in(companyIds),
                order.deletedAt.isNull(),
        };

        return getOrderDetailsQuerydslResponseDtos(pageable, whereClause);
    }

    @Override
    public Page<OrderDetailsQuerydslResponseDto> findCompanyOrderPage(Pageable pageable, UUID companyId) {
        Predicate[] whereClause = {
                order.receiveCompanyId.eq(companyId),
                order.deletedAt.isNull(),
        };

        return getOrderDetailsQuerydslResponseDtos(pageable, whereClause);
    }

    private Page<OrderDetailsQuerydslResponseDto> getOrderDetailsQuerydslResponseDtos(
            Pageable pageable,
            Predicate[] whereClause
    ) {
        List<OrderDetailsQuerydslResponseDto> records = queryFactory.select(new QOrderDetailsQuerydslResponseDto(
                        order.id,
                        order.receiveCompanyId,
                        order.productId,
                        order.price,
                        order.quantity,
                        order.requestMemo,
                        order.status,
                        order.createdAt,
                        order.updatedAt
                ))
                .from(order)
                .where(whereClause)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(order.count())
                .where(whereClause)
                .from(order);

        return PageableExecutionUtils.getPage(records, pageable, count::fetchOne);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order sortOrder : sort) {
            Order order = sortOrder.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder<com.tunaforce.order.entity.Order> pathBuilder
                    = new PathBuilder<>(QOrder.order.getType(), QOrder.order.getMetadata());

            orderSpecifiers.add(new OrderSpecifier<>(order, pathBuilder.getString(sortOrder.getProperty())));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}
