package ru.organizilla.workspace.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "check_list_item")
@Data
public class CheckListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false)
    private boolean checked;
    @ManyToOne
    @JoinColumn(name = "check_list")
    private CheckList checkList;
}
