package xyz.krakenkat.batchprocessing.dto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransientDTO implements Serializable {
    public String key;
    public String id;
}
