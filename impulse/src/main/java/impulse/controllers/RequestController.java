package impulse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import impulse.models.ImpulseRequest;
import impulse.repos.RequestRepository;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RequestController {

	private RequestRepository reqRepo;

    @Autowired
    public RequestController(RequestRepository reqRepo) {
        this.reqRepo = reqRepo;
    }

	@RequestMapping(method = RequestMethod.POST, path = "/request")
	public ResponseEntity<?> createRepo(
			@RequestBody ImpulseRequest req) {
		try {
			ImpulseRequest givenReq = new ImpulseRequest(req.getData());
			ImpulseRequest newReq = reqRepo.save(givenReq);

			return ResponseEntity.status(201).body(newReq);
		} catch (Exception e) {
			return ResponseEntity.status(201).body("Invalid Input");
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/requests", produces = "application/json")
    public ResponseEntity<?> getAllRequests() {

    	String serverErr = "Internal Server Error";
    	try {
    		serverErr = new ObjectMapper().writeValueAsString(serverErr);
			Iterable<ImpulseRequest> requests = reqRepo.findAll();

			return ResponseEntity.status(200).body(requests);
    	} catch (Exception e) {
    		return ResponseEntity.status(500).body(serverErr);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/request/{repoId}", produces = "application/json")
    public ResponseEntity<?> updateRepo(@RequestBody(required = true) ImpulseRequest repoJson, @PathVariable(value="repoId") Long repoId, HttpServletRequest request) {
    	long userIDLong;
    	String invalidId = "Invalid user supplied";
    	try {

			if (reqRepo.exists(repoId)) {
				ImpulseRequest foundRepo = reqRepo.findOne(repoId);

				foundRepo.setData(repoJson.getData());

			} else
				return ResponseEntity.status(400).body(new ObjectMapper().writeValueAsString("Invalid user supplied"));
		} catch(Exception numFrmtExp) {
    		return ResponseEntity.status(400).body(invalidId);
    	}
    	return null;
    }
}
