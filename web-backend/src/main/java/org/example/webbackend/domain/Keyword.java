package org.example.webbackend.domain;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

@Table(name = "keywords")
@Entity
@TypeDefs(@TypeDef(name = "string-array", typeClass = StringArrayType.class))
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String keyword;

    @Type(type = "string-array")
    @Column(name="category", columnDefinition = "TEXT[]")
    private String[] category;

}
