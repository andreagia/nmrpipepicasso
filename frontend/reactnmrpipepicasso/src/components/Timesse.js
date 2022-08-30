import React, { useState, useEffect  } from 'react';

function Timesse() {

    const [listening, setListening] = useState(false);
    const [data, setData] = useState([]);
    let eventSource = undefined;

    useEffect(() => {
        if (!listening) {
            eventSource = new EventSource("http://localhost:8080/time");

            eventSource.onopen = (event) => {
                console.log("connection opened")
            }

            eventSource.onmessage = (event) => {
                console.log("result", event.data);
                setData(old => [...old, event.data])
            }

            eventSource.onerror = (event) => {
                console.log(event.target.readyState)
                if (event.target.readyState === EventSource.CLOSED) {
                    console.log('eventsource closed (' + event.target.readyState + ')')
                }
                eventSource.close();
            }

            setListening(true);
        }

        return () => {
            eventSource.close();
            console.log("eventsource closed")
        }

    }, [])

    return (
        <div className="Timesse">
            <header className="App-header">
                Received Data
                {data.map(d =>
                    <span key={d}>{d}</span>
                )}
            </header>
        </div>
    );
}

export default Timesse;