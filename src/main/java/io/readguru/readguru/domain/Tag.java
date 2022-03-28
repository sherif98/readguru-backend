package io.readguru.readguru.domain;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = { "highlights" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "tags")
@Table(name = "tags")
public class Tag {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private String userId;

    @Column(name = "number_of_highlights")
    @Builder.Default
    private int numberOfHighlights = 0;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private User user;

    @ManyToMany(mappedBy = "tags")
    @JsonBackReference
    Set<Highlight> highlights;

}
