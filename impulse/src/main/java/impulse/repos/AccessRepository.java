package impulse.repos;

import impulse.models.ImpulseAccess;
import org.springframework.data.repository.CrudRepository;

public interface AccessRepository extends CrudRepository<ImpulseAccess, Long> {
}
