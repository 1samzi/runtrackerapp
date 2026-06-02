package com.example.runtrackerapp.service;

import com.example.runtrackerapp.dto.*;
import com.example.runtrackerapp.exception.ResourceNotFoundException;
import com.example.runtrackerapp.mapper.UserMapper;
import com.example.runtrackerapp.model.Run;
import com.example.runtrackerapp.model.User;
import com.example.runtrackerapp.repository.UserRepository;
import com.example.runtrackerapp.repository.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repo;
    private final UserMapper userMapper;

    public UserService(UserRepository repository, UserMapper userMapper){
        this.repo = repository;
        this.userMapper = userMapper;
    }

    public Page<UserResponseDTO> findUsersByCriteria(UserFilter filter, Pageable pageable){
        //init specification to TRUE
        Specification<User> spec = (root, query, cb) -> cb.conjunction();

        if (filter.getUserId() != null){
            spec = spec.and(UserSpecification.hasId(filter.getUserId()));
        }
        if (filter.getUsername() != null){
            spec = spec.and(UserSpecification.hasUsername(filter.getUsername()));
        }

        return repo.findAll(spec, pageable).map(userMapper::mapUserToUserResponseDTO);
    }

    public UserStatsResponseDTO getUserStats(Long id){
        User user = repo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found (id): " + id));

        List<Run> runs = user.getRuns();

        int totalRuns = runs.size();

        double totalDistance = round(runs.stream()
                .mapToDouble(Run::getDistanceKM)
                .sum());
        int totalDurationMinutes = runs.stream()
                .mapToInt(Run::getDurationMinutes)
                .sum();

        Run longestRun = findLongestRun(runs);
        Run shortestRun = runs.stream()
                .min(Comparator.comparingDouble(Run::getDistanceKM))
                .orElse(null);
        Run fastestRun = runs.stream()
                .filter(run -> run.getDurationMinutes() > 0)
                .max(Comparator.comparingDouble(this::calculateRunPace))
                .orElse(null);

        double avgDistance = totalRuns == 0 ? 0 : round(totalDistance / totalRuns);
        double avgDurationMinutes = totalRuns == 0 ? 0 : round((double) totalDurationMinutes / totalRuns);
        double avgPace = totalDurationMinutes == 0 ? 0 : calculatePace(totalDistance, totalDurationMinutes);
        double avgRating = totalRuns == 0 ? 0 : round(runs.stream()
                .mapToInt(Run::getRating)
                .average()
                .orElse(0));

        UserStatsResponseDTO dto = new UserStatsResponseDTO();
        dto.setUserId(id);
        dto.setTotalRuns(totalRuns);
        dto.setTotalDistance(totalDistance);
        dto.setTotalDurationMinutes(totalDurationMinutes);
        dto.setAverageDistance(avgDistance);
        dto.setAverageDurationMinutes(avgDurationMinutes);
        dto.setAveragePace(avgPace);
        dto.setAverageRating(avgRating);
        dto.setLongestRun(longestRun);
        dto.setShortestRun(shortestRun);
        dto.setFastestRun(fastestRun);
        dto.setWeeklyStats(buildPeriodStats(runs, this::getWeekStartDate, this::formatWeekPeriod, this::getWeekEndDate));
        dto.setMonthlyStats(buildPeriodStats(runs, this::getMonthStartDate, this::formatMonthPeriod, this::getMonthEndDate));
        dto.setYearlyStats(buildPeriodStats(runs, this::getYearStartDate, this::formatYearPeriod, this::getYearEndDate));

        return dto;
    }



    public User saveUser(UserCreateRequestDTO dto){
        User user = userMapper.mapUserRequestDTOToUser(dto);
        return repo.save(user);
    }

    public List<User> saveUsers(List<UserCreateRequestDTO> dtos){
        List<User> users = dtos.stream()
                .map(userMapper::mapUserRequestDTOToUser)
                .toList();
        return repo.saveAll(users);
    }

    public User updateUser(Long id, UserUpdateRequestDTO dto){
        User user = repo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found (id): " + id));

        user.setUsername(dto.getUsername());

        return repo.save(user);
    }

    public User patchUser(Long id, UserUpdateRequestDTO dto){
        User user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found (id): " + id));

        if (dto.getUsername() != null){
            user.setUsername(dto.getUsername());
        }

        return repo.save(user);
    }


    public User deleteUserById(Long id){
        User userToDelete = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found (id): " + id));

        repo.deleteById(id);

        return userToDelete;
    }

    private List<PeriodStatsResponseDTO> buildPeriodStats(
            List<Run> runs,
            Function<Run, LocalDate> periodStartExtractor,
            Function<LocalDate, String> periodFormatter,
            Function<LocalDate, LocalDate> periodEndExtractor
    ) {
        Map<LocalDate, List<Run>> runsByPeriod = runs.stream()
                .collect(Collectors.groupingBy(periodStartExtractor));

        return runsByPeriod.entrySet().stream()
                .sorted(Map.Entry.<LocalDate, List<Run>>comparingByKey().reversed())
                .map(entry -> buildSinglePeriodStats(
                        entry.getKey(),
                        periodEndExtractor.apply(entry.getKey()),
                        periodFormatter.apply(entry.getKey()),
                        entry.getValue()))
                .toList();
    }

    private PeriodStatsResponseDTO buildSinglePeriodStats(
            LocalDate periodStart,
            LocalDate periodEnd,
            String period,
            List<Run> runs
    ) {
        int totalRuns = runs.size();
        double totalDistance = round(runs.stream()
                .mapToDouble(Run::getDistanceKM)
                .sum());
        int totalDurationMinutes = runs.stream()
                .mapToInt(Run::getDurationMinutes)
                .sum();
        double averageDistance = totalRuns == 0 ? 0 : round(totalDistance / totalRuns);
        double averagePace = totalDurationMinutes == 0 ? 0 : calculatePace(totalDistance, totalDurationMinutes);
        double averageRating = totalRuns == 0 ? 0 : round(runs.stream()
                .mapToInt(Run::getRating)
                .average()
                .orElse(0));

        PeriodStatsResponseDTO dto = new PeriodStatsResponseDTO();
        dto.setPeriod(period);
        dto.setPeriodStart(periodStart);
        dto.setPeriodEnd(periodEnd);
        dto.setTotalRuns(totalRuns);
        dto.setTotalDistance(totalDistance);
        dto.setTotalDurationMinutes(totalDurationMinutes);
        dto.setAverageDistance(averageDistance);
        dto.setAveragePace(averagePace);
        dto.setAverageRating(averageRating);
        dto.setLongestRun(findLongestRun(runs));
        return dto;
    }


    private Run findLongestRun(List<Run> runs) {
        return runs.stream()
                .max(Comparator.comparingDouble(Run::getDistanceKM))
                .orElse(null);
    }

    private double calculateRunPace(Run run) {
        return calculatePace(run.getDistanceKM(), run.getDurationMinutes());
    }

    private double calculatePace(double distance, int durationMinutes) {
        return round(distance / (durationMinutes / 60.0));
    }

    private LocalDate getWeekStartDate(Run run) {
        return run.getDate().with(DayOfWeek.MONDAY);
    }

    private LocalDate getMonthStartDate(Run run) {
        return run.getDate().withDayOfMonth(1);
    }

    private LocalDate getYearStartDate(Run run) {
        return run.getDate().withDayOfYear(1);
    }

    private LocalDate getWeekEndDate(LocalDate periodStart) {
        return periodStart.plusDays(6);
    }

    private LocalDate getMonthEndDate(LocalDate periodStart) {
        return YearMonth.from(periodStart).atEndOfMonth();
    }

    private LocalDate getYearEndDate(LocalDate periodStart) {
        return periodStart.plusYears(1).minusDays(1);
    }

    private String formatWeekPeriod(LocalDate periodStart) {
        WeekFields weekFields = WeekFields.ISO;
        int weekBasedYear = periodStart.get(weekFields.weekBasedYear());
        int weekNumber = periodStart.get(weekFields.weekOfWeekBasedYear());
        return String.format("%d-W%02d", weekBasedYear, weekNumber);
    }

    private String formatMonthPeriod(LocalDate periodStart) {
        return YearMonth.from(periodStart).toString();
    }

    private String formatYearPeriod(LocalDate periodStart) {
        return String.valueOf(periodStart.getYear());
    }


    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}
