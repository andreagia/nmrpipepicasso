package org.cirmmp.nmrpipepicasso.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Job {
    private String name;
    private String tag;
    private String directory;
    private String mail;
    private String command;
    private String exec;
    private String sessid;
}