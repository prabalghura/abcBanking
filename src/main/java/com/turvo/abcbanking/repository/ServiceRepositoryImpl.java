package com.turvo.abcbanking.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.model.CustomerType;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.utils.CustomQueries;

/**
 * Custom repository implementation for Service operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
@Transactional(readOnly = true)
public class ServiceRepositoryImpl implements ServiceRepositoryCustom {
	
	@PersistenceContext
    EntityManager entityManager;
    @Override
    public List<Service> getServicesForBranch(Long branchId, CustomerType type) {
        Query query = entityManager.createNativeQuery(CustomQueries.SERVICE_FOR_BRANCH);
        query.setParameter(1, branchId);
        query.setParameter(2, type.toString());
        List<Object[]> resultList =  query.getResultList();
        Integer previousServiceId = null;
        Service service = null;
        
        List<Service> services = new ArrayList<>();
        List<ServiceStep> steps = new ArrayList<>();
        
        for(Object[] record: resultList) {
        	Integer serviceId = (Integer) record[0];
        	String serviceName = (String) record[1];
        	Integer stepId = (Integer) record[2];
        	String stepName = (String) record[3];
        	if(!serviceId.equals(previousServiceId)) {
        		if(!Objects.isNull(service)) {
        			service.setSteps(steps);
        			steps = new ArrayList<>();
        			services.add(service);
        		}
        		service = new Service();
        		service.setId(serviceId.longValue());
        		service.setName(serviceName);
        	}
    		ServiceStep step = new ServiceStep();
    		step.setId(stepId.longValue());
    		step.setName(stepName);
    		steps.add(step);
        	previousServiceId = serviceId;
        }
        if(!Objects.isNull(service)) {
			service.setSteps(steps);
			services.add(service);
		}
        return services;
    }
}
