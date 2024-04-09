package ru.practicum.ewm.category.model;

import lombok.*;
import ru.practicum.ewm.util.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@Builder
@Entity
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category extends BaseEntity {

        private String name;
}
