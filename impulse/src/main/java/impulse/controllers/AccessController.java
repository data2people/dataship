package impulse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import impulse.models.ImpulseAccess;
import impulse.repos.AccessRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccessController {

	private AccessRepository reqRepo;

    @Autowired
    public AccessController(AccessRepository reqRepo) {
        this.reqRepo = reqRepo;
    }

	@RequestMapping(method = RequestMethod.POST, path = "/access")
	public ResponseEntity<?> createRepo(
			@RequestBody ImpulseAccess req) {
		try {
			ImpulseAccess givenReq = new ImpulseAccess(req.getData());
			ImpulseAccess newReq = reqRepo.save(givenReq);

			return ResponseEntity.status(201).body(newReq);
		} catch (Exception e) {
			return ResponseEntity.status(201).body("Invalid Input");
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/accesslist", produces = "application/json")
    public ResponseEntity<?> getAllRequests() {

    	String serverErr = "Internal Server Error";
    	try {
    		serverErr = new ObjectMapper().writeValueAsString(serverErr);
			Iterable<ImpulseAccess> requests = reqRepo.findAll();

			return ResponseEntity.status(200).body(requests);
    	} catch (Exception e) {
    		return ResponseEntity.status(500).body(serverErr);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/access/{repoId}", produces = "application/json")
    public ResponseEntity<?> updateRepo(@RequestBody(required = true) ImpulseAccess repoJson, @PathVariable(value="repoId") Long repoId, HttpServletRequest request) {
    	long userIDLong;
    	String invalidId = "Invalid user supplied";
    	try {

			if (reqRepo.exists(repoId)) {
				ImpulseAccess foundRepo = reqRepo.findOne(repoId);

				foundRepo.setData(repoJson.getData());

			} else
				return ResponseEntity.status(400).body(new ObjectMapper().writeValueAsString("Invalid user supplied"));
		} catch(Exception numFrmtExp) {
    		return ResponseEntity.status(400).body(invalidId);
    	}
    	return null;
    }
}
