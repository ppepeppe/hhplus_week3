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
}
