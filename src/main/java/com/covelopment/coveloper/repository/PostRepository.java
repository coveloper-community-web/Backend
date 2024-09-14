package com.covelopment.coveloper.repository;

import com.covelopment.coveloper.entity.Member;
import com.covelopment.coveloper.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMember(Member member);

    List<Post> findByTeamMembersContaining(Member member);

}
