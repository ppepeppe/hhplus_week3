package kr.hhplus.be.server.apps.user.domain.models.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_point")
public class UserPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPointId;
    private Long userId;
    private Integer point;

    public UserPoint(long userId, int point) {
        this.userId = userId;
        this.point = point;
    }
    public void addPoints(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        if (this.point + points > 1_000_000) {
            throw new IllegalArgumentException("총 포인트는 1,000,000을 초과할 수 없습니다.");
        }
        this.point += points;
    }

    public void deductPoints(int points) {
        if (this.point < points) {
            throw new IllegalArgumentException(String.format("포인트 부족: 사용 가능한 포인트는 %d, 차감하려는 포인트는 %d입니다.", this.point, points));
        }
        this.point -= points;
    }




}
