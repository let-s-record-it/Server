package com.sillim.recordit.global.querydsl;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@Getter(AccessLevel.PROTECTED)
public abstract class QuerydslRepositorySupport {

	private final Class<?> entityClass;
	private Querydsl querydsl;
	private EntityManager entityManager;
	private JPAQueryFactory jpaQueryFactory;

	public QuerydslRepositorySupport(Class<?> entityClass) {
		this.entityClass = entityClass;
	}

	@Autowired
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;

		EntityPath<?> path =
				SimpleEntityPathResolver.INSTANCE.createPath(
						JpaEntityInformationSupport.getEntityInformation(entityClass, entityManager)
								.getJavaType());

		this.querydsl =
				new Querydsl(entityManager, new PathBuilder<>(path.getType(), path.getMetadata()));
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}

	protected <T> JPAQuery<T> selectFrom(EntityPath<T> from) {
		return this.jpaQueryFactory.selectFrom(from);
	}

	protected <T> JPAUpdateClause update(EntityPath<T> from) {
		return this.jpaQueryFactory.update(from);
	}

	protected <T> Page<T> applyPagination(
			Pageable pageable,
			Function<JPAQueryFactory, JPAQuery<T>> contentQuery,
			Function<JPAQueryFactory, JPAQuery<Long>> countQuery) {
		JPAQuery<T> jpaContentQuery = contentQuery.apply(jpaQueryFactory);

		List<T> content = this.querydsl.applyPagination(pageable, jpaContentQuery).fetch();
		JPAQuery<Long> jpaCountQuery = countQuery.apply(this.jpaQueryFactory);

		return PageableExecutionUtils.getPage(content, pageable, jpaCountQuery::fetchOne);
	}
}
