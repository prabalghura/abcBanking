package com.turvo.abcbanking.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.Token;
import com.turvo.abcbanking.utils.CustomQueries;

/**
 * Standard repository class for Token operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface TokenRepository extends BaseRepository<Token, Long>, TokenRepositoryCustom{
	
	/**
	 * For finding max token number in a branch
	 * 
	 * @param branchId
	 * @return max token number
	 */
	@Query(CustomQueries.TOKEN_MAX_NUMBER)
	Integer getMaxCounterNumber(Long branchId);
}
