package impulse.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import impulse.repos.UserRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import impulse.models.ImpulseUser;

@RestController
public class UserController {

	private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user", produces = "application/json")
    public ResponseEntity<?> createUser(@RequestBody(required = true) ImpulseUser user, HttpServletRequest request) {
    	String invalidId = "Invalid input";
    	try {
    		invalidId = new ObjectMapper().writeValueAsString(invalidId);

	    	ImpulseUser newUser = userRepository.save(user);
	    	return ResponseEntity.status(201).body(newUser);
		} catch (Exception ex) {
			return ResponseEntity.status(400).body(invalidId);
		}
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user", produces = "application/json")
    public ResponseEntity<?> getAllUsers() {
    	List<Long> userIDs = new ArrayList<Long>();
    	String serverErr = "Internal Server Error";
    	try {
    		serverErr = new ObjectMapper().writeValueAsString(serverErr);
			Iterable<ImpulseUser> users = userRepository.findAll();
//	    	for(ImpulseUser user : userRepository.findAll())
//	    		userIDs.add(user.getId());

			return ResponseEntity.status(200).body(users);
    	} catch (Exception e) {
    		return ResponseEntity.status(500).body(serverErr);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/user/{userId}", produces = "application/json")
    public ResponseEntity<?> getUser(@PathVariable(value="userId") String userId) {
    	long userIDLong;
    	String invalidId = "Invalid user ID supplied";
    	try {
    		invalidId = new ObjectMapper().writeValueAsString(invalidId);
    		userIDLong = Long.parseLong(userId);
	    	if(userRepository.exists(userIDLong))
		    	return ResponseEntity.status(200).body(new ObjectMapper().writeValueAsString(userRepository.findOne(userIDLong)));
	    	else
	    		return ResponseEntity.status(404).body(new ObjectMapper().writeValueAsString("ImpulseUser could not be found"));
    	} catch(Exception numFrmtExp) {
    		return ResponseEntity.status(400).body(invalidId);
    	}
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/user/{userId}", produces = "application/json")
    public ResponseEntity<?> updateUser(@RequestBody(required = true) String userJson, @PathVariable(value="userId") String userId, HttpServletRequest request) {
    	long userIDLong;
    	String invalidId = "Invalid user supplied";
    	try {
    		invalidId = new ObjectMapper().writeValueAsString(invalidId);
    		userIDLong = Long.parseLong(userId);
	    	boolean nameKey = false;
    		if(userRepository.exists(userIDLong)) {
	    		Iterator<String> keys = new ObjectMapper().readTree(userJson).fieldNames();
	    		while(keys.hasNext()) {
	    			String key = keys.next().toLowerCase();
	    			if(key.equals("name"))
	    				nameKey = true;
	    		}
		    	ImpulseUser user = new ObjectMapper().readValue(userJson, ImpulseUser.class);
		    	if((nameKey  && user.getName() == null))
		    		return ResponseEntity.status(400).body("Invalid user supplied");
		    	if(user.getName() != null) {
		    		ImpulseUser modifiedUser = userRepository.findOne(userIDLong);
		    		if(user.getName() != null)
		    			modifiedUser.setName(user.getName());

		    		userRepository.save(modifiedUser);
			    	return ResponseEntity.status(204).body(new ObjectMapper().writeValueAsString("Update okay"));
		    	} else
		    		return ResponseEntity.status(400).body(new ObjectMapper().writeValueAsString("Invalid user supplied"));		    	}
	    	else
	    		return ResponseEntity.status(404).body(new ObjectMapper().writeValueAsString("ImpulseUser not found"));
    	} catch(Exception numFrmtExp) {
    		return ResponseEntity.status(400).body(invalidId);
    	}
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{userId}", produces = "application/json")
    public ResponseEntity<?> deleteUser(@PathVariable(value="userId") String userId) {
    	long userIDLong;
    	String invalidId = "Invalid ID supplied";
    	try {
    		invalidId = new ObjectMapper().writeValueAsString(invalidId);
    		userIDLong = Long.parseLong(userId);
	    	if(userRepository.exists(userIDLong)) {
	    		userRepository.delete(userIDLong);
		    	return ResponseEntity.status(204).body(new ObjectMapper().writeValueAsString("deleted"));
	    	}
	    	else
	    		return ResponseEntity.status(404).body(new ObjectMapper().writeValueAsString("ImpulseUser not found"));
    	} catch(Exception numFrmtExp) {
    		return ResponseEntity.status(400).body(invalidId);
    	}
    }
}
