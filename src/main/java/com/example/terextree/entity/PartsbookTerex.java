package com.example.terextree.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "partsbook_terex")
public class PartsbookTerex {
    @Id
    private Long id;
    
    @Column(name = "family_name")
    private String familyName;
    
    private String uuid;
    
    @Column(name = "oss_image_path")
    private String ossImagePath;
    
    @Column(name = "hie_dir")
    private String hieDir;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
} 