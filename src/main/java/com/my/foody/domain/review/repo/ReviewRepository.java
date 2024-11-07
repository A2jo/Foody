package com.my.foody.domain.review.repo;

import com.my.foody.domain.review.entity.Review;
import com.my.foody.domain.review.repo.dto.DetailedReviewProjectionRespDto;
import com.my.foody.domain.review.repo.dto.ReviewProjectionRespDto;
import com.my.foody.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r.id as reviewId, " +
            "r.store.id as storeId, " +
            "r.store.name as storeName, " +
            "r.comment as comment, " +
            "r.rating as rating " +
            "from Review r " +
            "where r.user = :user")
    Page<ReviewProjectionRespDto> findReviewsByUser(@Param(value = "user") User user, Pageable pageable);

    boolean existsByOrderId(Long orderId);

    long countByStoreId(Long storeId);

    @Query("SELECT r.id AS reviewId, r.rating AS rating, r.comment AS comment, u.nickname AS userNickname " +
            "FROM Review r JOIN r.user u WHERE r.store.id = :storeId")
    Page<ReviewProjectionRespDto> findReviewsByStoreId(Long storeId, Pageable pageable);

    Page<Review> findByStoreId(Long storeId, Pageable pageable);
    @Query("SELECT r.id AS reviewId, r.store.id AS storeId, r.store.name AS storeName, r.comment AS comment, r.rating AS rating, u.nickname AS userNickname " +
            "FROM Review r JOIN r.user u WHERE r.store.id = :storeId")
    Page<DetailedReviewProjectionRespDto> findDetailedReviewsByStoreId(@Param(value = "storeId") Long storeId, Pageable pageable);
}
