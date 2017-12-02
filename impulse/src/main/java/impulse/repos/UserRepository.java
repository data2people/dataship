package impulse.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import impulse.models.ImpulseUser;

public interface UserRepository extends CrudRepository<ImpulseUser, Long> {
}
