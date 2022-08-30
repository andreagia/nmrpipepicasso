import React, { useState, useEffect  } from 'react';
import 'react-inner-image-zoom/lib/InnerImageZoom/styles.css'
import InnerImageZoom from 'react-inner-image-zoom'
import Plotcsv from "./Plotcsv";

function ImageSSE(props) {

    const [listening, setListening] = useState(false);
    //const [data, setData] = useState([]);
    const [data, setData] = useState({status: "pino", dir: "lino"});
    let eventSource = undefined;

    useEffect(() => {
        if (!listening) {
            eventSource = new EventSource("http://localhost:8080/runsse?sessid="+props.dirtmp);

            eventSource.onopen = (event) => {
                console.log("connection opened SSE")
            }

            eventSource.onmessage = (event) => {
                console.log("result", event.data);
                console.log(JSON.parse(event.data))

                let ret = JSON.parse(event.data);
                console.log(ret.dir);
                console.log(ret.status);
                //setData(old => [...old, event.data])
                setData({
                    status: ret.status,
                    dir: ret.dir,
                    statusroi: ret.statusroi,
                    statuscsv: ret.statuscsv
                });
            }

            eventSource.onerror = (event) => {
                console.log(event.target.readyState)
                if (event.target.readyState === EventSource.CLOSED) {
                    console.log('eventsource closed SSE (' + event.target.readyState + ')')
                }
                eventSource.close();
            }

            setListening(true);
        }

        return () => {
            eventSource.close();
            console.log("eventsource closed SSE")
        }

    }, [])

    return (
        <div className="Timesse">
            <header className="App-header">
                Received Data<b></b>
               DIR {props.dirtmp}<b></b>
               STATUS  {data.status}<b></b>
                {data.dir}<b></b>
               STATUS ROI {data.statusroi}<b></b>
               STATUS CSV {data.statuscsv}<b></b>
                <b></b>

                <a href={"/images/"+props.dirtmp+"/report.html"} target="_blank">report HTML</a>

                {/*<img src={"/images/roi.jpg"}
                     style={{
                         width: '80%',
                         height: 'auto',
                         paddingTop: 10,
                         float: 'right',
                         paddingRight: 10,
                         paddingLeft: 10,
                         transform: "rotate(90deg)",
                         backgroundColor: "white"
                     }}
                />*/}
                { (data.statusroi === "Finished") ?
                    <div>Finished ROI
                        <InnerImageZoom  src={"/images/"+props.dirtmp+"/roi.jpg"} />
                   {/* <img src={"/images/"+props.dirtmp+"/roi.jpg"}
                    style={{
                        width: '70%',
                        height: 'auto',
                        paddingTop: 10,
                        float: 'right',
                        paddingRight: 10,
                        paddingLeft: 10,
                        backgroundColor: "white"
                    }}
                    />*/}
                    </div>
                    : <div>not Finished</div>}

                { (data.statuscsv === "Finished") ?
                    <div>Finished CSV
                       <Plotcsv dirtmp={props.dirtmp}/>

                    </div>
                    : <div>not Finished</div>}
                {/*{data.map(d =>
                    <span key={d}>{d}</span>
                )}*/}
            </header>
        </div>
    );
}

export default ImageSSE;