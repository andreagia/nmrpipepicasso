package org.cirmmp.nmrpipepicasso.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class StatusSSE {
    String status;
    String statusroi;
    String statuscsv;
    String dir;
}
