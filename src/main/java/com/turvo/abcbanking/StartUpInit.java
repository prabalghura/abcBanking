package com.turvo.abcbanking;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.turvo.abcbanking.service.BranchService;

/**
 * Component to build up initial cache at start
 * 
 * @author Prabal Ghura
 *
 */
@Component
public class StartUpInit {
	
	private static final Logger log = Logger.getLogger(StartUpInit.class.getName());
	
	@Autowired
	BranchService branchService;
	
	@PostConstruct
	public void init(){
		DecimalFormat df = new DecimalFormat("#000");
		long time = System.currentTimeMillis();
		branchService.getAllBranches();
		long timetaken = System.currentTimeMillis() - time;
		log.log(Level.INFO, () -> "Initial Cache built in " + (timetaken/1000) + 
				"." + df.format(timetaken%1000) + " seconds");
	}
}
