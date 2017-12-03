package impulse.repos;

import impulse.models.ImpulseRequest;
import impulse.models.ImpulseUser;
import org.springframework.data.repository.CrudRepository;

public interface RequestRepository extends CrudRepository<ImpulseRequest, Long> {
}
