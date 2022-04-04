#!/usr/bin/env nextflow

in1 = Channel.fromPath("../input/3/*").collect()

convert = Channel.fromPath("convert.py")

picref = Channel.fromPath("ref.csv")

params.filter = '../input/3'

process nmrpipe {
    echo true
    //publishDir "./", mode: 'move'
    publishDir "./nmrpipe", mode: 'copy'
    container="andreagia/nmrpipe2022"
    //container="andreagia/nmrpipecwl"
    input:
    file x from in1
    output:
    file 'test.tab' into tab
    file 'roi.ps'

    script:
    def filter = params.filter
    """
    ls ./
    pwd
    echo $filter
    echo "@@@@@@@@@@@@@@@"
    echo $baseDir/$filter

    bruker -auto -notk
    sh fid.com
    #source /opt/software/NMRPipe/com/nmrInit.linux212_64.com
    basicFT2.com -xP0 auto -yP0 90 -list > nmr.com
    tcsh nmr.com
    plot1D2D.tcl
    pkDetect2D.tcl -in test.ft2
    ls
    """
}

process covert2csv {
    publishDir "./", mode: 'copy'
    echo true
    container="python"
    input:
    file 'test.tab' from tab
    file 'convert.py' from convert

    output:
    file 'out.csv' into outcsv
    script:
    """
    python convert.py test.tab

    """

}

process runPicasso {
    publishDir "./out", mode: 'copy'
    echo true
    container="cermcirmmp/picasso"
    input:
    file "out.csv" from outcsv
    file "ref.csv" from picref
    output:
    file "*"

    script:
    """
    python /picasso/estimateShifts.py --free ref.csv  --with_ligand  out.csv  --N_divisor 5 --alg RA 
    """
}