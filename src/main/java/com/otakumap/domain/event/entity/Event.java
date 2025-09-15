package com.otakumap.domain.event.entity;

import com.otakumap.domain.event.entity.enums.EventStatus;
import com.otakumap.domain.event.entity.enums.EventType;
import com.otakumap.domain.event.entity.enums.Genre;
import com.otakumap.domain.event_location.entity.EventLocation;
import com.otakumap.domain.event_like.entity.EventLike;
import com.otakumap.domain.image.entity.Image;
import com.otakumap.domain.mapping.EventAnimation;
import com.otakumap.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50) // 대표로 사용되는 이벤트 한글 제목
    private String title;

    @Column(nullable = false, length = 50) // 이벤트 일본어 원제
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20)")
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(10)")
    private Genre genre;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15)", nullable = false)
    private EventStatus status;

    @Column(columnDefinition = "TEXT")
    private String site;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_image_id", referencedColumnName = "id")
    private Image thumbnailImage;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "backgroud_image_id", referencedColumnName = "id")
    private Image backgroudImage;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_image_id", referencedColumnName = "id")
    private Image goodsImage;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "event_location_id")
    private EventLocation eventLocation;

    // 랜덤 조회용 컬럼
    @Column(name = "rand_id", nullable = false)
    private int randId;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventLike> eventLikeList = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventAnimation> eventAnimationList = new ArrayList<>();

    public void startEvent() { this.status = EventStatus.IN_PROCESS; }
}
