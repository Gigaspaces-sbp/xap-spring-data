package org.springframework.data.xap.querydsl.predicate;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.xap.model.Team;
import org.springframework.data.xap.repository.XapRepository;

/**
 * @author Leonid_Poliakov
 */
public interface PredicateTeamRepository extends XapRepository<Team, String>, QueryDslPredicateExecutor<Team> {
}