package org.dkn.cartrello.repository;

import org.dkn.cartrello.model.Car;
import org.dkn.cartrello.model.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findByCar(Car car);
}