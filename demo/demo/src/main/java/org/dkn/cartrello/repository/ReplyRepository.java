package org.dkn.cartrello.repository;

import org.dkn.cartrello.model.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}