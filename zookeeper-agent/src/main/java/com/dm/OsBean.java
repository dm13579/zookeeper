package com.dm;

import lombok.*;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OsBean implements Serializable {
    public String ip;
    public Double cpu = null;
    public long usedMemorySize;
    public long usableMemorySize;
    public String pid;
    public long lastUpdateTime;
}
