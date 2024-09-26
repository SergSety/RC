package by.nca.rc.security.repository;

import by.nca.rc.security.model.ERole;
import by.nca.rc.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(ERole name);
}
