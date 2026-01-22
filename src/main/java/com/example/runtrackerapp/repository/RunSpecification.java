package com.example.runtrackerapp.repository;

import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Extra notes on specification object
//root: represents the Run entity, used to get properties like `distanceKM`
//cb: (CriteriaBuilder): A factory for creating query parts like between, greaterThanOrEqual

public class RunSpecification {

    // For findByDistanceKMBetween
    public static Specification<Run> distanceIsBetween(Double min, Double max){
        return ((root, query, cb) -> cb.between(root.get("distanceKM"), min, max));
    }

    // For: findByRatingGreaterThanEqualOrderByDurationMinutesDesc
    public static Specification<Run> ratingIsGreaterThanOrEqual(Integer minRating) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<Run> ratingIsGreaterLessThanOrEqual(Integer maxRating) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("rating"), maxRating);
    }

    //Find by duration less than
    public static Specification<Run> durationLessThan(Integer maxDuration) {
        return (root, query, cb) -> cb.lessThan(root.get("durationMinutes"), maxDuration);
    }

    //Find runs with specific rating
    public static Specification<Run> hasRating(Integer rating) {
        return (root, query, cb) -> cb.equal(root.get("rating"), rating);
    }

    //Find runs after a given date
    public static Specification<Run> dateIsAfter(LocalDate dateTime) {
        return (root, query, cb) -> cb.greaterThan(root.get("date"), dateTime);
    }

    //Find runs before a given date
    public static Specification<Run> dateIsBefore(LocalDate dateTime) {
        return (root, query, cb) -> cb.lessThan(root.get("date"), dateTime);
    }

    //Finding short and easy runs need a distance less than and use duration less than from above
    public static Specification<Run> distanceLessThanOrEqual(Double distance){
        return ((root, query, cb) -> cb.lessThanOrEqualTo(root.get("distanceKM"), distance));
    }

    public static Specification<Run> distanceGreaterThanOrEqual(Double distance){
        return ((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("distanceKM"), distance));
    }

    //Runs on a specific day
    public static Specification<Run> dateIsOn(LocalDate dateTime){
        return ((root, query, cb) -> cb.equal(root.get("date"), dateTime));
    }

    //Finding Specific users
    public static Specification<Run> hasUser(long userId){
        return (root, query, cb) -> {
            Join<Run, User> userJoin = root.join("user");
            return cb.equal(userJoin.get("user_id"), userId);
        };
    }
}
