package com.geo.easypoint.point.entity;

import com.geo.easypoint.area.entity.AreaStructure;
import com.geo.easypoint.employee.entity.Employee;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "points")
@NamedEntityGraph(
        name = "point-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("creator"),
                @NamedAttributeNode("pointType"),
                @NamedAttributeNode("pointState"),
                @NamedAttributeNode(value = "areaStructure", subgraph = "areaStructure.graph")
        },
        subgraphs = {
                @NamedSubgraph(name = "areaStructure.graph", attributeNodes = {
                        @NamedAttributeNode("areaStructureType"),
                        @NamedAttributeNode("children"),
                        @NamedAttributeNode("parent")
                })
        }
)
public class Point {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "point_sequence_generator")
    @SequenceGenerator(name = "point_sequence_generator", sequenceName = "point_seq")
    private Long id;
    private String name;
    private BigDecimal x;
    private BigDecimal y;
    private BigDecimal h;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee creator;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_type_id")
    private PointType pointType;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_state_id")
    private PointState pointState;
    @JoinColumn(name = "areaStructureId")
    @ManyToOne(fetch = FetchType.LAZY)
    private AreaStructure areaStructure;
    @CreationTimestamp
    private LocalDateTime created;
    @UpdateTimestamp
    private LocalDateTime updated;
}
