package com.example.jpa;
import com.example.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface TokenRepository extends JpaRepository<TokenEntity,String> {
    @Transactional
    void deleteAllByUserId(int userId);
}
