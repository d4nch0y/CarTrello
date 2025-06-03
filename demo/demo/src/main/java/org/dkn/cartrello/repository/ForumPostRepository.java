package org.dkn.cartrello.repository;

import org.dkn.cartrello.Models.Car;
import org.dkn.cartrello.Models.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findByCar(Car car);
}