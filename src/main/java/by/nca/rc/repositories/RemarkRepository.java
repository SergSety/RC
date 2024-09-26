package by.nca.rc.repositories;

import by.nca.rc.models.Remark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RemarkRepository extends JpaRepository<Remark, Long> {

    Optional<Remark> findRemarkByText(String text);
    Page<Remark> findAllByNewsId(Long id, Pageable paging);
}
