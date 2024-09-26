package by.nca.rc.repositories;

import by.nca.rc.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    Optional<News> findNewsByTitle(String title);
    Optional<News> findNewsByText(String text);

    @Query("SELECT a FROM News a WHERE " +

            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
            "LOWER(a.text) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<News> findNewsBySearchText(@Param("searchText") String searchText);
}
