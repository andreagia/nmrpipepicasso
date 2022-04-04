package org.cirmmp.nmrpipepicasso.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class OutRunCommnad {
    private int status;
    private List<String> output;
}
