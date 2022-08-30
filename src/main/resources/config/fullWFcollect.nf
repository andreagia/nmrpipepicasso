#!/usr/bin/env nextflow

in1 = Channel.fromPath("./input/*").collect()

convert = Channel.fromPath("convert.py")

picref = Channel.fromPath("./csv/ref.csv", checkIfExists:false)

params.filter = './input'

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
    file 'roi.ps' into roi

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

process rotatejpg{
    publishDir "./", mode: 'copy'
    container="andreagia/imagemagick"
    echo true
    input:
    file "roi.ps" from roi
    output:
    file "roi.jpg"
    script:
    """
    gs -sDEVICE=jpeg -dJPEGQ=100 -dNOPAUSE -dBATCH -dSAFER -r300 -sOutputFile=roi.jpg roi.ps
    mogrify -rotate 90 *.jpg
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
    output:
    file "out.csv"

    script:
    """
    if  test -f "./csv/ref.csv"; then
        echo "TROVATO"
        python /picasso/estimateShifts.py --free ./csv/ref.csv  --with_ligand  out.csv  --N_divisor 5 --alg RA
    else
        echo "NON TROVATO" > out.csv
    fi
    """
}