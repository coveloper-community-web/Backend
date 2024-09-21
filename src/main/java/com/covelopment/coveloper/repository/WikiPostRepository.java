package com.covelopment.coveloper.repository;

import com.covelopment.coveloper.entity.Post;
import com.covelopment.coveloper.entity.WikiPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WikiPostRepository extends JpaRepository<WikiPost, Long> {
    Optional<WikiPost> findByTeamPost(Post teamPost);
}
