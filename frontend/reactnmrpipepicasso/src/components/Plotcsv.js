import React, {useEffect} from "react";
import Plot from 'react-plotly.js';
import {usePapaParse} from 'react-papaparse';

export default function Plotcsv(props) {
    //const [rows, setRows] = React.useState([])
    //const [column, setColumn] = React.useState([])
    const [data, setData] = React.useState([])
    const {readRemoteFile} = usePapaParse();

    //https://react-papaparse.js.org
    useEffect(() => {
        readRemoteFile("/images/" + props.dirtmp + "/out.csv", {
            complete: (results) => {
                console.log('---------------------------');
                console.log('Results:', results);
                console.log('---------------------------');
                setData(results.data);
                //let xdata= results.data.map(a=> a[1])
                //setRows(xdata.slice(1))
                //let ydata= results.data.map(a=> a[2])
                //setColumn(ydata.slice[1])
            },
        });
        // async function getData() {
        //     const response = await fetch("/images/"+props.dirtmp+"/out.csv")
        //     const reader = response.body.getReader()
        //     const result = await reader.read() // raw array
        //     const decoder = new TextDecoder('utf-8')
        //     const csv = decoder.decode(result.value) // the csv text
        //     const results = Papa.parse(csv, { header: true }) // object with { data, errors, meta }
        //     const rows = results.data // array of objects
        //     setRows(rows)
        // }
        // getData()
    }, []) // [] means just do this once, after initial render
    let datasca = []
    if (data.length > 0) {
        let xi = data.map(a => a[1]).slice(1);
        let yi = data.map(a => a[2]).slice(1);
        let ki = data.map(a => a[0]).slice(1);
        datasca = [

            {
                type: 'scatter',
                mode: 'markers+text',
                name: 'Free',
                x: [...xi],
                y: [...yi],
                text: [...ki],
                marker_color: 'blue',
                textfont: {
                    family: 'Times New Roman'
                },
                textposition: 'bottom center',
                marker: {size: 12}
            }];
    }
    let dataplot = (
        <Plot
            data={datasca}
            layout={{
                width: 1024,
                height: 768,
                title: '   ',
                yaxis: {autorange: "reversed"},
                xaxis: {autorange: "reversed"}
            }}
        />)

    console.log(data);
    return (
        <div>
            <h1>Sono in Plotcvs</h1>
            {(data.length > 0) ?
                <div>Finished CSV
                    {dataplot}
                </div>
                : <div>not Finished</div>}
            {/*<Table cols={tripColumns} rows={rows} />*/}
        </div>
    );

}